package controller

import com.jfoenix.controls.JFXButton
import dao.AccesRights
import dao.WriteXLS
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import model.AccessRule
import model.ApplicationSettings
import model.RuleRow

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Log4j2
@CompileStatic
class AccessRightsReceived implements Initializable {

    private ApplicationSettings settingsData
    private List<AccessRule> accessRule

    @FXML
    private JFXButton exportToExcel

    @FXML
    private BorderPane accessRightPane

    @FXML
    private TreeTableView<RuleRow> accessRightTable

    @FXML
    private Label statusMessage

    @FXML
    void exportToExcel() {
        def
        final fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "accessRulesReceived.xslx"
        log.debug "speichere alles nach Excel [" + fileName + "]."

        WriteXLS.writeAccessRights(fileName, accessRule)
        statusMessage.setText("Gespeichert")
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        log.debug "starte controller für settings"

        settingsData = ApplicationSettings.getInstance()
        settingsData.readSettingsFromFile()

        AccesRights ar = new AccesRights()
        accessRule = ar.readAccesRightsRecieved()

        def rootTable = []
        TreeItem<RuleRow> root = new TreeItem<>()
        accessRule.each { rule ->
            rootTable = []
            TreeItem<RuleRow> ruleId = new TreeItem<RuleRow>(
                    new RuleRow(id: rule.id as String,
                            contentType: rule.contentType as String,
                            profile: rule.profile as String,
                            dataSupplierCreatorShort: rule.dataSupplierCreatorShort as String,
                            dataSupplierCreatorName: rule.dataSupplierCreatorName as String,
                            creationTime: rule.creationTime as String,
                            accessDelayInDays: rule.accessDelayInDays as String,
                            dateFrom: rule.dateFrom as String,
                            dateTo: rule.dateTo as String,
                            frequency: rule.frequency as String,
                            costsByDataSupplier: rule.getCostsByDataSupplier() as Boolean,
                            LEI: rule.LEI.findAll { it.toString().size() > 0 }.size().toString(),
                            OENB_ID: rule.OENB_ID.findAll { it.toString().size() > 0 }.size().toString(),
                            SHARECLASS_ISIN: rule.ISIN_SHARECLASS.size().toString(),
                            SEGMENT_ISIN: rule.ISIN_SEGMENT.size().toString()
                    )
            )

            rule.LEI.each { LEI ->
                log.trace "neuer LEI " + LEI
                TreeItem<RuleRow> l = new TreeItem<RuleRow>(
                        new RuleRow(id: rule.id as String,
                                contentType: rule.contentType as String,
                                profile: rule.profile as String,
                                dataSupplierCreatorShort: rule.dataSupplierCreatorShort as String,
                                dataSupplierCreatorName: rule.dataSupplierCreatorName as String,
                                creationTime: rule.creationTime as String,
                                accessDelayInDays: rule.accessDelayInDays as String,
                                dateFrom: rule.dateFrom as String,
                                dateTo: rule.dateTo as String,
                                frequency: rule.frequency as String,
                                costsByDataSupplier: rule.getCostsByDataSupplier() as Boolean,
                                LEI: LEI as String,
                                OENB_ID: null as String,
                                SHARECLASS_ISIN: null as String,
                                SEGMENT_ISIN: null as String
                        ))
                rootTable.add(l)
            }

            rule.OENB_ID.each { OENB_ID ->
                log.trace "NEUE OENB GEFUNDEN: " + OENB_ID
                TreeItem<RuleRow> oenbTemp = new TreeItem<RuleRow>(
                        new RuleRow(id: rule.id as String,
                                contentType: rule.contentType as String,
                                profile: rule.profile as String,
                                dataSupplierCreatorShort: rule.dataSupplierCreatorShort as String,
                                dataSupplierCreatorName: rule.dataSupplierCreatorName as String,
                                creationTime: rule.creationTime as String,
                                accessDelayInDays: rule.accessDelayInDays as String,
                                dateFrom: rule.dateFrom as String,
                                dateTo: rule.dateTo as String,
                                frequency: rule.frequency as String,
                                costsByDataSupplier: rule.getCostsByDataSupplier() as Boolean,
                                LEI: null as String,
                                OENB_ID: OENB_ID as String,
                                SHARECLASS_ISIN: null as String,
                                SEGMENT_ISIN: null as String
                        ))
                rootTable.add(oenbTemp)
            }

            rule.ISIN_SHARECLASS.each { ISIN ->
                log.trace "NEUE ISIN Shareclass GEFUNDEN: " + ISIN
                TreeItem<RuleRow> isinTemp = new TreeItem<RuleRow>(
                        new RuleRow(id: rule.id as String,
                                contentType: rule.contentType as String,
                                profile: rule.profile as String,
                                dataSupplierCreatorShort: rule.dataSupplierCreatorShort as String,
                                dataSupplierCreatorName: rule.dataSupplierCreatorName as String,
                                creationTime: rule.creationTime as String,
                                accessDelayInDays: rule.accessDelayInDays as String,
                                dateFrom: rule.dateFrom as String,
                                dateTo: rule.dateTo as String,
                                frequency: rule.frequency as String,
                                costsByDataSupplier: rule.getCostsByDataSupplier() as Boolean,
                                LEI: null as String,
                                OENB_ID: null as String,
                                SHARECLASS_ISIN: ISIN as String,
                                SEGMENT_ISIN: null as String
                        ))
                rootTable.add(isinTemp)
            }

            rule.ISIN_SEGMENT.each { ISIN ->
                log.trace "NEUE ISIN Shareclass GEFUNDEN: " + ISIN
                TreeItem<RuleRow> isinTemp = new TreeItem<RuleRow>(
                        new RuleRow(id: rule.id as String,
                                contentType: rule.contentType as String,
                                profile: rule.profile as String,
                                dataSupplierCreatorShort: rule.dataSupplierCreatorShort as String,
                                dataSupplierCreatorName: rule.dataSupplierCreatorName as String,
                                creationTime: rule.creationTime as String,
                                accessDelayInDays: rule.accessDelayInDays as String,
                                dateFrom: rule.dateFrom as String,
                                dateTo: rule.dateTo as String,
                                frequency: rule.frequency as String,
                                costsByDataSupplier: rule.getCostsByDataSupplier() as Boolean,
                                LEI: null as String,
                                OENB_ID: null as String,
                                SHARECLASS_ISIN: null as String,
                                SEGMENT_ISIN: ISIN as String
                        ))
                rootTable.add(isinTemp)
            }

            ruleId.getChildren().addAll(rootTable)
            root.getChildren().addAll(ruleId)
        }

        accessRightTable.root = root
        accessRightTable.showRoot = false

        TreeTableColumn<RuleRow, String> ruleId = new TreeTableColumn<RuleRow, String>("Rule ID")
        ruleId.prefWidth = 150
        ruleId.setCellValueFactory(new TreeItemPropertyValueFactory<RuleRow, String>("id"))

        TreeTableColumn<RuleRow, String> profile = new TreeTableColumn<RuleRow, String>("Profile")
        profile.prefWidth = 100
        profile.setCellValueFactory(new TreeItemPropertyValueFactory<RuleRow, String>("profile"))

        TreeTableColumn<RuleRow, String> contentType = new TreeTableColumn<RuleRow, String>("Content Type")
        contentType.prefWidth = 100
        contentType.setCellValueFactory(new TreeItemPropertyValueFactory<RuleRow, String>("ContentType"))

        TreeTableColumn<RuleRow, String> dds = new TreeTableColumn<RuleRow, String>("Data Suppliere")
        TreeTableColumn<RuleRow, String> ddsShort = new TreeTableColumn<RuleRow, String>("Data Supplier Short")
        ddsShort.prefWidth = 50
        ddsShort.prefWidth = 50
        ddsShort.setCellValueFactory(new TreeItemPropertyValueFactory<RuleRow, String>("dataSupplierCreatorShort"))
        TreeTableColumn<RuleRow, String> ddsLong = new TreeTableColumn<RuleRow, String>("Long Name")
        ddsLong.prefWidth = 180
        ddsLong.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("dataSupplierCreatorName")))

        dds.columns.addAll(ddsShort, ddsLong)

        TreeTableColumn<RuleRow, String> lei = new TreeTableColumn<RuleRow, String>("LEI")
        lei.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("LEI")))
        lei.prefWidth = 150

        TreeTableColumn<RuleRow, String> oenbId = new TreeTableColumn<RuleRow, String>("OENB ID")
        oenbId.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("OENB_ID")))
        oenbId.prefWidth = 80

        TreeTableColumn<RuleRow, String> isinShareClass = new TreeTableColumn<RuleRow, String>("ISIN SC")
        isinShareClass.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("SHARECLASS_ISIN")))
        isinShareClass.prefWidth = 110

        TreeTableColumn<RuleRow, String> isinSegment = new TreeTableColumn<RuleRow, String>("ISIN Segm.")
        isinSegment.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("SEGMENT_ISIN")))
        isinSegment.prefWidth = 110

        TreeTableColumn<RuleRow, String> fundName = new TreeTableColumn<RuleRow, String>("Fund Name")
        fundName.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("fundName")))
        fundName.prefWidth = 200

        TreeTableColumn<RuleRow, String> dateFrom = new TreeTableColumn<RuleRow, String>("From")
        dateFrom.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("dateFrom")))
        TreeTableColumn<RuleRow, String> dateTo = new TreeTableColumn<RuleRow, String>("To")
        dateTo.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("dateTo")))
        TreeTableColumn<RuleRow, String> frequency = new TreeTableColumn<RuleRow, String>("frequency")
        frequency.setCellValueFactory((new TreeItemPropertyValueFactory<RuleRow, String>("frequency")))

        accessRightTable.tableMenuButtonVisible = true
        accessRightTable.columns.addAll(ruleId, profile, contentType, dds, lei, oenbId, isinShareClass, isinSegment, fundName, dateFrom, dateTo, frequency)
    }
}