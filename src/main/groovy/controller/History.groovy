/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package controller

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXTreeTableColumn
import com.jfoenix.controls.JFXTreeTableView
import com.jfoenix.controls.RecursiveTreeItem
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import controller.History.User
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Log4j2
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback

import java.util.function.Function
import java.util.function.Predicate

@Log4j2
// @CompileStatic
class History implements Initializable {

    @FXML
    JFXTreeTableView<User> table

    @FXML
    JFXTreeTableColumn<User, String> department

    @FXML
    JFXTreeTableColumn<User, Integer> age

    @FXML
    JFXTreeTableColumn<User, String> userName

    @FXML
    JFXTextField searchField

    @FXML
    JFXButton dump

    ObservableList<User> users = FXCollections.observableArrayList()

    private <T> void setupCellValueFactory(JFXTreeTableColumn<User, T> column, Function<User, ObservableValue<T>> mapper) {
        column.cellValueFactory = { TreeTableColumn.CellDataFeatures<User, T> param ->
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue())
            } else {
                return column.getComputedValue(param)
            }
        }
    }

    @FXML
    private dumpValues() {
        println " DUMP : "
        users.each {
            println it
        }
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        println "bin in groovy hisroty"

        users.add(new User("Computer Department", 23, "CD 1"))
        users.add(new User("Sales Department", 22, "Employee 1"))
        users.add(new User("Sales Department", 22, "Employee 2"))
        users.add(new User("Sales Department", 25, "Employee 4"))
        users.add(new User("Sales Department", 25, "Employee 5"))
        users.add(new User("IT Department", 42, "ID 2"))
        users.add(new User("HR Department", 22, "HR 1"))
        users.add(new User("HR Department", 22, "HR 2"))

        // https://github.com/jfoenixadmin/JFoenix/blob/master/demo/src/main/java/demos/gui/uicomponents/TreeTableViewController.java
        setupCellValueFactory(department, new Function<User, ObservableValue<String>>() {
            @Override
            ObservableValue<String> apply(User user) {
                return user.department
            }
        })
        setupCellValueFactory(age, new Function<User, ObservableValue<Integer>>() {
            @Override
            ObservableValue<Integer> apply(User p) {
                return p.age.asObject()
            }
        })
        setupCellValueFactory(userName, new Function<User, ObservableValue<String>>() {
            @Override
            ObservableValue<String> apply(User user) {
                return user.userName
            }
        })

        userName.setCellFactory(new Callback<TreeTableColumn<User, String>, TreeTableCell<User, String>>() {
            @Override
            TreeTableCell<User, String> call(TreeTableColumn<User, String> param) {
                return new GenericEditableTreeTableCell<>(
                        new TextFieldEditorBuilder())
            }
        })
        userName.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<User, String>>() {
            @Override
            void handle(TreeTableColumn.CellEditEvent<User, String> t) {
                t.getTreeTableView()
                        .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                        .getValue().userName.set(t.getNewValue())
            }
        })

        table.setRoot(new RecursiveTreeItem<>(users, new Callback<RecursiveTreeObject<User>, ObservableList<User>>() {
            @Override
            ObservableList<User> call(RecursiveTreeObject<User> userRecursiveTreeObject) {
                return userRecursiveTreeObject.getChildren()
            }
        }))


        department.setPrefWidth(200)
        table.group(department)
        table.tableMenuButtonVisible = true
        table.autosize()

        table.setShowRoot(false)
        table.setEditable(true)
        table.getColumns().setAll(department, age, userName)

        searchField.textProperty().addListener(setupSearchField(table))
    }

    private ChangeListener<String> setupSearchField(final JFXTreeTableView<User> tableView) {
        return new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> o, String oldVal, String newVal) {
                tableView.setPredicate(new Predicate<TreeItem<User>>() {
                    @Override
                    boolean test(TreeItem<User> personProp) {
                        final User person = personProp.getValue()
                        return person.userName.get().contains(newVal) || person.department.get().contains(newVal) || Integer.toString(person.age.get()).contains(newVal)
                    }
                })
            }
        }
    }


    @ToString(includeNames = true)
    class User extends RecursiveTreeObject<User> {
        StringProperty userName
        SimpleIntegerProperty age
        StringProperty department

        User(String department, Integer age, String userName) {
            this.department = new SimpleStringProperty(department)
            this.userName = new SimpleStringProperty(userName)
            this.age = new SimpleIntegerProperty(age)
        }
    }
}