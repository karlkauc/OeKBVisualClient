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
import common.XMLHelper;
import dao.OeKBHTTP;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class DataUpload implements Initializable {
    private static final Logger log = LogManager.getLogger(DataUpload.class);

    @FXML
    private TextArea dataUploadMessage;

    @FXML
    private StackPane dataUpload;

    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("bin im init...");
        dataUploadMessage.clear();
    }

    // wenn file auf pane gezogen wird
    @FXML
    void onDataUpload(DragEvent e) {
        final Dragboard db = e.getDragboard();

        boolean isNotAccepted = false;
        for (File file : db.getFiles()) {
            if (!file.getName().toLowerCase().endsWith("xml")) {
                isNotAccepted = true;
            }
        }

        if (db.hasFiles()) {
            if (!isNotAccepted) {
                dataUpload.setStyle("-fx-border-color: red;" +
                                   "-fx-border-width: 5;" +
                                   "-fx-background-color: #C6C6C6;" +
                                   "-fx-border-style: solid;");
                e.acceptTransferModes(TransferMode.COPY);
            }
        }
    }

    public static void checkForNextActions(String stringData) {
        // String xmlData = new XmlSlurper().parseText(stringData)

        // fehler: id schon vorhanden
        // ofi meldung
        // access rights
    }

    // hier wird das file verarbeitet
    @FXML
    void dataUploadDropped(DragEvent e) {
        log.debug("bin in dropped ");

        final Dragboard db = e.getDragboard();
        StringBuilder text = new StringBuilder();

        for (File file : db.getFiles()) {
            log.debug("speichere File: " + file);
            text.append("speichere File: ").append(file).append(System.lineSeparator());

            try {
                // Check if in FileSystem mode
                model.ApplicationSettings settings = model.ApplicationSettings.getInstance();
                if (settings.isFileSystem()) {
                    dataUploadMessage.setText("⚠️ OFFLINE MODE\n\nThis feature is not available in File System Mode.\n\nTo use this feature:\n1. Go to Settings\n2. Uncheck 'Use File System Mode (Mock XML Data)'\n3. Make sure you have valid OeKB credentials configured");
                    e.setDropCompleted(false);
                    e.consume();
                    return;
                }

                String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                XMLHelper.FileTypes fileType = XMLHelper.getFileType(fileContent);

                String responseFile = OeKBHTTP.uploadDataFile(file);
                if (fileType == XMLHelper.FileTypes.OFI) {
                    text.append("OFI File gefunden ").append(System.lineSeparator());
                    log.debug("OFI FIle.. Summen Checken... ");
                    boolean t = XMLHelper.isOfiResponseOk(responseFile);
                    text.append("OFI Summen OK? ").append(t);
                    log.debug("OFI Summen sind OK?? " + t);
                }
                checkForNextActions(responseFile);
            } catch (Exception ex) {
                log.error("Error processing file", ex);
                text.append("Error: ").append(ex.getMessage()).append(System.lineSeparator());
            }
        }

        dataUploadMessage.setText(text.toString());
        e.setDropCompleted(true);
        e.consume();
    }

    // setDropCompleted can be called only from DRAG_DROPPED handler

    @FXML
    void dataUploadDone(DragEvent e) {
        dataUpload.setStyle("");
        final Dragboard db = e.getDragboard();

        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            // Only get the first file from the list
            final File file = db.getFiles().get(0);
            Platform.runLater(() -> {
                System.out.println(file.getAbsolutePath());
                try {
                    if (!dataUpload.getChildren().isEmpty()) {
                        dataUpload.getChildren().remove(0);
                    }
                    Image img = new Image(new FileInputStream(file.getAbsolutePath()));
                    addImage(img, dataUpload);
                } catch (FileNotFoundException ex) {
                    log.error(ex.toString());
                    log.error(ex.getMessage());
                }
            });
        }
        e.setDropCompleted(success);
        e.consume();
    }

    // Hilfsfunktion
    void addImage(Image i, StackPane pane) {
        imageView = new ImageView();
        imageView.setImage(i);
        pane.getChildren().add(imageView);
    }
}
