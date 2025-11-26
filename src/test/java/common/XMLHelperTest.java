package common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class XMLHelperTest {

    @Test
    @DisplayName("Should detect OFI file from specific Meldungstyp")
    void testGetFileType_OFI() {
        String ofiXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<Funds>" +
                        "  <Fund>" +
                        "    <CountrySpecificData>" +
                        "      <AT>" +
                        "        <OeNB>" +
                        "          <Meldungstyp>OFI</Meldungstyp>" +
                        "        </OeNB>" +
                        "      </AT>" +
                        "    </CountrySpecificData>" +
                        "  </Fund>" +
                        "</Funds>";
        assertEquals(XMLHelper.FileTypes.OFI, XMLHelper.getFileType(ofiXml));
    }

    @Test
    @DisplayName("Should detect FUND_DATA for standard Funds XML")
    void testGetFileType_FundData() {
        String fundDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                             "<Funds>" +
                             "  <Fund></Fund>" +
                             "</Funds>";
        assertEquals(XMLHelper.FileTypes.FUND_DATA, XMLHelper.getFileType(fundDataXml));
    }

    @Test
    @DisplayName("Should detect ACCESS_RIGHTS from root element")
    void testGetFileType_AccessRights() {
        String accessRightsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                 "<AccessRules>" +
                                 "  <Rule></Rule>" +
                                 "</AccessRules>";
        assertEquals(XMLHelper.FileTypes.ACCESS_RIGHTS, XMLHelper.getFileType(accessRightsXml));
    }

    @Test
    @DisplayName("Should throw exception for unknown XML type")
    void testGetFileType_Unknown() {
        String unknownXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "<UnknownType>" +
                            "  <Data></Data>" +
                            "</UnknownType>";
        
        assertThrows(IllegalArgumentException.class, () -> {
            XMLHelper.getFileType(unknownXml);
        });
    }

    @Test
    @DisplayName("Should throw exception for malformed XML")
    void testGetFileType_MalformedXML() {
        String malformedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                              "<Funds>" +
                              "  <Fund>"; // Missing closing tags
        
        assertThrows(RuntimeException.class, () -> {
            XMLHelper.getFileType(malformedXml);
        });
    }
}
