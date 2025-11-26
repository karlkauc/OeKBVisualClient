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

import dao.AccesRights;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.AccessRule;
import model.ApplicationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Access Rule Edit Dialog
 * Provides comprehensive editing of access rules with tabs for different sections
 */
public class AccessRuleEditDialog implements Initializable {
    private static final Logger log = LogManager.getLogger(AccessRuleEditDialog.class);

    // ========== Tab 1: Basis-Daten ==========
    @FXML
    private TextField ruleIdField;

    @FXML
    private ComboBox<String> contentTypeCombo;

    @FXML
    private ListView<String> dataSuppliersListView;

    @FXML
    private TextField newDataSupplierField;

    @FXML
    private ListView<String> profilesListView;

    @FXML
    private ComboBox<String> newProfileCombo;

    @FXML
    private TextField usageField;

    @FXML
    private CheckBox costsByDataSupplierCheckBox;

    // ========== Tab 2: Access Objects ==========
    @FXML
    private TableView<String> leiTableView;

    @FXML
    private TableColumn<String, String> leiColumn;

    @FXML
    private TextField newLeiField;

    @FXML
    private TableView<String> oenbIdTableView;

    @FXML
    private TableColumn<String, String> oenbIdColumn;

    @FXML
    private TextField newOenbIdField;

    @FXML
    private TableView<String> segmentIsinTableView;

    @FXML
    private TableColumn<String, String> segmentIsinColumn;

    @FXML
    private TextField newSegmentIsinField;

    @FXML
    private TableView<String> shareclassIsinTableView;

    @FXML
    private TableColumn<String, String> shareclassIsinColumn;

    @FXML
    private TextField newShareclassIsinField;

    // ========== Tab 3: Schedule ==========
    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private ComboBox<String> frequencyCombo;

    @FXML
    private Spinner<Integer> accessDelaySpinner;

    // ========== Tab 4: Advanced ==========
    @FXML
    private ListView<String> documentTypesListView;

    @FXML
    private ComboBox<String> newDocumentTypeCombo;

    @FXML
    private ListView<String> regulatoryReportingsListView;

    @FXML
    private ComboBox<String> newRegulatoryReportingCombo;

    // ========== Dialog Buttons ==========
    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    // ========== Internal State ==========
    private AccessRule accessRule;
    private AccessRule originalRule;  // Store original to detect if it's a new rule
    private boolean saveClicked = false;
    private Stage dialogStage;

    // Observable lists for UI bindings
    private ObservableList<String> dataSuppliers = FXCollections.observableArrayList();
    private ObservableList<String> profiles = FXCollections.observableArrayList();
    private ObservableList<String> leiList = FXCollections.observableArrayList();
    private ObservableList<String> oenbIdList = FXCollections.observableArrayList();
    private ObservableList<String> segmentIsinList = FXCollections.observableArrayList();
    private ObservableList<String> shareclassIsinList = FXCollections.observableArrayList();
    private ObservableList<String> documentTypes = FXCollections.observableArrayList();
    private ObservableList<String> regulatoryReportings = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initialize AccessRuleEditDialog");

        // Setup ContentType ComboBox
        contentTypeCombo.setItems(FXCollections.observableArrayList("FUND", "DOC", "REG"));

        // Setup Profile ComboBox
        newProfileCombo.setItems(FXCollections.observableArrayList(
                "all", "PKG", "Vendor", "allOhneSegmente",
                "VendorOhneShareClassPositions", "VendorMitShareClass"
        ));

        // Setup Frequency ComboBox
        frequencyCombo.setItems(FXCollections.observableArrayList("daily", "monthly"));

        // Setup Access Delay Spinner (0-365 days)
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 365, 0, 1);
        accessDelaySpinner.setValueFactory(valueFactory);
        accessDelaySpinner.setEditable(true);

        // Setup Document Types ComboBox
        newDocumentTypeCombo.setItems(FXCollections.observableArrayList(
                "AIFMD", "AnnualReport", "AuditReport", "Factsheet",
                "KID", "Prospectus", "PRIIPS-KID"
        ));

        // Setup Regulatory Reporting ComboBox
        newRegulatoryReportingCombo.setItems(FXCollections.observableArrayList(
                "EMIR", "KIID", "EMT", "TPTSolvencyII", "PRIIPS"
        ));

        // Bind ListViews
        dataSuppliersListView.setItems(dataSuppliers);
        profilesListView.setItems(profiles);
        documentTypesListView.setItems(documentTypes);
        regulatoryReportingsListView.setItems(regulatoryReportings);

        // Setup TableViews
        leiColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        leiTableView.setItems(leiList);

        oenbIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        oenbIdTableView.setItems(oenbIdList);

        segmentIsinColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        segmentIsinTableView.setItems(segmentIsinList);

        shareclassIsinColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        shareclassIsinTableView.setItems(shareclassIsinList);
    }

    /**
     * Set the access rule to edit
     */
    public void setAccessRule(AccessRule rule) {
        this.accessRule = rule;
        this.originalRule = rule;  // Store original for comparison
        loadAccessRuleData();
    }

    /**
     * Set the dialog stage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Check if save was clicked
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }

    /**
     * Get the edited access rule
     */
    public AccessRule getAccessRule() {
        return accessRule;
    }

    /**
     * Load data from access rule into UI
     */
    private void loadAccessRuleData() {
        if (accessRule == null) {
            log.warn("Access rule is null");
            return;
        }

        // Tab 1: Basis-Daten
        ruleIdField.setText(accessRule.getId());
        contentTypeCombo.setValue(accessRule.getContentType());

        if (accessRule.getDataSuppliersGivenShort() != null) {
            dataSuppliers.setAll(accessRule.getDataSuppliersGivenShort());
        }

        // Load all profiles from the access rule
        if (accessRule.getProfiles() != null && !accessRule.getProfiles().isEmpty()) {
            profiles.setAll(accessRule.getProfiles());
        }

        usageField.setText(""); // Not in current model

        // Set checkbox for CostsByDataSupplier (boolean value)
        if (accessRule.getCostsByDataSupplier() != null) {
            costsByDataSupplierCheckBox.setSelected(
                "true".equalsIgnoreCase(accessRule.getCostsByDataSupplier()) ||
                "1".equals(accessRule.getCostsByDataSupplier())
            );
        } else {
            costsByDataSupplierCheckBox.setSelected(false); // Default: false per XSD
        }

        // Tab 2: Access Objects
        if (accessRule.getLEI() != null) {
            leiList.setAll(accessRule.getLEI());
        }
        if (accessRule.getOENB_ID() != null) {
            oenbIdList.setAll(accessRule.getOENB_ID());
        }
        if (accessRule.getISIN_SEGMENT() != null) {
            segmentIsinList.setAll(accessRule.getISIN_SEGMENT());
        }
        if (accessRule.getISIN_SHARECLASS() != null) {
            shareclassIsinList.setAll(accessRule.getISIN_SHARECLASS());
        }

        // Tab 3: Schedule
        if (accessRule.getDateFrom() != null && !accessRule.getDateFrom().isEmpty()) {
            try {
                dateFromPicker.setValue(LocalDate.parse(accessRule.getDateFrom()));
            } catch (Exception e) {
                log.warn("Could not parse dateFrom: {}", accessRule.getDateFrom());
            }
        }
        if (accessRule.getDateTo() != null && !accessRule.getDateTo().isEmpty()) {
            try {
                dateToPicker.setValue(LocalDate.parse(accessRule.getDateTo()));
            } catch (Exception e) {
                log.warn("Could not parse dateTo: {}", accessRule.getDateTo());
            }
        }
        frequencyCombo.setValue(accessRule.getFrequency());

        // Set access delay spinner value
        if (accessRule.getAccessDelayInDays() != null && !accessRule.getAccessDelayInDays().isEmpty()) {
            try {
                int delayDays = Integer.parseInt(accessRule.getAccessDelayInDays());
                accessDelaySpinner.getValueFactory().setValue(delayDays);
            } catch (NumberFormatException e) {
                log.warn("Could not parse accessDelayInDays: {}", accessRule.getAccessDelayInDays());
                accessDelaySpinner.getValueFactory().setValue(0);
            }
        } else {
            accessDelaySpinner.getValueFactory().setValue(0);
        }

        // Tab 4: Advanced
        if (accessRule.getDocumentTypes() != null && !accessRule.getDocumentTypes().isEmpty()) {
            documentTypes.setAll(accessRule.getDocumentTypes());
        }
        if (accessRule.getRegulatoryReportings() != null && !accessRule.getRegulatoryReportings().isEmpty()) {
            regulatoryReportings.setAll(accessRule.getRegulatoryReportings());
        }
    }

    /**
     * Save data from UI back to access rule
     */
    private void saveAccessRuleData() {
        // Tab 1: Basis-Daten
        accessRule.setId(ruleIdField.getText());
        accessRule.setContentType(contentTypeCombo.getValue());
        accessRule.setDataSuppliersGivenShort(new ArrayList<>(dataSuppliers));

        // Save all profiles to the access rule
        accessRule.setProfiles(new ArrayList<>(profiles));

        // Save checkbox value as boolean string
        accessRule.setCostsByDataSupplier(String.valueOf(costsByDataSupplierCheckBox.isSelected()));

        // Tab 2: Access Objects
        accessRule.setLEI(new ArrayList<>(leiList));
        accessRule.setOENB_ID(new ArrayList<>(oenbIdList));
        accessRule.setISIN_SEGMENT(new ArrayList<>(segmentIsinList));
        accessRule.setISIN_SHARECLASS(new ArrayList<>(shareclassIsinList));

        // Tab 3: Schedule
        if (dateFromPicker.getValue() != null) {
            accessRule.setDateFrom(dateFromPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (dateToPicker.getValue() != null) {
            accessRule.setDateTo(dateToPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        accessRule.setFrequency(frequencyCombo.getValue());
        accessRule.setAccessDelayInDays(String.valueOf(accessDelaySpinner.getValue()));

        // Tab 4: Advanced
        accessRule.setDocumentTypes(new ArrayList<>(documentTypes));
        accessRule.setRegulatoryReportings(new ArrayList<>(regulatoryReportings));
    }

    // ========== Event Handlers - Tab 1: Basis-Daten ==========

    @FXML
    private void handleAddDataSupplier() {
        String ds = newDataSupplierField.getText().trim();
        if (!ds.isEmpty() && !dataSuppliers.contains(ds)) {
            dataSuppliers.add(ds);
            newDataSupplierField.clear();
        }
    }

    @FXML
    private void handleRemoveDataSupplier() {
        String selected = dataSuppliersListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dataSuppliers.remove(selected);
        }
    }

    @FXML
    private void handleAddProfile() {
        String profile = newProfileCombo.getValue();
        if (profile != null && !profiles.contains(profile)) {
            profiles.add(profile);
        }
    }

    @FXML
    private void handleRemoveProfile() {
        String selected = profilesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            profiles.remove(selected);
        }
    }

    // ========== Event Handlers - Tab 2: Access Objects ==========

    @FXML
    private void handleAddLei() {
        String lei = newLeiField.getText().trim();
        if (!lei.isEmpty() && !leiList.contains(lei)) {
            leiList.add(lei);
            newLeiField.clear();
        }
    }

    @FXML
    private void handleRemoveLei() {
        String selected = leiTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            leiList.remove(selected);
        }
    }

    @FXML
    private void handleAddOenbId() {
        String id = newOenbIdField.getText().trim();
        if (!id.isEmpty() && !oenbIdList.contains(id)) {
            oenbIdList.add(id);
            newOenbIdField.clear();
        }
    }

    @FXML
    private void handleRemoveOenbId() {
        String selected = oenbIdTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            oenbIdList.remove(selected);
        }
    }

    @FXML
    private void handleAddSegmentIsin() {
        String isin = newSegmentIsinField.getText().trim();
        if (!isin.isEmpty() && !segmentIsinList.contains(isin)) {
            segmentIsinList.add(isin);
            newSegmentIsinField.clear();
        }
    }

    @FXML
    private void handleRemoveSegmentIsin() {
        String selected = segmentIsinTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            segmentIsinList.remove(selected);
        }
    }

    @FXML
    private void handleAddShareclassIsin() {
        String isin = newShareclassIsinField.getText().trim();
        if (!isin.isEmpty() && !shareclassIsinList.contains(isin)) {
            shareclassIsinList.add(isin);
            newShareclassIsinField.clear();
        }
    }

    @FXML
    private void handleRemoveShareclassIsin() {
        String selected = shareclassIsinTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            shareclassIsinList.remove(selected);
        }
    }

    // ========== Event Handlers - Tab 4: Advanced ==========

    @FXML
    private void handleAddDocumentType() {
        String docType = newDocumentTypeCombo.getValue();
        if (docType != null && !documentTypes.contains(docType)) {
            documentTypes.add(docType);
        }
    }

    @FXML
    private void handleRemoveDocumentType() {
        String selected = documentTypesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            documentTypes.remove(selected);
        }
    }

    @FXML
    private void handleAddRegulatoryReporting() {
        String regRep = newRegulatoryReportingCombo.getValue();
        if (regRep != null && !regulatoryReportings.contains(regRep)) {
            regulatoryReportings.add(regRep);
        }
    }

    @FXML
    private void handleRemoveRegulatoryReporting() {
        String selected = regulatoryReportingsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            regulatoryReportings.remove(selected);
        }
    }

    // ========== Dialog Buttons ==========

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            // Save UI data to model
            saveAccessRuleData();

            // Check if in FileSystem mode
            ApplicationSettings settings = ApplicationSettings.getInstance();
            boolean isFileSystemMode = settings.isFileSystem();

            // Check if this is a new rule (Rule ID starts with "NEW_RULE_")
            boolean isNewRule = accessRule.getId().startsWith("NEW_RULE_");

            // Show confirmation
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.initOwner(dialogStage);
            confirmAlert.setTitle("Confirm Save");

            if (isNewRule) {
                if (isFileSystemMode) {
                    confirmAlert.setHeaderText("Create New Access Rule (OFFLINE MODE)");
                    confirmAlert.setContentText(
                            "OFFLINE MODE - No server upload will be performed.\n\n" +
                            "This will:\n" +
                            "1. Log the IMPORT XML\n" +
                            "2. Save rule to local backup\n\n" +
                            "Rule ID: " + accessRule.getId() + "\n\n" +
                            "Do you want to continue?");
                } else {
                    confirmAlert.setHeaderText("Create New Access Rule");
                    confirmAlert.setContentText(
                            "This will upload a new access rule to server.\n\n" +
                            "Rule ID: " + accessRule.getId() + "\n\n" +
                            "Do you want to continue?");
                }
            } else {
                if (isFileSystemMode) {
                    confirmAlert.setHeaderText("Save Modified Access Rule (OFFLINE MODE)");
                    confirmAlert.setContentText(
                            "OFFLINE MODE - No server upload will be performed.\n\n" +
                            "This will:\n" +
                            "1. Log the DELETE XML\n" +
                            "2. Log the IMPORT XML\n" +
                            "3. Save changes to local backup\n\n" +
                            "Rule ID: " + accessRule.getId() + "\n\n" +
                            "Do you want to continue?");
                } else {
                    confirmAlert.setHeaderText("Upload Modified Access Rule");
                    confirmAlert.setContentText(
                            "This will:\n" +
                            "1. Delete the old rule from server\n" +
                            "2. Upload the modified rule as new rule\n\n" +
                            "Rule ID: " + accessRule.getId() + "\n\n" +
                            "Do you want to continue?");
                }
            }

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            // Disable buttons during upload
            saveButton.setDisable(true);
            cancelButton.setDisable(true);

            // Run upload in background thread
            new Thread(() -> {
                try {
                    log.info("Starting save workflow for rule: {} (New: {}, FileSystem Mode: {})",
                            accessRule.getId(), isNewRule, isFileSystemMode);

                    // Step 1: Only DELETE if it's not a new rule
                    if (!isNewRule) {
                        String deleteXml = AccesRights.deleteRule(accessRule);
                        if (deleteXml == null || deleteXml.isEmpty()) {
                            throw new Exception("Failed to generate DELETE XML");
                        }

                        if (isFileSystemMode) {
                            // OFFLINE MODE: Just log the XML
                            log.info("=== OFFLINE MODE: DELETE XML ===");
                            log.info(deleteXml);
                            log.info("=== END DELETE XML ===");
                        } else {
                            // Upload DELETE XML
                            log.info("Uploading DELETE XML to server");
                            String deleteResult = AccesRights.uploadAccessRuleXml(deleteXml);
                            if (deleteResult.startsWith("ERROR")) {
                                throw new Exception("Delete failed: " + deleteResult);
                            }
                        }
                    }

                    // Step 2: Generate IMPORT XML
                    String importXml = AccesRights.deleteRule(accessRule); // Generate XML structure
                    AccesRights ar = new AccesRights();
                    ar.newRule(importXml); // Convert Task from "delete" to "import"

                    if (isFileSystemMode) {
                        // OFFLINE MODE: Just log the XML
                        log.info("=== OFFLINE MODE: IMPORT XML ===");
                        log.info(importXml);
                        log.info("=== END IMPORT XML ===");
                    } else {
                        // Upload IMPORT XML
                        log.info("Uploading IMPORT XML to server");
                        String importResult = AccesRights.uploadAccessRuleXml(importXml);
                        if (importResult.startsWith("ERROR")) {
                            throw new Exception("Import failed: " + importResult);
                        }
                    }

                    // Success!
                    Platform.runLater(() -> {
                        saveClicked = true;
                        if (isNewRule) {
                            if (isFileSystemMode) {
                                showSuccess("Success (Offline Mode)",
                                           "New access rule created!\n\n" +
                                           "IMPORT XML logged.\n" +
                                           "No upload to server performed (FileSystem Mode).");
                            } else {
                                showSuccess("Success",
                                           "New access rule created successfully!\n\n" +
                                           "Rule uploaded to server.");
                            }
                        } else {
                            if (isFileSystemMode) {
                                showSuccess("Success (Offline Mode)",
                                           "Access rule modifications saved!\n\n" +
                                           "DELETE and IMPORT XML logged.\n" +
                                           "No upload to server performed (FileSystem Mode).");
                            } else {
                                showSuccess("Success",
                                           "Access rule updated successfully!\n\n" +
                                           "Old rule deleted and new rule uploaded to server.");
                            }
                        }
                        dialogStage.close();
                    });

                } catch (Exception e) {
                    log.error("Error during save workflow", e);
                    Platform.runLater(() -> {
                        showUploadError("Save Failed",
                                      "Could not save access rule:\n\n" + e.getMessage());
                        saveButton.setDisable(false);
                        cancelButton.setDisable(false);
                    });
                }
            }).start();
        }
    }

    /**
     * Show success message
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show upload error
     */
    private void showUploadError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText("Upload Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validate user input based on XSD mandatory fields
     * XSD: FundsXML_AccessRules_2.3.0.xsd
     *
     * Mandatory fields per XSD schema:
     * - id attribute (use="required")
     * - ContentType element
     * - DataSuppliers/DataSupplier (at least one)
     * - Profiles/Profile (at least one)
     * - AccessObjects/AccessObject (at least one)
     */
    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        // 1. Rule ID - MANDATORY (XSD line 310: attribute id use="required")
        if (ruleIdField.getText() == null || ruleIdField.getText().trim().isEmpty()) {
            errorMessage.append("• Rule ID is required (XSD: mandatory attribute)\n");
        } else {
            // Validate ID format (minLength=1)
            String ruleId = ruleIdField.getText().trim();
            if (ruleId.length() < 1) {
                errorMessage.append("• Rule ID must have at least 1 character\n");
            }
        }

        // 2. ContentType - MANDATORY (XSD line 35-46)
        if (contentTypeCombo.getValue() == null || contentTypeCombo.getValue().isEmpty()) {
            errorMessage.append("• Content Type is required (must be FUND, DOC, or REG)\n");
        }

        // 3. DataSuppliers - MANDATORY (XSD line 47-60: at least one DataSupplier required)
        if (dataSuppliers.isEmpty()) {
            errorMessage.append("• At least one Data Supplier (recipient) is required\n");
        }

        // 4. Profiles - MANDATORY (XSD line 66-90: at least one Profile required)
        if (profiles.isEmpty()) {
            errorMessage.append("• At least one Profile is required (e.g., 'all', 'PKG', 'Vendor')\n");
        }

        // 5. AccessObjects - MANDATORY (XSD line 91-189: at least one AccessObject required)
        // Each AccessObject must have either Fund (LEI or OeNB_ID), Segment (ISIN), or ShareClass (ISIN)
        boolean hasAccessObject = !leiList.isEmpty() || !oenbIdList.isEmpty() ||
                                  !segmentIsinList.isEmpty() || !shareclassIsinList.isEmpty();

        if (!hasAccessObject) {
            errorMessage.append("• At least one Access Object is required:\n");
            errorMessage.append("  - LEI (Fund level access), or\n");
            errorMessage.append("  - OeNB-ID (Fund level access), or\n");
            errorMessage.append("  - Segment ISIN, or\n");
            errorMessage.append("  - ShareClass ISIN\n");
        }

        // 6. Optional but validated: LEI format validation (if provided)
        // XSD line 446-459: LEI must be exactly 20 characters
        for (String lei : leiList) {
            if (lei.length() != 20) {
                errorMessage.append("• Invalid LEI format: '").append(lei)
                            .append("' (must be exactly 20 characters)\n");
            }
            if (!lei.matches("[0-9a-zA-Z]{18}[0-9]{2}")) {
                errorMessage.append("• Invalid LEI format: '").append(lei)
                            .append("' (must be 18 alphanumeric + 2 digits)\n");
            }
        }

        // 7. Optional but validated: OeNB-ID format validation (if provided)
        // XSD line 470-479: OeNB_Identnr must be 2-8 digits
        for (String oenbId : oenbIdList) {
            if (oenbId.length() < 2 || oenbId.length() > 8) {
                errorMessage.append("• Invalid OeNB-ID format: '").append(oenbId)
                            .append("' (must be 2-8 digits)\n");
            }
            if (!oenbId.matches("[0-9]{2,8}")) {
                errorMessage.append("• Invalid OeNB-ID format: '").append(oenbId)
                            .append("' (must contain only digits)\n");
            }
        }

        // 8. Optional but validated: ISIN format validation (if provided)
        // XSD line 460-469: ISIN must be exactly 12 characters (2 letters + 9 alphanumeric + 1 digit)
        List<String> allIsins = new ArrayList<>();
        allIsins.addAll(segmentIsinList);
        allIsins.addAll(shareclassIsinList);

        for (String isin : allIsins) {
            if (isin.length() != 12) {
                errorMessage.append("• Invalid ISIN format: '").append(isin)
                            .append("' (must be exactly 12 characters)\n");
            }
            if (!isin.matches("[A-Z]{2}[A-Z0-9]{9}[0-9]{1}")) {
                errorMessage.append("• Invalid ISIN format: '").append(isin)
                            .append("' (format: 2 letters + 9 alphanumeric + 1 digit)\n");
            }
        }

        // 9. Access Delay validation
        // XSD line 264: AccessDelayInDays is xs:long
        // Note: Spinner ensures valid values (0-365), no additional validation needed
        // The spinner value is always valid

        // Show errors if any
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }
    }
}
