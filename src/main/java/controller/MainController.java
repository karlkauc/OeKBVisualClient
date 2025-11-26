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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
    private Button prodServerButton;

    @FXML
    private Button devServerButton;

    @FXML
    private URL location;

    @FXML
    private Pane mainPane;

    @FXML
    private Label globalServerStatus;

    @FXML
    private Label globalDdsStatus;

    // Menu buttons
    @FXML
    private Button btnSettings;

    @FXML
    private Button btnAccessRightsReceived;

    @FXML
    private Button btnAccessRightsGrant;

    @FXML
    private Button btnJournal;

    @FXML
    private Button btnNewInformation;

    @FXML
    private Button btnAvailableData;

    @FXML
    private Button btnDownloadStats;

    @FXML
    private Button btnDataUpload;

    @FXML
    private Button btnOeNB;

    @FXML
    private Button btnFundDownload;

    @FXML
    private Button btnShareClassDownload;

    @FXML
    private Button btnDocumentDownload;

    @FXML
    private Button btnRegulatoryReporting;

    @FXML
    private Button btnAbout;

    /**
     * Updates the active menu button styling
     */
    private void setActiveMenuButton(Button activeButton) {
        // Remove active class from all menu buttons
        Button[] allMenuButtons = {
            btnSettings, btnAccessRightsReceived, btnAccessRightsGrant,
            btnJournal, btnNewInformation, btnAvailableData, btnDownloadStats,
            btnDataUpload, btnOeNB, btnFundDownload, btnShareClassDownload,
            btnDocumentDownload, btnRegulatoryReporting, btnAbout
        };

        for (Button btn : allMenuButtons) {
            if (btn != null) {
                btn.getStyleClass().removeAll("menu-button-active");
            }
        }

        // Add active class to the current button
        if (activeButton != null) {
            if (!activeButton.getStyleClass().contains("menu-button-active")) {
                activeButton.getStyleClass().add("menu-button-active");
            }
        }
    }

    @FXML
    private void changeToSettings() {
        setActiveMenuButton(btnSettings);
        log.debug("bin jetzt in settings");

        try {
            javafx.scene.Parent tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageApplicationSettings.fxml"));
            mainPane.getChildren().setAll(tempPane);
        } catch (IOException e) {
            log.error("Error loading settings page", e);
        }
    }

    private void updateServerButtonStyles() {
        if (settingsData.isUseProdServer()) {
            prodServerButton.getStyleClass().removeAll("inactive");
            prodServerButton.getStyleClass().add("active");
            devServerButton.getStyleClass().removeAll("active");
            devServerButton.getStyleClass().add("inactive");
        } else {
            devServerButton.getStyleClass().removeAll("inactive");
            devServerButton.getStyleClass().add("active");
            prodServerButton.getStyleClass().removeAll("active");
            prodServerButton.getStyleClass().add("inactive");
        }
    }

    private void updateGlobalStatus() {
        // Update server status
        if (settingsData.isUseProdServer()) {
            globalServerStatus.setText("PRODUCTION SERVER");
            globalServerStatus.getStyleClass().removeAll("server-status-dev");
            globalServerStatus.getStyleClass().add("server-status-prod");
        } else {
            globalServerStatus.setText("DEVELOPMENT SERVER");
            globalServerStatus.getStyleClass().removeAll("server-status-prod");
            globalServerStatus.getStyleClass().add("server-status-dev");
        }

        // Update DDS status
        String dds = settingsData.getDataSupplierList();
        if (dds != null && !dds.isEmpty()) {
            globalDdsStatus.setText("DDS: " + dds);
        } else {
            globalDdsStatus.setText("");
        }
    }

    private boolean hasValidSettings() {
        return settingsData.getOekbUserName() != null && !settingsData.getOekbUserName().isEmpty() &&
               settingsData.getOekbPasswort() != null && !settingsData.getOekbPasswort().isEmpty();
    }

    @FXML
    private void changeToProdServer() {
        log.debug("switching to PRODUCTION server");
        settingsData.setUseProdServer(true);
        settingsData.saveSettingsDataToFile();
        updateServerButtonStyles();
        updateGlobalStatus();
    }

    @FXML
    private void changeToDevServer() {
        log.debug("switching to DEVELOPMENT server");
        settingsData.setUseProdServer(false);
        settingsData.saveSettingsDataToFile();
        updateServerButtonStyles();
        updateGlobalStatus();
    }

    @FXML
    private void changeToAccessRightsReceived() {
        setActiveMenuButton(btnAccessRightsReceived);
        log.debug("bin jetzt im access rights receive");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAccesRightsReceived.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading access rights received page", e);
            }
        }
    }

    @FXML
    private void changeToAccessRightsGrant() {
        setActiveMenuButton(btnAccessRightsGrant);
        log.debug("bin jetzt im access rights GRANT");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAccessRightGrant.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading access rights grant page", e);
            }
        }
    }

    @FXML
    private void changeToJournal() {
        setActiveMenuButton(btnJournal);
        log.debug("change to Activity Journal");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageJournal.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading journal page", e);
            }
        }
    }

    @FXML
    private void changeToNewInformation() {
        setActiveMenuButton(btnNewInformation);
        log.debug("change to New Information");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageNewInformation.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading new information page", e);
            }
        }
    }

    @FXML
    private void changeToOFI() {
        setActiveMenuButton(btnOeNB);
        log.debug("chenage to OFI");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageOFI_new.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading OFI page", e);
            }
        }
    }

    @FXML
    private void changeToDataUpload() {
        setActiveMenuButton(btnDataUpload);
        log.debug("bin jetzt in changeToDataUpload");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDataUpload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading data upload page", e);
            }
        }
    }

    @FXML
    private void changeToFundDownload() {
        setActiveMenuButton(btnFundDownload);
        log.debug("change to Fund Download");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageFundDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading fund download page", e);
            }
        }
    }

    @FXML
    private void changeToShareClassDownload() {
        setActiveMenuButton(btnShareClassDownload);
        log.debug("change to ShareClass Download");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageShareClassDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading shareclass download page", e);
            }
        }
    }

    @FXML
    private void changeToDocumentDownload() {
        setActiveMenuButton(btnDocumentDownload);
        log.debug("change to Document Download");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDocumentDownload.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading document download page", e);
            }
        }
    }

    @FXML
    private void changeToRegulatoryReporting() {
        setActiveMenuButton(btnRegulatoryReporting);
        log.debug("change to Regulatory Reporting");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageRegulatoryReporting.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading regulatory reporting page", e);
            }
        }
    }

    @FXML
    private void changeToAbout() {
        setActiveMenuButton(btnAbout);
        log.debug("change to About");

        try {
            javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAbout.fxml"));
            mainPane.getChildren().setAll(tempPane);
            mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
        } catch (IOException e) {
            log.error("Error loading about page", e);
        }
    }

    @FXML
    private void changeToAvailableData() {
        setActiveMenuButton(btnAvailableData);
        log.debug("change to Available Data");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAvailableData.fxml"));
                mainPane.getChildren().setAll(tempPane);
                mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
            } catch (IOException e) {
                log.error("Error loading available data page", e);
            }
        }
    }

    @FXML
    private void changeToOwnDataDownloaded() {
        setActiveMenuButton(btnDownloadStats);
        log.debug("change to Own Data Downloaded");

        if (!hasValidSettings()) {
            log.debug("no settings found");
            changeToSettings();
        } else {
            try {
                javafx.scene.Node tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageOwnDataDownloaded.fxml"));
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

        // Update server button styles based on settings
        updateServerButtonStyles();

        // Update global status bar
        updateGlobalStatus();

        // Immer Settings Seite beim Start laden
        changeToSettings();
    }
}
