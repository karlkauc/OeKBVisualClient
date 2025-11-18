/*
 * Copyright 2018 Karl Kauc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class History implements Initializable {
    private static final Logger log = LogManager.getLogger(History.class);

    @FXML
    private JFXTreeTableView<User> table;

    @FXML
    private JFXTreeTableColumn<User, String> department;

    @FXML
    private JFXTreeTableColumn<User, Integer> age;

    @FXML
    private JFXTreeTableColumn<User, String> userName;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXButton dump;

    private ObservableList<User> users = FXCollections.observableArrayList();

    private <T> void setupCellValueFactory(JFXTreeTableColumn<User, T> column, Function<User, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    @FXML
    private void dumpValues() {
        System.out.println(" DUMP : ");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("bin in groovy hisroty");

        users.add(new User("Computer Department", 23, "CD 1"));
        users.add(new User("Sales Department", 22, "Employee 1"));
        users.add(new User("Sales Department", 22, "Employee 2"));
        users.add(new User("Sales Department", 25, "Employee 4"));
        users.add(new User("Sales Department", 25, "Employee 5"));
        users.add(new User("IT Department", 42, "ID 2"));
        users.add(new User("HR Department", 22, "HR 1"));
        users.add(new User("HR Department", 22, "HR 2"));

        // https://github.com/jfoenixadmin/JFoenix/blob/master/demo/src/main/java/demos/gui/uicomponents/TreeTableViewController.java
        setupCellValueFactory(department, user -> user.department);
        setupCellValueFactory(age, user -> user.age.asObject());
        setupCellValueFactory(userName, user -> user.userName);

        userName.setCellFactory(param -> new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        userName.setOnEditCommit(t -> {
            t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition().getRow())
                    .getValue().userName.set(t.getNewValue());
        });

        table.setRoot(new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren));

        department.setPrefWidth(200);
        table.group(department);
        table.setTableMenuButtonVisible(true);
        table.autosize();

        table.setShowRoot(false);
        table.setEditable(true);
        table.getColumns().setAll(department, age, userName);

        searchField.textProperty().addListener(setupSearchField(table));
    }

    private javafx.beans.value.ChangeListener<String> setupSearchField(final JFXTreeTableView<User> tableView) {
        return (o, oldVal, newVal) -> {
            tableView.setPredicate(personProp -> {
                final User person = personProp.getValue();
                return person.userName.get().contains(newVal) ||
                       person.department.get().contains(newVal) ||
                       Integer.toString(person.age.get()).contains(newVal);
            });
        };
    }

    public static class User extends RecursiveTreeObject<User> {
        private StringProperty userName;
        private SimpleIntegerProperty age;
        private StringProperty department;

        public User(String department, Integer age, String userName) {
            this.department = new SimpleStringProperty(department);
            this.userName = new SimpleStringProperty(userName);
            this.age = new SimpleIntegerProperty(age);
        }

        @Override
        public String toString() {
            return "User{" +
                    "userName=" + userName.get() +
                    ", age=" + age.get() +
                    ", department=" + department.get() +
                    '}';
        }
    }
}
