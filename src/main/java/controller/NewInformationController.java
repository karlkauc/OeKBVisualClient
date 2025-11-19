/*
 * Copyright 2024 Karl Kauc
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

import dao.NewInformation;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.NewInformationEntry;
import model.NewInformationEntry.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for New Information page - displays notifications about newly available data
 */
public class NewInformationController implements Initializable {
    private static final Logger log = LogManager.getLogger(NewInformationController.class);

    @FXML
    private VBox newInformationPane;

    @FXML
    private DatePicker contentDatePicker;

    @FXML
    private DatePicker uploadFromPicker;

    @FXML
    private DatePicker uploadToPicker;

    @FXML
    private ComboBox<String> contentTypeComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label countLabel;

    @FXML
    private TableView<NewInformationEntry> newInformationTable;

    private final ObservableList<NewInformationEntry> allEntries = FXCollections.observableArrayList();
    private final ObservableList<NewInformationEntry> filteredEntries = FXCollections.observableArrayList();

    private final NewInformation newInformationDao = new NewInformation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing NewInformation controller");

        setupComboBoxes();
        setupTable();
        setupSearch();

        // Set default date range (last 7 days)
        uploadFromPicker.setValue(LocalDate.now().minusDays(7));
        uploadToPicker.setValue(LocalDate.now());
    }

    private void setupComboBoxes() {
        // Content Type ComboBox
        contentTypeComboBox.setItems(FXCollections.observableArrayList(
                "All",
                "FUND - Fund Data",
                "DOC - Documents",
                "REG - Regulatory Reporting"
        ));
        contentTypeComboBox.setValue("All");
    }

    private void setupTable() {
        // Content Type column
        TableColumn<NewInformationEntry, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(100);
        typeCol.setCellValueFactory(cellData -> {
            ContentType type = cellData.getValue().getContentType();
            return new SimpleStringProperty(type != null ? type.name() : "");
        });
        typeCol.setCellFactory(column -> new TableCell<NewInformationEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "FUND" -> setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
                        case "DOC" -> setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                        case "REG" -> setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // Content Date column
        TableColumn<NewInformationEntry, String> contentDateCol = new TableColumn<>("Content Date");
        contentDateCol.setPrefWidth(120);
        contentDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormattedContentDate()));

        // Upload Date Time column
        TableColumn<NewInformationEntry, String> uploadDateCol = new TableColumn<>("Upload Time");
        uploadDateCol.setPrefWidth(150);
        uploadDateCol.setCellValueFactory(cellData -> {
            LocalDateTime uploadTime = cellData.getValue().getUploadDateTime();
            String formatted = uploadTime != null ?
                    uploadTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new SimpleStringProperty(formatted);
        });

        // Data Suppliers column
        TableColumn<NewInformationEntry, String> dataSupplierCol = new TableColumn<>("Data Supplier");
        dataSupplierCol.setPrefWidth(150);
        dataSupplierCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDataSuppliersString()));

        // Identifiers column
        TableColumn<NewInformationEntry, String> identifiersCol = new TableColumn<>("Identifiers");
        identifiersCol.setPrefWidth(200);
        identifiersCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIdentifierSummary()));

        // Details column
        TableColumn<NewInformationEntry, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setPrefWidth(250);
        detailsCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetails()));
        detailsCol.setCellFactory(column -> new TableCell<NewInformationEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    if (item.length() > 30) {
                        setTooltip(new Tooltip(item));
                    }
                }
            }
        });

        // Profiles column
        TableColumn<NewInformationEntry, String> profilesCol = new TableColumn<>("Profiles");
        profilesCol.setPrefWidth(150);
        profilesCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProfilesString()));

        newInformationTable.getColumns().addAll(
                typeCol, contentDateCol, uploadDateCol, dataSupplierCol,
                identifiersCol, detailsCol, profilesCol
        );

        newInformationTable.setItems(filteredEntries);

        // Enable row selection
        newInformationTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEntries();
        });
    }

    @FXML
    private void loadNewInformation() {
        log.debug("Loading new information");
        statusLabel.setText("Loading...");

        new Thread(() -> {
            try {
                LocalDate contentDate = contentDatePicker.getValue();
                LocalDateTime uploadFrom = uploadFromPicker.getValue() != null ?
                        LocalDateTime.of(uploadFromPicker.getValue(), LocalTime.MIN) : null;
                LocalDateTime uploadTo = uploadToPicker.getValue() != null ?
                        LocalDateTime.of(uploadToPicker.getValue(), LocalTime.MAX) : null;

                log.debug("Fetching new information entries: contentDate={}, uploadFrom={}, uploadTo={}",
                        contentDate, uploadFrom, uploadTo);

                List<NewInformationEntry> entries = newInformationDao.getNewInformationEntries(
                        contentDate, uploadFrom, uploadTo);

                Platform.runLater(() -> {
                    allEntries.clear();
                    allEntries.addAll(entries);
                    filterEntries();
                    statusLabel.setText("Loaded successfully");
                    log.info("Loaded {} new information entries", entries.size());
                });

            } catch (Exception e) {
                log.error("Error loading new information", e);
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    showError("Error loading new information", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void clearFilters() {
        contentDatePicker.setValue(null);
        uploadFromPicker.setValue(LocalDate.now().minusDays(7));
        uploadToPicker.setValue(LocalDate.now());
        contentTypeComboBox.setValue("All");
        statusLabel.setText("");
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
    }

    private void filterEntries() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedType = contentTypeComboBox.getValue();

        List<NewInformationEntry> filtered = allEntries.stream()
                .filter(entry -> {
                    // Filter by content type
                    if (!selectedType.equals("All")) {
                        String typePrefix = selectedType.split(" ")[0]; // Extract FUND, DOC, or REG
                        if (entry.getContentType() == null ||
                            !entry.getContentType().name().equals(typePrefix)) {
                            return false;
                        }
                    }

                    // Filter by search text
                    if (!searchText.isEmpty()) {
                        String entryText = String.format("%s %s %s %s %s %s %s",
                                entry.getContentType(),
                                entry.getFormattedContentDate(),
                                entry.getFormattedUploadDateTime(),
                                entry.getDataSuppliersString(),
                                entry.getIdentifierSummary(),
                                entry.getDetails(),
                                entry.getProfilesString()
                        ).toLowerCase();

                        return entryText.contains(searchText);
                    }

                    return true;
                })
                .collect(Collectors.toList());

        filteredEntries.clear();
        filteredEntries.addAll(filtered);
        updateCountLabel();
    }

    private void updateCountLabel() {
        int total = allEntries.size();
        int filtered = filteredEntries.size();

        if (total == filtered) {
            countLabel.setText(total + " entries");
        } else {
            countLabel.setText(filtered + " of " + total + " entries");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
