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

// JFoenix removed
import dao.OeKBHTTP;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.ApplicationSettings;
import model.DownloadParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class RegulatoryReportingDownload implements Initializable {
    private static final Logger log = LogManager.getLogger(RegulatoryReportingDownload.class);
    private ApplicationSettings settingsData;

    @FXML
    private TextField identifierField;

    @FXML
    private ComboBox<String> identifierTypeCombo;

    @FXML
    private Button selectFileButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> profileComboBox;

    @FXML
    private ComboBox<String> reportingTypeCombo;

    @FXML
    private Button downloadButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextArea resultTextArea;

    @FXML
    private Label statusLabel;

    @FXML
    private Label fileLabel;

    private List<String> idsFromFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize RegulatoryReportingDownload controller");
        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        // Setup identifier type combo
        identifierTypeCombo.setItems(FXCollections.observableArrayList(
                "LEI/OeNB-ID", "ISIN"
        ));
        identifierTypeCombo.setValue("LEI/OeNB-ID");

        // Setup profile combo box
        profileComboBox.setItems(FXCollections.observableArrayList(
                "all", "PKG", "Vendor", "OeNB", "OENB_Meldungen"
        ));
        profileComboBox.setValue("all");

        // Setup reporting type combo
        reportingTypeCombo.setItems(FXCollections.observableArrayList(
                "all",
                "EMIR",
                "KIIDs",
                "EMT",
                "TripartiteTemplateSolvencyII",
                "PRIIPS"
        ));
        reportingTypeCombo.setValue("all");

        // Set default date to today
        datePicker.setValue(LocalDate.now());

        progressIndicator.setVisible(false);
        idsFromFile = new ArrayList<>();
    }

    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select ID File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
        if (file != null) {
            try {
                idsFromFile = Files.readAllLines(file.toPath());
                idsFromFile.removeIf(String::isEmpty);
                fileLabel.setText("File: " + file.getName() + " (" + idsFromFile.size() + " IDs)");
                statusLabel.setText("Loaded " + idsFromFile.size() + " IDs from file");
                log.info("Loaded {} IDs from file: {}", idsFromFile.size(), file.getName());
            } catch (Exception e) {
                log.error("Error reading file", e);
                statusLabel.setText("Error reading file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void download() {
        resultTextArea.clear();
        statusLabel.setText("Downloading regulatory reportings...");
        progressIndicator.setVisible(true);
        downloadButton.setDisable(true);

        new Thread(() -> {
            try {
                DownloadParameters params = new DownloadParameters("DOWNLOAD_REG_REPORTINGS");

                // Set date
                if (datePicker.getValue() != null) {
                    params.setDate(datePicker.getValue());
                }

                // Set profile
                params.setProfile(profileComboBox.getValue());

                // Get IDs from text field or file
                List<String> ids = new ArrayList<>();
                if (!idsFromFile.isEmpty()) {
                    ids.addAll(idsFromFile);
                } else {
                    String inputText = identifierField.getText().trim();
                    if (!inputText.isEmpty()) {
                        String[] idArray = inputText.split("[,\\s]+");
                        ids.addAll(Arrays.asList(idArray));
                    }
                }

                if (ids.isEmpty()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Error: No identifiers provided");
                        progressIndicator.setVisible(false);
                        downloadButton.setDisable(false);
                    });
                    return;
                }

                // Set IDs based on type
                String idType = identifierTypeCombo.getValue();
                if (idType.equals("ISIN")) {
                    params.setIsins(ids);
                } else {
                    params.setLeiOenIds(ids);
                }

                String reportingType = reportingTypeCombo.getValue();

                log.info("Downloading regulatory reportings for {} IDs, type: {}", ids.size(), reportingType);
                String result = OeKBHTTP.downloadRegulatoryReportings(params, reportingType);

                Platform.runLater(() -> {
                    resultTextArea.setText(result);
                    statusLabel.setText("Regulatory reporting download completed for " + ids.size() + " identifier(s)");
                    progressIndicator.setVisible(false);
                    downloadButton.setDisable(false);
                });

            } catch (Exception e) {
                log.error("Error during download", e);
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    resultTextArea.setText("Error occurred:\n" + e.getMessage());
                    progressIndicator.setVisible(false);
                    downloadButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void clearFile() {
        idsFromFile.clear();
        fileLabel.setText("No file selected");
        statusLabel.setText("File cleared");
    }
}
