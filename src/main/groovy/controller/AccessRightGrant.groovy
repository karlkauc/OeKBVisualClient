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
                    // ar.deleteRule(rule.item)

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION)

                    Image image = new Image("img/icons8-people-80.png")
                    ImageView imageView = new ImageView(image)
                    alert.setGraphic(imageView)

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
                    grid.add(toDDS, 1,1)
                    grid.add(new Label("Costs by DDS: "), 2, 1)
                    grid.add(costsByDDS, 3,1)

                    grid.add(new Label("From Date: "), 0, 2)
                    grid.add(dateFrom, 1,2)
                    grid.add(new Label("To Date: "), 2, 2)
                    grid.add(dateTo, 3,2)

                    grid.add(new Label("Freuency: "), 0, 3)
                    grid.add(frequency, 1,3)
                    grid.add(new Label("Delay in days: "), 2, 3)
                    grid.add(delay, 3,3)

                    grid.add(new Label("Description: "), 0,4)
                    grid.add(comment, 1,4)
                    grid.add(new Label("Content Type: "), 2,4)
                    grid.add(contentType, 3,4)

                    grid.add(new Label("Add LEI: "), 0, 5)
                    grid.add(addLEI, 1,5)
                    grid.add(new Label("Add OeNB ID: "), 2, 5)
                    grid.add(addOENB_ID, 3,5)

                    grid.add(new Label("Add ShareClass ISIN: "), 0, 6)
                    grid.add(addSHARECLASS_ISIN, 1,6)
                    grid.add(new Label("Add Segement ISIN: "), 2, 6)
                    grid.add(addSEGMENT_ISIN, 3,6)

                    alert.dialogPane.content = grid

                    Optional<ButtonType> result = alert.showAndWait()
                    if (result.get() == ButtonType.OK){
                        logButton.debug "ALLES OK"

                        logButton.debug "RULE ID: " + ruleId.text
                        logButton.debug "Profile: " + profile.text

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
                    alert.contentText = "lösche ISIN aus rule: " + rule.item.LEI + "/" + rule.item.OENB_ID + "/" + rule.item.SHARECLASS_ISIN + "/" + rule.item.SEGMENT_ISIN

                    Optional<ButtonType> result = alert.showAndWait()
                    if (result.get() == ButtonType.OK){
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