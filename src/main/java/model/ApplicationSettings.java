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
package model;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ApplicationSettings {
    private static final Logger log = LogManager.getLogger(ApplicationSettings.class);
    private static ApplicationSettings instance;

    private String oekbPasswort;
    private String oekbUserName;

    private Boolean connectionUseSystemSettings;
    private String connectionProxyHost;
    private Integer connectionProxyPort;
    private String connectionProxyUser;
    private String connectionProxyPassword;

    private Boolean overwriteData;
    private Boolean newAccesRuleId;

    private Boolean useProdServer;
    private String backupDirectory;

    private Boolean fileSystem;

    private String dataSupplierList;

    private ApplicationSettings() {
        // Private constructor for singleton
    }

    public static synchronized ApplicationSettings getInstance() {
        if (instance == null) {
            instance = new ApplicationSettings();
        }
        return instance;
    }

    public boolean saveSettingsDataToFile() {
        return saveSettingsDataToFile("settings.xml");
    }

    public boolean saveSettingsDataToFile(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("settings");
            doc.appendChild(rootElement);

            createElement(doc, rootElement, "OeKBUserName", oekbUserName);
            createElement(doc, rootElement, "OeKBUserPassword", oekbPasswort);
            createElement(doc, rootElement, "useProdServer", String.valueOf(useProdServer));

            Element dataSupplierListElement = doc.createElement("DataSupplierList");
            rootElement.appendChild(dataSupplierListElement);
            createElement(doc, dataSupplierListElement, "DataSupplier", dataSupplierList);

            Element proxyElement = doc.createElement("proxy");
            rootElement.appendChild(proxyElement);
            createElement(doc, proxyElement, "host", connectionProxyHost);
            createElement(doc, proxyElement, "port", connectionProxyPort != null ? String.valueOf(connectionProxyPort) : "");
            createElement(doc, proxyElement, "user", connectionProxyUser);
            createElement(doc, proxyElement, "password", connectionProxyPassword);
            createElement(doc, proxyElement, "useSystemSettings", String.valueOf(connectionUseSystemSettings));

            createElement(doc, rootElement, "overwriteData", String.valueOf(overwriteData));
            createElement(doc, rootElement, "newAccesRuleId", String.valueOf(newAccesRuleId));
            createElement(doc, rootElement, "BackupDirectory", "backup");
            createElement(doc, rootElement, "fileSystem", String.valueOf(fileSystem));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            log.debug("File " + fileName + " saved...");
            return true;
        } catch (Exception e) {
            log.error("Error saving settings to file: " + fileName, e);
            return false;
        }
    }

    private void createElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value != null ? value : "");
        parent.appendChild(element);
    }

    public void readSettingsFromFile() {
        readSettingsFromFile("settings.xml");
    }

    public void readSettingsFromFile(String filename) {
        log.debug("lese settings aus file ein: " + filename);

        File file = new File(filename);
        if (!file.exists()) {
            log.error("Settings File " + file.getAbsolutePath() + " does not exists!");
            saveSettingsDataToFile();
            return;
        } else {
            log.debug("settings File " + file.getAbsolutePath() + " wird eingelesen");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            oekbUserName = getElementText(doc, "OeKBUserName");
            oekbPasswort = getElementText(doc, "OeKBUserPassword");
            useProdServer = Boolean.parseBoolean(getElementText(doc, "useProdServer"));

            connectionProxyHost = getElementText(doc, "host");
            String portText = getElementText(doc, "port");
            if (portText != null && !portText.isEmpty()) {
                connectionProxyPort = Integer.parseInt(portText);
            } else {
                connectionProxyPort = null;
            }

            dataSupplierList = getElementText(doc, "DataSupplier");

            connectionProxyUser = getElementText(doc, "user");
            connectionProxyPassword = getElementText(doc, "password");
            connectionUseSystemSettings = Boolean.parseBoolean(getElementText(doc, "useSystemSettings"));

            overwriteData = Boolean.parseBoolean(getElementText(doc, "overwriteData"));
            newAccesRuleId = Boolean.parseBoolean(getElementText(doc, "newAccesRuleId"));
            backupDirectory = getElementText(doc, "BackupDirectory");
            if (backupDirectory == null || backupDirectory.isEmpty()) {
                backupDirectory = "backup";
            }
            fileSystem = Boolean.parseBoolean(getElementText(doc, "fileSystem"));
        } catch (Exception e) {
            log.error("Error reading settings from file: " + filename, e);
        }
    }

    private String getElementText(Document doc, String tagName) {
        try {
            org.w3c.dom.NodeList nodeList = doc.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent();
            }
        } catch (Exception e) {
            log.warn("Error getting element text for tag: " + tagName, e);
        }
        return "";
    }

    public String getServerURL() {
        if (useProdServer != null && useProdServer) {
            return "https://fdp-service.oekb.at/client";
        }
        return "https://fdp-qas-service.oekb.at/client";
    }

    public String getAuthCredentialsBasic() {
        String pwString = new String((oekbUserName + ":" + oekbPasswort).getBytes(), StandardCharsets.US_ASCII);
        return Base64.encodeBase64String(pwString.getBytes());
    }

    // Getters and Setters
    public String getOekbPasswort() {
        return oekbPasswort;
    }

    public void setOekbPasswort(String oekbPasswort) {
        this.oekbPasswort = oekbPasswort;
    }

    public String getOekbUserName() {
        return oekbUserName;
    }

    public void setOekbUserName(String oekbUserName) {
        this.oekbUserName = oekbUserName;
    }

    public Boolean getConnectionUseSystemSettings() {
        return connectionUseSystemSettings;
    }

    public boolean isConnectionUseSystemSettings() {
        return Boolean.TRUE.equals(connectionUseSystemSettings);
    }

    public void setConnectionUseSystemSettings(Boolean connectionUseSystemSettings) {
        this.connectionUseSystemSettings = connectionUseSystemSettings;
    }

    public String getConnectionProxyHost() {
        return connectionProxyHost;
    }

    public void setConnectionProxyHost(String connectionProxyHost) {
        this.connectionProxyHost = connectionProxyHost;
    }

    public Integer getConnectionProxyPort() {
        return connectionProxyPort;
    }

    public void setConnectionProxyPort(Integer connectionProxyPort) {
        this.connectionProxyPort = connectionProxyPort;
    }

    public String getConnectionProxyUser() {
        return connectionProxyUser;
    }

    public void setConnectionProxyUser(String connectionProxyUser) {
        this.connectionProxyUser = connectionProxyUser;
    }

    public String getConnectionProxyPassword() {
        return connectionProxyPassword;
    }

    public void setConnectionProxyPassword(String connectionProxyPassword) {
        this.connectionProxyPassword = connectionProxyPassword;
    }

    public Boolean getOverwriteData() {
        return overwriteData;
    }

    public boolean isOverwriteData() {
        return Boolean.TRUE.equals(overwriteData);
    }

    public void setOverwriteData(Boolean overwriteData) {
        this.overwriteData = overwriteData;
    }

    public Boolean getNewAccesRuleId() {
        return newAccesRuleId;
    }

    public boolean isNewAccesRuleId() {
        return Boolean.TRUE.equals(newAccesRuleId);
    }

    public void setNewAccesRuleId(Boolean newAccesRuleId) {
        this.newAccesRuleId = newAccesRuleId;
    }

    public Boolean getUseProdServer() {
        return useProdServer;
    }

    public boolean isUseProdServer() {
        return Boolean.TRUE.equals(useProdServer);
    }

    public void setUseProdServer(Boolean useProdServer) {
        this.useProdServer = useProdServer;
    }

    public String getBackupDirectory() {
        return backupDirectory;
    }

    public void setBackupDirectory(String backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    public Boolean getFileSystem() {
        return fileSystem;
    }

    public boolean isFileSystem() {
        return Boolean.TRUE.equals(fileSystem);
    }

    public void setFileSystem(Boolean fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String getDataSupplierList() {
        return dataSupplierList;
    }

    public void setDataSupplierList(String dataSupplierList) {
        this.dataSupplierList = dataSupplierList;
    }

    @Override
    public String toString() {
        return "ApplicationSettings{" +
                "oekbUserName='" + oekbUserName + '\'' +
                ", connectionUseSystemSettings=" + connectionUseSystemSettings +
                ", connectionProxyHost='" + connectionProxyHost + '\'' +
                ", connectionProxyPort=" + connectionProxyPort +
                ", overwriteData=" + overwriteData +
                ", newAccesRuleId=" + newAccesRuleId +
                ", useProdServer=" + useProdServer +
                ", backupDirectory='" + backupDirectory + '\'' +
                ", fileSystem=" + fileSystem +
                ", dataSupplierList='" + dataSupplierList + '\'' +
                '}';
    }
}
