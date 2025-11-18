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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class History implements Initializable {
    private static final Logger log = LogManager.getLogger(History.class);

    @FXML
    private TreeTableView<User> table;

    @FXML
    private TreeTableColumn<User, String> department;

    @FXML
    private TreeTableColumn<User, Integer> age;

    @FXML
    private TreeTableColumn<User, String> userName;

    @FXML
    private TreeTableColumn<User, String> email;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing History controller");

        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        department.setCellValueFactory(param -> param.getValue().getValue().department);
        userName.setCellValueFactory(param -> param.getValue().getValue().userName);
        age.setCellValueFactory(param -> param.getValue().getValue().age.asObject());
        email.setCellValueFactory(param -> param.getValue().getValue().email);
    }

    private void loadData() {
        users.add(new User("Computer Department", "23", "CD 1", "exampleMail@ex.com"));
        users.add(new User("Sales Department", "22", "Employee 1", "exampleMail@ex.com"));
        users.add(new User("IT Department", "25", "IT 1", "exampleMail@ex.com"));
        users.add(new User("HR Department", "30", "HR 1", "exampleMail@ex.com"));
        users.add(new User("Logistics Department", "19", "LOG 1", "exampleMail@ex.com"));

        TreeItem<User> root = new TreeItem<>(new User("Departments", "", "", ""));
        root.setExpanded(true);

        for (User user : users) {
            TreeItem<User> item = new TreeItem<>(user);
            root.getChildren().add(item);
        }

        table.setRoot(root);
        table.setShowRoot(false);
    }

    @FXML
    private void search() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadData();
            return;
        }

        ObservableList<User> filteredUsers = FXCollections.observableArrayList();
        for (User user : users) {
            if (user.userName.get().toLowerCase().contains(searchText) ||
                user.department.get().toLowerCase().contains(searchText) ||
                user.email.get().toLowerCase().contains(searchText)) {
                filteredUsers.add(user);
            }
        }

        TreeItem<User> root = new TreeItem<>(new User("Filtered Results", "", "", ""));
        root.setExpanded(true);

        for (User user : filteredUsers) {
            TreeItem<User> item = new TreeItem<>(user);
            root.getChildren().add(item);
        }

        table.setRoot(root);
        table.setShowRoot(false);
    }

    public static class User {
        public final SimpleStringProperty department;
        public final SimpleIntegerProperty age;
        public final SimpleStringProperty userName;
        public final SimpleStringProperty email;

        public User(String department, String age, String userName, String email) {
            this.department = new SimpleStringProperty(department);
            int ageValue = 0;
            try {
                ageValue = age.isEmpty() ? 0 : Integer.parseInt(age);
            } catch (NumberFormatException e) {
                ageValue = 0;
            }
            this.age = new SimpleIntegerProperty(ageValue);
            this.userName = new SimpleStringProperty(userName);
            this.email = new SimpleStringProperty(email);
        }
    }
}
