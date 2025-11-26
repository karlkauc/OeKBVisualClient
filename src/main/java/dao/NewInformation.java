/*
 * Copyright 2024 Karl Kauc
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
package dao;

import model.ApplicationSettings;
import model.NewInformationEntry;
import model.NewInformationEntry.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO for retrieving and parsing New Information data from OeKB FDP.
 * Based on FundsXML_NewInformation_1.0.2.xsd
 */
public class NewInformation {
    private static final Logger log = LogManager.getLogger(NewInformation.class);
    private final ApplicationSettings applicationSettings = ApplicationSettings.getInstance();

    /**
     * Get new information entries from OeKB FDP or filesystem
     * Uses DOWNLOAD_AVAILABLE_DATA mode which provides similar information
     *
     * @param contentDate Content date filter (optional)
     * @param uploadTimeFrom Upload time from filter (optional)
     * @param uploadTimeTo Upload time to filter (optional)
     * @return List of new information entries
     */
    public List<NewInformationEntry> getNewInformationEntries(LocalDate contentDate,
                                                              LocalDateTime uploadTimeFrom,
                                                              LocalDateTime uploadTimeTo) {
        String xmlString;

        if (applicationSettings.isFileSystem()) {
            // Read from filesystem backup
            File backupDir = new File(applicationSettings.getBackupDirectory());
            File[] potentialFiles = backupDir.listFiles((dir, name) ->
                    name.contains("DOWNLOAD_AVAILABLE_DATA.xml") ||
                    name.contains("DOWNLOAD_NEWINFORMATION.xml"));

            log.debug("Potential new information files: " + (potentialFiles != null ? Arrays.toString(potentialFiles) : "none"));

            if (potentialFiles != null && potentialFiles.length > 0) {
                File latestFile = Arrays.stream(potentialFiles)
                        .max((f1, f2) -> f1.getName().compareTo(f2.getName()))
                        .orElse(potentialFiles[0]);
                log.debug("Reading new information from file: " + latestFile);

                try {
                    xmlString = new String(java.nio.file.Files.readAllBytes(latestFile.toPath()), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("Error reading new information file", e);
                    return new ArrayList<>();
                }
            } else {
                log.warn("No new information backup files found");
                return new ArrayList<>();
            }
        } else {
            log.debug("Downloading new information from OeKB server");
            xmlString = new OeKBHTTP().downloadAvailableData(contentDate, uploadTimeFrom, uploadTimeTo, null, null);
        }

        return parseNewInformationXml(xmlString);
    }

    /**
     * Parse FundsXML_NewInformation XML string into list of NewInformationEntry objects
     */
    private List<NewInformationEntry> parseNewInformationXml(String xmlString) {
        List<NewInformationEntry> entries = new ArrayList<>();

        if (xmlString == null || xmlString.isEmpty()) {
            log.warn("Empty XML string for new information parsing");
            return entries;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));

            doc.getDocumentElement().normalize();

            // Get all NewInformation elements (or AvailableData elements)
            NodeList infoNodes = doc.getElementsByTagName("NewInformation");
            if (infoNodes.getLength() == 0) {
                // Try alternative structure
                infoNodes = doc.getElementsByTagName("AvailableData");
            }

            log.debug("Found " + infoNodes.getLength() + " new information entries");

            for (int i = 0; i < infoNodes.getLength(); i++) {
                Node node = infoNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NewInformationEntry entry = parseNewInformationEntry(element);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error parsing new information XML", e);
        }

        return entries;
    }

    /**
     * Parse a single NewInformation element
     */
    private NewInformationEntry parseNewInformationEntry(Element element) {
        try {
            NewInformationEntry entry = new NewInformationEntry();

            // ContentType (FUND, DOC, REG)
            String contentTypeStr = getElementText(element, "ContentType");
            ContentType contentType = ContentType.fromString(contentTypeStr);
            entry.setContentType(contentType);

            // ContentDate
            String contentDateStr = getElementText(element, "ContentDate");
            if (!contentDateStr.isEmpty()) {
                try {
                    LocalDate contentDate = LocalDate.parse(contentDateStr);
                    entry.setContentDate(contentDate);
                } catch (DateTimeParseException e) {
                    log.warn("Could not parse content date: " + contentDateStr);
                }
            }

            // UploadDateTime
            String uploadDateTimeStr = getElementText(element, "UploadDateTime");
            if (!uploadDateTimeStr.isEmpty()) {
                try {
                    LocalDateTime uploadDateTime = LocalDateTime.parse(uploadDateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                    entry.setUploadDateTime(uploadDateTime);
                } catch (DateTimeParseException e) {
                    log.warn("Could not parse upload date time: " + uploadDateTimeStr);
                }
            }

            // DataSuppliers
            NodeList dataSupplierNodes = element.getElementsByTagName("DataSupplier");
            for (int i = 0; i < dataSupplierNodes.getLength(); i++) {
                Element dsElement = (Element) dataSupplierNodes.item(i);
                String dataSupplierShort = getElementText(dsElement, "Short");
                if (!dataSupplierShort.isEmpty()) {
                    entry.addDataSupplier(dataSupplierShort);
                }
            }

            // Fund/ShareClass identifiers
            NodeList fundOrShareClassNodes = element.getElementsByTagName("FundOrShareclass");
            if (fundOrShareClassNodes.getLength() > 0) {
                Element fundElement = (Element) fundOrShareClassNodes.item(0);

                // LEI
                String lei = getElementText(fundElement, "LEI");
                if (!lei.isEmpty()) {
                    entry.setLei(lei);
                }

                // ISINs
                NodeList isinNodes = fundElement.getElementsByTagName("ISIN");
                for (int i = 0; i < isinNodes.getLength(); i++) {
                    String isin = isinNodes.item(i).getTextContent();
                    if (isin != null && !isin.trim().isEmpty()) {
                        entry.addIsin(isin.trim());
                    }
                }
            }

            // DocumentTypes (for DOC content type)
            if (contentType == ContentType.DOC) {
                NodeList docTypeNodes = element.getElementsByTagName("DocumentType");
                if (docTypeNodes.getLength() > 0) {
                    Element docTypeElement = (Element) docTypeNodes.item(0);

                    String listedType = getElementText(docTypeElement, "ListedType");
                    String unlistedType = getElementText(docTypeElement, "UnlistedType");
                    entry.setDocumentType(!listedType.isEmpty() ? listedType : unlistedType);

                    String language = getElementText(docTypeElement, "Language");
                    entry.setDocumentLanguage(language);

                    String format = getElementText(docTypeElement, "Format");
                    entry.setDocumentFormat(format);
                }
            }

            // RegulatoryReportings (for REG content type)
            if (contentType == ContentType.REG) {
                NodeList regTypeNodes = element.getElementsByTagName("Type");
                if (regTypeNodes.getLength() > 0) {
                    String reportingType = regTypeNodes.item(0).getTextContent();
                    entry.setReportingType(reportingType);
                }
            }

            // Profiles
            NodeList profileNodes = element.getElementsByTagName("Profile");
            for (int i = 0; i < profileNodes.getLength(); i++) {
                String profile = profileNodes.item(i).getTextContent();
                if (profile != null && !profile.trim().isEmpty()) {
                    entry.addProfile(profile.trim());
                }
            }

            return entry;

        } catch (Exception e) {
            log.error("Error parsing new information entry", e);
            return null;
        }
    }

    /**
     * Helper method to get text content of an element by tag name
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return "";
    }
}
