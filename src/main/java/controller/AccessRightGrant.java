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
import dao.AccesRights;
import dao.WriteXLS;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.AccessRule;
import model.ApplicationSettings;
import model.RuleRow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AccessRightGrant implements Initializable {
    private static final Logger log = LogManager.getLogger(AccessRightGrant.class);

    private ApplicationSettings settingsData;
    private List<AccessRule> accessRule;
    private TreeItem<RuleRow> root = new TreeItem<>();
    private AccesRights ar = new AccesRights();

    @FXML
    private TreeTableView<RuleRow> accessRightTable;

    @FXML
    private Button exportToExcel;

    @FXML
    private Label statusMessage;

    @FXML
    private Button dumpData;

    @FXML
    private TextField searchField;

    @FXML
    private Label searchResultLabel;

    @FXML
    void dumpData() {
        for (AccessRule rule : accessRule) {
            log.debug("Rule: {}", rule);
        }
    }

    @FXML
    void exportToExcel() {
        final String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "_accessRulesGranted.xlsx";
        log.debug("speichere alle ab [" + fileName + "].");
        WriteXLS.writeAccessRights(fileName, accessRule);
        statusMessage.setText("Alles gespeichert!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("starte controller für settings");

        settingsData = ApplicationSettings.getInstance();
        accessRule = ar.getAccessRightsGivenFromOEKB();
        accessRightTable.setEditable(true);

        // Display user-friendly message if no data was retrieved
        if (accessRule == null || accessRule.isEmpty()) {
            log.info("No granted access rights found. This may be due to: invalid credentials, network issues, proxy blocking, or no rights granted.");
            if (statusMessage != null) {
                statusMessage.setText("No data available. Check: 1) Credentials in Settings, 2) Network/Proxy settings, 3) Server connection");
                statusMessage.setStyle("-fx-text-fill: #c8102e; -fx-font-weight: bold;");
            }
        }

        for (AccessRule rule : accessRule) {
            String ddsGivenShort = String.join(";", rule.getDataSuppliersGivenShort());

            List<TreeItem<RuleRow>> rootTable = new ArrayList<>();

            TreeItem<RuleRow> ruleId = new TreeItem<>(
                    new RuleRow(rule.getId(),
                            rule.getContentType(),
                            rule.getProfile(),
                            rule.getDataSupplierCreatorShort(),
                            rule.getDataSupplierCreatorName(),
                            ddsGivenShort,
                            rule.getCreationTime(),
                            rule.getAccessDelayInDays(),
                            rule.getDateFrom(),
                            rule.getDateTo(),
                            rule.getFrequency(),
                            rule.getCostsByDataSupplier(),
                            String.valueOf(rule.getLEI().stream().filter(s -> s != null && !s.isEmpty()).count()),
                            String.valueOf(rule.getOENB_ID().stream().filter(s -> s != null && !s.isEmpty()).count()),
                            String.valueOf(rule.getISIN_SHARECLASS().size()),
                            String.valueOf(rule.getISIN_SEGMENT().size()),
                            true
                    )
            );

            for (String lei : rule.getLEI()) {
                log.trace("neuer LEI " + lei);
                TreeItem<RuleRow> l = new TreeItem<>(
                        new RuleRow(rule.getId(),
                                rule.getContentType(),
                                rule.getProfile(),
                                rule.getDataSupplierCreatorShort(),
                                rule.getDataSupplierCreatorName(),
                                ddsGivenShort,
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                lei,
                                null,
                                null,
                                null,
                                false
                        ));
                rootTable.add(l);
            }

            for (String oenbId : rule.getOENB_ID()) {
                log.trace("NEUE OENB GEFUNDEN: " + oenbId);
                TreeItem<RuleRow> oenbTemp = new TreeItem<>(
                        new RuleRow(rule.getId(),
                                rule.getContentType(),
                                rule.getProfile(),
                                rule.getDataSupplierCreatorShort(),
                                rule.getDataSupplierCreatorName(),
                                ddsGivenShort,
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                null,
                                oenbId,
                                null,
                                null,
                                false
                        ));
                rootTable.add(oenbTemp);
            }

            for (String isin : rule.getISIN_SHARECLASS()) {
                log.trace("NEUE ISIN GEFUNDEN " + isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(
                        new RuleRow(rule.getId(),
                                rule.getContentType(),
                                rule.getProfile(),
                                rule.getDataSupplierCreatorShort(),
                                rule.getDataSupplierCreatorName(),
                                ddsGivenShort,
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                null,
                                null,
                                isin,
                                null,
                                false
                        ));
                rootTable.add(isinTemp);
            }

            for (String isin : rule.getISIN_SEGMENT()) {
                log.trace("NEUE ISIN GEFUNDEN " + isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(
                        new RuleRow(rule.getId(),
                                rule.getContentType(),
                                rule.getProfile(),
                                rule.getDataSupplierCreatorShort(),
                                rule.getDataSupplierCreatorName(),
                                ddsGivenShort,
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                null,
                                null,
                                null,
                                isin,
                                false
                        ));
                rootTable.add(isinTemp);
            }

            ruleId.getChildren().addAll(rootTable);
            root.getChildren().addAll(ruleId);
        }

        accessRightTable.setRoot(root);
        accessRightTable.setShowRoot(false);

        // Store controller reference for ButtonCell
        accessRightTable.setUserData(this);

        TreeTableColumn<RuleRow, String> ruleIdCol = new TreeTableColumn<>("Rule ID");
        ruleIdCol.setPrefWidth(150);
        ruleIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        ruleIdCol.setEditable(true);

        TreeTableColumn<RuleRow, Boolean> removeButton = new TreeTableColumn<>();
        removeButton.setCellFactory(param -> new ButtonCell());
        removeButton.setPrefWidth(180); // Increased width to fit both Edit and Delete buttons

        TreeTableColumn<RuleRow, String> profile = new TreeTableColumn<>("Profile");
        profile.setPrefWidth(80);
        profile.setCellValueFactory(new TreeItemPropertyValueFactory<>("profile"));

        TreeTableColumn<RuleRow, String> contentType = new TreeTableColumn<>("Content Type");
        contentType.setPrefWidth(100);
        contentType.setCellValueFactory(new TreeItemPropertyValueFactory<>("ContentType"));

        TreeTableColumn<RuleRow, String> dds = new TreeTableColumn<>("Data Suppliere");
        TreeTableColumn<RuleRow, String> ddsFrom = new TreeTableColumn<>("from");
        ddsFrom.setPrefWidth(50);
        ddsFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSupplierCreatorShort"));
        TreeTableColumn<RuleRow, String> ddsTo = new TreeTableColumn<>("to");
        ddsTo.setPrefWidth(150);
        ddsTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSuppliersGivenShort"));
        dds.getColumns().addAll(ddsFrom, ddsTo);

        TreeTableColumn<RuleRow, String> ids = new TreeTableColumn<>("LEI / OENB ID / ISIN");
        TreeTableColumn<RuleRow, String> lei = new TreeTableColumn<>("LEI");
        lei.setCellValueFactory(new TreeItemPropertyValueFactory<>("LEI"));
        lei.setPrefWidth(130);
        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<>("OENB ID");
        oenbId.setCellValueFactory(new TreeItemPropertyValueFactory<>("OENB_ID"));
        oenbId.setPrefWidth(80);
        TreeTableColumn<RuleRow, String> isin = new TreeTableColumn<>("ISIN SC");
        isin.setCellValueFactory(new TreeItemPropertyValueFactory<>("SHARECLASS_ISIN"));
        isin.setPrefWidth(90);
        TreeTableColumn<RuleRow, String> isinSeg = new TreeTableColumn<>("ISIN Seg");
        isinSeg.setCellValueFactory(new TreeItemPropertyValueFactory<>("SEGMENT_ISIN"));
        isinSeg.setPrefWidth(90);
        ids.getColumns().addAll(lei, oenbId, isin, isinSeg);

        TreeTableColumn<RuleRow, String> fundName = new TreeTableColumn<>("Fund Name");
        fundName.setCellValueFactory(new TreeItemPropertyValueFactory<>("fundName"));
        fundName.setPrefWidth(110);

        TreeTableColumn<RuleRow, String> dateFrom = new TreeTableColumn<>("From");
        dateFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateFrom"));
        TreeTableColumn<RuleRow, String> dateTo = new TreeTableColumn<>("To");
        dateTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateTo"));
        TreeTableColumn<RuleRow, String> frequency = new TreeTableColumn<>("frequency");
        frequency.setCellValueFactory(new TreeItemPropertyValueFactory<>("frequency"));

        TreeTableColumn<RuleRow, String> delay = new TreeTableColumn<>("Delay");
        delay.setCellValueFactory(new TreeItemPropertyValueFactory<>("accessDelayInDays"));

        accessRightTable.setTableMenuButtonVisible(true);
        accessRightTable.getColumns().addAll(ruleIdCol, removeButton, profile, contentType, dds, ids, fundName, dateFrom, dateTo, frequency, delay);

        // Setup search functionality
        setupSearch();
    }

    /**
     * Setup search filter for table
     */
    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTable(newValue);
            });
        }
    }

    /**
     * Filter table based on search text
     */
    private void filterTable(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            // Show all items
            accessRightTable.setRoot(root);
            accessRightTable.setShowRoot(false);
            searchResultLabel.setText("");
            return;
        }

        String lowerCaseFilter = searchText.toLowerCase().trim();
        TreeItem<RuleRow> filteredRoot = new TreeItem<>();
        int matchCount = 0;

        // Search through all rules
        for (TreeItem<RuleRow> ruleItem : root.getChildren()) {
            RuleRow rule = ruleItem.getValue();
            boolean ruleMatches = false;

            // Check if rule matches search
            if (matches(rule, lowerCaseFilter)) {
                ruleMatches = true;
            }

            // Check children (LEI, OENB, ISINs)
            List<TreeItem<RuleRow>> matchingChildren = new ArrayList<>();
            for (TreeItem<RuleRow> child : ruleItem.getChildren()) {
                if (matches(child.getValue(), lowerCaseFilter)) {
                    matchingChildren.add(child);
                }
            }

            // Add rule if it or any of its children match
            if (ruleMatches || !matchingChildren.isEmpty()) {
                TreeItem<RuleRow> filteredRuleItem = new TreeItem<>(rule);
                if (ruleMatches) {
                    // Add all children if rule itself matches
                    filteredRuleItem.getChildren().addAll(ruleItem.getChildren());
                } else {
                    // Add only matching children
                    filteredRuleItem.getChildren().addAll(matchingChildren);
                }
                filteredRoot.getChildren().add(filteredRuleItem);
                matchCount++;
            }
        }

        accessRightTable.setRoot(filteredRoot);
        accessRightTable.setShowRoot(false);

        if (matchCount == 0) {
            searchResultLabel.setText("No matches found");
            searchResultLabel.setStyle("-fx-text-fill: #c8102e;");
        } else {
            searchResultLabel.setText(matchCount + " rule(s) found");
            searchResultLabel.setStyle("-fx-text-fill: #28a745;");
        }
    }

    /**
     * Check if a RuleRow matches the search text
     */
    private boolean matches(RuleRow row, String searchText) {
        if (row == null) return false;

        return contains(row.getId(), searchText) ||
               contains(row.getContentType(), searchText) ||
               contains(row.getProfile(), searchText) ||
               contains(row.getDataSupplierCreatorShort(), searchText) ||
               contains(row.getDataSuppliersGivenShort(), searchText) ||
               contains(row.getLEI(), searchText) ||
               contains(row.getOENB_ID(), searchText) ||
               contains(row.getSHARECLASS_ISIN(), searchText) ||
               contains(row.getSEGMENT_ISIN(), searchText) ||
               contains(row.getDateFrom(), searchText) ||
               contains(row.getDateTo(), searchText) ||
               contains(row.getFrequency(), searchText);
    }

    /**
     * Helper method to check if string contains search text
     */
    private boolean contains(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }

    /**
     * Open edit dialog for an access rule
     * @param ruleRow The rule row to edit
     * @return true if changes were saved, false if cancelled
     */
    public boolean openEditDialog(RuleRow ruleRow) {
        try {
            // Find the AccessRule object from the rule ID
            AccessRule ruleToEdit = accessRule.stream()
                    .filter(ar -> ar.getId().equals(ruleRow.getId()))
                    .findFirst()
                    .orElse(null);

            if (ruleToEdit == null) {
                log.error("Could not find AccessRule with ID: {}", ruleRow.getId());
                showError("Error", "Could not find access rule to edit");
                return false;
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("pages/dialogAccessRuleEdit.fxml"));
            javafx.scene.Parent page = loader.load();

            // Create dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Access Rule: " + ruleToEdit.getId());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(accessRightTable.getScene().getWindow());

            // Set icon
            try {
                Image icon = new Image(getClass().getClassLoader().getResourceAsStream("img/connectdevelop.png"));
                dialogStage.getIcons().add(icon);
            } catch (Exception e) {
                log.warn("Could not load dialog icon", e);
            }

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set access rule in controller
            AccessRuleEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAccessRule(ruleToEdit);

            // Show dialog and wait
            dialogStage.showAndWait();

            // Return whether save was clicked
            return controller.isSaveClicked();

        } catch (IOException e) {
            log.error("Error loading edit dialog", e);
            showError("Error", "Could not load edit dialog: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reload access rights data and refresh table
     */
    public void refreshData() {
        log.info("Refreshing access rights data");

        // Clear existing data
        root.getChildren().clear();

        // Reload from server/file
        accessRule = ar.getAccessRightsGivenFromOEKB();

        if (accessRule == null || accessRule.isEmpty()) {
            statusMessage.setText("No data available after refresh");
            statusMessage.setStyle("-fx-text-fill: #c8102e; -fx-font-weight: bold;");
            return;
        }

        // Rebuild tree
        for (AccessRule rule : accessRule) {
            String ddsGivenShort = String.join(";", rule.getDataSuppliersGivenShort());
            List<TreeItem<RuleRow>> rootTable = new ArrayList<>();

            TreeItem<RuleRow> ruleId = new TreeItem<>(
                    new RuleRow(rule.getId(),
                            rule.getContentType(),
                            rule.getProfile(),
                            rule.getDataSupplierCreatorShort(),
                            rule.getDataSupplierCreatorName(),
                            ddsGivenShort,
                            rule.getCreationTime(),
                            rule.getAccessDelayInDays(),
                            rule.getDateFrom(),
                            rule.getDateTo(),
                            rule.getFrequency(),
                            rule.getCostsByDataSupplier(),
                            String.valueOf(rule.getLEI().stream().filter(s -> s != null && !s.isEmpty()).count()),
                            String.valueOf(rule.getOENB_ID().stream().filter(s -> s != null && !s.isEmpty()).count()),
                            String.valueOf(rule.getISIN_SHARECLASS().size()),
                            String.valueOf(rule.getISIN_SEGMENT().size()),
                            true
                    )
            );

            // Add children (LEI, OENB_ID, ISINs)
            for (String lei : rule.getLEI()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), lei, null, null, null, false)));
            }

            for (String oenbId : rule.getOENB_ID()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, oenbId, null, null, false)));
            }

            for (String isin : rule.getISIN_SHARECLASS()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, isin, null, false)));
            }

            for (String isin : rule.getISIN_SEGMENT()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, null, isin, false)));
            }

            ruleId.getChildren().addAll(rootTable);
            root.getChildren().addAll(ruleId);
        }

        accessRightTable.setRoot(root);
        statusMessage.setText("Data refreshed successfully - " + accessRule.size() + " rules loaded");
        statusMessage.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
    }

    /**
     * Show error alert
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(accessRightTable.getScene().getWindow());
        alert.showAndWait();
    }

    /**
     * Delete an entire access rule
     * @param ruleRow The rule to delete
     * @return true if deleted successfully
     */
    public boolean deleteRule(RuleRow ruleRow) {
        // Find the AccessRule object
        AccessRule ruleToDelete = accessRule.stream()
                .filter(ar -> ar.getId().equals(ruleRow.getId()))
                .findFirst()
                .orElse(null);

        if (ruleToDelete == null) {
            log.error("Could not find AccessRule with ID: {}", ruleRow.getId());
            showError("Error", "Could not find access rule to delete");
            return false;
        }

        // Check FileSystem mode
        ApplicationSettings settings = ApplicationSettings.getInstance();
        boolean isFileSystemMode = settings.isFileSystem();

        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.initOwner(accessRightTable.getScene().getWindow());
        confirmAlert.setTitle("Confirm Delete");

        if (isFileSystemMode) {
            confirmAlert.setHeaderText("Delete Access Rule (OFFLINE MODE)");
            confirmAlert.setContentText(
                    "OFFLINE MODE - No server delete will be performed.\n\n" +
                    "This will:\n" +
                    "1. Log the DELETE XML\n" +
                    "2. Remove from local display\n\n" +
                    "Rule ID: " + ruleToDelete.getId() + "\n\n" +
                    "Do you really want to delete this rule?");
        } else {
            confirmAlert.setHeaderText("Delete Access Rule");
            confirmAlert.setContentText(
                    "This will permanently delete the access rule from server.\n\n" +
                    "Rule ID: " + ruleToDelete.getId() + "\n" +
                    "Content Type: " + ruleToDelete.getContentType() + "\n" +
                    "Profile: " + ruleToDelete.getProfile() + "\n\n" +
                    "Do you really want to delete this rule?");
        }

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return false;
        }

        try {
            // Generate DELETE XML
            String deleteXml = AccesRights.deleteRule(ruleToDelete);
            if (deleteXml == null || deleteXml.isEmpty()) {
                throw new Exception("Failed to generate DELETE XML");
            }

            if (isFileSystemMode) {
                // OFFLINE MODE: Just log
                log.info("=== OFFLINE MODE: DELETE ENTIRE RULE ===");
                log.info("Rule ID: {}", ruleToDelete.getId());
                log.info(deleteXml);
                log.info("=== END DELETE XML ===");

                showSuccess("Success (Offline Mode)",
                           "Access rule marked for deletion.\n\n" +
                           "DELETE XML logged.\n" +
                           "No server delete performed (FileSystem Mode).");
            } else {
                // Upload DELETE XML
                log.info("Uploading DELETE XML for rule: {}", ruleToDelete.getId());
                String result = AccesRights.uploadAccessRuleXml(deleteXml);

                if (result.startsWith("ERROR")) {
                    throw new Exception("Delete failed: " + result);
                }

                showSuccess("Success", "Access rule deleted successfully from server!");
            }

            return true;

        } catch (Exception e) {
            log.error("Error deleting rule", e);
            showError("Delete Failed", "Could not delete access rule:\n\n" + e.getMessage());
            return false;
        }
    }

    /**
     * Show success alert
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(accessRightTable.getScene().getWindow());
        alert.showAndWait();
    }

    /**
     * Create a new access rule
     */
    @FXML
    public void createNewRule() {
        log.info("Creating new access rule");

        try {
            // Create empty AccessRule
            AccessRule newRule = new AccessRule();
            newRule.setId("NEW_RULE_" + System.currentTimeMillis());
            newRule.setContentType("FUND");
            newRule.setProfile("all");
            newRule.setDataSuppliersGivenShort(new ArrayList<>());
            newRule.setLEI(new ArrayList<>());
            newRule.setOENB_ID(new ArrayList<>());
            newRule.setISIN_SEGMENT(new ArrayList<>());
            newRule.setISIN_SHARECLASS(new ArrayList<>());
            newRule.setFrequency("daily");
            newRule.setAccessDelayInDays("0");
            newRule.setDateFrom(LocalDate.now().toString());
            newRule.setCostsByDataSupplier("false");
            newRule.setDataSupplierCreatorShort(settingsData.getDataSupplierList());

            // Load FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("pages/dialogAccessRuleEdit.fxml"));
            javafx.scene.Parent page = loader.load();

            // Create dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Access Rule");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(accessRightTable.getScene().getWindow());

            // Set icon
            try {
                Image icon = new Image(getClass().getClassLoader().getResourceAsStream("img/connectdevelop.png"));
                dialogStage.getIcons().add(icon);
            } catch (Exception e) {
                log.warn("Could not load dialog icon", e);
            }

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set access rule in controller
            AccessRuleEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAccessRule(newRule);

            // Modify save behavior for new rule (no delete needed)
            // We'll need to adjust the handleSave() method to check if it's a new rule

            // Show dialog and wait
            dialogStage.showAndWait();

            // If saved, refresh
            if (controller.isSaveClicked()) {
                log.info("New rule created, refreshing data");
                refreshData();
            }

        } catch (IOException e) {
            log.error("Error loading create new rule dialog", e);
            showError("Error", "Could not load dialog: " + e.getMessage());
        }
    }
}


class ButtonCell extends TreeTableCell<RuleRow, Boolean> {
    private static final Logger logButton = LogManager.getLogger(ButtonCell.class);
    private final HBox buttonBox = new HBox(5); // Container for buttons with 5px spacing
    private final Button editButton = new Button("Edit");
    private final Button deleteButton = new Button("Delete");
    private final Button removeButton = new Button("Remove");
    private AccessRightGrant parentController;

    public ButtonCell() {
        // Apply modern styling and icons to the buttons
        editButton.getStyleClass().addAll("button", "button-primary");
        editButton.setMinWidth(70);
        FontIcon editIcon = new FontIcon("bi-pencil-square");
        editIcon.setIconSize(12);
        editButton.setGraphic(editIcon);

        deleteButton.getStyleClass().addAll("button", "button-danger");
        deleteButton.setMinWidth(70);
        FontIcon deleteIcon = new FontIcon("bi-trash");
        deleteIcon.setIconSize(12);
        deleteButton.setGraphic(deleteIcon);

        removeButton.getStyleClass().addAll("button", "button-danger");
        removeButton.setMinWidth(80);
        FontIcon removeIcon = new FontIcon("bi-x-circle");
        removeIcon.setIconSize(12);
        removeButton.setGraphic(removeIcon);

        // Edit button action - for root rows (entire rules)
        editButton.setOnAction(e -> {
            TreeTableRow<RuleRow> rule = ButtonCell.this.getTreeTableRow();
            logButton.debug("Edit button clicked for root row: " + rule.getItem());

            AccessRightGrant controller = findParentController();
            if (controller != null) {
                boolean saved = controller.openEditDialog(rule.getItem());
                if (saved) {
                    logButton.info("Rule was modified, refreshing data");
                    controller.refreshData();
                }
            } else {
                logButton.error("Could not find parent controller");
            }
        });

        // Delete button action - for root rows (entire rules)
        deleteButton.setOnAction(e -> {
            TreeTableRow<RuleRow> rule = ButtonCell.this.getTreeTableRow();
            logButton.debug("Delete button clicked for root row: " + rule.getItem());

            AccessRightGrant controller = findParentController();
            if (controller != null) {
                boolean deleted = controller.deleteRule(rule.getItem());
                if (deleted) {
                    logButton.info("Rule was deleted, refreshing data");
                    controller.refreshData();
                }
            } else {
                logButton.error("Could not find parent controller");
            }
        });

        // Remove button action - for child rows (fund entries)
        removeButton.setOnAction(e -> {
            TreeTableRow<RuleRow> rule = ButtonCell.this.getTreeTableRow();
            logButton.debug("lösche ISIN aus rule: " + rule.getItem().getLEI() + "/" +
                           rule.getItem().getOENB_ID() + "/" + rule.getItem().getSHARECLASS_ISIN() + "/" +
                           rule.getItem().getSEGMENT_ISIN());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete fund from rule");
            alert.setHeaderText("Do you really want to delete the Fund?");

            Image image = new Image("img/icons8-trash-40.png");
            ImageView imageView = new ImageView(image);
            alert.setGraphic(imageView);

            String text = "delete ";
            if (rule.getItem().getLEI() != null) {
                text += "LEI [" + rule.getItem().getLEI() + "]";
            }
            if (rule.getItem().getOENB_ID() != null) {
                text += "OENB ID [" + rule.getItem().getOENB_ID() + "]";
            }
            if (rule.getItem().getSHARECLASS_ISIN() != null) {
                text += "Shareclass ISIN [" + rule.getItem().getSHARECLASS_ISIN() + "]";
            }
            if (rule.getItem().getSEGMENT_ISIN() != null) {
                text += "Segment ISIN [" + rule.getItem().getSEGMENT_ISIN() + "]";
            }
            text += " from rule [" + rule.getItem().getId() + "]";

            alert.setContentText(text);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logButton.debug("ALLES OK");

                AccesRights ar = new AccesRights();
                ar.deleteFundFromRule(rule.getItem());

            } else {
                logButton.debug("WOLLTE DOCH NICHT");
            }
        });
    }

    // Display buttons based on row type
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);

        if (empty || getTreeTableRow() == null || getTreeTableRow().getItem() == null) {
            setGraphic(null);
            return;
        }

        RuleRow current = getTreeTableRow().getItem();
        logButton.debug("current: " + current);

        if (current.isRootRow()) {
            // Root row: Show Edit and Delete buttons side by side
            buttonBox.getChildren().clear();
            buttonBox.getChildren().addAll(editButton, deleteButton);
            setGraphic(buttonBox);
        } else {
            // Child row: Show only Remove button
            setGraphic(removeButton);
        }
    }

    /**
     * Find the parent AccessRightGrant controller by traversing the scene graph
     */
    private AccessRightGrant findParentController() {
        try {
            // The controller is stored in the TreeTableView's properties during initialization
            // We need to access it via the main controller
            TreeTableView<RuleRow> table = getTreeTableView();
            if (table != null) {
                // Find the AccessRightGrant controller by traversing up the scene graph
                javafx.scene.Node node = table;
                while (node != null) {
                    if (node.getUserData() instanceof AccessRightGrant) {
                        return (AccessRightGrant) node.getUserData();
                    }
                    node = node.getParent();
                }
            }
        } catch (Exception e) {
            logButton.error("Error finding parent controller", e);
        }
        return null;
    }
}
