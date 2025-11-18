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

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationSettings implements Initializable {
    private static final Logger log = LogManager.getLogger(ApplicationSettings.class);

    private model.ApplicationSettings settingsData;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button save;

    @FXML
    private TextField proxyHost;

    @FXML
    private TextField proxyPort;

    @FXML
    private CheckBox proxySystemSettings;

    @FXML
    private CheckBox jbOverwriteData;

    @FXML
    private CheckBox jbNewAccesRuleId;

    @FXML
    private TextField proxyUser;

    @FXML
    private PasswordField proxyPassword;

    @FXML
    void saveSettings() {
        settingsData.setOekbUserName(userNameField.getText());
        settingsData.setOekbPasswort(passwordField.getText());
        settingsData.setConnectionProxyHost(proxyHost.getText());

        if (proxyPort.getText() != null && proxyPort.getText().length() > 1) {
            settingsData.setConnectionProxyPort(Integer.parseInt(proxyPort.getText()));
        } else {
            settingsData.setConnectionProxyPort(null);
        }

        settingsData.setConnectionProxyUser(proxyUser.getText());
        settingsData.setConnectionProxyPassword(proxyPassword.getText());
        settingsData.setOverwriteData(jbOverwriteData.isSelected());
        settingsData.setConnectionUseSystemSettings(proxySystemSettings.isSelected());
        settingsData.setNewAccesRuleId(jbNewAccesRuleId.isSelected());
        settingsData.saveSettingsDataToFile();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("starte controller für settings");

        settingsData = model.ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        userNameField.setText(settingsData.getOekbUserName());
        passwordField.setText(settingsData.getOekbPasswort());

        proxyHost.setText(settingsData.getConnectionProxyHost());
        proxyUser.setText(settingsData.getConnectionProxyUser());

        if (settingsData.getConnectionProxyPort() != null) {
            proxyPort.setText(settingsData.getConnectionProxyPort().toString());
        }

        proxyPassword.setText(settingsData.getConnectionProxyPassword());
        proxySystemSettings.setSelected(settingsData.isConnectionUseSystemSettings());

        jbOverwriteData.setSelected(settingsData.isOverwriteData());
        jbNewAccesRuleId.setSelected(settingsData.isNewAccesRuleId());
    }
}
