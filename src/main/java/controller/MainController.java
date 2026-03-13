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

import javafx.geometry.Pos;
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
    private static final Logger LOG = LogManager.getLogger(MainController.class);

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
        Button[] allMenuButtons = {btnSettings, btnAccessRightsReceived, btnAccessRightsGrant, btnJournal,
                btnNewInformation, btnAvailableData, btnDownloadStats, btnDataUpload, btnOeNB, btnFundDownload,
                btnShareClassDownload, btnDocumentDownload, btnRegulatoryReporting, btnAbout};

        for (Button btn : allMenuButtons) {
            if (btn != null) {
                btn.getStyleClass().removeAll("menu-button-active");
            }
        }

        // Add active class to the current button
        if (activeButton != null && !activeButton.getStyleClass().contains("menu-button-active")) {
            activeButton.getStyleClass().add("menu-button-active");
        }
    }

    /**
     * Common helper to load an FXML page into the main content area. Checks for
     * valid settings before loading; redirects to Settings if invalid.
     */
    private void loadPage(String fxmlPath, String pageName, Button menuButton) {
        setActiveMenuButton(menuButton);
        if (!hasValidSettings()) {
            changeToSettings();
            return;
        }
        try {
            javafx.scene.Node tempPane = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainPane.getChildren().setAll(tempPane);
            mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
        } catch (IOException e) {
            LOG.error("Error loading " + pageName, e);
            showErrorPane(pageName, e);
        }
    }

    /**
     * Displays an error recovery pane when FXML loading fails.
     */
    private void showErrorPane(String pageName, Exception e) {
        javafx.scene.layout.VBox errorBox = new javafx.scene.layout.VBox(12);
        errorBox.setStyle("-fx-background-color: #f5f7f9; -fx-padding: 24;");
        errorBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Fehler beim Laden: " + pageName);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");

        Label messageLabel = new Label(e.getMessage() != null ? e.getMessage() : "Unknown error");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6c757d;");
        messageLabel.setWrapText(true);

        mainPane.getChildren().setAll(errorBox);
        errorBox.getChildren().addAll(titleLabel, messageLabel);
    }

    @FXML
    private void changeToSettings() {
        setActiveMenuButton(btnSettings);
        try {
            javafx.scene.Parent tempPane = FXMLLoader
                    .load(getClass().getResource("/pages/pageApplicationSettings.fxml"));
            mainPane.getChildren().setAll(tempPane);
        } catch (IOException e) {
            LOG.error("Error loading settings page", e);
            showErrorPane("Settings", e);
        }
    }

    @FXML
    private void changeToAbout() {
        setActiveMenuButton(btnAbout);
        try {
            javafx.scene.Node tempPane = FXMLLoader.load(getClass().getResource("/pages/pageAbout.fxml"));
            mainPane.getChildren().setAll(tempPane);
            mainPane.setPrefSize(mainPane.getMaxWidth(), mainPane.getMaxHeight());
        } catch (IOException e) {
            LOG.error("Error loading about page", e);
            showErrorPane("About", e);
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
        return settingsData.getOekbUserName() != null && !settingsData.getOekbUserName().isEmpty()
                && settingsData.getOekbPasswort() != null && !settingsData.getOekbPasswort().isEmpty();
    }

    @FXML
    private void changeToProdServer() {
        LOG.debug("switching to PRODUCTION server");
        settingsData.setUseProdServer(true);
        settingsData.saveSettingsDataToFile();
        updateServerButtonStyles();
        updateGlobalStatus();
    }

    @FXML
    private void changeToDevServer() {
        LOG.debug("switching to DEVELOPMENT server");
        settingsData.setUseProdServer(false);
        settingsData.saveSettingsDataToFile();
        updateServerButtonStyles();
        updateGlobalStatus();
    }

    @FXML
    private void changeToAccessRightsReceived() {
        loadPage("/pages/pageAccesRightsReceived.fxml", "Access Rights Received", btnAccessRightsReceived);
    }

    @FXML
    private void changeToAccessRightsGrant() {
        loadPage("/pages/pageAccessRightGrant.fxml", "Access Rights Grant", btnAccessRightsGrant);
    }

    @FXML
    private void changeToJournal() {
        loadPage("/pages/pageJournal.fxml", "Journal", btnJournal);
    }

    @FXML
    private void changeToNewInformation() {
        loadPage("/pages/pageNewInformation.fxml", "New Information", btnNewInformation);
    }

    @FXML
    private void changeToOFI() {
        loadPage("/pages/pageOFI_new.fxml", "OFI", btnOeNB);
    }

    @FXML
    private void changeToDataUpload() {
        loadPage("/pages/pageDataUpload.fxml", "Data Upload", btnDataUpload);
    }

    @FXML
    private void changeToFundDownload() {
        loadPage("/pages/pageFundDownload.fxml", "Fund Download", btnFundDownload);
    }

    @FXML
    private void changeToShareClassDownload() {
        loadPage("/pages/pageShareClassDownload.fxml", "ShareClass Download", btnShareClassDownload);
    }

    @FXML
    private void changeToDocumentDownload() {
        loadPage("/pages/pageDocumentDownload.fxml", "Document Download", btnDocumentDownload);
    }

    @FXML
    private void changeToRegulatoryReporting() {
        loadPage("/pages/pageRegulatoryReporting.fxml", "Regulatory Reporting", btnRegulatoryReporting);
    }

    @FXML
    private void changeToAvailableData() {
        loadPage("/pages/pageAvailableData.fxml", "Available Data", btnAvailableData);
    }

    @FXML
    private void changeToOwnDataDownloaded() {
        loadPage("/pages/pageOwnDataDownloaded.fxml", "Own Data Downloaded", btnDownloadStats);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("starte controller MAIN");
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
