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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
    private static final Logger log = LogManager.getLogger(AccessRightsReceived.class);

    private ApplicationSettings settingsData;
    private List<AccessRule> accessRule;

    @FXML
    private Button exportToExcel;

    @FXML
    private BorderPane accessRightPane;

    @FXML
    private TreeTableView<RuleRow> accessRightTable;

    @FXML
    private Label statusMessage;

    @FXML
    void exportToExcel() {
        final String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "accessRulesReceived.xslx";
        log.debug("speichere alles nach Excel [" + fileName + "].");

        WriteXLS.writeAccessRights(fileName, accessRule);
        statusMessage.setText("Gespeichert");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("starte controller für settings");

        settingsData = ApplicationSettings.getInstance();
        settingsData.readSettingsFromFile();

        AccesRights ar = new AccesRights();
        accessRule = ar.getAccesRightsRecievedFromOEKB();

        TreeItem<RuleRow> root = new TreeItem<>();

        for (AccessRule rule : accessRule) {
            List<TreeItem<RuleRow>> rootTable = new ArrayList<>();

            TreeItem<RuleRow> ruleId = new TreeItem<>(
                    new RuleRow(rule.getId(),
                            rule.getContentType(),
                            rule.getProfile(),
                            rule.getDataSupplierCreatorShort(),
                            rule.getDataSupplierCreatorName(),
                            rule.getCreationTime(),
                            rule.getAccessDelayInDays(),
                            rule.getDateFrom(),
                            rule.getDateTo(),
                            rule.getFrequency(),
                            rule.getCostsByDataSupplier(),
                            String.valueOf(rule.getLEI().stream().filter(s -> s != null && !s.isEmpty()).count()),
                            String.valueOf(rule.getOENB_ID().stream().filter(s -> s != null && !s.isEmpty()).count()),
                            String.valueOf(rule.getISIN_SHARECLASS().size()),
                            String.valueOf(rule.getISIN_SEGMENT().size())
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
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                lei,
                                null,
                                null,
                                null
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
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                null,
                                oenbId,
                                null,
                                null
                        ));
                rootTable.add(oenbTemp);
            }

            for (String isin : rule.getISIN_SHARECLASS()) {
                log.trace("NEUE ISIN Shareclass GEFUNDEN: " + isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(
                        new RuleRow(rule.getId(),
                                rule.getContentType(),
                                rule.getProfile(),
                                rule.getDataSupplierCreatorShort(),
                                rule.getDataSupplierCreatorName(),
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                null,
                                null,
                                isin,
                                null
                        ));
                rootTable.add(isinTemp);
            }

            for (String isin : rule.getISIN_SEGMENT()) {
                log.trace("NEUE ISIN Segment GEFUNDEN: " + isin);
                TreeItem<RuleRow> isinTemp = new TreeItem<>(
                        new RuleRow(rule.getId(),
                                rule.getContentType(),
                                rule.getProfile(),
                                rule.getDataSupplierCreatorShort(),
                                rule.getDataSupplierCreatorName(),
                                rule.getCreationTime(),
                                rule.getAccessDelayInDays(),
                                rule.getDateFrom(),
                                rule.getDateTo(),
                                rule.getFrequency(),
                                rule.getCostsByDataSupplier(),
                                null,
                                null,
                                null,
                                isin
                        ));
                rootTable.add(isinTemp);
            }

            ruleId.getChildren().addAll(rootTable);
            root.getChildren().addAll(ruleId);
        }

        accessRightTable.setRoot(root);
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
        lei.setCellValueFactory(new TreeItemPropertyValueFactory<>("LEI"));
        lei.setPrefWidth(150);

        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<>("OENB ID");
        oenbId.setCellValueFactory(new TreeItemPropertyValueFactory<>("OENB_ID"));
        oenbId.setPrefWidth(80);

        TreeTableColumn<RuleRow, String> isinShareClass = new TreeTableColumn<>("ISIN SC");
        isinShareClass.setCellValueFactory(new TreeItemPropertyValueFactory<>("SHARECLASS_ISIN"));
        isinShareClass.setPrefWidth(110);

        TreeTableColumn<RuleRow, String> isinSegment = new TreeTableColumn<>("ISIN Segm.");
        isinSegment.setCellValueFactory(new TreeItemPropertyValueFactory<>("SEGMENT_ISIN"));
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
        accessRightTable.getColumns().addAll(ruleIdCol, profile, contentType, dds, lei, oenbId, isinShareClass, isinSegment, fundName, dateFrom, dateTo, frequency);
    }
}
