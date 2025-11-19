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
package dao;

import model.AccessRule;
import model.ApplicationSettings;
import model.RuleRow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccesRights {
    private static final Logger log = LogManager.getLogger(AccesRights.class);

    private ApplicationSettings applicationSettings = ApplicationSettings.getInstance();

    public List<AccessRule> getAccesRightsRecievedFromOEKB() {
        String outputString;

        if (applicationSettings.isFileSystem()) {
            // Daten sollen aus Filesystem gelesen werden
            File backupDir = new File(applicationSettings.getBackupDirectory());
            File[] potentialFiles = backupDir.listFiles((dir, name) ->
                name.contains("DOWNLOAD_ACCESS_RULE.xml"));

            log.debug("Potential files: " + (potentialFiles != null ? Arrays.toString(potentialFiles) : "none"));

            if (potentialFiles != null && potentialFiles.length > 0) {
                File dasIstEs = Arrays.stream(potentialFiles)
                    .max((f1, f2) -> f1.getName().compareTo(f2.getName()))
                    .orElse(potentialFiles[0]);
                log.debug("Das ist es: " + dasIstEs);

                try {
                    outputString = new String(java.nio.file.Files.readAllBytes(dasIstEs.toPath()), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("Error reading file", e);
                    return new ArrayList<>();
                }
            } else {
                log.error("No backup files found");
                return new ArrayList<>();
            }
        } else {
            log.debug("lese von OeKB Server");
            outputString = OeKBHTTP.downloadAccessRules();
        }

        return parseAccessRules(outputString, false);
    }

    public List<AccessRule> getAccessRightsGivenFromOEKB() {
        String outputString;

        if (applicationSettings.isFileSystem()) {
            // Daten sollen aus Filesystem gelesen werden
            File backupDir = new File(applicationSettings.getBackupDirectory());
            File[] potentialFiles = backupDir.listFiles((dir, name) ->
                name.contains("DOWNLOAD_AR_ASSIGNED.xml"));

            log.debug("Potential files: " + (potentialFiles != null ? Arrays.toString(potentialFiles) : "none"));

            if (potentialFiles != null && potentialFiles.length > 0) {
                File dasIstEs = Arrays.stream(potentialFiles)
                    .max((f1, f2) -> f1.getName().compareTo(f2.getName()))
                    .orElse(potentialFiles[0]);
                log.debug("Das ist es: " + dasIstEs);

                try {
                    outputString = new String(java.nio.file.Files.readAllBytes(dasIstEs.toPath()), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("Error reading file", e);
                    return new ArrayList<>();
                }
            } else {
                log.error("No backup files found");
                return new ArrayList<>();
            }
        } else {
            outputString = OeKBHTTP.downloadGivenAccessRules();
        }

        return parseAccessRules(outputString, true);
    }

    private List<AccessRule> parseAccessRules(String xmlString, boolean includeDataSuppliers) {
        List<AccessRule> accessRules = new ArrayList<>();

        // Validate input before parsing
        if (xmlString == null || xmlString.trim().isEmpty()) {
            log.warn("Empty or null XML string received from server. Server may have returned an error or proxy blocked the connection.");
            return accessRules;
        }

        // Check if response looks like XML
        if (!xmlString.trim().startsWith("<")) {
            log.error("Server response does not appear to be XML. Response: {}",
                xmlString.length() > 200 ? xmlString.substring(0, 200) + "..." : xmlString);
            return accessRules;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));

            NodeList ruleNodes = doc.getElementsByTagName("AccessRule");

            for (int i = 0; i < ruleNodes.getLength(); i++) {
                Element ruleElement = (Element) ruleNodes.item(i);

                List<String> leiList = new ArrayList<>();
                List<String> oenbIdList = new ArrayList<>();
                List<String> isinSegmentList = new ArrayList<>();
                List<String> isinShareClassList = new ArrayList<>();

                NodeList accessObjects = ruleElement.getElementsByTagName("AccessObject");
                for (int j = 0; j < accessObjects.getLength(); j++) {
                    Element accessObject = (Element) accessObjects.item(j);

                    String lei = getElementText(accessObject, "LEI");
                    if (lei != null && !lei.isEmpty()) {
                        leiList.add(lei);
                    }

                    String oenbId = getElementText(accessObject, "OeNB_Identnr");
                    if (oenbId != null && !oenbId.isEmpty()) {
                        oenbIdList.add(oenbId);
                    }

                    NodeList shareClassNodes = accessObject.getElementsByTagName("ShareClass");
                    if (shareClassNodes.getLength() > 0) {
                        Element shareClass = (Element) shareClassNodes.item(0);
                        String isin = getElementText(shareClass, "ISIN");
                        if (isin != null && !isin.isEmpty()) {
                            isinShareClassList.add(isin);
                        }
                    }

                    NodeList segmentNodes = accessObject.getElementsByTagName("Segment");
                    if (segmentNodes.getLength() > 0) {
                        Element segment = (Element) segmentNodes.item(0);
                        String isin = getElementText(segment, "ISIN");
                        if (isin != null && !isin.isEmpty()) {
                            isinSegmentList.add(isin);
                        }
                    }
                }

                AccessRule rule = new AccessRule();
                rule.setId(ruleElement.getAttribute("id"));
                rule.setContentType(getElementText(ruleElement, "ContentType"));
                rule.setProfile(getElementText(ruleElement, "Profile"));

                String dataSupplierCreatorShort = getNestedElementText(ruleElement, "DataSupplier_Creator", "Short");
                String dataSupplierCreatorName = getNestedElementText(ruleElement, "DataSupplier_Creator", "Name");
                rule.setDataSupplierCreatorShort(dataSupplierCreatorShort);
                rule.setDataSupplierCreatorName(dataSupplierCreatorName);

                rule.setCreationTime(getElementText(ruleElement, "CreationTime"));
                rule.setAccessDelayInDays(getNestedElementText(ruleElement, "Schedule", "AccessDelayInDays"));
                rule.setDateFrom(getNestedElementText(ruleElement, "DataAccessRange", "DateFrom"));
                rule.setDateTo(getNestedElementText(ruleElement, "DataAccessRange", "DateTo"));
                rule.setFrequency(getNestedElementText(ruleElement, "DataAccessRange", "Frequency"));

                String costsByDataSupplier = getElementText(ruleElement, "CostsByDataSupplier");
                rule.setCostsByDataSupplier(costsByDataSupplier);

                rule.setLEI(leiList);
                rule.setOENB_ID(oenbIdList);
                rule.setISIN_SEGMENT(isinSegmentList);
                rule.setISIN_SHARECLASS(isinShareClassList);

                if (includeDataSuppliers) {
                    List<String> kagShort = new ArrayList<>();
                    NodeList dataSuppliers = ruleElement.getElementsByTagName("DataSupplier");
                    for (int j = 0; j < dataSuppliers.getLength(); j++) {
                        Element dataSupplier = (Element) dataSuppliers.item(j);
                        String shortName = getElementText(dataSupplier, "Short");
                        if (shortName != null && !shortName.isEmpty()) {
                            kagShort.add(shortName);
                        }
                    }
                    rule.setDataSuppliersGivenShort(kagShort);
                }

                accessRules.add(rule);
            }
        } catch (org.xml.sax.SAXParseException e) {
            log.error("XML parsing error: {}. This typically means the server returned invalid XML, " +
                "an empty response, or the proxy blocked the connection. Check credentials and network connection.",
                e.getMessage());
            log.debug("SAXParseException details", e);
        } catch (Exception e) {
            log.error("Unexpected error parsing access rules XML: {}. Check server connection and proxy settings.",
                e.getMessage(), e);
        }

        return accessRules;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent();
        }
        return null;
    }

    private String getNestedElementText(Element parent, String... tagNames) {
        Element current = parent;
        for (int i = 0; i < tagNames.length - 1; i++) {
            NodeList nodes = current.getElementsByTagName(tagNames[i]);
            if (nodes.getLength() > 0) {
                current = (Element) nodes.item(0);
            } else {
                return null;
            }
        }
        return getElementText(current, tagNames[tagNames.length - 1]);
    }

    public static String deleteRule(AccessRule rule) {
        log.debug("Delete Rule: " + rule.getId());

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("FundsXML_AccessRules");
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:noNamespaceSchemaLocation", "https://fdp-service.oekb.at/FundsXML_AccessRules_2.3.0.xsd");
            doc.appendChild(root);

            Element task = doc.createElement("Task");
            task.setTextContent("delete");
            root.appendChild(task);

            Element dataSupplier = doc.createElement("DataSupplier");
            root.appendChild(dataSupplier);

            Element shortElem = doc.createElement("Short");
            shortElem.setTextContent(rule.getDataSupplierCreatorShort());
            dataSupplier.appendChild(shortElem);

            Element nameElem = doc.createElement("Name");
            nameElem.setTextContent(rule.getDataSupplierCreatorName());
            dataSupplier.appendChild(nameElem);

            Element contact = doc.createElement("Contact");
            dataSupplier.appendChild(contact);

            Element contactName = doc.createElement("Name");
            contactName.setTextContent("Karl Kauc");
            contact.appendChild(contactName);

            Element phone = doc.createElement("Phone");
            phone.setTextContent("050100 19850");
            contact.appendChild(phone);

            Element email = doc.createElement("Email");
            email.setTextContent("karl.kauc@sparinvest.com");
            contact.appendChild(email);

            Element accessRule = doc.createElement("AccessRule");
            accessRule.setAttribute("id", rule.getId());
            root.appendChild(accessRule);

            Element contentType = doc.createElement("ContentType");
            contentType.setTextContent(rule.getContentType());
            accessRule.appendChild(contentType);

            Element dataSuppliers = doc.createElement("DataSuppliers");
            accessRule.appendChild(dataSuppliers);

            Element dataSupplierElem = doc.createElement("DataSupplier");
            dataSuppliers.appendChild(dataSupplierElem);

            for (String dds : rule.getDataSuppliersGivenShort()) {
                Element ddsShort = doc.createElement("Short");
                ddsShort.setTextContent(dds);
                dataSupplierElem.appendChild(ddsShort);
            }

            Element profiles = doc.createElement("Profiles");
            accessRule.appendChild(profiles);

            Element profile = doc.createElement("Profile");
            profile.setTextContent(rule.getProfile());
            profiles.appendChild(profile);

            Element accessObjects = doc.createElement("AccessObjects");
            accessRule.appendChild(accessObjects);

            for (String lei : rule.getLEI()) {
                Element accessObject = doc.createElement("AccessObject");
                accessObjects.appendChild(accessObject);

                Element fund = doc.createElement("Fund");
                accessObject.appendChild(fund);

                Element leiElem = doc.createElement("LEI");
                leiElem.setTextContent(lei);
                fund.appendChild(leiElem);
            }

            for (String oenbId : rule.getOENB_ID()) {
                Element accessObject = doc.createElement("AccessObject");
                accessObjects.appendChild(accessObject);

                Element fund = doc.createElement("Fund");
                accessObject.appendChild(fund);

                Element oenbIdElem = doc.createElement("OeNB_Identnr");
                oenbIdElem.setTextContent(oenbId);
                fund.appendChild(oenbIdElem);
            }

            for (String isinSegment : rule.getISIN_SEGMENT()) {
                Element accessObject = doc.createElement("AccessObject");
                accessObjects.appendChild(accessObject);

                Element segment = doc.createElement("Segment");
                accessObject.appendChild(segment);

                Element isinElem = doc.createElement("ISIN");
                isinElem.setTextContent(isinSegment);
                segment.appendChild(isinElem);
            }

            for (String isinShareClass : rule.getISIN_SHARECLASS()) {
                Element accessObject = doc.createElement("AccessObject");
                accessObjects.appendChild(accessObject);

                Element shareClass = doc.createElement("ShareClass");
                accessObject.appendChild(shareClass);

                Element isinElem = doc.createElement("ISIN");
                isinElem.setTextContent(isinShareClass);
                shareClass.appendChild(isinElem);
            }

            Element schedule = doc.createElement("Schedule");
            accessRule.appendChild(schedule);

            Element accessDelayInDays = doc.createElement("AccessDelayInDays");
            accessDelayInDays.setTextContent(rule.getAccessDelayInDays());
            schedule.appendChild(accessDelayInDays);

            Element dataAccessRange = doc.createElement("DataAccessRange");
            schedule.appendChild(dataAccessRange);

            Element dateFrom = doc.createElement("DateFrom");
            dateFrom.setTextContent(rule.getDateFrom());
            dataAccessRange.appendChild(dateFrom);

            Element dateTo = doc.createElement("DateTo");
            dateTo.setTextContent(rule.getDateTo());
            dataAccessRange.appendChild(dateTo);

            Element frequency = doc.createElement("Frequency");
            frequency.setTextContent(rule.getFrequency());
            dataAccessRange.appendChild(frequency);

            Element costsByDataSupplier = doc.createElement("CostsByDataSupplier");
            costsByDataSupplier.setTextContent(String.valueOf(rule.getCostsByDataSupplier()));
            accessRule.appendChild(costsByDataSupplier);

            // Convert to string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            String xmlString = writer.toString();
            log.debug(xmlString);
            return xmlString;

        } catch (Exception e) {
            log.error("Error creating delete rule XML", e);
            return "";
        }
    }

    public void deleteFundFromRule(RuleRow rule) {
        log.debug("im in deleteFundFromFule");
        log.debug("lösche Fund aus Rule: " + rule.getId() + ": " + rule.getLEI() + "/" +
                  rule.getOENB_ID() + "/" + rule.getSHARECLASS_ISIN() + "/" + rule.getSEGMENT_ISIN());

        List<AccessRule> given = getAccesRightsRecievedFromOEKB();
        AccessRule idToDelete = null;
        for (AccessRule ar : given) {
            if (ar.getId().equals(rule.getId())) {
                idToDelete = ar;
                break;
            }
        }

        if (idToDelete != null) {
            log.debug("LEI vorher: " + idToDelete.getLEI());
            idToDelete.getLEI().remove(rule.getLEI());
            log.debug("LEI nachher: " + idToDelete.getLEI());

            String newRule1 = deleteRule(idToDelete);
            newRule(newRule1);
        }
    }

    public void newRule(String newRule) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(newRule.getBytes(StandardCharsets.UTF_8)));

            NodeList taskNodes = doc.getElementsByTagName("Task");
            if (taskNodes.getLength() > 0) {
                Element task = (Element) taskNodes.item(0);
                task.setTextContent("import");
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            log.debug(writer.toString());
        } catch (Exception e) {
            log.error("Error modifying new rule", e);
        }
    }

    /**
     * Upload access rule XML string to server
     * Creates a temporary file from the XML string and uploads it
     *
     * @param xmlContent XML string to upload
     * @return Server response or success/error message
     */
    public static String uploadAccessRuleXml(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            log.error("Cannot upload empty XML content");
            return "ERROR: XML content is empty";
        }

        File tempFile = null;
        try {
            // Create temporary file with timestamp to ensure uniqueness
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            tempFile = File.createTempFile("access_rule_" + timestamp + "_", ".xml");

            // Write XML content to file
            java.nio.file.Files.write(tempFile.toPath(),
                    xmlContent.getBytes(StandardCharsets.UTF_8));

            log.info("Created temporary file for upload: {}", tempFile.getAbsolutePath());

            // Upload the file
            String result = OeKBHTTP.uploadAccessRule(tempFile);

            // Check if upload was successful
            if (result != null && (result.contains("SUCCESS") || result.contains("OK"))) {
                log.info("Access rule uploaded successfully");
            } else if (result != null && result.startsWith("ERROR")) {
                log.error("Upload failed: {}", result);
            }

            return result;

        } catch (Exception e) {
            log.error("Error uploading access rule XML", e);
            return "ERROR: " + e.getMessage();
        } finally {
            // Clean up temporary file
            if (tempFile != null && tempFile.exists()) {
                try {
                    tempFile.delete();
                    log.debug("Deleted temporary file: {}", tempFile.getAbsolutePath());
                } catch (Exception e) {
                    log.warn("Could not delete temporary file: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }
}
