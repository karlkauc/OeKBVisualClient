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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
    void dumpData() {
        for (AccessRule rule : accessRule) {
            System.out.println(rule);
        }
    }

    @FXML
    void exportToExcel() {
        final String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "_accessRulesGranted.xslx";
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

        TreeTableColumn<RuleRow, String> ruleIdCol = new TreeTableColumn<>("Rule ID");
        ruleIdCol.setPrefWidth(150);
        ruleIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        ruleIdCol.setEditable(true);

        TreeTableColumn<RuleRow, Boolean> removeButton = new TreeTableColumn<>();
        removeButton.setCellFactory(param -> new ButtonCell());
        removeButton.setPrefWidth(85);

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
    }
}


class ButtonCell extends TreeTableCell<RuleRow, Boolean> {
    private static final Logger logButton = LogManager.getLogger(ButtonCell.class);
    private final Button cellButton = new Button("Remove");

    public ButtonCell() {
        cellButton.setOnAction(t -> {
            TreeTableRow<RuleRow> rule = ButtonCell.this.getTreeTableRow();
            if (rule.getItem().isRootRow()) {
                logButton.debug("Rule zum Löschen: " + rule.getItem());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                Image image = new Image("img/icons8-people-80.png");
                ImageView imageView = new ImageView(image);
                alert.setGraphic(imageView);

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                Image icon = new Image("img/connectdevelop.png");
                if (icon == null) {
                    logButton.error("ICON NULL");
                } else {
                    stage.getIcons().add(icon);
                }

                alert.setTitle("Modify Access Rights");
                alert.setHeaderText("Access Rights from: " + rule.getItem().getDataSupplierCreatorShort());

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 30, 10, 20));

                TextField ruleId = new TextField(rule.getItem().getId());
                TextField profile = new TextField(rule.getItem().getProfile());
                TextField toDDS = new TextField(rule.getItem().getDataSuppliersGivenShort());
                TextField dateFrom = new TextField(rule.getItem().getDateFrom());
                TextField dateTo = new TextField(rule.getItem().getDateTo());
                TextField frequency = new TextField(rule.getItem().getFrequency());
                TextField delay = new TextField(rule.getItem().getAccessDelayInDays());

                CheckBox costsByDDS = new CheckBox();
                costsByDDS.setSelected(rule.getItem().isCostsByDataSupplier());

                TextField comment = new TextField("TBD");
                TextField contentType = new TextField(rule.getItem().getContentType());

                TextField addLEI = new TextField();
                TextField addOENB_ID = new TextField();
                TextField addSHARECLASS_ISIN = new TextField();
                TextField addSEGMENT_ISIN = new TextField();

                grid.add(new Label("Rule ID:"), 0, 0);
                grid.add(ruleId, 1, 0);
                grid.add(new Label("Profile:"), 2, 0);
                grid.add(profile, 3, 0);

                grid.add(new Label("Datasupplier: "), 0, 1);
                grid.add(toDDS, 1, 1);
                grid.add(new Label("Costs by DDS: "), 2, 1);
                grid.add(costsByDDS, 3, 1);

                grid.add(new Label("From Date: "), 0, 2);
                grid.add(dateFrom, 1, 2);
                grid.add(new Label("To Date: "), 2, 2);
                grid.add(dateTo, 3, 2);

                grid.add(new Label("Freuency: "), 0, 3);
                grid.add(frequency, 1, 3);
                grid.add(new Label("Delay in days: "), 2, 3);
                grid.add(delay, 3, 3);

                grid.add(new Label("Description: "), 0, 4);
                grid.add(comment, 1, 4);
                grid.add(new Label("Content Type: "), 2, 4);
                grid.add(contentType, 3, 4);

                grid.add(new Label("Add LEI: "), 0, 5);
                grid.add(addLEI, 1, 5);
                grid.add(new Label("Add OeNB ID: "), 2, 5);
                grid.add(addOENB_ID, 3, 5);

                grid.add(new Label("Add ShareClass ISIN: "), 0, 6);
                grid.add(addSHARECLASS_ISIN, 1, 6);
                grid.add(new Label("Add Segement ISIN: "), 2, 6);
                grid.add(addSEGMENT_ISIN, 3, 6);

                alert.getDialogPane().setContent(grid);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    logButton.debug("ALLES OK");

                    if (!addLEI.getText().isEmpty() || !addOENB_ID.getText().isEmpty() ||
                        !addSHARECLASS_ISIN.getText().isEmpty() || !addSEGMENT_ISIN.getText().isEmpty()) {
                        logButton.info("have to add fund to rule....");
                        logButton.debug("LEI: " + addLEI.getText());
                        logButton.debug("OENB ID: " + addOENB_ID.getText());
                        logButton.debug("ShareClass ISIN: " + addSHARECLASS_ISIN.getText());
                        logButton.debug("Segment ISIN: " + addSEGMENT_ISIN.getText());

                        logButton.debug(addLEI.getText().getClass());
                    } else {
                        logButton.debug("nichts zum dazufügen.");
                    }

                    RuleRow newRuleRow = new RuleRow(ruleId.getText(), contentType.getText(), profile.getText(),
                            rule.getItem().getDataSupplierCreatorShort(), rule.getItem().getDataSupplierCreatorName(),
                            toDDS.getText(),
                            rule.getItem().getCreationTime(),
                            delay.getText(),
                            dateFrom.getText(), dateTo.getText(), frequency.getText(), costsByDDS.isSelected(),
                            rule.getItem().getLEI(), rule.getItem().getOENB_ID(),
                            rule.getItem().getSHARECLASS_ISIN(), rule.getItem().getSEGMENT_ISIN(),
                            rule.getItem().isRootRow());

                    logButton.debug("old Rule: " + rule.getItem());
                    logButton.debug("new Rule: " + newRuleRow);

                    if (newRuleRow.equals(rule.getItem())) {
                        logButton.debug("alles gleich");
                    } else {
                        logButton.debug("unterschiedlich");
                    }

                    // AccesRights ar = new AccesRights()
                    //ar.deleteRule(rule.item)
                } else {
                    logButton.debug("WOLLTE DOCH NICHT");
                }

            } else {
                logButton.debug("lösche ISIN aus rule: " + rule.getItem().getLEI() + "/" +
                               rule.getItem().getOENB_ID() + "/" + rule.getItem().getSHARECLASS_ISIN() + "/" +
                               rule.getItem().getSEGMENT_ISIN());
                // ar.deleteFundFromRule(rule.item)

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
            }
        });
    }

    // Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        this.getTreeTableRow();
        RuleRow current = this.getTreeTableRow().getItem();
        logButton.debug("current: " + current);
        if (current == null) {
            cellButton.setText("NULL");
        } else {
            if (current.isRootRow()) {
                cellButton.setText("EDIT");
            } else {
                cellButton.setText("REMOVE");
            }
        }

        super.updateItem(t, empty);
        if (!empty) {
            setGraphic(cellButton);
        }
    }
}
