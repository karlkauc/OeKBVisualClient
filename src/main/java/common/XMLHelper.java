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
package common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class XMLHelper {
    private static final Logger log = LogManager.getLogger(XMLHelper.class);

    public enum FileTypes {
        OFI,
        ACCESS_RIGHTS,
        FUND_DATA
    }

    public static FileTypes getFileType(String fileData) {
        FileTypes detectedType = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(fileData.getBytes(StandardCharsets.UTF_8)));

            // First, check for the specific OFI case
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = "//Funds/Fund/CountrySpecificData/AT/OeNB/Meldungstyp";
            NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

            if (nodes.getLength() > 0) {
                String meldungstyp = nodes.item(0).getTextContent();
                if ("OFI".equals(meldungstyp)) {
                    log.debug("OFI fund file detected by Meldungstyp.");
                    detectedType = FileTypes.OFI;
                }
            }

            // If not OFI, check the root element for other types
            if (detectedType == null) {
                String rootElement = doc.getDocumentElement().getTagName();
                log.debug("XML root element is <{}>", rootElement);

                if (rootElement.contains("AccessRules")) {
                    detectedType = FileTypes.ACCESS_RIGHTS;
                } else if (rootElement.contains("Funds")) {
                    // This is the default for fund data that is not OFI
                    detectedType = FileTypes.FUND_DATA;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing XML to determine file type", e);
            // Re-throw as a runtime exception to signal a failure in processing
            throw new RuntimeException("Failed to determine XML file type", e);
        }

        if (detectedType != null) {
            return detectedType;
        }

        // If we reach here, the file type is unknown.
        throw new IllegalArgumentException("Unknown XML file type");
    }

    public static boolean isOfiFile(String fileData) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(fileData.getBytes(StandardCharsets.UTF_8)));

            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = "//Funds/Fund/CountrySpecificData/AT/OeNB/Meldungstyp";
            NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

            if (nodes.getLength() > 0) {
                String meldungstyp = nodes.item(0).getTextContent();
                if ("OFI".equals(meldungstyp)) {
                    log.debug("Ofi Fonds gefunden");
                    return true;
                }
            }
            log.debug("KEIN Ofi Fonds gefunden");
            return false;
        } catch (Exception e) {
            log.error("Error checking if file is OFI", e);
            return false;
        }
    }

    public static boolean isOfiResponseOk(String fileData) {
        return true;
    }
}
