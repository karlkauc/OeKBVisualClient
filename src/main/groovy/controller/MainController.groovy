package controller

import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXToggleButton
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import model.ApplicationSettings

@Log4j2
@CompileStatic
class MainController implements Initializable {
    private ApplicationSettings settingsData

    @FXML
    private JFXTextArea debugMessages

    @FXML
    private JFXToggleButton prodServer

    @FXML
    CheckBox fileSystem

    @FXML
    private URL location

    @FXML
    private Pane mainPane

    @FXML
    TextField dataSupplier

    @FXML
    Label server

    @FXML
    void changeDDS() {
        settingsData.dataSupplierList = dataSupplier.text
        settingsData.saveSettingsDataToFile()
    }

    @FXML
    private void changeToSettings() {
        log.debug "bin jetzt in settings"

        Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageApplicationSettings.fxml"))
        mainPane.getChildren().setAll(tempPane)
    }

    @FXML
    private void changeFileSystem() {
        settingsData.fileSystem = fileSystem.selected
        log.debug "file system: " + fileSystem.selected
        settingsData.saveSettingsDataToFile()
    }

    def setLabelTextForServer() {
        if (settingsData.useProdServer) {
            server.text = "PROD SERVER"
        } else {
            server.text = "DEV SERVER"
        }

    }

    @FXML
    private void changeServer() {
        settingsData.useProdServer = prodServer.selected
        settingsData.saveSettingsDataToFile()

        setLabelTextForServer()
    }

    @FXML
    private void changeToAccessRightsReceived() {
        log.debug "bin jetzt im access rights receive"

        if (settingsData.oekbUserName.isEmpty() || settingsData.oekbPasswort.isEmpty()) {
            log.debug "no settings found"
            changeToSettings()
        } else {
            Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAccesRightsReceived.fxml"))
            mainPane.getChildren().setAll(tempPane)
            mainPane.setPrefSize(mainPane.maxWidth, mainPane.maxHeight)
        }
    }

    @FXML
    private void changeToAccessRightsGrant() {
        println "bin jetzt im access rights GRANT"

        if (settingsData.oekbUserName.isEmpty() || settingsData.oekbPasswort.isEmpty()) {
            log.debug "no settings found"
            changeToSettings()
        } else {
            Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageAccessRightGrant.fxml"))
            mainPane.getChildren().setAll(tempPane)
            mainPane.setPrefSize(mainPane.maxWidth, mainPane.maxHeight)
        }
    }

    @FXML
    private void changeToDataUpload() {
        log.debug "bin jetzt in changeToDataUpload"

        if (settingsData.oekbUserName.isEmpty() || settingsData.oekbPasswort.isEmpty()) {
            log.debug "no settings found"
            changeToSettings()
        } else {
            Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDataUpload.fxml"))
            mainPane.getChildren().setAll(tempPane)
            mainPane.setPrefSize(mainPane.maxWidth, mainPane.maxHeight)
        }
    }

    @FXML
    private void changeToHistory() {
        log.debug "bin jetzt in changeToHistory"

        if (settingsData.oekbUserName.isEmpty() || settingsData.oekbPasswort.isEmpty()) {
            log.debug "no settings found"
            changeToSettings()
        } else {
            Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageHistory.fxml"))
            mainPane.getChildren().setAll(tempPane)
            mainPane.setPrefSize(mainPane.maxWidth, mainPane.maxHeight)
        }
    }

    @FXML
    private void changeToDataDownload() {
        log.debug "bin jetzt in changeToDataDownload"

        if (settingsData.oekbUserName.isEmpty() || settingsData.oekbPasswort.isEmpty()) {
            log.debug "no settings found"
            changeToSettings()
        } else {
            Pane tempPane = FXMLLoader.load(getClass().getClassLoader().getResource("pages/pageDataDownload.fxml"))
            mainPane.getChildren().setAll(tempPane)
            mainPane.setPrefSize(mainPane.maxWidth, mainPane.maxHeight)
        }
    }

    MainController() {
        super()
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        log.debug "starte controller MAIN"
        settingsData = ApplicationSettings.getInstance()
        settingsData.readSettingsFromFile()

        prodServer.selected = settingsData.useProdServer
        fileSystem.setSelected(settingsData.fileSystem)

        // keine Zugangsdaten eingegeben -> zuerst mal zur Settings Seite
        if (settingsData.oekbUserName.isEmpty() || settingsData.oekbPasswort.isEmpty()) {
            log.debug "no settings found"
            changeToSettings()
        }

        dataSupplier.editable = true
        dataSupplier.text = settingsData.dataSupplierList
        setLabelTextForServer()

        if (settingsData.fileSystem) {
            fileSystem.visible = true
        }
        else {
            fileSystem.visible = false
        }
    }

}
