package controller

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXPasswordField
import com.jfoenix.controls.JFXTextField
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.fxml.FXML
import javafx.fxml.Initializable

@Log4j2
@CompileStatic
class ApplicationSettings implements Initializable {
    private model.ApplicationSettings settingsData

    @FXML
    private JFXTextField userNameField

    @FXML
    private JFXPasswordField passwordField

    @FXML
    private JFXButton save

    @FXML
    private JFXTextField proxyHost

    @FXML
    private JFXTextField proxyPort

    @FXML
    private JFXCheckBox proxySystemSettings

    @FXML
    private JFXCheckBox jbOverwriteData

    @FXML
    private JFXCheckBox jbNewAccesRuleId

    @FXML
    private JFXTextField proxyUser

    @FXML
    private JFXPasswordField proxyPassword

    @FXML
    void saveSettings() {
        settingsData.with {
            oekbUserName = userNameField.getText()
            oekbPasswort = passwordField.getText()
            connectionProxyHost = proxyHost.getText()
            if (proxyPort.getText().toString().size() > 1) {
                connectionProxyPort = proxyPort.getText().toInteger()
            }
            else {
                connectionProxyPort = null
            }
            connectionProxyUser = proxyUser.getText()
            connectionProxyPassword = proxyPassword.getText()
            overwriteData = jbOverwriteData.selected
            connectionUseSystemSettings = proxySystemSettings.selected
            newAccesRuleId = jbNewAccesRuleId.selected
            saveSettingsDataToFile()
        }
    }


    @Override
    void initialize(URL location, ResourceBundle resources) {
        log.debug "starte controller für settings"

        settingsData = model.ApplicationSettings.getInstance()
        settingsData.readSettingsFromFile()

        userNameField.setText(settingsData.oekbUserName)
        passwordField.setText(settingsData.oekbPasswort)

        proxyHost.setText(settingsData.connectionProxyHost)
        proxyUser.setText(settingsData.connectionProxyUser)
        if (settingsData.connectionProxyPort != null) {
            proxyPort.setText(settingsData.connectionProxyPort.toString())
        }

        proxyPassword.setText(settingsData.connectionProxyPassword)
        proxySystemSettings.setSelected(settingsData.connectionUseSystemSettings)

        jbOverwriteData.setSelected(settingsData.overwriteData)
        jbNewAccesRuleId.setSelected(settingsData.newAccesRuleId)
    }
}
