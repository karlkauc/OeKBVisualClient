package controller

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import controller.AccessRightGrant.ButtonCell
import dao.AccesRights
import dao.WriteXLS
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.TreeItemPropertyValueFactory
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
        final fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "_accessRulesGranted.xlsx"
        log.debug "speichere alle ab [" + fileName + "]."
        WriteXLS.writeAccessRights(fileName, accessRule)
        statusMessage.setText("Alles gespeichert!")
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        log.debug "starte controller für settings"

        settingsData = ApplicationSettings.getInstance()
        accessRule = ar.readAccessRightsGive()
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
                            deleteRule: false,
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
                                deleteRule: false,
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
                                deleteRule: false,
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
                                deleteRule: false,
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
                                deleteRule: false,
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

        TreeTableColumn<RuleRow, Boolean> removeButton = new TreeTableColumn<RuleRow, Boolean>("Remove")
        removeButton.setCellFactory(new Callback<TreeTableColumn<RuleRow, Boolean>, TreeTableCell<RuleRow, Boolean>>() {
            @Override
            TreeTableCell<RuleRow, Boolean> call(TreeTableColumn<RuleRow, Boolean> param) {
                return new ButtonCell()
            }
        })
        removeButton.prefWidth = 95

        TreeTableColumn<RuleRow, String> profile = new TreeTableColumn<>("Profile")
        profile.prefWidth = 100
        profile.setCellValueFactory(new TreeItemPropertyValueFactory<>("profile"))

        TreeTableColumn<RuleRow, String> contentType = new TreeTableColumn<>("Content Type")
        contentType.prefWidth = 100
        contentType.setCellValueFactory(new TreeItemPropertyValueFactory<>("ContentType"))

        TreeTableColumn<RuleRow, String> dds = new TreeTableColumn<>("Data Suppliere")
        TreeTableColumn<RuleRow, String> ddsFrom = new TreeTableColumn<>("from")
        ddsFrom.prefWidth = 50
        ddsFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSupplierCreatorShort"))
        TreeTableColumn<RuleRow, String> ddsTo = new TreeTableColumn<>("to")
        ddsTo.prefWidth = 180
        ddsTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dataSuppliersGivenShort"))
        dds.columns.addAll(ddsFrom, ddsTo)

        TreeTableColumn<RuleRow, String> ids = new TreeTableColumn<>("LEI / OENB ID / ISIN")
        TreeTableColumn<RuleRow, String> lei = new TreeTableColumn<>("LEI")
        lei.setCellValueFactory(new TreeItemPropertyValueFactory<>("LEI"))
        lei.prefWidth = 150
        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<>("OENB ID")
        oenbId.setCellValueFactory(new TreeItemPropertyValueFactory<>("OENB_ID"))
        oenbId.prefWidth = 80
        TreeTableColumn<RuleRow, String> isin = new TreeTableColumn<>("ISIN SC")
        isin.setCellValueFactory(new TreeItemPropertyValueFactory<>("SHARECLASS_ISIN"))
        isin.prefWidth = 110
        TreeTableColumn<RuleRow, String> isinSeg = new TreeTableColumn<>("ISIN Seg")
        isinSeg.setCellValueFactory(new TreeItemPropertyValueFactory<>("SEGMENT_ISIN"))
        isinSeg.prefWidth = 110
        ids.columns.addAll(lei, oenbId, isin, isinSeg)

        TreeTableColumn<RuleRow, String> fundName = new TreeTableColumn<>("Fund Name")
        fundName.setCellValueFactory(new TreeItemPropertyValueFactory<>("fundName"))
        fundName.prefWidth = 200

        TreeTableColumn<RuleRow, String> dateFrom = new TreeTableColumn<>("From")
        dateFrom.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateFrom"))
        TreeTableColumn<RuleRow, String> dateTo = new TreeTableColumn<>("To")
        dateTo.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateTo"))
        TreeTableColumn<RuleRow, String> frequency = new TreeTableColumn<>("frequency")
        frequency.setCellValueFactory(new TreeItemPropertyValueFactory<>("frequency"))

        accessRightTable.tableMenuButtonVisible = true
        accessRightTable.columns.addAll(ruleId, removeButton, profile, contentType, dds, ids, fundName, dateFrom, dateTo, frequency)
    }

    @CompileStatic
    @Log4j2
    private class ButtonCell extends TreeTableCell<RuleRow, Boolean> {
        final Button cellButton = new Button("Remove")

        ButtonCell() {
            // println "drinnen!"
            cellButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                void handle(ActionEvent t) {
                    TreeTableRow<RuleRow> rule = ButtonCell.this.getTreeTableRow()
                    if (rule.item.rootRow) {


                        log.debug "Rule zum Löschen: " + rule.item
                        // ar.deleteRule(rule.item)
                    } else {
                        log.debug "lösche ISIN aus rule: " + rule.item.LEI + "/" + rule.item.OENB_ID + "/" + rule.item.SHARECLASS_ISIN + "/" + rule.item.SEGMENT_ISIN
                        ar.deleteFundFromRule(rule.item)
                    }
                }
            })
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            this.getTreeTableRow()
            RuleRow current = (RuleRow) this.getTreeTableRow().item
            println "current: " + current
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

}
