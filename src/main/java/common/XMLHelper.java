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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class XMLHelper {
    private static final Logger log = LogManager.getLogger(XMLHelper.class);

    /**
     * Creates a DocumentBuilderFactory with XXE protections enabled.
     * Use this instead of DocumentBuilderFactory.newInstance() everywhere.
     */
    public static DocumentBuilderFactory createSecureDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory;
    }

    /**
     * Creates a TransformerFactory with secure processing enabled.
     */
    public static TransformerFactory createSecureTransformerFactory() {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException e) {
            log.warn("TransformerFactory does not support external access restriction", e);
        }
        return tf;
    }

    public enum FileTypes {
        OFI,
        ACCESS_RIGHTS,
        FUND_DATA
    }

    public static FileTypes getFileType(String fileData) {
        FileTypes detectedType = null;
        try {
            DocumentBuilderFactory factory = createSecureDocumentBuilderFactory();
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
            DocumentBuilderFactory factory = createSecureDocumentBuilderFactory();
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
