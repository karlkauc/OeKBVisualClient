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
package controller

import com.jfoenix.controls.JFXButton
import dao.AccesRights
import dao.WriteXLS
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.util.Callback
import model.AccessRule
import model.ApplicationSettings
import model.RuleRow

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Log4j2
@CompileStatic
class AccessRightGrant implements Initializable {

    private ApplicationSettings settingsData
    private List<AccessRule> accessRule
    private TreeItem<RuleRow> root = new TreeItem<>()
    private AccesRights ar = new AccesRights()

    @FXML
    private TreeTableView<RuleRow> accessRightTable

    @FXML
    private JFXButton exportToExcel

    @FXML
    Label statusMessage

    @FXML
    JFXButton dumpData


    @FXML
    void dumpData() {
        accessRule.each { rule ->
            println rule
        }
    }

    @FXML
    void exportToExcel() {
        def
        final fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "_accessRulesGranted.xslx"
        log.debug "speichere alle ab [" + fileName + "]."
        WriteXLS.writeAccessRights(fileName, accessRule)
        statusMessage.setText("Alles gespeichert!")
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        log.debug "starte controller für settings"

        settingsData = ApplicationSettings.getInstance()
        accessRule = ar.getAccessRightsGivenFromOEKB()
        accessRightTable.editable = true

        def rootTable = []

        accessRule.each { rule ->
            def ddsGivenShort = rule.dataSuppliersGivenShort.join(';')

            rootTable = []
            TreeItem<RuleRow> ruleId = new TreeItem<RuleRow>(
                    new RuleRow(id: (String) rule.id,
                            contentType: (String) rule.contentType,
                            profile: (String) rule.profile,
                            dataSupplierCreatorShort: (String) rule.dataSupplierCreatorShort,
                            dataSupplierCreatorName: (String) rule.dataSupplierCreatorName,
                            dataSuppliersGivenShort: (String) ddsGivenShort,
                            creationTime: (String) rule.creationTime,
                            accessDelayInDays: (String) rule.accessDelayInDays,
                            dateFrom: (String) rule.dateFrom,
                            dateTo: (String) rule.dateTo,
                            frequency: (String) rule.frequency,
                            costsByDataSupplier: (Boolean) rule.getCostsByDataSupplier(),
                            LEI: rule.LEI.findAll { it.toString().size() > 0 }.size().toString(),
                            OENB_ID: rule.OENB_ID.findAll { it.toString().size() > 0 }.size().toString(),
                            SHARECLASS_ISIN: rule.ISIN_SHARECLASS.size().toString(),
                            SEGMENT_ISIN: rule.ISIN_SEGMENT.size().toString(),
                            rootRow: true
                    )
            )

            rule.LEI.each { LEI ->
                log.trace "neuer LEI " + LEI
                TreeItem<RuleRow> l = new TreeItem<RuleRow>(
                        new RuleRow(id: rule.id as String,
                                contentType: (String) rule.contentType,
                                profile: (String) rule.profile,
                                dataSupplierCreatorShort: (String) rule.dataSupplierCreatorShort,
                                dataSupplierCreatorName: (String) rule.dataSupplierCreatorName,
                                dataSuppliersGivenShort: (String) ddsGivenShort,
                                creationTime: (String) rule.creationTime,
                                accessDelayInDays: (String) rule.accessDelayInDays,
                                dateFrom: (String) rule.dateFrom,
                                dateTo: (String) rule.dateTo,
                                frequency: (String) rule.frequency,
                                costsByDataSupplier: (Boolean) rule.getCostsByDataSupplier(),
                                LEI: (String) LEI,
                                OENB_ID: null as String,
                                SHARECLASS_ISIN: null as String,
                                rootRow: false
                        ))
                rootTable.add(l)
            }

            rule.OENB_ID.each { OENB_ID ->
                log.trace "NEUE OENB GEFUNDEN: " + OENB_ID
                TreeItem<RuleRow> oenbTemp = new TreeItem<RuleRow>(
                        new RuleRow(id: (String) rule.id,
                                contentType: (String) rule.contentType,
                                profile: (String) rule.profile,
                                dataSupplierCreatorShort: (String) rule.dataSupplierCreatorShort,
                                dataSupplierCreatorName: (String) rule.dataSupplierCreatorName,
                                dataSuppliersGivenShort: (String) ddsGivenShort,
                                creationTime: (String) rule.creationTime,
                                accessDelayInDays: (String) rule.accessDelayInDays,
                                dateFrom: (String) rule.dateFrom,
                                dateTo: (String) rule.dateTo,
                                frequency: (String) rule.frequency,
                                costsByDataSupplier: (Boolean) rule.getCostsByDataSupplier(),
                                LEI: null as String,
                                OENB_ID: (String) OENB_ID,
                                SHARECLASS_ISIN: null as String,
                                rootRow: false
                        ))
                rootTable.add(oenbTemp)
            }

            rule.ISIN_SHARECLASS.each { ISIN ->
                log.trace "NEUE ISIN GEFUNDEN " + ISIN
                TreeItem<RuleRow> isinTemp = new TreeItem<RuleRow>(
                        new RuleRow(id: (String) rule.id,
                                contentType: (String) rule.contentType,
                                profile: (String) rule.profile,
                                dataSupplierCreatorShort: (String) rule.dataSupplierCreatorShort,
                                dataSupplierCreatorName: (String) rule.dataSupplierCreatorName,
                                dataSuppliersGivenShort: (String) ddsGivenShort,
                                creationTime: (String) rule.creationTime,
                                accessDelayInDays: (String) rule.accessDelayInDays,
                                dateFrom: (String) rule.dateFrom,
                                dateTo: (String) rule.dateTo,
                                frequency: (String) rule.frequency,
                                costsByDataSupplier: (Boolean) rule.getCostsByDataSupplier(),
                                LEI: null as String,
                                OENB_ID: null as String,
                                SHARECLASS_ISIN: (String) ISIN,
                                rootRow: false
                        ))
                rootTable.add(isinTemp)
            }

            rule.ISIN_SEGMENT.each { ISIN ->
                log.trace "NEUE ISIN GEFUNDEN " + ISIN
                TreeItem<RuleRow> isinTemp = new TreeItem<RuleRow>(
                        new RuleRow(id: (String) rule.id,
                                contentType: (String) rule.contentType,
                                profile: (String) rule.profile,
                                dataSupplierCreatorShort: (String) rule.dataSupplierCreatorShort,
                                dataSupplierCreatorName: (String) rule.dataSupplierCreatorName,
                                dataSuppliersGivenShort: (String) ddsGivenShort,
                                creationTime: (String) rule.creationTime,
                                accessDelayInDays: (String) rule.accessDelayInDays,
                                dateFrom: (String) rule.dateFrom,
                                dateTo: (String) rule.dateTo,
                                frequency: (String) rule.frequency,
                                costsByDataSupplier: (Boolean) rule.getCostsByDataSupplier(),
                                LEI: null as String,
                                OENB_ID: null as String,
                                SEGMENT_ISIN: (String) ISIN,
                                SHARECLASS_ISIN: null as String,
                                rootRow: false
                        ))
                rootTable.add(isinTemp)
            }

            ruleId.children.addAll(rootTable)
            root.children.addAll(ruleId)
        }

        accessRightTable.root = root
        accessRightTable.showRoot = false

        TreeTableColumn<RuleRow, String> ruleId = new TreeTableColumn<>("Rule ID")
        ruleId.prefWidth = 150
        ruleId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"))
        ruleId.editable = true

        TreeTableColumn<RuleRow, Boolean> removeButton = new TreeTableColumn<RuleRow, Boolean>()
        removeButton.setCellFactory(new Callback<TreeTableColumn<RuleRow, Boolean>, TreeTableCell<RuleRow, Boolean>>() {
            @Override
            TreeTableCell<RuleRow, Boolean> call(TreeTableColumn<RuleRow, Boolean> param) {
                return new ButtonCell()
            }
        })
        removeButton.prefWidth = 85

        TreeTableColumn<RuleRow, String> profile = new TreeTableColumn<>("Profile")
        profile.prefWidth = 80
        profile.setCellValueFactory(new TreeItemPropertyValueFactory<>("profile"))

        TreeTableColumn<RuleRow, String> contentType = new TreeTableColumn<>("Content Type")
        contentType.prefWidth = 100
        contentType.setCellValueFactory(new TreeItemPropertyValueFactory<>("ContentType"))

        TreeTableColumn<RuleRow, String> dds = new TreeTableColumn<>("Data Suppliere")
        TreeTableColumn<RuleRow, String> ddsFrom = new TreeTableColumn<>("from")
        ddsFrom.prefWidth = 50
        ddsFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSupplierCreatorShort"))
        TreeTableColumn<RuleRow, String> ddsTo = new TreeTableColumn<>("to")
        ddsTo.prefWidth = 150
        ddsTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSuppliersGivenShort"))
        dds.columns.addAll(ddsFrom, ddsTo)

        TreeTableColumn<RuleRow, String> ids = new TreeTableColumn<>("LEI / OENB ID / ISIN")
        TreeTableColumn<RuleRow, String> lei = new TreeTableColumn<>("LEI")
        lei.setCellValueFactory(new TreeItemPropertyValueFactory<>("LEI"))
        lei.prefWidth = 130
        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<>("OENB ID")
        oenbId.setCellValueFactory(new TreeItemPropertyValueFactory<>("OENB_ID"))
        oenbId.prefWidth = 80
        TreeTableColumn<RuleRow, String> isin = new TreeTableColumn<>("ISIN SC")
        isin.setCellValueFactory(new TreeItemPropertyValueFactory<>("SHARECLASS_ISIN"))
        isin.prefWidth = 90
        TreeTableColumn<RuleRow, String> isinSeg = new TreeTableColumn<>("ISIN Seg")
        isinSeg.setCellValueFactory(new TreeItemPropertyValueFactory<>("SEGMENT_ISIN"))
        isinSeg.prefWidth = 90
        ids.columns.addAll(lei, oenbId, isin, isinSeg)

        TreeTableColumn<RuleRow, String> fundName = new TreeTableColumn<>("Fund Name")
        fundName.setCellValueFactory(new TreeItemPropertyValueFactory<>("fundName"))
        fundName.prefWidth = 110

        TreeTableColumn<RuleRow, String> dateFrom = new TreeTableColumn<>("From")
        dateFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateFrom"))
        TreeTableColumn<RuleRow, String> dateTo = new TreeTableColumn<>("To")
        dateTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateTo"))
        TreeTableColumn<RuleRow, String> frequency = new TreeTableColumn<>("frequency")
        frequency.setCellValueFactory(new TreeItemPropertyValueFactory<>("frequency"))

        TreeTableColumn<RuleRow, String> delay = new TreeTableColumn<>("Delay")
        delay.setCellValueFactory(new TreeItemPropertyValueFactory<>("accessDelayInDays"))


        accessRightTable.tableMenuButtonVisible = true
        accessRightTable.columns.addAll(ruleId, removeButton, profile, contentType, dds, ids, fundName, dateFrom, dateTo, frequency, delay)
    }
}


@CompileStatic
@Log4j2(value = "logButton")
class ButtonCell extends TreeTableCell<RuleRow, Boolean> {
    final Button cellButton = new Button("Remove")

    ButtonCell() {
        cellButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent t) {
                TreeTableRow<RuleRow> rule = ButtonCell.this.getTreeTableRow()
                if (rule.item.rootRow) {
                    logButton.debug "Rule zum Löschen: " + rule.item

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION)

                    Image image = new Image("img/icons8-people-80.png")
                    ImageView imageView = new ImageView(image)
                    alert.setGraphic(imageView)

                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow()
                    Image icon = new Image("img/connectdevelop.png")
                    if (icon == null) {
                        logButton.error "ICON NULL"
                    }
                    else {
                        stage.getIcons().add(icon)
                    }

                    alert.setTitle("Modify Access Rights")
                    alert.setHeaderText("Access Rights from: " + rule.item.dataSupplierCreatorShort)

                    GridPane grid = new GridPane()
                    grid.setHgap(10)
                    grid.setVgap(10)
                    grid.setPadding(new Insets(20, 30, 10, 20))

                    TextField ruleId = new TextField(rule.item.id)
                    TextField profile = new TextField(rule.item.profile)
                    TextField toDDS = new TextField(rule.item.dataSuppliersGivenShort)
                    TextField dateFrom = new TextField(rule.item.dateFrom)
                    TextField dateTo = new TextField(rule.item.dateTo)
                    TextField frequency = new TextField(rule.item.frequency)
                    TextField delay = new TextField(rule.item.accessDelayInDays)

                    CheckBox costsByDDS = new CheckBox()
                    costsByDDS.selected = rule.item.costsByDataSupplier

                    TextField comment = new TextField("TBD")
                    TextField contentType = new TextField(rule.item.contentType)

                    TextField addLEI = new TextField()
                    TextField addOENB_ID = new TextField()
                    TextField addSHARECLASS_ISIN = new TextField()
                    TextField addSEGMENT_ISIN = new TextField()

                    grid.add(new Label("Rule ID:"), 0, 0)
                    grid.add(ruleId, 1, 0)
                    grid.add(new Label("Profile:"), 2, 0)
                    grid.add(profile, 3, 0)

                    grid.add(new Label("Datasupplier: "), 0, 1)
                    grid.add(toDDS, 1, 1)
                    grid.add(new Label("Costs by DDS: "), 2, 1)
                    grid.add(costsByDDS, 3, 1)

                    grid.add(new Label("From Date: "), 0, 2)
                    grid.add(dateFrom, 1, 2)
                    grid.add(new Label("To Date: "), 2, 2)
                    grid.add(dateTo, 3, 2)

                    grid.add(new Label("Freuency: "), 0, 3)
                    grid.add(frequency, 1, 3)
                    grid.add(new Label("Delay in days: "), 2, 3)
                    grid.add(delay, 3, 3)

                    grid.add(new Label("Description: "), 0, 4)
                    grid.add(comment, 1, 4)
                    grid.add(new Label("Content Type: "), 2, 4)
                    grid.add(contentType, 3, 4)

                    grid.add(new Label("Add LEI: "), 0, 5)
                    grid.add(addLEI, 1, 5)
                    grid.add(new Label("Add OeNB ID: "), 2, 5)
                    grid.add(addOENB_ID, 3, 5)

                    grid.add(new Label("Add ShareClass ISIN: "), 0, 6)
                    grid.add(addSHARECLASS_ISIN, 1, 6)
                    grid.add(new Label("Add Segement ISIN: "), 2, 6)
                    grid.add(addSEGMENT_ISIN, 3, 6)

                    alert.dialogPane.content = grid
                    Optional<ButtonType> result = alert.showAndWait()

                    if (result.get() == ButtonType.OK) {
                        logButton.debug "ALLES OK"

                        if (addLEI.text != "" || addOENB_ID.text != "" || addSHARECLASS_ISIN.text != "" || addSEGMENT_ISIN.text != "") {
                            logButton.info "have to add fund to rule...."
                            logButton.debug "LEI: " + addLEI.text
                            logButton.debug "OENB ID: " + addOENB_ID.text
                            logButton.debug "ShareClass ISIN: " + addSHARECLASS_ISIN.text
                            logButton.debug "Segment ISIN: " + addSEGMENT_ISIN.text

                            logButton.debug addLEI.text.class
                        } else {
                            logButton.debug "nichts zum dazufügen."
                        }

                        RuleRow newRuleRow = new RuleRow(id: ruleId.text, contentType: contentType.text, profile: profile.text,
                                dataSupplierCreatorShort: rule.item.dataSupplierCreatorShort, dataSupplierCreatorName: rule.item.dataSupplierCreatorName,
                                creationTime: rule.item.creationTime,
                                dataSuppliersGivenShort: toDDS.text, accessDelayInDays: delay.text,
                                dateFrom: dateFrom.text, dateTo: dateTo.text, frequency: frequency.text, costsByDataSupplier: costsByDDS.selected,
                                LEI: rule.item.LEI, OENB_ID: rule.item.OENB_ID, SHARECLASS_ISIN: rule.item.SHARECLASS_ISIN, SEGMENT_ISIN: rule.item.SEGMENT_ISIN,
                                rootRow: rule.item.rootRow)

                        logButton.debug "old Rule: " + rule.item
                        logButton.debug "new Rule: " + newRuleRow

                        if (newRuleRow == rule.item) {
                            logButton.debug "alles gleich"
                        } else {
                            logButton.debug "unterschiedlich"
                        }

                        // AccesRights ar = new AccesRights()
                        //ar.deleteRule(rule.item)
                    } else {
                        logButton.debug "WOLLTE DOCH NICHT"
                    }

                } else {
                    logButton.debug "lösche ISIN aus rule: " + rule.item.LEI + "/" + rule.item.OENB_ID + "/" + rule.item.SHARECLASS_ISIN + "/" + rule.item.SEGMENT_ISIN
                    // ar.deleteFundFromRule(rule.item)

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION)
                    alert.title = "Delete fund from rule"
                    alert.headerText = "Do you really want to delete the Fund?"

                    Image image = new Image("img/icons8-trash-40.png")
                    ImageView imageView = new ImageView(image)
                    alert.setGraphic(imageView)

                    String text = "delete "
                    if (rule.item.LEI) {
                        text += "LEI [" + rule.item.LEI + "]"
                    }
                    if (rule.item.OENB_ID) {
                        text += "OENB ID [" + rule.item.OENB_ID + "]"
                    }
                    if (rule.item.SHARECLASS_ISIN) {
                        text += "Shareclass ISIN [" + rule.item.SHARECLASS_ISIN + "]"
                    }
                    if (rule.item.SEGMENT_ISIN) {
                        text += "Segment ISIN [" + rule.item.SEGMENT_ISIN + "]"
                    }
                    text += " from rule [" + rule.item.id + "]"

                    alert.contentText = text

                    Optional<ButtonType> result = alert.showAndWait()
                    if (result.get() == ButtonType.OK) {
                        logButton.debug "ALLES OK"

                        AccesRights ar = new AccesRights()
                        ar.deleteFundFromRule(rule.item)

                    } else {
                        logButton.debug "WOLLTE DOCH NICHT"
                    }
                }
            }
        })
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        this.getTreeTableRow()
        RuleRow current = (RuleRow) this.getTreeTableRow().item
        logButton.debug "current: " + current
        if (current == null) {
            cellButton.setText("NULL")
        } else {
            if (current?.rootRow) {
                cellButton.setText("EDIT")
            } else {
                cellButton.setText("REMOVE")
            }
        }

        super.updateItem(t, empty)
        if (!empty) {
            setGraphic(cellButton)
        }
    }
}