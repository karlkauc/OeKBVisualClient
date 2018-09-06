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
package model

import groovy.transform.ToString
import groovy.util.logging.Log4j2
import groovy.xml.MarkupBuilder
import org.apache.commons.codec.binary.Base64

import java.nio.charset.StandardCharsets

@Singleton
@ToString(includeNames = true)
@Log4j2
class ApplicationSettings {
    String oekbPasswort
    String oekbUserName

    Boolean connectionUseSystemSettings
    String connectionProxyHost
    Integer connectionProxyPort
    String connectionProxyUser
    String connectionProxyPassword

    Boolean overwriteData
    Boolean newAccesRuleId

    Boolean useProdServer
    String backupDirectory

    Boolean fileSystem

    String dataSupplierList

    boolean saveSettingsDataToFile(String fileName = 'settings.xml') {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.settings() {
            OeKBUserName(oekbUserName)
            OeKBUserPassword(oekbPasswort)
            useProdServer(useProdServer)
            DataSupplierList {
                DataSupplier(dataSupplierList)
            }
            proxy {
                host(connectionProxyHost)
                port(connectionProxyPort)
                user(connectionProxyUser)
                password(connectionProxyPassword)
                useSystemSettings(connectionUseSystemSettings)
            }
            overwriteData(overwriteData)
            newAccesRuleId(newAccesRuleId)
            BackupDirectory("backup")
            fileSystem(fileSystem)
        }

        new File(fileName).delete()
        def nf = new File(fileName)
        nf << writer.toString()

        log.debug "File " + fileName + " saved..."
        return true
    }

    def readSettingsFromFile(String filename = 'settings.xml') {
        log.debug "lese settings aus file ein: " + filename

        def file = new File(filename)
        if (!file.exists()) {
            log.error "Settings File " + file.absolutePath + " does not exists! "
            saveSettingsDataToFile()
        }
        else {
            log.debug "settings File " + file.absolutePath + " wird eingelesen"
        }
        def xml = new XmlSlurper().parseText(file.text)

        oekbUserName = xml.OeKBUserName.text()
        oekbPasswort = xml.OeKBUserPassword.text()
        useProdServer = xml.useProdServer.toBoolean()
        connectionProxyHost = xml.proxy.host.text()
        if (xml.proxy.port.text().toString().size() != 0) {
            connectionProxyPort = xml.proxy.port.text().toInteger()
        }
        else {
            connectionProxyPort = null
        }

        dataSupplierList = xml.DataSupplierList.DataSupplier.text()

        connectionProxyUser = xml.proxy.user.text()
        connectionProxyPassword = xml.proxy.password.text()
        connectionUseSystemSettings = xml.proxy.useSystemSettings.toBoolean()

        overwriteData = xml.overwriteData.toBoolean()
        newAccesRuleId = xml.newAccesRuleId.toBoolean()
        backupDirectory = xml.BackupDirectory.text()
        if (backupDirectory.size() == 0) {
            backupDirectory = "backup"
        }
        fileSystem = xml.fileSystem.toBoolean()
        // println XmlUtil.serialize(xml)
    }

    def getServerURL() {
        if (useProdServer) {
            return 'https://fdp-service.oekb.at/client'
        }
        return 'https://fdp-qas-service.oekb.at/client'
    }

    String getAuthCredentialsBasic() {
        String pwString = new String((oekbUserName + ":" + oekbPasswort).getBytes(), StandardCharsets.US_ASCII)
        return Base64.encodeBase64String(pwString.getBytes()).toString()
    }


}
