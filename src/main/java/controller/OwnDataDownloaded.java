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
import java.util.ResourceBundle;

public class OwnDataDownloaded implements Initializable {
    private static final Logger log = LogManager.getLogger(OwnDataDownloaded.class);
    private ApplicationSettings settingsData;

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize OwnDataDownloaded controller");
        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        // Setup FDP content type combo
        fdpContentCombo.setItems(FXCollections.observableArrayList(
                "FUND", "REG", "DOC"
        ));
        fdpContentCombo.setValue("FUND");

        // Set default dates (last 30 days)
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
        dateToPicker.setValue(LocalDate.now());

        progressIndicator.setVisible(false);
    }

    @FXML
    private void download() {
        resultTextArea.clear();
        statusLabel.setText("Querying download statistics...");
        progressIndicator.setVisible(true);
        downloadButton.setDisable(true);

        new Thread(() -> {
            try {
                LocalDate dateFrom = dateFromPicker.getValue();
                LocalDate dateTo = dateToPicker.getValue();
                String fdpContent = fdpContentCombo.getValue();

                // Optional: IDs filter
                DownloadParameters params = null;
                String identifierText = identifierField.getText().trim();
                if (!identifierText.isEmpty()) {
                    params = new DownloadParameters();
                    String[] ids = identifierText.split("[,\\s]+");
                    for (String id : ids) {
                        if (id.matches("^[A-Z]{2}.*")) { // Looks like ISIN
                            params.addIsin(id);
                        } else {
                            params.addLeiOenId(id);
                        }
                    }
                }

                log.info("Querying own data downloaded, from: {}, to: {}, content: {}", dateFrom, dateTo, fdpContent);
                String result = OeKBHTTP.downloadOwnDataDownloaded(dateFrom, dateTo, fdpContent, params);

                Platform.runLater(() -> {
                    resultTextArea.setText(result);
                    statusLabel.setText("Download statistics query completed");
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
}
