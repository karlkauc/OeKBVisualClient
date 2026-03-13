package dao;

import model.AccessRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AccesRights XML parsing. Tests the package-private
 * parseAccessRules method and the static deleteRule method directly, without
 * network or filesystem interaction.
 */
class AccesRightsParseTest {

    private AccesRights accesRights;

    @BeforeEach
    void setUp() {
        accesRights = new AccesRights();
    }

    // ---------------------------------------------------------------
    // parseAccessRules - null / empty / invalid input
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - null/empty/invalid input")
    class InvalidInputTests {

        @Test
        @DisplayName("null XML returns empty list")
        void nullXmlReturnsEmptyList() {
            List<AccessRule> result = accesRights.parseAccessRules(null, false);
            assertNotNull(result, "Result should never be null");
            assertTrue(result.isEmpty(), "Null XML should produce empty list");
        }

        @Test
        @DisplayName("empty string returns empty list")
        void emptyStringReturnsEmptyList() {
            List<AccessRule> result = accesRights.parseAccessRules("", false);
            assertTrue(result.isEmpty(), "Empty string should produce empty list");
        }

        @Test
        @DisplayName("blank string returns empty list")
        void blankStringReturnsEmptyList() {
            List<AccessRule> result = accesRights.parseAccessRules("   \t\n  ", false);
            assertTrue(result.isEmpty(), "Blank string should produce empty list");
        }

        @Test
        @DisplayName("non-XML string returns empty list")
        void nonXmlStringReturnsEmptyList() {
            List<AccessRule> result = accesRights.parseAccessRules("This is not XML at all", false);
            assertTrue(result.isEmpty(), "Non-XML string should produce empty list");
        }

        @Test
        @DisplayName("malformed XML returns empty list without exception")
        void malformedXmlReturnsEmptyList() {
            List<AccessRule> result = accesRights.parseAccessRules("<broken><xml", false);
            assertTrue(result.isEmpty(), "Malformed XML should produce empty list");
        }

        @Test
        @DisplayName("valid XML with no AccessRule elements returns empty list")
        void validXmlNoAccessRulesReturnsEmptyList() {
            String xml = "<?xml version=\"1.0\"?><Root><Other>data</Other></Root>";
            List<AccessRule> result = accesRights.parseAccessRules(xml, false);
            assertTrue(result.isEmpty(), "XML without AccessRule elements should produce empty list");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - single AccessRule with various fields
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - single AccessRule parsing")
    class SingleAccessRuleTests {

        @Test
        @DisplayName("parses id attribute and ContentType")
        void parsesIdAndContentType() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"AR-001\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size(), "Should parse exactly one rule");
            assertEquals("AR-001", result.get(0).getId(), "Should parse id attribute");
            assertEquals("FUND", result.get(0).getContentType(), "Should parse ContentType");
        }

        @Test
        @DisplayName("parses LEI from AccessObject")
        void parsesLei() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R1\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessObjects>"
                    + "      <AccessObject>"
                    + "        <Fund><LEI>529900HNOAA1KXQJUQ27</LEI></Fund>"
                    + "      </AccessObject>"
                    + "    </AccessObjects>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> leis = result.get(0).getLei();
            assertNotNull(leis, "LEI list should not be null");
            assertEquals(1, leis.size(), "Should have one LEI");
            assertEquals("529900HNOAA1KXQJUQ27", leis.get(0), "LEI value should match");
        }

        @Test
        @DisplayName("parses OeNB_Identnr from AccessObject")
        void parsesOenbIdentnr() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R2\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessObjects>"
                    + "      <AccessObject>"
                    + "        <Fund><OeNB_Identnr>12345</OeNB_Identnr></Fund>"
                    + "      </AccessObject>"
                    + "    </AccessObjects>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> oenbIds = result.get(0).getOenbId();
            assertNotNull(oenbIds, "OeNB ID list should not be null");
            assertEquals(1, oenbIds.size(), "Should have one OeNB ID");
            assertEquals("12345", oenbIds.get(0), "OeNB ID value should match");
        }

        @Test
        @DisplayName("parses ShareClass ISIN from AccessObject")
        void parsesShareClassIsin() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R3\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessObjects>"
                    + "      <AccessObject>"
                    + "        <ShareClass><ISIN>AT0000A0ABC1</ISIN></ShareClass>"
                    + "      </AccessObject>"
                    + "    </AccessObjects>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> isinShareClass = result.get(0).getIsinShareclass();
            assertNotNull(isinShareClass, "ShareClass ISIN list should not be null");
            assertEquals(1, isinShareClass.size(), "Should have one ShareClass ISIN");
            assertEquals("AT0000A0ABC1", isinShareClass.get(0), "ShareClass ISIN should match");
        }

        @Test
        @DisplayName("parses Segment ISIN from AccessObject")
        void parsesSegmentIsin() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R4\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessObjects>"
                    + "      <AccessObject>"
                    + "        <Segment><ISIN>AT0000A0SEG9</ISIN></Segment>"
                    + "      </AccessObject>"
                    + "    </AccessObjects>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> isinSegments = result.get(0).getIsinSegment();
            assertNotNull(isinSegments, "Segment ISIN list should not be null");
            assertEquals(1, isinSegments.size(), "Should have one Segment ISIN");
            assertEquals("AT0000A0SEG9", isinSegments.get(0), "Segment ISIN should match");
        }

        @Test
        @DisplayName("parses CreationTime")
        void parsesCreationTime() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R5\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <CreationTime>2024-01-15T10:30:00</CreationTime>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            assertEquals("2024-01-15T10:30:00", result.get(0).getCreationTime(),
                    "CreationTime should be parsed correctly");
        }

        @Test
        @DisplayName("parses Schedule AccessDelayInDays")
        void parsesScheduleAccessDelay() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R6\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Schedule>"
                    + "      <AccessDelayInDays>5</AccessDelayInDays>"
                    + "    </Schedule>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            assertEquals("5", result.get(0).getAccessDelayInDays(),
                    "AccessDelayInDays should be parsed from nested Schedule element");
        }

        @Test
        @DisplayName("parses DataAccessRange fields")
        void parsesDataAccessRange() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R7\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <DataAccessRange>"
                    + "      <DateFrom>2023-01-01</DateFrom>"
                    + "      <DateTo>2025-12-31</DateTo>"
                    + "      <Frequency>daily</Frequency>"
                    + "    </DataAccessRange>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            AccessRule rule = result.get(0);
            assertEquals("2023-01-01", rule.getDateFrom(), "DateFrom should be parsed");
            assertEquals("2025-12-31", rule.getDateTo(), "DateTo should be parsed");
            assertEquals("daily", rule.getFrequency(), "Frequency should be parsed");
        }

        @Test
        @DisplayName("parses DataSupplier_Creator Short and Name")
        void parsesDataSupplierCreator() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R8\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <DataSupplier_Creator>"
                    + "      <Short>KAGABC</Short>"
                    + "      <Name>Test KAG GmbH</Name>"
                    + "    </DataSupplier_Creator>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            AccessRule rule = result.get(0);
            assertEquals("KAGABC", rule.getDataSupplierCreatorShort(),
                    "DataSupplier_Creator Short should be parsed");
            assertEquals("Test KAG GmbH", rule.getDataSupplierCreatorName(),
                    "DataSupplier_Creator Name should be parsed");
        }

        @Test
        @DisplayName("parses CostsByDataSupplier")
        void parsesCostsByDataSupplier() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"R9\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <CostsByDataSupplier>true</CostsByDataSupplier>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            assertEquals("true", result.get(0).getCostsByDataSupplier(),
                    "CostsByDataSupplier should be parsed");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - profiles
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - Profile parsing")
    class ProfileTests {

        @Test
        @DisplayName("parses single Profile")
        void parsesSingleProfile() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"P1\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Profiles>"
                    + "      <Profile>FundsXML4</Profile>"
                    + "    </Profiles>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> profiles = result.get(0).getProfiles();
            assertNotNull(profiles, "Profiles list should not be null");
            assertEquals(1, profiles.size(), "Should have one profile");
            assertEquals("FundsXML4", profiles.get(0), "Profile name should match");
        }

        @Test
        @DisplayName("parses multiple Profiles")
        void parsesMultipleProfiles() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"P2\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Profiles>"
                    + "      <Profile>FundsXML4</Profile>"
                    + "      <Profile>FundsXML_FULL</Profile>"
                    + "      <Profile>FundsXML_OeKB</Profile>"
                    + "    </Profiles>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> profiles = result.get(0).getProfiles();
            assertEquals(3, profiles.size(), "Should have three profiles");
            assertTrue(profiles.contains("FundsXML4"), "Should contain FundsXML4");
            assertTrue(profiles.contains("FundsXML_FULL"), "Should contain FundsXML_FULL");
            assertTrue(profiles.contains("FundsXML_OeKB"), "Should contain FundsXML_OeKB");
        }

        @Test
        @DisplayName("empty Profiles element yields empty list")
        void emptyProfilesElement() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"P3\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Profiles/>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> profiles = result.get(0).getProfiles();
            assertNotNull(profiles, "Profiles should not be null");
            assertTrue(profiles.isEmpty(), "Empty Profiles element should yield empty list");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - DocumentTypes and RegulatoryReportings
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - DocumentTypes and RegulatoryReportings")
    class DocTypeAndRegRepTests {

        @Test
        @DisplayName("parses DocumentType elements")
        void parsesDocumentTypes() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"D1\">"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <DocumentTypes>"
                    + "      <DocumentType>AnnualReport</DocumentType>"
                    + "      <DocumentType>KID</DocumentType>"
                    + "      <DocumentType>Prospectus</DocumentType>"
                    + "    </DocumentTypes>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> docTypes = result.get(0).getDocumentTypes();
            assertNotNull(docTypes, "DocumentTypes list should not be null");
            assertEquals(3, docTypes.size(), "Should have three document types");
            assertTrue(docTypes.contains("AnnualReport"), "Should contain AnnualReport");
            assertTrue(docTypes.contains("KID"), "Should contain KID");
            assertTrue(docTypes.contains("Prospectus"), "Should contain Prospectus");
        }

        @Test
        @DisplayName("parses RegulatoryReporting elements")
        void parsesRegulatoryReportings() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"REG1\">"
                    + "    <ContentType>REG</ContentType>"
                    + "    <RegulatoryReportings>"
                    + "      <RegulatoryReporting>EMIR</RegulatoryReporting>"
                    + "      <RegulatoryReporting>PRIIPS</RegulatoryReporting>"
                    + "    </RegulatoryReportings>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> regReps = result.get(0).getRegulatoryReportings();
            assertNotNull(regReps, "RegulatoryReportings list should not be null");
            assertEquals(2, regReps.size(), "Should have two regulatory reportings");
            assertTrue(regReps.contains("EMIR"), "Should contain EMIR");
            assertTrue(regReps.contains("PRIIPS"), "Should contain PRIIPS");
        }

        @Test
        @DisplayName("no DocumentTypes element yields empty list")
        void noDocumentTypesYieldsEmptyList() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"D2\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            List<String> docTypes = result.get(0).getDocumentTypes();
            assertNotNull(docTypes, "DocumentTypes should not be null even when missing from XML");
            assertTrue(docTypes.isEmpty(), "Missing DocumentTypes should yield empty list");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - includeDataSuppliers flag
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - includeDataSuppliers flag")
    class DataSuppliersTests {

        @Test
        @DisplayName("includeDataSuppliers=true populates DataSuppliersGivenShort")
        void includeDataSuppliersTrue() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"DS1\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>KAG001</Short></DataSupplier>"
                    + "      <DataSupplier><Short>KAG002</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, true);

            assertEquals(1, result.size());
            List<String> dsShorts = result.get(0).getDataSuppliersGivenShort();
            assertNotNull(dsShorts, "DataSuppliersGivenShort should not be null when includeDataSuppliers=true");
            assertEquals(2, dsShorts.size(), "Should have two data supplier shorts");
            assertTrue(dsShorts.contains("KAG001"), "Should contain KAG001");
            assertTrue(dsShorts.contains("KAG002"), "Should contain KAG002");
        }

        @Test
        @DisplayName("includeDataSuppliers=false does not populate DataSuppliersGivenShort")
        void includeDataSuppliersFalse() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"DS2\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>KAG001</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            assertNull(result.get(0).getDataSuppliersGivenShort(),
                    "DataSuppliersGivenShort should remain null when includeDataSuppliers=false");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - multiple AccessRules
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - multiple rules")
    class MultipleRulesTests {

        @Test
        @DisplayName("parses multiple AccessRule elements")
        void parsesMultipleRules() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"M1\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Profiles><Profile>Profile_A</Profile></Profiles>"
                    + "  </AccessRule>"
                    + "  <AccessRule id=\"M2\">"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <Profiles><Profile>Profile_B</Profile></Profiles>"
                    + "  </AccessRule>"
                    + "  <AccessRule id=\"M3\">"
                    + "    <ContentType>REG</ContentType>"
                    + "    <Profiles><Profile>Profile_C</Profile></Profiles>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(3, result.size(), "Should parse all three rules");
            assertEquals("M1", result.get(0).getId());
            assertEquals("M2", result.get(1).getId());
            assertEquals("M3", result.get(2).getId());
            assertEquals("FUND", result.get(0).getContentType());
            assertEquals("DOC", result.get(1).getContentType());
            assertEquals("REG", result.get(2).getContentType());
        }

        @Test
        @DisplayName("parses multiple AccessObjects with LEI and OeNB in one rule")
        void parsesMultipleAccessObjects() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"MO1\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessObjects>"
                    + "      <AccessObject><Fund><LEI>LEI_ONE</LEI></Fund></AccessObject>"
                    + "      <AccessObject><Fund><LEI>LEI_TWO</LEI><OeNB_Identnr>99999</OeNB_Identnr></Fund></AccessObject>"
                    + "      <AccessObject><ShareClass><ISIN>AT0000SC1111</ISIN></ShareClass></AccessObject>"
                    + "      <AccessObject><Segment><ISIN>AT0000SG2222</ISIN></Segment></AccessObject>"
                    + "    </AccessObjects>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            AccessRule rule = result.get(0);
            assertEquals(2, rule.getLei().size(), "Should have two LEIs");
            assertEquals(1, rule.getOenbId().size(), "Should have one OeNB ID");
            assertEquals(1, rule.getIsinShareclass().size(), "Should have one ShareClass ISIN");
            assertEquals(1, rule.getIsinSegment().size(), "Should have one Segment ISIN");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - edge cases
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseAccessRules - edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("empty AccessObject elements produce empty identifier lists")
        void emptyAccessObjectProducesEmptyLists() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"E1\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessObjects>"
                    + "      <AccessObject/>"
                    + "    </AccessObjects>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            AccessRule rule = result.get(0);
            assertTrue(rule.getLei().isEmpty(), "LEI list should be empty for empty AccessObject");
            assertTrue(rule.getOenbId().isEmpty(), "OeNB ID list should be empty for empty AccessObject");
            assertTrue(rule.getIsinShareclass().isEmpty(), "ShareClass ISINs should be empty");
            assertTrue(rule.getIsinSegment().isEmpty(), "Segment ISINs should be empty");
        }

        @Test
        @DisplayName("missing optional nested elements yield null values")
        void missingOptionalElementsYieldNull() {
            String xml = "<FundsXML_AccessRules>"
                    + "  <AccessRule id=\"E2\">"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </AccessRule>"
                    + "</FundsXML_AccessRules>";

            List<AccessRule> result = accesRights.parseAccessRules(xml, false);

            assertEquals(1, result.size());
            AccessRule rule = result.get(0);
            assertNull(rule.getCreationTime(), "CreationTime should be null when missing");
            assertNull(rule.getAccessDelayInDays(), "AccessDelayInDays should be null when missing");
            assertNull(rule.getDateFrom(), "DateFrom should be null when missing");
            assertNull(rule.getDateTo(), "DateTo should be null when missing");
            assertNull(rule.getFrequency(), "Frequency should be null when missing");
            assertNull(rule.getDataSupplierCreatorShort(),
                    "DataSupplierCreatorShort should be null when missing");
        }
    }

    // ---------------------------------------------------------------
    // parseAccessRules - comprehensive full XML
    // ---------------------------------------------------------------

    @Test
    @DisplayName("parses comprehensive AccessRule with all fields populated")
    void parsesComprehensiveRule() {
        String xml = "<FundsXML_AccessRules>"
                + "  <AccessRule id=\"FULL-001\">"
                + "    <ContentType>FUND</ContentType>"
                + "    <DataSupplier_Creator>"
                + "      <Short>SPARINVEST</Short>"
                + "      <Name>Sparinvest KAG</Name>"
                + "    </DataSupplier_Creator>"
                + "    <CreationTime>2024-06-15T08:00:00</CreationTime>"
                + "    <Profiles>"
                + "      <Profile>FundsXML4</Profile>"
                + "      <Profile>FundsXML_OeKB</Profile>"
                + "    </Profiles>"
                + "    <AccessObjects>"
                + "      <AccessObject>"
                + "        <Fund><LEI>529900HNOAA1KXQJUQ27</LEI><OeNB_Identnr>54321</OeNB_Identnr></Fund>"
                + "      </AccessObject>"
                + "      <AccessObject>"
                + "        <ShareClass><ISIN>AT0000A1B2C3</ISIN></ShareClass>"
                + "      </AccessObject>"
                + "      <AccessObject>"
                + "        <Segment><ISIN>AT0000XSEG01</ISIN></Segment>"
                + "      </AccessObject>"
                + "    </AccessObjects>"
                + "    <Schedule>"
                + "      <AccessDelayInDays>3</AccessDelayInDays>"
                + "    </Schedule>"
                + "    <DataAccessRange>"
                + "      <DateFrom>2023-01-01</DateFrom>"
                + "      <DateTo>2025-12-31</DateTo>"
                + "      <Frequency>daily</Frequency>"
                + "    </DataAccessRange>"
                + "    <CostsByDataSupplier>false</CostsByDataSupplier>"
                + "    <DocumentTypes>"
                + "      <DocumentType>KID</DocumentType>"
                + "    </DocumentTypes>"
                + "    <RegulatoryReportings>"
                + "      <RegulatoryReporting>EMIR</RegulatoryReporting>"
                + "    </RegulatoryReportings>"
                + "  </AccessRule>"
                + "</FundsXML_AccessRules>";

        List<AccessRule> result = accesRights.parseAccessRules(xml, false);

        assertEquals(1, result.size());
        AccessRule rule = result.get(0);

        assertEquals("FULL-001", rule.getId());
        assertEquals("FUND", rule.getContentType());
        assertEquals("SPARINVEST", rule.getDataSupplierCreatorShort());
        assertEquals("Sparinvest KAG", rule.getDataSupplierCreatorName());
        assertEquals("2024-06-15T08:00:00", rule.getCreationTime());
        assertEquals(2, rule.getProfiles().size());
        assertEquals(1, rule.getLei().size());
        assertEquals("529900HNOAA1KXQJUQ27", rule.getLei().get(0));
        assertEquals(1, rule.getOenbId().size());
        assertEquals("54321", rule.getOenbId().get(0));
        assertEquals(1, rule.getIsinShareclass().size());
        assertEquals("AT0000A1B2C3", rule.getIsinShareclass().get(0));
        assertEquals(1, rule.getIsinSegment().size());
        assertEquals("AT0000XSEG01", rule.getIsinSegment().get(0));
        assertEquals("3", rule.getAccessDelayInDays());
        assertEquals("2023-01-01", rule.getDateFrom());
        assertEquals("2025-12-31", rule.getDateTo());
        assertEquals("daily", rule.getFrequency());
        assertEquals("false", rule.getCostsByDataSupplier());
        assertEquals(1, rule.getDocumentTypes().size());
        assertEquals("KID", rule.getDocumentTypes().get(0));
        assertEquals(1, rule.getRegulatoryReportings().size());
        assertEquals("EMIR", rule.getRegulatoryReportings().get(0));
    }

    // ---------------------------------------------------------------
    // deleteRule - static method tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("deleteRule - XML generation")
    class DeleteRuleTests {

        @Test
        @DisplayName("deleteRule produces valid XML with Task=delete")
        void deleteRuleProducesValidXml() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertNotNull(xml, "deleteRule should return non-null XML");
            assertFalse(xml.isEmpty(), "deleteRule should return non-empty XML");
            assertTrue(xml.contains("<Task>delete</Task>"), "XML should contain Task=delete");
            assertTrue(xml.contains("FundsXML_AccessRules"), "XML should have root element FundsXML_AccessRules");
        }

        @Test
        @DisplayName("deleteRule includes rule id attribute")
        void deleteRuleIncludesId() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("id=\"TEST-99\""), "XML should contain the rule id attribute");
        }

        @Test
        @DisplayName("deleteRule includes ContentType")
        void deleteRuleIncludesContentType() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<ContentType>FUND</ContentType>"),
                    "XML should contain ContentType element");
        }

        @Test
        @DisplayName("deleteRule includes DataSupplier Short and Name")
        void deleteRuleIncludesDataSupplier() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<Short>TESTKAG</Short>"), "XML should contain DataSupplier Short");
            assertTrue(xml.contains("<Name>Test KAG</Name>"), "XML should contain DataSupplier Name");
        }

        @Test
        @DisplayName("deleteRule includes LEI AccessObjects")
        void deleteRuleIncludesLei() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<LEI>LEI_ALPHA</LEI>"), "XML should contain LEI value");
        }

        @Test
        @DisplayName("deleteRule includes OeNB_Identnr AccessObjects")
        void deleteRuleIncludesOenbId() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<OeNB_Identnr>11111</OeNB_Identnr>"),
                    "XML should contain OeNB_Identnr value");
        }

        @Test
        @DisplayName("deleteRule includes Segment and ShareClass ISINs")
        void deleteRuleIncludesIsins() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<ISIN>AT_SEG_001</ISIN>"), "XML should contain Segment ISIN");
            assertTrue(xml.contains("<ISIN>AT_SC_002</ISIN>"), "XML should contain ShareClass ISIN");
        }

        @Test
        @DisplayName("deleteRule includes Profiles")
        void deleteRuleIncludesProfiles() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<Profile>FundsXML4</Profile>"), "XML should contain profile");
            assertTrue(xml.contains("<Profile>OeKB_Profile</Profile>"), "XML should contain second profile");
        }

        @Test
        @DisplayName("deleteRule includes DocumentTypes when present")
        void deleteRuleIncludesDocumentTypes() {
            AccessRule rule = createTestRule();
            rule.setDocumentTypes(Arrays.asList("AnnualReport", "KID"));

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<DocumentType>AnnualReport</DocumentType>"),
                    "XML should contain DocumentType AnnualReport");
            assertTrue(xml.contains("<DocumentType>KID</DocumentType>"),
                    "XML should contain DocumentType KID");
        }

        @Test
        @DisplayName("deleteRule includes RegulatoryReportings when present")
        void deleteRuleIncludesRegulatoryReportings() {
            AccessRule rule = createTestRule();
            rule.setRegulatoryReportings(Arrays.asList("EMIR", "PRIIPS"));

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<RegulatoryReporting>EMIR</RegulatoryReporting>"),
                    "XML should contain RegulatoryReporting EMIR");
            assertTrue(xml.contains("<RegulatoryReporting>PRIIPS</RegulatoryReporting>"),
                    "XML should contain RegulatoryReporting PRIIPS");
        }

        @Test
        @DisplayName("deleteRule includes Schedule and DataAccessRange")
        void deleteRuleIncludesSchedule() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<AccessDelayInDays>7</AccessDelayInDays>"),
                    "XML should contain AccessDelayInDays");
            assertTrue(xml.contains("<DateFrom>2024-01-01</DateFrom>"),
                    "XML should contain DateFrom");
            assertTrue(xml.contains("<DateTo>2024-12-31</DateTo>"),
                    "XML should contain DateTo");
            assertTrue(xml.contains("<Frequency>monthly</Frequency>"),
                    "XML should contain Frequency");
        }

        @Test
        @DisplayName("deleteRule includes DataSuppliers/DataSupplier/Short for given suppliers")
        void deleteRuleIncludesGivenDataSuppliers() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            assertTrue(xml.contains("<DataSuppliers>"), "XML should contain DataSuppliers element");
            assertTrue(xml.contains("<Short>DDS_A</Short>"), "XML should contain given DataSupplier short DDS_A");
            assertTrue(xml.contains("<Short>DDS_B</Short>"), "XML should contain given DataSupplier short DDS_B");
        }

        @Test
        @DisplayName("deleteRule output can be re-parsed by parseAccessRules")
        void deleteRuleOutputIsReparsable() {
            AccessRule rule = createTestRule();

            String xml = AccesRights.deleteRule(rule);

            // The generated XML should be parseable
            List<AccessRule> reparsed = accesRights.parseAccessRules(xml, false);
            assertEquals(1, reparsed.size(), "Re-parsed XML should yield one rule");
            assertEquals("TEST-99", reparsed.get(0).getId(), "Re-parsed rule should have same id");
            assertEquals("FUND", reparsed.get(0).getContentType(), "Re-parsed rule should have same ContentType");
        }

        private AccessRule createTestRule() {
            AccessRule rule = new AccessRule();
            rule.setId("TEST-99");
            rule.setContentType("FUND");
            rule.setDataSupplierCreatorShort("TESTKAG");
            rule.setDataSupplierCreatorName("Test KAG");
            rule.setProfiles(Arrays.asList("FundsXML4", "OeKB_Profile"));
            rule.setLei(Arrays.asList("LEI_ALPHA"));
            rule.setOenbId(Arrays.asList("11111"));
            rule.setIsinSegment(Arrays.asList("AT_SEG_001"));
            rule.setIsinShareclass(Arrays.asList("AT_SC_002"));
            rule.setDataSuppliersGivenShort(Arrays.asList("DDS_A", "DDS_B"));
            rule.setAccessDelayInDays("7");
            rule.setDateFrom("2024-01-01");
            rule.setDateTo("2024-12-31");
            rule.setFrequency("monthly");
            rule.setCostsByDataSupplier("true");
            return rule;
        }
    }
}
