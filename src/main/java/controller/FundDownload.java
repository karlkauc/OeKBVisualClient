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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import dao.OeKBHTTP;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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

public class FundDownload implements Initializable {
    private static final Logger log = LogManager.getLogger(FundDownload.class);
    private ApplicationSettings settingsData;

    @FXML
    private TextField leiOenIdField;

    @FXML
    private Button selectFileButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> profileComboBox;

    @FXML
    private TextField blockSizeField;

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
        log.debug("Initialize FundDownload controller");
        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        // Setup profile combo box
        profileComboBox.setItems(FXCollections.observableArrayList(
                "all", "PKG", "Vendor", "OeNB", "OENB_Meldungen"
        ));
        profileComboBox.setValue("all");

        // Set default block size
        blockSizeField.setText("10");

        // Set default date to today
        datePicker.setValue(LocalDate.now());

        progressIndicator.setVisible(false);
        idsFromFile = new ArrayList<>();
    }

    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select LEI/OeNB-ID File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
        if (file != null) {
            try {
                idsFromFile = Files.readAllLines(file.toPath());
                idsFromFile.removeIf(String::isEmpty); // Remove empty lines
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
        statusLabel.setText("Downloading...");
        progressIndicator.setVisible(true);
        downloadButton.setDisable(true);

        // Run download in background thread
        new Thread(() -> {
            try {
                DownloadParameters params = new DownloadParameters("DOWNLOAD_FUND");

                // Set date
                if (datePicker.getValue() != null) {
                    params.setDate(datePicker.getValue());
                }

                // Set profile
                params.setProfile(profileComboBox.getValue());

                // Set block size
                try {
                    int blockSize = Integer.parseInt(blockSizeField.getText());
                    params.setRequestBlockSize(blockSize);
                } catch (NumberFormatException e) {
                    params.setRequestBlockSize(10); // Default
                }

                // Get IDs from text field or file
                List<String> ids = new ArrayList<>();
                if (!idsFromFile.isEmpty()) {
                    ids.addAll(idsFromFile);
                } else {
                    String inputText = leiOenIdField.getText().trim();
                    if (!inputText.isEmpty()) {
                        // Support multiple IDs separated by space, comma, or newline
                        String[] idArray = inputText.split("[,\\s]+");
                        ids.addAll(Arrays.asList(idArray));
                    }
                }

                if (ids.isEmpty()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Error: No LEI/OeNB-IDs provided");
                        progressIndicator.setVisible(false);
                        downloadButton.setDisable(false);
                    });
                    return;
                }

                params.setLeiOenIds(ids);

                log.info("Downloading fund data for {} IDs", ids.size());

                // Check if in FileSystem mode
                ApplicationSettings settings = ApplicationSettings.getInstance();
                if (settings.isFileSystem()) {
                    Platform.runLater(() -> {
                        resultTextArea.setText("⚠️ OFFLINE MODE\n\nThis feature is not available in File System Mode.\n\nTo use this feature:\n1. Go to Settings\n2. Uncheck 'Use File System Mode (Mock XML Data)'\n3. Make sure you have valid OeKB credentials configured");
                        statusLabel.setText("Feature not available in offline mode");
                        progressIndicator.setVisible(false);
                        downloadButton.setDisable(false);
                    });
                    return;
                }

                String result = new OeKBHTTP().downloadFund(params);

                Platform.runLater(() -> {
                    resultTextArea.setText(result);
                    statusLabel.setText("Download completed for " + ids.size() + " fund(s)");
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
