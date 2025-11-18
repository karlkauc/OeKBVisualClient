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

import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import model.ApplicationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger log = LogManager.getLogger(MainController.class);

    private ApplicationSettings settingsData;

    @FXML
    private TextArea debugMessages;

    @FXML
    private ToggleButton prodServer;

    @FXML
    private CheckBox fileSystem;

    @FXML
    private URL location;

    @FXML
    private Pane mainPane;

    @FXML
    private TextField dataSupplier;

    @FXML
    private Label server;

    @FXML
    void changeDDS() {
        settingsData.setDataSupplierList(dataSupplier.getText());
        settingsData.saveSettingsDataToFile();
    }

    @FXML
    private void changeToSettings() {
        log.debug("bin jetzt in settings");

        try {
            javafx.scene.Parent tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageApplicationSettings.fxml"));
            mainPane.getChildren().setAll(tempPane);
        } catch (IOException e) {
            log.error("Error loading settings page", e);
        }
    }

    @FXML
    private void changeFileSystem() {
        settingsData.setFileSystem(fileSystem.isSelected());
        log.debug("file system: " + fileSystem.isSelected());
        settingsData.saveSettingsDataToFile();
    }

    private void setLabelTextForServer() {
        if (settingsData.isUseProdServer()) {
            server.setText("PROD SERVER");
        } else {
            server.setText("DEV SERVER");
        }
    }

    private boolean hasValidSettings() {
        return settingsData.getOekbUserName() != null && !settingsData.getOekbUserName().isEmpty() &&
               settingsData.getOekbPasswort() != null && !settingsData.getOekbPasswort().isEmpty();
    }

    @FXML
    private void changeServer() {
        settingsData.setUseProdServer(prodServer.isSelected());
        settingsData.saveSettingsDataToFile();
        setLabelTextForServer();
    }

    @FXML
    private void changeToAccessRightsReceived() {
        log.debug("bin jetzt im access rights receive");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAccesRightsReceived.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading access rights received page", e);
            }
        }
    }

    @FXML
    private void changeToAccessRightsGrant() {
        System.out.println("bin jetzt im access rights GRANT");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAccessRightGrant.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading access rights grant page", e);
            }
        }
    }

    @FXML
    private void changeToOFI() {
        log.debug("chenage to OFI");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageOFI_new.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading OFI page", e);
            }
        }
    }

    @FXML
    private void changeToDataUpload() {
        log.debug("bin jetzt in changeToDataUpload");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDataUpload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading data upload page", e);
            }
        }
    }

    @FXML
    private void changeToHistory() {
        log.debug("bin jetzt in changeToHistory");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageHistory.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading history page", e);
            }
        }
    }

    @FXML
    private void changeToDataDownload() {
        log.debug("bin jetzt in changeToDataDownload");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDataDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading data download page", e);
            }
        }
    }

    @FXML
    private void changeToFundDownload() {
        log.debug("change to Fund Download");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageFundDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading fund download page", e);
            }
        }
    }

    @FXML
    private void changeToShareClassDownload() {
        log.debug("change to ShareClass Download");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageShareClassDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading shareclass download page", e);
            }
        }
    }

    @FXML
    private void changeToDocumentDownload() {
        log.debug("change to Document Download");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDocumentDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading document download page", e);
            }
        }
    }

    @FXML
    private void changeToRegulatoryReporting() {
        log.debug("change to Regulatory Reporting");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageRegulatoryReporting.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading regulatory reporting page", e);
            }
        }
    }

    @FXML
    private void changeToAvailableData() {
        log.debug("change to Available Data");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAvailableData.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading available data page", e);
            }
        }
    }

    @FXML
    private void changeToOwnDataDownloaded() {
        log.debug("change to Own Data Downloaded");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageOwnDataDownloaded.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading own data downloaded page", e);
            }
        }
    }

    public MainController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("starte controller MAIN");
        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        prodServer.setSelected(settingsData.isUseProdServer());
        fileSystem.setSelected(settingsData.isFileSystem());

        // keine Zugangsdaten eingegeben -> zuerst mal zur Settings Seite
        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        }

        dataSupplier.setEditable(true);
        dataSupplier.setText(settingsData.getDataSupplierList() != null ? settingsData.getDataSupplierList() : "");
        setLabelTextForServer();

        fileSystem.setVisible(settingsData.isFileSystem());
    }
}
