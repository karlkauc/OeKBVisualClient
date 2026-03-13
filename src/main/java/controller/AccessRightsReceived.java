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
import javafx.scene.control.TextField;
import dao.AccesRights;
import dao.WriteXLS;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.VBox;
import model.AccessRule;
import model.ApplicationSettings;
import model.RuleRow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AccessRightsReceived implements Initializable {
    private static final Logger LOG = LogManager.getLogger(AccessRightsReceived.class);

    private ApplicationSettings settingsData;
    private List<AccessRule> accessRule;
    private TreeItem<RuleRow> rootData;

    @FXML
    private Button exportToExcel;

    @FXML
    private VBox accessRightPane;

    @FXML
    private TreeTableView<RuleRow> accessRightTable;

    @FXML
    private Label statusMessage;

    @FXML
    private TextField searchField;

    @FXML
    private Label searchResultLabel;

    @FXML
    void exportToExcel() {
        final String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s"))
                + "_accessRulesReceived.xlsx";
        LOG.debug("speichere alles nach Excel [{}].", fileName);

        WriteXLS.writeAccessRights(fileName, accessRule);
        statusMessage.setText("Gespeichert");
    }

    @FXML
    void filterTable() {
        String searchText = searchField.getText().toLowerCase(java.util.Locale.ROOT).trim();

        if (searchText.isEmpty()) {
            // No search text, show all data
            accessRightTable.setRoot(rootData);
            searchResultLabel.setText("");
            return;
        }

        // Create filtered root
        TreeItem<RuleRow> filteredRoot = new TreeItem<>();
        int totalMatches = 0;

        for (TreeItem<RuleRow> ruleItem : rootData.getChildren()) {
            RuleRow rule = ruleItem.getValue();
            boolean ruleMatchesLocal = matchesSearchCriteria(rule, searchText);

            List<TreeItem<RuleRow>> matchingChildren = new ArrayList<>();

            // Check children (LEI, OENB_ID, ISIN entries)
            for (TreeItem<RuleRow> child : ruleItem.getChildren()) {
                if (matchesSearchCriteria(child.getValue(), searchText)) {
                    matchingChildren.add(child);
                    totalMatches++;
                }
            }

            // If rule itself matches or has matching children, include it
            if (ruleMatchesLocal || !matchingChildren.isEmpty()) {
                TreeItem<RuleRow> filteredRuleItem = new TreeItem<>(rule);
                filteredRuleItem.getChildren().addAll(matchingChildren);
                filteredRuleItem.setExpanded(true); // Expand to show matches
                filteredRoot.getChildren().add(filteredRuleItem);

                if (ruleMatchesLocal) {
                    totalMatches++;
                }
            }
        }

        accessRightTable.setRoot(filteredRoot);

        // Update result label
        if (totalMatches > 0) {
            searchResultLabel.setText(totalMatches + " Treffer gefunden");
            searchResultLabel.setStyle("-fx-text-fill: #2e7d32;");
        } else {
            searchResultLabel.setText("Keine Treffer");
            searchResultLabel.setStyle("-fx-text-fill: #d32f2f;");
        }
    }

    private boolean matchesSearchCriteria(RuleRow row, String searchText) {
        if (row == null) {
            return false;
        }

        return containsIgnoreCase(row.getId(), searchText) || containsIgnoreCase(row.getProfile(), searchText)
                || containsIgnoreCase(row.getContentType(), searchText)
                || containsIgnoreCase(row.getDataSupplierCreatorShort(), searchText)
                || containsIgnoreCase(row.getDataSupplierCreatorName(), searchText)
                || containsIgnoreCase(row.getCreationTime(), searchText)
                || containsIgnoreCase(row.getAccessDelayInDays(), searchText)
                || containsIgnoreCase(row.getDateFrom(), searchText) || containsIgnoreCase(row.getDateTo(), searchText)
                || containsIgnoreCase(row.getFrequency(), searchText)
                || containsBoolean(row.isCostsByDataSupplier(), searchText)
                || containsIgnoreCase(row.getLei(), searchText) || containsIgnoreCase(row.getOenbId(), searchText)
                || containsIgnoreCase(row.getShareclassIsin(), searchText)
                || containsIgnoreCase(row.getSegmentIsin(), searchText);
    }

    private boolean containsIgnoreCase(String value, String searchText) {
        return value != null && value.toLowerCase(java.util.Locale.ROOT).contains(searchText);
    }

    private boolean containsBoolean(boolean value, String searchText) {
        String boolStr = String.valueOf(value).toLowerCase(java.util.Locale.ROOT);
        return boolStr.contains(searchText);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("starte controller für settings");

        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        AccesRights ar = new AccesRights();
        accessRule = ar.getAccesRightsRecievedFromOEKB();

        rootData = new TreeItem<>();

        // Display user-friendly message if no data was retrieved
        if (accessRule == null || accessRule.isEmpty()) {
            LOG.info(
                    "No access rights received. This may be due to: invalid credentials, network issues, proxy blocking, or no rights assigned.");
            if (statusMessage != null) {
                statusMessage.setText(
                        "No data available. Check: 1) Credentials in Settings, 2) Network/Proxy settings, 3) Server connection");
                statusMessage.setStyle("-fx-text-fill: #c8102e; -fx-font-weight: bold;");
            }
        }

        for (AccessRule rule : accessRule) {
            List<TreeItem<RuleRow>> rootTable = new ArrayList<>();

            TreeItem<RuleRow> ruleId = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                    rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                    rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                    rule.getFrequency(), rule.getCostsByDataSupplier(),
                    String.valueOf(rule.getLei().stream().filter(s -> s != null && !s.isEmpty()).count()),
                    String.valueOf(rule.getOenbId().stream().filter(s -> s != null && !s.isEmpty()).count()),
                    String.valueOf(rule.getIsinShareclass().size()), String.valueOf(rule.getIsinSegment().size())));

            for (String lei : rule.getLei()) {
                LOG.trace("neuer LEI {}", lei);
                TreeItem<RuleRow> l = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(), rule.getProfile(),
                        rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(), rule.getCreationTime(),
                        rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(), rule.getFrequency(),
                        rule.getCostsByDataSupplier(), lei, null, null, null));
                rootTable.add(l);
            }

            for (String oenbId : rule.getOenbId()) {
                LOG.trace("NEUE OENB GEFUNDEN: {}", oenbId);
                TreeItem<RuleRow> oenbTemp = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                        rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, oenbId, null, null));
                rootTable.add(oenbTemp);
            }

            for (String isin : rule.getIsinShareclass()) {
                LOG.trace("NEUE ISIN Shareclass GEFUNDEN: {}", isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                        rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, isin, null));
                rootTable.add(isinTemp);
            }

            for (String isin : rule.getIsinSegment()) {
                LOG.trace("NEUE ISIN Segment GEFUNDEN: {}", isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(new RuleRow(rule.getId(), rule.getContentType(),
                        rule.getProfile(), rule.getDataSupplierCreatorShort(), rule.getDataSupplierCreatorName(),
                        rule.getCreationTime(), rule.getAccessDelayInDays(), rule.getDateFrom(), rule.getDateTo(),
                        rule.getFrequency(), rule.getCostsByDataSupplier(), null, null, null, isin));
                rootTable.add(isinTemp);
            }

            ruleId.getChildren().addAll(rootTable);
            rootData.getChildren().addAll(ruleId);
        }

        accessRightTable.setRoot(rootData);
        accessRightTable.setShowRoot(false);

        TreeTableColumn<RuleRow, String> ruleIdCol = new TreeTableColumn<>("Rule ID");
        ruleIdCol.setPrefWidth(150);
        ruleIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));

        TreeTableColumn<RuleRow, String> profile = new TreeTableColumn<>("Profile");
        profile.setPrefWidth(100);
        profile.setCellValueFactory(new TreeItemPropertyValueFactory<>("profile"));

        TreeTableColumn<RuleRow, String> contentType = new TreeTableColumn<>("Content Type");
        contentType.setPrefWidth(100);
        contentType.setCellValueFactory(new TreeItemPropertyValueFactory<>("ContentType"));

        TreeTableColumn<RuleRow, String> dds = new TreeTableColumn<>("Data Suppliere");
        TreeTableColumn<RuleRow, String> ddsShort = new TreeTableColumn<>("Data Supplier Short");
        ddsShort.setPrefWidth(50);
        ddsShort.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSupplierCreatorShort"));
        TreeTableColumn<RuleRow, String> ddsLong = new TreeTableColumn<>("Long Name");
        ddsLong.setPrefWidth(180);
        ddsLong.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSupplierCreatorName"));

        dds.getColumns().addAll(ddsShort, ddsLong);

        TreeTableColumn<RuleRow, String> lei = new TreeTableColumn<>("LEI");
        lei.setCellValueFactory(new TreeItemPropertyValueFactory<>("lei"));
        lei.setPrefWidth(150);

        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<>("OENB ID");
        oenbId.setCellValueFactory(new TreeItemPropertyValueFactory<>("oenbId"));
        oenbId.setPrefWidth(80);

        TreeTableColumn<RuleRow, String> isinShareClass = new TreeTableColumn<>("ISIN SC");
        isinShareClass.setCellValueFactory(new TreeItemPropertyValueFactory<>("shareclassIsin"));
        isinShareClass.setPrefWidth(110);

        TreeTableColumn<RuleRow, String> isinSegment = new TreeTableColumn<>("ISIN Segm.");
        isinSegment.setCellValueFactory(new TreeItemPropertyValueFactory<>("segmentIsin"));
        isinSegment.setPrefWidth(110);

        TreeTableColumn<RuleRow, String> fundName = new TreeTableColumn<>("Fund Name");
        fundName.setCellValueFactory(new TreeItemPropertyValueFactory<>("fundName"));
        fundName.setPrefWidth(200);

        TreeTableColumn<RuleRow, String> dateFrom = new TreeTableColumn<>("From");
        dateFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateFrom"));
        TreeTableColumn<RuleRow, String> dateTo = new TreeTableColumn<>("To");
        dateTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateTo"));
        TreeTableColumn<RuleRow, String> frequency = new TreeTableColumn<>("frequency");
        frequency.setCellValueFactory(new TreeItemPropertyValueFactory<>("frequency"));

        accessRightTable.setTableMenuButtonVisible(true);
        accessRightTable.getColumns().addAll(ruleIdCol, profile, contentType, dds, lei, oenbId, isinShareClass,
                isinSegment, fundName, dateFrom, dateTo, frequency);
    }
}
