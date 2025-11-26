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
import model.JournalEntry;
import model.JournalEntry.ActionType;
import model.JournalEntry.JournalType;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO for retrieving and parsing Journal data from OeKB FDP.
 * Based on FundsXML_Journal_1.0.3.xsd
 */
public class Journal {
    private static final Logger log = LogManager.getLogger(Journal.class);
    private final ApplicationSettings applicationSettings = ApplicationSettings.getInstance();

    /**
     * Get journal entries from OeKB FDP or filesystem
     *
     * @param timeFrom Start date/time filter (optional)
     * @param timeTo End date/time filter (optional)
     * @param action Filter by action type: UL (Upload) or DL (Download) (optional)
     * @param type Filter by journal type (optional)
     * @param excludeEmptyDownloads Exclude empty data downloads (default: true)
     * @return List of journal entries
     */
    public List<JournalEntry> getJournalEntries(LocalDateTime timeFrom, LocalDateTime timeTo,
                                                String action, String type,
                                                boolean excludeEmptyDownloads) {
        String xmlString;

        if (applicationSettings.isFileSystem()) {
            // Read from filesystem backup
            File backupDir = new File(applicationSettings.getBackupDirectory());
            File[] potentialFiles = backupDir.listFiles((dir, name) ->
                    name.contains("DOWNLOAD_JOURNAL.xml"));

            log.debug("Potential journal files: " + (potentialFiles != null ? Arrays.toString(potentialFiles) : "none"));

            if (potentialFiles != null && potentialFiles.length > 0) {
                File latestFile = Arrays.stream(potentialFiles)
                        .max((f1, f2) -> f1.getName().compareTo(f2.getName()))
                        .orElse(potentialFiles[0]);
                log.debug("Reading journal from file: " + latestFile);

                try {
                    xmlString = new String(java.nio.file.Files.readAllBytes(latestFile.toPath()), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("Error reading journal file", e);
                    return new ArrayList<>();
                }
            } else {
                log.warn("No journal backup files found");
                return new ArrayList<>();
            }
        } else {
            log.debug("Downloading journal from OeKB server");
            xmlString = new OeKBHTTP().downloadJournal(timeFrom, timeTo, action, type, null, null, excludeEmptyDownloads);
        }

        return parseJournalXml(xmlString);
    }

    /**
     * Parse FundsXMLJournal XML string into list of JournalEntry objects
     */
    private List<JournalEntry> parseJournalXml(String xmlString) {
        List<JournalEntry> entries = new ArrayList<>();

        if (xmlString == null || xmlString.isEmpty()) {
            log.warn("Empty XML string for journal parsing");
            return entries;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));

            doc.getDocumentElement().normalize();

            // Get all JournalEntry elements
            NodeList journalEntryNodes = doc.getElementsByTagName("JournalEntry");
            log.debug("Found " + journalEntryNodes.getLength() + " journal entries");

            for (int i = 0; i < journalEntryNodes.getLength(); i++) {
                Node node = journalEntryNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    JournalEntry entry = parseJournalEntry(element);
                    entries.add(entry);
                }
            }

        } catch (Exception e) {
            log.error("Error parsing journal XML", e);
        }

        return entries;
    }

    /**
     * Parse a single JournalEntry element
     */
    private JournalEntry parseJournalEntry(Element element) {
        JournalEntry entry = new JournalEntry();

        // Action (UL/DL)
        String actionStr = getElementText(element, "Action");
        try {
            entry.setAction(ActionType.valueOf(actionStr));
        } catch (Exception e) {
            log.warn("Unknown action type: " + actionStr);
        }

        // Type
        String typeStr = getElementText(element, "Type");
        try {
            entry.setType(JournalType.valueOf(typeStr));
        } catch (Exception e) {
            log.warn("Unknown journal type: " + typeStr);
        }

        // DataSupplier
        NodeList dataSupplierNodes = element.getElementsByTagName("DataSupplier");
        if (dataSupplierNodes.getLength() > 0) {
            Element dataSupplierElement = (Element) dataSupplierNodes.item(0);
            String dataSupplierShort = getElementText(dataSupplierElement, "Short");
            entry.setDataSupplier(dataSupplierShort);
        }

        // User
        String user = getElementText(element, "User");
        entry.setUserName(user);

        // ActionTime
        String actionTimeStr = getElementText(element, "ActionTime");
        if (actionTimeStr != null && !actionTimeStr.isEmpty()) {
            try {
                LocalDateTime actionTime = LocalDateTime.parse(actionTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                entry.setTimestamp(actionTime);
            } catch (DateTimeParseException e) {
                log.warn("Could not parse action time: " + actionTimeStr, e);
            }
        }

        // For uploads: check if data was processed or errors occurred
        NodeList uploadNodes = element.getElementsByTagName("Upload");
        if (uploadNodes.getLength() > 0) {
            Element uploadElement = (Element) uploadNodes.item(0);

            // Get status
            String status = getElementText(uploadElement, "Status");
            if (status != null && !status.isEmpty()) {
                entry.setDetails("Status: " + status);
            }

            // Get UniqueDocumentID
            String uniqueDocId = getElementText(uploadElement, "UniqueDocumentID");
            if (uniqueDocId != null && !uniqueDocId.isEmpty()) {
                entry.setUniqueId(uniqueDocId);
            }
        }

        // For downloads: check what was downloaded
        NodeList downloadNodes = element.getElementsByTagName("Download");
        if (downloadNodes.getLength() > 0) {
            Element downloadElement = (Element) downloadNodes.item(0);

            // Check if it was an empty download
            NodeList dataNodes = downloadElement.getElementsByTagName("Data");
            if (dataNodes.getLength() > 0) {
                Element dataElement = (Element) dataNodes.item(0);
                String isEmpty = dataElement.getAttribute("IsEmpty");
                if ("true".equals(isEmpty)) {
                    entry.setEmpty(true);
                    entry.setDetails("Empty download (no data available)");
                }
            }
        }

        return entry;
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
