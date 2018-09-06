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
