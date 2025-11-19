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

import dao.DownloadedInformation;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.ApplicationSettings;
import model.DownloadParameters;
import model.DownloadedInformationEntry;
import model.DownloadedInformationEntry.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for Own Data Downloaded page - shows download statistics
 * (who downloaded my data)
 */
public class OwnDataDownloaded implements Initializable {
    private static final Logger log = LogManager.getLogger(OwnDataDownloaded.class);

    @FXML
    private VBox downloadedInfoPane;

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private ComboBox<String> fdpContentCombo;

    @FXML
    private TextField identifierField;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label countLabel;

    @FXML
    private TableView<DownloadedInformationEntry> downloadedInfoTable;

    private final ObservableList<DownloadedInformationEntry> allEntries = FXCollections.observableArrayList();
    private final ObservableList<DownloadedInformationEntry> filteredEntries = FXCollections.observableArrayList();

    private final DownloadedInformation downloadedInformationDao = new DownloadedInformation();
    private final ApplicationSettings settingsData = ApplicationSettings.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing OwnDataDownloaded controller");
        settingsData.readSettingsFromFile();

        setupComboBoxes();
        setupTable();
        setupSearch();

        // Set default dates (last 30 days)
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
        dateToPicker.setValue(LocalDate.now());
    }

    private void setupComboBoxes() {
        // FDP Content Type ComboBox
        fdpContentCombo.setItems(FXCollections.observableArrayList(
                "FUND", "DOC", "REG"
        ));
        fdpContentCombo.setValue("FUND");
    }

    private void setupTable() {
        // Content Type column
        TableColumn<DownloadedInformationEntry, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(80);
        typeCol.setCellValueFactory(cellData -> {
            ContentType type = cellData.getValue().getContentType();
            return new SimpleStringProperty(type != null ? type.name() : "");
        });
        typeCol.setCellFactory(column -> new TableCell<DownloadedInformationEntry, String>() {
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
        TableColumn<DownloadedInformationEntry, String> contentDateCol = new TableColumn<>("Content Date");
        contentDateCol.setPrefWidth(110);
        contentDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormattedContentDate()));

        // Download Date Time column
        TableColumn<DownloadedInformationEntry, String> downloadDateCol = new TableColumn<>("Download Time");
        downloadDateCol.setPrefWidth(140);
        downloadDateCol.setCellValueFactory(cellData -> {
            LocalDateTime downloadTime = cellData.getValue().getDownloadDateTime();
            String formatted = downloadTime != null ?
                    downloadTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new SimpleStringProperty(formatted);
        });

        // Downloaded By (Data Suppliers) column
        TableColumn<DownloadedInformationEntry, String> downloadedByCol = new TableColumn<>("Downloaded By");
        downloadedByCol.setPrefWidth(150);
        downloadedByCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDataSuppliersString()));
        downloadedByCol.setCellFactory(column -> new TableCell<DownloadedInformationEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #c8102e;");
                }
            }
        });

        // Access Info column
        TableColumn<DownloadedInformationEntry, String> accessCol = new TableColumn<>("Access");
        accessCol.setPrefWidth(100);
        accessCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAccessInfo()));

        // Identifiers column
        TableColumn<DownloadedInformationEntry, String> identifiersCol = new TableColumn<>("Identifiers");
        identifiersCol.setPrefWidth(250);
        identifiersCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIdentifierSummary()));

        // Details column
        TableColumn<DownloadedInformationEntry, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setPrefWidth(200);
        detailsCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetails()));
        detailsCol.setCellFactory(column -> new TableCell<DownloadedInformationEntry, String>() {
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
        TableColumn<DownloadedInformationEntry, String> profilesCol = new TableColumn<>("Profiles");
        profilesCol.setPrefWidth(120);
        profilesCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProfilesString()));

        downloadedInfoTable.getColumns().addAll(
                typeCol, contentDateCol, downloadDateCol, downloadedByCol,
                accessCol, identifiersCol, detailsCol, profilesCol
        );

        downloadedInfoTable.setItems(filteredEntries);

        // Enable row selection
        downloadedInfoTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEntries();
        });
    }

    @FXML
    private void loadStatistics() {
        log.debug("Loading download statistics");
        statusLabel.setText("Loading...");

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

                log.debug("Fetching download statistics: from={}, to={}, content={}", dateFrom, dateTo, fdpContent);

                List<DownloadedInformationEntry> entries = downloadedInformationDao.getDownloadedInformationEntries(
                        dateFrom, dateTo, fdpContent, params);

                Platform.runLater(() -> {
                    allEntries.clear();
                    allEntries.addAll(entries);
                    filterEntries();
                    statusLabel.setText("Loaded successfully");
                    log.info("Loaded {} downloaded information entries", entries.size());
                });

            } catch (Exception e) {
                log.error("Error loading download statistics", e);
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    showError("Error loading download statistics", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void clearFilters() {
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
        dateToPicker.setValue(LocalDate.now());
        fdpContentCombo.setValue("FUND");
        identifierField.clear();
        statusLabel.setText("");
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
    }

    private void filterEntries() {
        String searchText = searchField.getText().toLowerCase().trim();

        List<DownloadedInformationEntry> filtered = allEntries.stream()
                .filter(entry -> {
                    // Filter by search text
                    if (!searchText.isEmpty()) {
                        String entryText = String.format("%s %s %s %s %s %s %s %s",
                                entry.getContentType(),
                                entry.getFormattedContentDate(),
                                entry.getFormattedDownloadDateTime(),
                                entry.getDataSuppliersString(),
                                entry.getAccessInfo(),
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
