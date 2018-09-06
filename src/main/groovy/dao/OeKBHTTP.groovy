/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package dao

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import groovy.xml.XmlUtil
import groovyx.net.http.CoreEncoders
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import model.ApplicationSettings

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static groovyx.net.http.MultipartContent.multipart

@Log4j2
@CompileStatic
class OeKBHTTP {
    static ApplicationSettings applicationSettings = ApplicationSettings.getInstance()

    static HttpBuilder getOekbConnection() {
        applicationSettings.readSettingsFromFile()

        String proxyHost = ""
        Integer proxyPort = 0
        boolean useCustomProxy = false

        if (applicationSettings.connectionUseSystemSettings) {
            log.debug "using system proxy Settings"
            System.setProperty("java.net.useSystemProxies", "true")

            useCustomProxy = false
        }
        else {
            if (applicationSettings.connectionProxyHost.size() != 0) {
                proxyHost = applicationSettings.connectionProxyHost as String
                proxyPort = applicationSettings.connectionProxyPort as Integer
            }
        }

        if (proxyHost.size() > 1 && proxyPort > 1) {
            useCustomProxy = true
            log.debug "proxy settings: " + proxyHost + ":" + proxyPort
        }
        else {
            log.info "no valid proxy settings found!"
        }

        HttpBuilder http = HttpBuilder.configure {
            request.uri = applicationSettings.serverURL
            request.headers = [
                    "Transfer-Encoding": 'chunked' as CharSequence,
                    "User-Agent"       : '(KarlK)' as CharSequence,
                    "Accept-Encoding"  : 'gzip,deflate' as CharSequence,
                    "Authorization"    : 'Basic ' + applicationSettings.authCredentialsBasic as CharSequence]
            request.contentType = 'application/x-www-form-urlencoded'

            if (useCustomProxy) {
                execution.proxy proxyHost, proxyPort, Proxy.Type.HTTP, false
            }
        }
        return http
    }

    static String uploadAccessRule(File file) {
        applicationSettings.readSettingsFromFile()
        String output = ""




        saveToBackup(file.text, file.name)
        return output
    }


    static String uploadDataFile(File file) {
        applicationSettings.readSettingsFromFile()
        String outputString = ""

        oekbConnection.post {
            request.contentType = 'multipart/form-data'
            request.body = multipart {
                field "mode", "UPLOAD_DATA"
                field "server", "test"
                field "user", applicationSettings.oekbUserName
                field "datasupplier", applicationSettings.dataSupplierList
                field "clientversion", "meine"
                field "fileToUploadName", file.name
                field "upload_xml", file.name
                part 'fileToUpload', file.name, 'text/plain', file
            }
            request.encoder 'multipart/form-data', CoreEncoders.&multipart
            response.success {
                FromServer from, Object body ->
                    outputString = new String((byte[]) body)
            }
        }

        saveToBackup(file.text, file.name)
        saveToBackup(outputString, "UPLOAD_DATA_REPLY")
        return outputString
    }

    static String downloadAccessRules() {
        applicationSettings.readSettingsFromFile()
        String outputString = ""

        oekbConnection.post {
            request.body = [
                    "mode"         : "DOWNLOAD_AR_RECEIVED",
                    "server"       : "test",
                    "user"         : applicationSettings.oekbUserName,
                    "datasupplier" : applicationSettings.dataSupplierList,
                    "clientversion": "meine"]
            response.success { FromServer from, Object body ->
                outputString = new String((byte[]) body)
            }
        }
        saveToBackup(outputString, "DOWNLOAD_ACCESS_RULE")
        return outputString
    }

    static String downloadGivenAccessRules() {
        applicationSettings.readSettingsFromFile()
        String outputString = ""

        oekbConnection.post {
            request.body = [
                    "mode"         : "DOWNLOAD_AR_ASSIGNED",
                    "server"       : "test",
                    "user"         : applicationSettings.oekbUserName,
                    "datasupplier" : applicationSettings.dataSupplierList,
                    "clientversion": "meine"]

            response.success { FromServer from, Object body ->
                outputString = new String((byte[]) body)
            }
        }
        saveToBackup(outputString, "DOWNLOAD_AR_ASSIGNED")
        return outputString
    }

    static saveToBackup(String xml, String fileName) {
        def filePraefix = applicationSettings.useProdServer ? "PROD" : "DEV"
        filePraefix += "_" + applicationSettings.dataSupplierList
        new File(applicationSettings.backupDirectory).mkdirs()
        def filePath = applicationSettings.backupDirectory + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "__" + filePraefix + "_" + fileName + ".xml"

        // ?? WARUM???
        // ToDo: schon beim Aufruf fixen und nicht mitgeben
        filePath.replace(".xml.xml", ".xml")

        def xmlContent = new XmlSlurper().parseText(xml)
        def xmlPretty = XmlUtil.serialize(xmlContent)

        def outputFile = new File(filePath)
        outputFile.write(xmlPretty)

        log.info "Saving Backup File: " + filePath
    }

}
