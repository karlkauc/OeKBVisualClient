package controller

import com.jfoenix.controls.JFXTextArea
import dao.OeKBHTTP
import common.XMLHelper

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.StackPane

@Log4j2
@CompileStatic
class DataUpload implements Initializable {

    @FXML
    private JFXTextArea dataUploadMessage

    @FXML
    private StackPane dataUpload

    ImageView imageView


    @Override
    void initialize(URL location, ResourceBundle resources) {
        log.debug "bin im init..."
        dataUploadMessage.clear()
    }

    // wenn file auf pane gezogen wird
    @FXML
    void onDataUpload(DragEvent e) {
        final Dragboard db = e.getDragboard()

        boolean isNotAccepted = false
        db.getFiles().each { file ->
            if (!file.getName().toLowerCase().endsWith('xml')) {
                isNotAccepted = true
            }
        }

        if (db.hasFiles()) {
            if (!isNotAccepted) {
                dataUpload.setStyle("-fx-border-color: red;" + "-fx-border-width: 5;" + "-fx-background-color: #C6C6C6;" + "-fx-border-style: solid;")
                e.acceptTransferModes(TransferMode.COPY)
            }
        } else {
            // e.consume()
        }

    }

    def static checkForNextActions(String stringData) {
        // def xmlData = new XmlSlurper().parseText(stringData)

        // fehler: id schon vorhanden
        // ofi meldung
        // access rights
    }

    // hier wird das file verarbeitet
    @FXML
    void dataUploadDropped(DragEvent e) {
        log.debug "bin in dropped "

        final Dragboard db = e.getDragboard()
        def text = ""
        // def fileCount = db.getFiles().size()

        db.getFiles().each { file ->
            log.debug "speichere File: " + file
            text += "speichere File: " + file + System.lineSeparator()

            def fileType = XMLHelper.getFileType(file.text)

            def responseFile = OeKBHTTP.uploadDataFile(file)
            if (fileType == XMLHelper.FileTypes.OFI) {
                text += "OFI File gefunden " + System.lineSeparator()
                log.debug "OFI FIle.. Summen Checken... "
                def t = XMLHelper.isOfiResponseOk(responseFile)
                text += "OFI Summen OK? " + t.toString()
                log.debug "OFI Summen sind OK?? " + t
            }
            checkForNextActions(responseFile)

        }

        dataUploadMessage.setText(text)
        e.setDropCompleted(true)
        e.consume()
    }

    // setDropCompleted can be called only from DRAG_DROPPED handler

    @FXML
    void dataUploadDone(DragEvent e) {
        // println "bin im dataUploadDone!"

        dataUpload.style = ""
        final Dragboard db = e.dragboard
        // println "Properties: " + db.properties

        boolean success = false
        if (db.hasFiles()) {
            success = true
            // Only get the first file from the list
            final File file = db.files.get(0)
            Platform.runLater(new Runnable() {
                @Override
                void run() {
                    System.out.println(file.absolutePath)
                    try {
                        if (!dataUpload.children.empty) {
                            dataUpload.children.remove(0)
                        }
                        Image img = new Image(new FileInputStream(file.absolutePath))

                        addImage(img, dataUpload)
                    } catch (FileNotFoundException ex) {
                        log.error ex.toString()
                        log.error ex.message
                    }
                }
            })
        }
        e.setDropCompleted(success)
        e.consume()
    }

    // Hilfsfunktion
    void addImage(Image i, StackPane pane) {
        imageView = new ImageView()
        imageView.image = i
        pane.children.add(imageView)
    }
}

