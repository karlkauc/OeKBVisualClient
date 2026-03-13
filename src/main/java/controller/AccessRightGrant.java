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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
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

public class AccessRightGrant implements Initializable {
    private static final Logger LOG = LogManager.getLogger(AccessRightGrant.class);

    private ApplicationSettings settingsData;
    private List<AccessRule> accessRule;
    private final TreeItem<RuleRow> root = new TreeItem<>();
    private final AccesRights ar = new AccesRights();

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
            LOG.debug("Rule: {}", rule);
        }
    }

    @FXML
    void exportToExcel() {
        final String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s"))
                + "_accessRulesGranted.xlsx";
        LOG.debug("speichere alle ab [{}].", fileName);
        WriteXLS.writeAccessRights(fileName, accessRule);
        statusMessage.setText("Alles gespeichert!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("starte controller für settings");

        settingsData = ApplicationSettings.getInstance();
        accessRule = ar.getAccessRightsGivenFromOEKB();
        accessRightTable.setEditable(true);

        // Display user-friendly message if no data was retrieved
        if (accessRule == null || accessRule.isEmpty()) {
            LOG.info(
                    "No granted access rights found. This may be due to: invalid credentials, network issues, proxy blocking, or no rights granted.");
            if (statusMessage != null) {
                statusMessage.setText(
                        "No data available. Check: 1) Credentials in Settings, 2) Network/Proxy settings, 3) Server connection");
                statusMessage.getStyleClass().removeAll("status-message-error", "status-message-success");
                statusMessage.getStyleClass().add("status-message-error");
            }
        }

        for (AccessRule rule : accessRule) {
            String ddsGivenShort = String.join(";", rule.getDataSuppliersGivenShort());

            List<TreeItem<RuleRow>> rootTable = new ArrayList<>();

            TreeItem<RuleRow> ruleId = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                    rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                    ddsGivenShort, rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(),
                    rule.getDateTo(), rule.getFrequency(), rule.getCostsByDataSupplier(),
                    String.valueOf(rule.getLei().stream().filter(s -> s != null && !s.isEmpty()).count()),
                    String.valueOf(rule.getOenbId().stream().filter(s -> s != null && !s.isEmpty()).count()),
                    String.valueOf(rule.getIsinShareclass().size()), String.valueOf(rule.getIsinSegment().size()),
                    true));

            for (String lei : rule.getLei()) {
                LOG.trace("neuer LEI {}", lei);
                TreeItem<RuleRow> l = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), lei, null, null, null, false));
                rootTable.add(l);
            }

            for (String oenbId : rule.getOenbId()) {
                LOG.trace("NEUE OENB GEFUNDEN: {}", oenbId);
                TreeItem<RuleRow> oenbTemp = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                        rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                        ddsGivenShort, rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(),
                        rule.getDateTo(), rule.getFrequency(), rule.getCostsByDataSupplier(), null, oenbId, null, null,
                        false));
                rootTable.add(oenbTemp);
            }

            for (String isin : rule.getIsinShareclass()) {
                LOG.trace("NEUE ISIN GEFUNDEN {}", isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                        rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                        ddsGivenShort, rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(),
                        rule.getDateTo(), rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, isin, null,
                        false));
                rootTable.add(isinTemp);
            }

            for (String isin : rule.getIsinSegment()) {
                LOG.trace("NEUE ISIN GEFUNDEN {}", isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                        rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                        ddsGivenShort, rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(),
                        rule.getDateTo(), rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, null, isin,
                        false));
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
        lei.setCellValueFactory(new TreeItemPropertyValueFactory<>("lei"));
        lei.setPrefWidth(130);
        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<>("OENB ID");
        oenbId.setCellValueFactory(new TreeItemPropertyValueFactory<>("oenbId"));
        oenbId.setPrefWidth(80);
        TreeTableColumn<RuleRow, String> isin = new TreeTableColumn<>("ISIN SC");
        isin.setCellValueFactory(new TreeItemPropertyValueFactory<>("shareclassIsin"));
        isin.setPrefWidth(90);
        TreeTableColumn<RuleRow, String> isinSeg = new TreeTableColumn<>("ISIN Seg");
        isinSeg.setCellValueFactory(new TreeItemPropertyValueFactory<>("segmentIsin"));
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
        accessRightTable.getColumns().addAll(ruleIdCol, removeButton, profile, contentType, dds, ids, fundName,
                dateFrom, dateTo, frequency, delay);

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
        if (searchText == null || searchText.isBlank()) {
            // Show all items
            accessRightTable.setRoot(root);
            accessRightTable.setShowRoot(false);
            searchResultLabel.setText("");
            return;
        }

        String lowerCaseFilter = searchText.toLowerCase(java.util.Locale.ROOT).trim();
        TreeItem<RuleRow> filteredRoot = new TreeItem<>();
        int matchCount = 0;

        // Search through all rules
        for (TreeItem<RuleRow> ruleItem : root.getChildren()) {
            RuleRow rule = ruleItem.getValue();
            // Check if rule matches search
            boolean ruleMatches = matches(rule, lowerCaseFilter);

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

        searchResultLabel.getStyleClass().removeAll("status-message-error", "status-message-success");
        if (matchCount == 0) {
            searchResultLabel.setText("No matches found");
            searchResultLabel.getStyleClass().add("status-message-error");
        } else {
            searchResultLabel.setText(matchCount + " rule(s) found");
            searchResultLabel.getStyleClass().add("status-message-success");
        }
    }

    /**
     * Check if a RuleRow matches the search text
     */
    private boolean matches(RuleRow row, String searchText) {
        if (row == null) {
            return false;
        }

        return contains(row.getId(), searchText) || contains(row.getContentType(), searchText)
                || contains(row.getProfile(), searchText) || contains(row.getDataSupplierCreatorShort(), searchText)
                || contains(row.getDataSuppliersGivenShort(), searchText) || contains(row.getLei(), searchText)
                || contains(row.getOenbId(), searchText) || contains(row.getShareclassIsin(), searchText)
                || contains(row.getSegmentIsin(), searchText) || contains(row.getDateFrom(), searchText)
                || contains(row.getDateTo(), searchText) || contains(row.getFrequency(), searchText);
    }

    /**
     * Helper method to check if string contains search text
     */
    private boolean contains(String value, String searchText) {
        return value != null && value.toLowerCase(java.util.Locale.ROOT).contains(searchText);
    }

    /**
     * Open edit dialog for an access rule
     *
     * @param ruleRow
     *            The rule row to edit
     * @return true if changes were saved, false if cancelled
     */
    public boolean openEditDialog(RuleRow ruleRow) {
        try {
            // Find the AccessRule object from the rule ID
            AccessRule ruleToEdit = accessRule.stream().filter(ar -> ar.getId().equals(ruleRow.getId())).findFirst()
                    .orElse(null);

            if (ruleToEdit == null) {
                LOG.error("Could not find AccessRule with ID: {}", ruleRow.getId());
                showError("Error", "Could not find access rule to edit");
                return false;
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/pages/dialogAccessRuleEdit.fxml"));
            javafx.scene.Parent page = loader.load();

            // Create dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Access Rule: " + ruleToEdit.getId());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(accessRightTable.getScene().getWindow());

            // Set icon
            try {
                Image icon = new Image(getClass().getResourceAsStream("/img/connectdevelop.png"));
                dialogStage.getIcons().add(icon);
            } catch (RuntimeException e) {
                LOG.warn("Could not load dialog icon", e);
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
            LOG.error("Error loading edit dialog", e);
            showError("Error", "Could not load edit dialog: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reload access rights data and refresh table
     */
    public void refreshData() {
        LOG.info("Refreshing access rights data");

        // Clear existing data
        root.getChildren().clear();

        // Reload from server/file
        accessRule = ar.getAccessRightsGivenFromOEKB();

        if (accessRule == null || accessRule.isEmpty()) {
            statusMessage.setText("No data available after refresh");
            statusMessage.getStyleClass().removeAll("status-message-error", "status-message-success");
            statusMessage.getStyleClass().add("status-message-error");
            return;
        }

        // Rebuild tree
        for (AccessRule rule : accessRule) {
            String ddsGivenShort = String.join(";", rule.getDataSuppliersGivenShort());
            List<TreeItem<RuleRow>> rootTable = new ArrayList<>();

            TreeItem<RuleRow> ruleId = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                    rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                    ddsGivenShort, rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(),
                    rule.getDateTo(), rule.getFrequency(), rule.getCostsByDataSupplier(),
                    String.valueOf(rule.getLei().stream().filter(s -> s != null && !s.isEmpty()).count()),
                    String.valueOf(rule.getOenbId().stream().filter(s -> s != null && !s.isEmpty()).count()),
                    String.valueOf(rule.getIsinShareclass().size()), String.valueOf(rule.getIsinSegment().size()),
                    true));

            // Add children (LEI, OENB_ID, ISINs)
            for (String lei : rule.getLei()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), lei, null, null, null, false)));
            }

            for (String oenbId : rule.getOenbId()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, oenbId, null, null, false)));
            }

            for (String isin : rule.getIsinShareclass()) {
                rootTable.add(new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), ddsGivenShort,
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, isin, null, false)));
            }

            for (String isin : rule.getIsinSegment()) {
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
        statusMessage.getStyleClass().removeAll("status-message-error", "status-message-success");
        statusMessage.getStyleClass().add("status-message-success");
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
     *
     * @param ruleRow
     *            The rule to delete
     * @return true if deleted successfully
     */
    public boolean deleteRule(RuleRow ruleRow) {
        // Find the AccessRule object
        AccessRule ruleToDelete = accessRule.stream().filter(ar -> ar.getId().equals(ruleRow.getId())).findFirst()
                .orElse(null);

        if (ruleToDelete == null) {
            LOG.error("Could not find AccessRule with ID: {}", ruleRow.getId());
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
            confirmAlert.setContentText("OFFLINE MODE - No server delete will be performed.\n\n" + "This will:\n"
                    + "1. Log the DELETE XML\n" + "2. Remove from local display\n\n" + "Rule ID: "
                    + ruleToDelete.getId() + "\n\n" + "Do you really want to delete this rule?");
        } else {
            confirmAlert.setHeaderText("Delete Access Rule");
            confirmAlert.setContentText("This will permanently delete the access rule from server.\n\n" + "Rule ID: "
                    + ruleToDelete.getId() + "\n" + "Content Type: " + ruleToDelete.getContentType() + "\n"
                    + "Profile: " + ruleToDelete.getProfile() + "\n\n" + "Do you really want to delete this rule?");
        }

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return false;
        }

        try {
            // Generate DELETE XML
            String deleteXml = AccesRights.deleteRule(ruleToDelete);
            if (deleteXml == null || deleteXml.isEmpty()) {
                throw new IllegalStateException("Failed to generate DELETE XML");
            }

            if (isFileSystemMode) {
                // OFFLINE MODE: Just log
                LOG.info("=== OFFLINE MODE: DELETE ENTIRE RULE ===");
                LOG.info("Rule ID: {}", ruleToDelete.getId());
                LOG.info(deleteXml);
                LOG.info("=== END DELETE XML ===");

                showSuccess("Success (Offline Mode)", "Access rule marked for deletion.\n\n" + "DELETE XML logged.\n"
                        + "No server delete performed (FileSystem Mode).");
            } else {
                // Upload DELETE XML
                LOG.info("Uploading DELETE XML for rule: {}", ruleToDelete.getId());
                String result = AccesRights.uploadAccessRuleXml(deleteXml);

                if (result.startsWith("ERROR")) {
                    throw new IllegalStateException("Delete failed: " + result);
                }

                showSuccess("Success", "Access rule deleted successfully from server!");
            }

            return true;

        } catch (RuntimeException e) {
            LOG.error("Error deleting rule", e);
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
        LOG.info("Creating new access rule");

        try {
            // Create empty AccessRule
            AccessRule newRule = new AccessRule();
            newRule.setId("NEW_RULE_" + System.currentTimeMillis());
            newRule.setContentType("FUND");
            newRule.setProfile("all");
            newRule.setDataSuppliersGivenShort(new ArrayList<>());
            newRule.setLei(new ArrayList<>());
            newRule.setOenbId(new ArrayList<>());
            newRule.setIsinSegment(new ArrayList<>());
            newRule.setIsinShareclass(new ArrayList<>());
            newRule.setFrequency("daily");
            newRule.setAccessDelayInDays("0");
            newRule.setDateFrom(LocalDate.now().toString());
            newRule.setCostsByDataSupplier("false");
            newRule.setDataSupplierCreatorShort(settingsData.getDataSupplierList());

            // Load FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/pages/dialogAccessRuleEdit.fxml"));
            javafx.scene.Parent page = loader.load();

            // Create dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Access Rule");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(accessRightTable.getScene().getWindow());

            // Set icon
            try {
                Image icon = new Image(getClass().getResourceAsStream("/img/connectdevelop.png"));
                dialogStage.getIcons().add(icon);
            } catch (RuntimeException e) {
                LOG.warn("Could not load dialog icon", e);
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
                LOG.info("New rule created, refreshing data");
                refreshData();
            }

        } catch (IOException e) {
            LOG.error("Error loading create new rule dialog", e);
            showError("Error", "Could not load dialog: " + e.getMessage());
        }
    }
}

class ButtonCell extends TreeTableCell<RuleRow, Boolean> {
    private static final Logger LOG_BUTTON = LogManager.getLogger(ButtonCell.class);
    private final HBox buttonBox = new HBox(5); // Container for buttons with 5px spacing
    private final Button editButton = new Button("Edit");
    private final Button deleteButton = new Button("Delete");
    private final Button removeButton = new Button("Remove");

    ButtonCell() {
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
            TreeTableRow<RuleRow> rule = this.getTreeTableRow();
            LOG_BUTTON.debug("Edit button clicked for root row: {}", rule.getItem());

            AccessRightGrant controller = findParentController();
            if (controller != null) {
                boolean saved = controller.openEditDialog(rule.getItem());
                if (saved) {
                    LOG_BUTTON.info("Rule was modified, refreshing data");
                    controller.refreshData();
                }
            } else {
                LOG_BUTTON.error("Could not find parent controller");
            }
        });

        // Delete button action - for root rows (entire rules)
        deleteButton.setOnAction(e -> {
            TreeTableRow<RuleRow> rule = this.getTreeTableRow();
            LOG_BUTTON.debug("Delete button clicked for root row: {}", rule.getItem());

            AccessRightGrant controller = findParentController();
            if (controller != null) {
                boolean deleted = controller.deleteRule(rule.getItem());
                if (deleted) {
                    LOG_BUTTON.info("Rule was deleted, refreshing data");
                    controller.refreshData();
                }
            } else {
                LOG_BUTTON.error("Could not find parent controller");
            }
        });

        // Remove button action - for child rows (fund entries)
        removeButton.setOnAction(e -> {
            TreeTableRow<RuleRow> rule = this.getTreeTableRow();
            LOG_BUTTON.debug("lösche ISIN aus rule: {}/{}/{}/{}", rule.getItem().getLei(), rule.getItem().getOenbId(),
                    rule.getItem().getShareclassIsin(), rule.getItem().getSegmentIsin());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete fund from rule");
            alert.setHeaderText("Do you really want to delete the Fund?");

            FontIcon trashIcon = new FontIcon("bi-trash");
            trashIcon.setIconSize(32);
            trashIcon.setIconColor(javafx.scene.paint.Color.web("#d32f2f"));
            alert.setGraphic(trashIcon);

            StringBuilder textBuilder = new StringBuilder(256);
            textBuilder.append("delete ");
            if (rule.getItem().getLei() != null) {
                textBuilder.append("LEI [").append(rule.getItem().getLei()).append(']');
            }
            if (rule.getItem().getOenbId() != null) {
                textBuilder.append("OENB ID [").append(rule.getItem().getOenbId()).append(']');
            }
            if (rule.getItem().getShareclassIsin() != null) {
                textBuilder.append("Shareclass ISIN [").append(rule.getItem().getShareclassIsin()).append(']');
            }
            if (rule.getItem().getSegmentIsin() != null) {
                textBuilder.append("Segment ISIN [").append(rule.getItem().getSegmentIsin()).append(']');
            }
            textBuilder.append(" from rule [").append(rule.getItem().getId()).append(']');
            String text = textBuilder.toString();

            alert.setContentText(text);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                LOG_BUTTON.debug("ALLES OK");

                AccesRights ar = new AccesRights();
                ar.deleteFundFromRule(rule.getItem());

            } else {
                LOG_BUTTON.debug("WOLLTE DOCH NICHT");
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
        LOG_BUTTON.debug("current: {}", current);

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
            // The controller is stored in the TreeTableView's properties during
            // initialization
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
        } catch (RuntimeException e) {
            LOG_BUTTON.error("Error finding parent controller", e);
        }
        return null;
    }
}
