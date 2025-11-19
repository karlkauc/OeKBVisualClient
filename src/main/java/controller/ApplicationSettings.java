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

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ApplicationSettings implements Initializable {
    private static final Logger log = LogManager.getLogger(ApplicationSettings.class);

    private model.ApplicationSettings settingsData;

    // Data Supplier codes from Liste+DataSupplier.pdf (Stand: 10.12.2024)
    private static final List<String> DATA_SUPPLIERS = Arrays.asList(
            // Datenmelder/Datenbezieher
            "FUM", "3BA", "AIB", "AMP", "CAP", "BAI", "EAM", "EIK", "FTCFFS", "GUT",
            "CSP", "KEP", "CPI", "CON", "INV", "HYP", "PFSSICAV", "RIK", "RAI", "SKW",
            "SEC", "ASL", "VBI",
            // Datenbezieher only
            "Dolomiten", "EDRF", "EDR", "Fenion", "Finee", "FOCUS", "NOEVERS", "RZB",
            "UniCredit", "ssat",
            // Datenvendor
            "FactSet", "ThomsonReuters",
            // OeKB
            "OeKB"
    );

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> dataSupplierComboBox;

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
    private CheckBox fileSystemCheckBox;

    @FXML
    private TextField backupDirectoryField;

    @FXML
    void saveSettings() {
        settingsData.setOekbUserName(userNameField.getText());
        settingsData.setOekbPasswort(passwordField.getText());
        settingsData.setDataSupplierList(dataSupplierComboBox.getValue());
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

        // Development & Testing settings
        settingsData.setFileSystem(fileSystemCheckBox.isSelected());
        settingsData.setBackupDirectory(backupDirectoryField.getText());

        settingsData.saveSettingsDataToFile();

        log.info("Settings saved - File System Mode: {}, Backup Directory: {}",
                fileSystemCheckBox.isSelected(), backupDirectoryField.getText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("starte controller für settings");

        settingsData = model.ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        // Initialize ComboBox with Data Supplier codes
        dataSupplierComboBox.setItems(FXCollections.observableArrayList(DATA_SUPPLIERS));

        userNameField.setText(settingsData.getOekbUserName());
        passwordField.setText(settingsData.getOekbPasswort());

        // Set current value in ComboBox
        String currentDDS = settingsData.getDataSupplierList();
        if (currentDDS != null && !currentDDS.isEmpty()) {
            dataSupplierComboBox.setValue(currentDDS);
        }

        proxyHost.setText(settingsData.getConnectionProxyHost());
        proxyUser.setText(settingsData.getConnectionProxyUser());

        if (settingsData.getConnectionProxyPort() != null) {
            proxyPort.setText(settingsData.getConnectionProxyPort().toString());
        }

        proxyPassword.setText(settingsData.getConnectionProxyPassword());
        proxySystemSettings.setSelected(settingsData.isConnectionUseSystemSettings());

        jbOverwriteData.setSelected(settingsData.isOverwriteData());
        jbNewAccesRuleId.setSelected(settingsData.isNewAccesRuleId());

        // Development & Testing settings
        fileSystemCheckBox.setSelected(settingsData.isFileSystem());
        backupDirectoryField.setText(settingsData.getBackupDirectory());
    }
}
