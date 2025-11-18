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
import model.ApplicationSettings;
import model.DownloadParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AvailableData implements Initializable {
    private static final Logger log = LogManager.getLogger(AvailableData.class);
    private ApplicationSettings settingsData;

    @FXML
    private DatePicker contentDatePicker;

    @FXML
    private DatePicker uploadDateFromPicker;

    @FXML
    private DatePicker uploadDateToPicker;

    @FXML
    private ComboBox<String> fdpContentCombo;

    @FXML
    private TextField identifierField;

    @FXML
    private Button downloadButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextArea resultTextArea;

    @FXML
    private Label statusLabel;

    @FXML
    private Label infoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize AvailableData controller");
        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        // Setup FDP content type combo
        fdpContentCombo.setItems(FXCollections.observableArrayList(
                "FUND", "REG", "DOC"
        ));
        fdpContentCombo.setValue("FUND");

        // Set default dates
        contentDatePicker.setValue(LocalDate.now());
        uploadDateFromPicker.setValue(LocalDate.now().minusDays(7));
        uploadDateToPicker.setValue(LocalDate.now());

        progressIndicator.setVisible(false);

        infoLabel.setText("Hinweis: Upload-Zeitraum maximal 7 Tage. Wenn ContentDate gesetzt ist, werden Upload-Zeiten ignoriert.");
    }

    @FXML
    private void download() {
        resultTextArea.clear();
        statusLabel.setText("Querying available data...");
        progressIndicator.setVisible(true);
        downloadButton.setDisable(true);

        new Thread(() -> {
            try {
                LocalDate contentDate = contentDatePicker.getValue();
                LocalDateTime uploadTimeFrom = null;
                LocalDateTime uploadTimeTo = null;

                // If no content date, use upload time range
                if (contentDate == null) {
                    if (uploadDateFromPicker.getValue() != null) {
                        uploadTimeFrom = LocalDateTime.of(uploadDateFromPicker.getValue(), LocalTime.MIN);
                    }
                    if (uploadDateToPicker.getValue() != null) {
                        uploadTimeTo = LocalDateTime.of(uploadDateToPicker.getValue(), LocalTime.MAX);
                    }

                    // Validate 7-day maximum range
                    if (uploadTimeFrom != null && uploadTimeTo != null) {
                        long daysBetween = java.time.Duration.between(uploadTimeFrom, uploadTimeTo).toDays();
                        if (daysBetween > 7) {
                            Platform.runLater(() -> {
                                statusLabel.setText("Error: Upload time range cannot exceed 7 days");
                                progressIndicator.setVisible(false);
                                downloadButton.setDisable(false);
                            });
                            return;
                        }
                    }
                }

                String fdpContent = fdpContentCombo.getValue();

                // Optional: IDs filter
                DownloadParameters params = null;
                String identifierText = identifierField.getText().trim();
                if (!identifierText.isEmpty()) {
                    params = new DownloadParameters();
                    // Simple parsing - could be improved
                    String[] ids = identifierText.split("[,\\s]+");
                    for (String id : ids) {
                        if (id.matches("^[A-Z]{2}.*")) { // Looks like ISIN
                            params.addIsin(id);
                        } else {
                            params.addLeiOenId(id);
                        }
                    }
                }

                log.info("Querying available data, content: {}, date: {}", fdpContent, contentDate);
                String result = OeKBHTTP.downloadAvailableData(contentDate, uploadTimeFrom, uploadTimeTo, fdpContent, params);

                Platform.runLater(() -> {
                    resultTextArea.setText(result);
                    statusLabel.setText("Available data query completed");
                    progressIndicator.setVisible(false);
                    downloadButton.setDisable(false);
                });

            } catch (Exception e) {
                log.error("Error during query", e);
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
    private void clearContentDate() {
        contentDatePicker.setValue(null);
        statusLabel.setText("Content date cleared - upload time range will be used");
    }
}
