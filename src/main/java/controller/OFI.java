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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
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

public class OFI implements Initializable {
    private static final Logger log = LogManager.getLogger(OFI.class);
    private ApplicationSettings settingsData;

    // Common controls
    @FXML
    private TextField oenbIdField;

    @FXML
    private Button selectFileButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label fileLabel;

    @FXML
    private Label statusLabel;

    // Tab 1: Aggregierung
    @FXML
    private CheckBox excludeInvalidAgg;

    @FXML
    private Button downloadAggButton;

    @FXML
    private ProgressIndicator progressAgg;

    @FXML
    private TextArea resultAggTextArea;

    // Tab 2: SecBySec
    @FXML
    private CheckBox excludeInvalidSec;

    @FXML
    private Button downloadSecButton;

    @FXML
    private ProgressIndicator progressSec;

    @FXML
    private TextArea resultSecTextArea;

    // Tab 3: Check
    @FXML
    private RadioButton validAll;

    @FXML
    private RadioButton validTrue;

    @FXML
    private RadioButton validFalse;

    @FXML
    private Button downloadCheckButton;

    @FXML
    private ProgressIndicator progressCheck;

    @FXML
    private TextArea resultCheckTextArea;

    private List<String> oenbIdsFromFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize OFI controller");
        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Hide progress indicators
        progressAgg.setVisible(false);
        progressSec.setVisible(false);
        progressCheck.setVisible(false);

        // Setup radio buttons for Check tab
        validAll.setSelected(true);

        oenbIdsFromFile = new ArrayList<>();
    }

    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select OeNB-ID File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
        if (file != null) {
            try {
                oenbIdsFromFile = Files.readAllLines(file.toPath());
                oenbIdsFromFile.removeIf(String::isEmpty);
                fileLabel.setText("File: " + file.getName() + " (" + oenbIdsFromFile.size() + " OeNB-IDs)");
                statusLabel.setText("Loaded " + oenbIdsFromFile.size() + " OeNB-IDs from file");
                log.info("Loaded {} OeNB-IDs from file: {}", oenbIdsFromFile.size(), file.getName());
            } catch (Exception e) {
                log.error("Error reading file", e);
                statusLabel.setText("Error reading file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearFile() {
        oenbIdsFromFile.clear();
        fileLabel.setText("No file selected");
        statusLabel.setText("File cleared");
    }

    @FXML
    private void downloadAggregierung() {
        resultAggTextArea.clear();
        statusLabel.setText("Downloading OeNB Aggregierung...");
        progressAgg.setVisible(true);
        downloadAggButton.setDisable(true);

        new Thread(() -> {
            try {
                DownloadParameters params = new DownloadParameters("DOWNLOAD_OENB_AGGREGIERUNG");
                params.setDate(datePicker.getValue());
                params.setExcludeInvalid(excludeInvalidAgg.isSelected());

                List<String> ids = getOeNBIds();
                if (ids.isEmpty()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Error: No OeNB-IDs provided");
                        progressAgg.setVisible(false);
                        downloadAggButton.setDisable(false);
                    });
                    return;
                }

                params.setLeiOenIds(ids);

                // Check if in FileSystem mode
                ApplicationSettings settings = ApplicationSettings.getInstance();
                if (settings.isFileSystem()) {
                    Platform.runLater(() -> {
                        resultAggTextArea.setText("⚠️ OFFLINE MODE\n\nThis feature is not available in File System Mode.\n\nTo use this feature:\n1. Go to Settings\n2. Uncheck 'Use File System Mode (Mock XML Data)'\n3. Make sure you have valid OeKB credentials configured");
                        statusLabel.setText("Feature not available in offline mode");
                        progressAgg.setVisible(false);
                        downloadAggButton.setDisable(false);
                    });
                    return;
                }

                log.info("Downloading OeNB Aggregierung for {} IDs", ids.size());
                String result = OeKBHTTP.downloadOeNBAggregierung(params);

                Platform.runLater(() -> {
                    resultAggTextArea.setText(result);
                    statusLabel.setText("Aggregierung download completed for " + ids.size() + " fund(s)");
                    progressAgg.setVisible(false);
                    downloadAggButton.setDisable(false);
                });

            } catch (Exception e) {
                log.error("Error during download", e);
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    resultAggTextArea.setText("Error occurred:\n" + e.getMessage());
                    progressAgg.setVisible(false);
                    downloadAggButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void downloadSecBySec() {
        resultSecTextArea.clear();
        statusLabel.setText("Downloading OeNB SecBySec...");
        progressSec.setVisible(true);
        downloadSecButton.setDisable(true);

        new Thread(() -> {
            try {
                DownloadParameters params = new DownloadParameters("DOWNLOAD_OENB_SECBYSEC");
                params.setDate(datePicker.getValue());
                params.setExcludeInvalid(excludeInvalidSec.isSelected());

                List<String> ids = getOeNBIds();
                if (ids.isEmpty()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Error: No OeNB-IDs provided");
                        progressSec.setVisible(false);
                        downloadSecButton.setDisable(false);
                    });
                    return;
                }

                params.setLeiOenIds(ids);

                // Check if in FileSystem mode
                ApplicationSettings settings = ApplicationSettings.getInstance();
                if (settings.isFileSystem()) {
                    Platform.runLater(() -> {
                        resultSecTextArea.setText("⚠️ OFFLINE MODE\n\nThis feature is not available in File System Mode.\n\nTo use this feature:\n1. Go to Settings\n2. Uncheck 'Use File System Mode (Mock XML Data)'\n3. Make sure you have valid OeKB credentials configured");
                        statusLabel.setText("Feature not available in offline mode");
                        progressSec.setVisible(false);
                        downloadSecButton.setDisable(false);
                    });
                    return;
                }

                log.info("Downloading OeNB SecBySec for {} IDs", ids.size());
                String result = OeKBHTTP.downloadOeNBSecBySec(params);

                Platform.runLater(() -> {
                    resultSecTextArea.setText(result);
                    statusLabel.setText("SecBySec download completed for " + ids.size() + " fund(s)");
                    progressSec.setVisible(false);
                    downloadSecButton.setDisable(false);
                });

            } catch (Exception e) {
                log.error("Error during download", e);
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    resultSecTextArea.setText("Error occurred:\n" + e.getMessage());
                    progressSec.setVisible(false);
                    downloadSecButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void downloadCheck() {
        resultCheckTextArea.clear();
        statusLabel.setText("Downloading OeNB Check...");
        progressCheck.setVisible(true);
        downloadCheckButton.setDisable(true);

        new Thread(() -> {
            try {
                LocalDate date = datePicker.getValue();

                String validFilter = null;
                if (validTrue.isSelected()) {
                    validFilter = "true";
                } else if (validFalse.isSelected()) {
                    validFilter = "false";
                }
                // validAll selected means no filter (null)

                // Check if in FileSystem mode
                ApplicationSettings settings = ApplicationSettings.getInstance();
                if (settings.isFileSystem()) {
                    Platform.runLater(() -> {
                        resultCheckTextArea.setText("⚠️ OFFLINE MODE\n\nThis feature is not available in File System Mode.\n\nTo use this feature:\n1. Go to Settings\n2. Uncheck 'Use File System Mode (Mock XML Data)'\n3. Make sure you have valid OeKB credentials configured");
                        statusLabel.setText("Feature not available in offline mode");
                        progressCheck.setVisible(false);
                        downloadCheckButton.setDisable(false);
                    });
                    return;
                }

                log.info("Downloading OeNB Check for date {}", date);
                String result = OeKBHTTP.downloadOeNBCheck(date, validFilter);

                Platform.runLater(() -> {
                    resultCheckTextArea.setText(result);
                    statusLabel.setText("Check download completed");
                    progressCheck.setVisible(false);
                    downloadCheckButton.setDisable(false);
                });

            } catch (Exception e) {
                log.error("Error during download", e);
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    resultCheckTextArea.setText("Error occurred:\n" + e.getMessage());
                    progressCheck.setVisible(false);
                    downloadCheckButton.setDisable(false);
                });
            }
        }).start();
    }

    private List<String> getOeNBIds() {
        List<String> ids = new ArrayList<>();
        if (!oenbIdsFromFile.isEmpty()) {
            ids.addAll(oenbIdsFromFile);
        } else {
            String inputText = oenbIdField.getText().trim();
            if (!inputText.isEmpty()) {
                String[] idArray = inputText.split("[,\\s]+");
                ids.addAll(Arrays.asList(idArray));
            }
        }
        return ids;
    }
}
