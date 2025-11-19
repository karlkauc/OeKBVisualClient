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

import dao.Journal;
import dao.WriteXLS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.JournalEntry;
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
 * Controller for Journal page - Activity log of all uploads/downloads
 */
public class JournalController implements Initializable {
    private static final Logger log = LogManager.getLogger(JournalController.class);

    @FXML
    private TableView<JournalEntry> journalTable;

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private ComboBox<String> actionComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusMessage;

    @FXML
    private Label entriesCountLabel;

    @FXML
    private Label searchResultLabel;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportButton;

    private ObservableList<JournalEntry> allEntries;
    private ObservableList<JournalEntry> filteredEntries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing Journal controller");

        allEntries = FXCollections.observableArrayList();
        filteredEntries = FXCollections.observableArrayList();

        setupTable();
        setupFilters();
        loadJournalEntries();
    }

    /**
     * Setup table columns
     */
    private void setupTable() {
        // Timestamp column
        TableColumn<JournalEntry, LocalDateTime> timestampCol = new TableColumn<>("Time");
        timestampCol.setPrefWidth(150);
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampCol.setCellFactory(column -> new TableCell<JournalEntry, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }
        });

        // Action column (Upload/Download)
        TableColumn<JournalEntry, JournalEntry.ActionType> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setCellFactory(column -> new TableCell<JournalEntry, JournalEntry.ActionType>() {
            @Override
            protected void updateItem(JournalEntry.ActionType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getDescription());
                    if (item == JournalEntry.ActionType.UL) {
                        setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Type column
        TableColumn<JournalEntry, JournalEntry.JournalType> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(180);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setCellFactory(column -> new TableCell<JournalEntry, JournalEntry.JournalType>() {
            @Override
            protected void updateItem(JournalEntry.JournalType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescription());
                }
            }
        });

        // Data Supplier column
        TableColumn<JournalEntry, String> dataSupplierCol = new TableColumn<>("Data Supplier");
        dataSupplierCol.setPrefWidth(120);
        dataSupplierCol.setCellValueFactory(new PropertyValueFactory<>("dataSupplier"));

        // User column
        TableColumn<JournalEntry, String> userCol = new TableColumn<>("User");
        userCol.setPrefWidth(100);
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        // Unique ID column
        TableColumn<JournalEntry, String> uniqueIdCol = new TableColumn<>("Document ID");
        uniqueIdCol.setPrefWidth(200);
        uniqueIdCol.setCellValueFactory(new PropertyValueFactory<>("uniqueId"));

        // Details column
        TableColumn<JournalEntry, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setPrefWidth(200);
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

        journalTable.getColumns().addAll(timestampCol, actionCol, typeCol, dataSupplierCol, userCol, uniqueIdCol, detailsCol);
        journalTable.setTableMenuButtonVisible(true);
    }

    /**
     * Setup filter controls
     */
    private void setupFilters() {
        // Set default filter values
        actionComboBox.setValue("All");
        typeComboBox.setValue("All");

        // Set default date range (last 30 days)
        dateToPicker.setValue(LocalDate.now());
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
    }

    /**
     * Load journal entries from OeKB or filesystem
     */
    private void loadJournalEntries() {
        log.debug("Loading journal entries");
        statusMessage.setText("Loading...");

        try {
            Journal journalDao = new Journal();

            LocalDateTime timeFrom = null;
            LocalDateTime timeTo = null;

            if (dateFromPicker.getValue() != null) {
                timeFrom = LocalDateTime.of(dateFromPicker.getValue(), LocalTime.MIN);
            }

            if (dateToPicker.getValue() != null) {
                timeTo = LocalDateTime.of(dateToPicker.getValue(), LocalTime.MAX);
            }

            String action = "All".equals(actionComboBox.getValue()) ? null : actionComboBox.getValue();
            String type = "All".equals(typeComboBox.getValue()) ? null : typeComboBox.getValue();

            List<JournalEntry> entries = journalDao.getJournalEntries(timeFrom, timeTo, action, type, true);

            allEntries.clear();
            allEntries.addAll(entries);

            filteredEntries.clear();
            filteredEntries.addAll(entries);

            journalTable.setItems(filteredEntries);

            entriesCountLabel.setText(entries.size() + " entries");
            statusMessage.setText("Loaded " + entries.size() + " entries");

            log.debug("Loaded " + entries.size() + " journal entries");

        } catch (Exception e) {
            log.error("Error loading journal entries", e);
            statusMessage.setText("Error loading journal");
            statusMessage.setStyle("-fx-text-fill: #c8102e;");
        }
    }

    /**
     * Apply filter button action
     */
    @FXML
    void applyFilter() {
        log.debug("Applying filter");
        loadJournalEntries();
    }

    /**
     * Clear filter button action
     */
    @FXML
    void clearFilter() {
        log.debug("Clearing filter");
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
        dateToPicker.setValue(LocalDate.now());
        actionComboBox.setValue("All");
        typeComboBox.setValue("All");
        searchField.clear();
        loadJournalEntries();
    }

    /**
     * Refresh button action
     */
    @FXML
    void refreshJournal() {
        log.debug("Refreshing journal");
        loadJournalEntries();
    }

    /**
     * Export to Excel
     */
    @FXML
    void exportToExcel() {
        log.debug("Exporting journal to Excel");
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "_journal.xlsx";

        // TODO: Implement WriteXLS.writeJournal() method
        statusMessage.setText("Export not yet implemented");
    }

    /**
     * Filter table based on search text
     */
    @FXML
    void filterTable() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            filteredEntries.clear();
            filteredEntries.addAll(allEntries);
            searchResultLabel.setText("");
        } else {
            List<JournalEntry> matches = allEntries.stream()
                    .filter(entry -> matchesSearchCriteria(entry, searchText))
                    .collect(Collectors.toList());

            filteredEntries.clear();
            filteredEntries.addAll(matches);

            if (matches.size() > 0) {
                searchResultLabel.setText(matches.size() + " matches found");
                searchResultLabel.setStyle("-fx-text-fill: #2e7d32;");
            } else {
                searchResultLabel.setText("No matches");
                searchResultLabel.setStyle("-fx-text-fill: #d32f2f;");
            }
        }

        journalTable.setItems(filteredEntries);
    }

    /**
     * Check if entry matches search criteria
     */
    private boolean matchesSearchCriteria(JournalEntry entry, String searchText) {
        return containsIgnoreCase(entry.getAction() != null ? entry.getAction().getDescription() : "", searchText) ||
               containsIgnoreCase(entry.getType() != null ? entry.getType().getDescription() : "", searchText) ||
               containsIgnoreCase(entry.getDataSupplier(), searchText) ||
               containsIgnoreCase(entry.getUserName(), searchText) ||
               containsIgnoreCase(entry.getUniqueId(), searchText) ||
               containsIgnoreCase(entry.getDetails(), searchText);
    }

    /**
     * Helper method for case-insensitive string comparison
     */
    private boolean containsIgnoreCase(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }
}
