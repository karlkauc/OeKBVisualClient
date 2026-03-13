package dao;

import model.DownloadedInformationEntry;
import model.NewInformationEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NewInformation and DownloadedInformation XML parsing. Tests
 * the package-private parseNewInformationXml and parseDownloadedInformationXml
 * methods directly.
 */
class NewInfoAndDownloadedInfoParseTest {

    private NewInformation newInformation;
    private DownloadedInformation downloadedInformation;

    @BeforeEach
    void setUp() {
        newInformation = new NewInformation();
        downloadedInformation = new DownloadedInformation();
    }

    // =============================================================
    // NewInformation.parseNewInformationXml
    // =============================================================

    @Nested
    @DisplayName("NewInformation - null/empty/invalid input")
    class NewInfoInvalidInputTests {

        @Test
        @DisplayName("null XML returns empty list")
        void nullXmlReturnsEmptyList() {
            List<NewInformationEntry> result = newInformation.parseNewInformationXml(null);
            assertNotNull(result, "Result should never be null");
            assertTrue(result.isEmpty(), "Null XML should produce empty list");
        }

        @Test
        @DisplayName("empty string returns empty list")
        void emptyStringReturnsEmptyList() {
            List<NewInformationEntry> result = newInformation.parseNewInformationXml("");
            assertTrue(result.isEmpty(), "Empty string should produce empty list");
        }

        @Test
        @DisplayName("blank string returns empty list")
        void blankStringReturnsEmptyList() {
            List<NewInformationEntry> result = newInformation.parseNewInformationXml("   \t\n  ");
            assertTrue(result.isEmpty(), "Blank string should produce empty list");
        }

        @Test
        @DisplayName("malformed XML returns empty list without exception")
        void malformedXmlReturnsEmptyList() {
            List<NewInformationEntry> result = newInformation.parseNewInformationXml("<broken><xml");
            assertTrue(result.isEmpty(), "Malformed XML should produce empty list");
        }
    }

    @Nested
    @DisplayName("NewInformation - FUND content type")
    class NewInfoFundTests {

        @Test
        @DisplayName("parses FUND entry with LEI and ISINs")
        void parsesFundWithLeiAndIsins() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-06-15</ContentDate>"
                    + "    <UploadDateTime>2024-06-15T10:30:00</UploadDateTime>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>KAGABC</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "    <FundOrShareclass>"
                    + "      <LEI>529900HNOAA1KXQJUQ27</LEI>"
                    + "      <ISIN>AT0000A0ABC1</ISIN>"
                    + "      <ISIN>AT0000A0ABC2</ISIN>"
                    + "    </FundOrShareclass>"
                    + "    <Profiles>"
                    + "      <Profile>FundsXML4</Profile>"
                    + "    </Profiles>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size(), "Should parse one entry");
            NewInformationEntry entry = result.get(0);

            assertEquals(NewInformationEntry.ContentType.FUND, entry.getContentType(),
                    "ContentType should be FUND");
            assertEquals(LocalDate.of(2024, 6, 15), entry.getContentDate(),
                    "ContentDate should be parsed");
            assertEquals(LocalDateTime.of(2024, 6, 15, 10, 30, 0), entry.getUploadDateTime(),
                    "UploadDateTime should be parsed");
            assertEquals("529900HNOAA1KXQJUQ27", entry.getLei(), "LEI should be parsed");
            assertEquals(2, entry.getIsins().size(), "Should have two ISINs");
            assertTrue(entry.getIsins().contains("AT0000A0ABC1"), "Should contain first ISIN");
            assertTrue(entry.getIsins().contains("AT0000A0ABC2"), "Should contain second ISIN");
        }

        @Test
        @DisplayName("parses FUND entry with multiple DataSuppliers")
        void parsesFundWithMultipleDataSuppliers() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>KAG_A</Short></DataSupplier>"
                    + "      <DataSupplier><Short>KAG_B</Short></DataSupplier>"
                    + "      <DataSupplier><Short>KAG_C</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            List<String> suppliers = result.get(0).getDataSuppliers();
            assertEquals(3, suppliers.size(), "Should have three data suppliers");
            assertTrue(suppliers.contains("KAG_A"), "Should contain KAG_A");
            assertTrue(suppliers.contains("KAG_B"), "Should contain KAG_B");
            assertTrue(suppliers.contains("KAG_C"), "Should contain KAG_C");
        }
    }

    @Nested
    @DisplayName("NewInformation - DOC content type")
    class NewInfoDocTests {

        @Test
        @DisplayName("parses DOC entry with ListedType")
        void parsesDocWithListedType() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <ContentDate>2024-07-01</ContentDate>"
                    + "    <DocumentType>"
                    + "      <ListedType>AnnualReport</ListedType>"
                    + "      <Language>de</Language>"
                    + "      <Format>PDF</Format>"
                    + "    </DocumentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            NewInformationEntry entry = result.get(0);
            assertEquals(NewInformationEntry.ContentType.DOC, entry.getContentType());
            assertEquals("AnnualReport", entry.getDocumentType(), "DocumentType should be the ListedType value");
            assertEquals("de", entry.getDocumentLanguage(), "Language should be parsed");
            assertEquals("PDF", entry.getDocumentFormat(), "Format should be parsed");
        }

        @Test
        @DisplayName("parses DOC entry with UnlistedType when ListedType is absent")
        void parsesDocWithUnlistedType() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <DocumentType>"
                    + "      <UnlistedType>CustomReport</UnlistedType>"
                    + "      <Language>en</Language>"
                    + "      <Format>XLSX</Format>"
                    + "    </DocumentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            NewInformationEntry entry = result.get(0);
            assertEquals("CustomReport", entry.getDocumentType(),
                    "DocumentType should fall back to UnlistedType when ListedType is absent");
            assertEquals("en", entry.getDocumentLanguage());
            assertEquals("XLSX", entry.getDocumentFormat());
        }

        @Test
        @DisplayName("DOC with ListedType preferred over UnlistedType")
        void listedTypePreferredOverUnlisted() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <DocumentType>"
                    + "      <ListedType>KID</ListedType>"
                    + "      <UnlistedType>ShouldNotUse</UnlistedType>"
                    + "      <Language>de</Language>"
                    + "      <Format>PDF</Format>"
                    + "    </DocumentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            assertEquals("KID", result.get(0).getDocumentType(),
                    "ListedType should be preferred over UnlistedType");
        }
    }

    @Nested
    @DisplayName("NewInformation - REG content type")
    class NewInfoRegTests {

        @Test
        @DisplayName("parses REG entry with Type element")
        void parsesRegWithType() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>REG</ContentType>"
                    + "    <ContentDate>2024-08-01</ContentDate>"
                    + "    <RegulatoryReporting>"
                    + "      <Type>EMT</Type>"
                    + "    </RegulatoryReporting>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            NewInformationEntry entry = result.get(0);
            assertEquals(NewInformationEntry.ContentType.REG, entry.getContentType());
            assertEquals("EMT", entry.getReportingType(), "ReportingType should be parsed from Type element");
        }
    }

    @Nested
    @DisplayName("NewInformation - profiles and multiple entries")
    class NewInfoProfilesAndMultipleTests {

        @Test
        @DisplayName("parses multiple profiles")
        void parsesMultipleProfiles() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Profiles>"
                    + "      <Profile>FundsXML4</Profile>"
                    + "      <Profile>FundsXML_OeKB</Profile>"
                    + "    </Profiles>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            List<String> profiles = result.get(0).getProfiles();
            assertEquals(2, profiles.size(), "Should have two profiles");
            assertTrue(profiles.contains("FundsXML4"), "Should contain FundsXML4");
            assertTrue(profiles.contains("FundsXML_OeKB"), "Should contain FundsXML_OeKB");
        }

        @Test
        @DisplayName("parses multiple NewInformation entries")
        void parsesMultipleEntries() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-01-01</ContentDate>"
                    + "  </NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <ContentDate>2024-02-01</ContentDate>"
                    + "    <DocumentType><ListedType>Prospectus</ListedType><Language>en</Language><Format>PDF</Format></DocumentType>"
                    + "  </NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>REG</ContentType>"
                    + "    <ContentDate>2024-03-01</ContentDate>"
                    + "    <RegulatoryReporting><Type>PRIIPS</Type></RegulatoryReporting>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(3, result.size(), "Should parse all three entries");
            assertEquals(NewInformationEntry.ContentType.FUND, result.get(0).getContentType());
            assertEquals(NewInformationEntry.ContentType.DOC, result.get(1).getContentType());
            assertEquals(NewInformationEntry.ContentType.REG, result.get(2).getContentType());
            assertEquals("Prospectus", result.get(1).getDocumentType());
            assertEquals("PRIIPS", result.get(2).getReportingType());
        }
    }

    @Nested
    @DisplayName("NewInformation - AvailableData fallback tag name")
    class NewInfoAvailableDataFallbackTests {

        @Test
        @DisplayName("falls back to AvailableData tag when NewInformation tag is absent")
        void fallsBackToAvailableDataTag() {
            String xml = "<FundsXML_AvailableData>"
                    + "  <AvailableData>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-05-10</ContentDate>"
                    + "    <UploadDateTime>2024-05-10T12:00:00</UploadDateTime>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>TESTKAG</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "    <FundOrShareclass>"
                    + "      <LEI>LEI_FALLBACK</LEI>"
                    + "    </FundOrShareclass>"
                    + "    <Profiles>"
                    + "      <Profile>TestProfile</Profile>"
                    + "    </Profiles>"
                    + "  </AvailableData>"
                    + "</FundsXML_AvailableData>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size(), "Should parse AvailableData as fallback");
            NewInformationEntry entry = result.get(0);
            assertEquals(NewInformationEntry.ContentType.FUND, entry.getContentType());
            assertEquals(LocalDate.of(2024, 5, 10), entry.getContentDate());
            assertEquals("LEI_FALLBACK", entry.getLei());
            assertEquals(1, entry.getDataSuppliers().size());
            assertEquals("TESTKAG", entry.getDataSuppliers().get(0));
        }

        @Test
        @DisplayName("NewInformation tag is preferred over AvailableData tag")
        void newInformationTagPreferred() {
            // When both tags exist, getElementsByTagName("NewInformation") will find them
            // first
            String xml = "<Root>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-01-01</ContentDate>"
                    + "  </NewInformation>"
                    + "</Root>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size(), "Should use NewInformation tag when present");
            assertEquals(NewInformationEntry.ContentType.FUND, result.get(0).getContentType());
        }
    }

    @Nested
    @DisplayName("NewInformation - edge cases")
    class NewInfoEdgeCaseTests {

        @Test
        @DisplayName("entry without FundOrShareclass leaves LEI null and ISINs empty")
        void noFundOrShareclass() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getLei(), "LEI should be null when FundOrShareclass is absent");
            assertTrue(result.get(0).getIsins().isEmpty(), "ISINs should be empty when FundOrShareclass is absent");
        }

        @Test
        @DisplayName("unknown ContentType string results in null enum value")
        void unknownContentType() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>UNKNOWN</ContentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getContentType(),
                    "ContentType should be null for unknown string value");
        }

        @Test
        @DisplayName("missing ContentDate leaves contentDate null")
        void missingContentDate() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getContentDate(), "ContentDate should be null when missing");
        }

        @Test
        @DisplayName("missing UploadDateTime leaves uploadDateTime null")
        void missingUploadDateTime() {
            String xml = "<FundsXML_NewInformation>"
                    + "  <NewInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </NewInformation>"
                    + "</FundsXML_NewInformation>";

            List<NewInformationEntry> result = newInformation.parseNewInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getUploadDateTime(), "UploadDateTime should be null when missing");
        }
    }

    // =============================================================
    // DownloadedInformation.parseDownloadedInformationXml
    // =============================================================

    @Nested
    @DisplayName("DownloadedInformation - null/empty/invalid input")
    class DownloadedInfoInvalidInputTests {

        @Test
        @DisplayName("null XML returns empty list")
        void nullXmlReturnsEmptyList() {
            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(null);
            assertNotNull(result, "Result should never be null");
            assertTrue(result.isEmpty(), "Null XML should produce empty list");
        }

        @Test
        @DisplayName("empty string returns empty list")
        void emptyStringReturnsEmptyList() {
            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml("");
            assertTrue(result.isEmpty(), "Empty string should produce empty list");
        }

        @Test
        @DisplayName("blank string returns empty list")
        void blankStringReturnsEmptyList() {
            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml("  \t\n ");
            assertTrue(result.isEmpty(), "Blank string should produce empty list");
        }

        @Test
        @DisplayName("malformed XML returns empty list without exception")
        void malformedXmlReturnsEmptyList() {
            List<DownloadedInformationEntry> result = downloadedInformation
                    .parseDownloadedInformationXml("<broken><xml");
            assertTrue(result.isEmpty(), "Malformed XML should produce empty list");
        }
    }

    @Nested
    @DisplayName("DownloadedInformation - FUND content type")
    class DownloadedInfoFundTests {

        @Test
        @DisplayName("parses FUND entry with LEI, OeNB_Identnr, and ISINs")
        void parsesFundWithAllIdentifiers() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-06-15</ContentDate>"
                    + "    <DownloadDateTime>2024-06-16T08:30:00</DownloadDateTime>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>DLKAG</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "    <FundOrShareclass>"
                    + "      <LEI>529900HNOAA1KXQJUQ27</LEI>"
                    + "      <OeNB_Identnr>67890</OeNB_Identnr>"
                    + "      <ISIN>AT0000A0DL01</ISIN>"
                    + "      <ISIN>AT0000A0DL02</ISIN>"
                    + "    </FundOrShareclass>"
                    + "    <Profiles>"
                    + "      <Profile>FundsXML4</Profile>"
                    + "    </Profiles>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size(), "Should parse one entry");
            DownloadedInformationEntry entry = result.get(0);

            assertEquals(DownloadedInformationEntry.ContentType.FUND, entry.getContentType(),
                    "ContentType should be FUND");
            assertEquals(LocalDate.of(2024, 6, 15), entry.getContentDate(),
                    "ContentDate should be parsed");
            assertEquals(LocalDateTime.of(2024, 6, 16, 8, 30, 0), entry.getDownloadDateTime(),
                    "DownloadDateTime should be parsed");
            assertEquals("529900HNOAA1KXQJUQ27", entry.getLei(), "LEI should be parsed");
            assertEquals("67890", entry.getOenbIdentnr(), "OeNB_Identnr should be parsed");
            assertEquals(2, entry.getIsins().size(), "Should have two ISINs");
            assertTrue(entry.getIsins().contains("AT0000A0DL01"), "Should contain first ISIN");
            assertTrue(entry.getIsins().contains("AT0000A0DL02"), "Should contain second ISIN");
        }

        @Test
        @DisplayName("parses OeNB_Identnr from FundOrShareclass")
        void parsesOenbIdentnr() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <FundOrShareclass>"
                    + "      <OeNB_Identnr>12345</OeNB_Identnr>"
                    + "    </FundOrShareclass>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertEquals("12345", result.get(0).getOenbIdentnr(),
                    "OeNB_Identnr should be extracted from FundOrShareclass");
        }
    }

    @Nested
    @DisplayName("DownloadedInformation - Owner vs AccessRuleID")
    class DownloadedInfoOwnerTests {

        @Test
        @DisplayName("parses Owner=OWNER and sets isOwner flag")
        void parsesOwnerFlag() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Owner>OWNER</Owner>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertTrue(result.get(0).isOwner(), "isOwner should be true when Owner=OWNER");
            assertNull(result.get(0).getAccessRuleId(),
                    "AccessRuleId should be null when Owner is set");
        }

        @Test
        @DisplayName("parses AccessRuleID when present")
        void parsesAccessRuleId() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <AccessRuleID>AR-42</AccessRuleID>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isOwner(), "isOwner should be false when AccessRuleID is used");
            assertEquals("AR-42", result.get(0).getAccessRuleId(), "AccessRuleID should be parsed");
        }

        @Test
        @DisplayName("neither Owner nor AccessRuleID leaves both at defaults")
        void neitherOwnerNorAccessRule() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isOwner(), "isOwner should default to false");
            assertNull(result.get(0).getAccessRuleId(), "AccessRuleId should be null when absent");
        }

        @Test
        @DisplayName("Owner with non-OWNER value does not set isOwner flag")
        void ownerWithNonOwnerValue() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Owner>OTHER</Owner>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isOwner(),
                    "isOwner should be false when Owner value is not exactly 'OWNER'");
        }
    }

    @Nested
    @DisplayName("DownloadedInformation - DOC content type")
    class DownloadedInfoDocTests {

        @Test
        @DisplayName("parses DOC entry with ListedType")
        void parsesDocWithListedType() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <DocumentType>"
                    + "      <ListedType>KID</ListedType>"
                    + "      <Language>de</Language>"
                    + "      <Format>PDF</Format>"
                    + "    </DocumentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            DownloadedInformationEntry entry = result.get(0);
            assertEquals(DownloadedInformationEntry.ContentType.DOC, entry.getContentType());
            assertEquals("KID", entry.getDocumentType(), "DocumentType should be the ListedType");
            assertEquals("de", entry.getDocumentLanguage(), "Language should be parsed");
            assertEquals("PDF", entry.getDocumentFormat(), "Format should be parsed");
        }

        @Test
        @DisplayName("parses DOC entry with UnlistedType when ListedType is absent")
        void parsesDocWithUnlistedType() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <DocumentType>"
                    + "      <UnlistedType>InternalReport</UnlistedType>"
                    + "      <Language>en</Language>"
                    + "      <Format>DOCX</Format>"
                    + "    </DocumentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertEquals("InternalReport", result.get(0).getDocumentType(),
                    "DocumentType should fall back to UnlistedType");
        }
    }

    @Nested
    @DisplayName("DownloadedInformation - REG content type")
    class DownloadedInfoRegTests {

        @Test
        @DisplayName("parses REG entry with Type element")
        void parsesRegWithType() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>REG</ContentType>"
                    + "    <RegulatoryReporting>"
                    + "      <Type>TPTSolvencyII</Type>"
                    + "    </RegulatoryReporting>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            DownloadedInformationEntry entry = result.get(0);
            assertEquals(DownloadedInformationEntry.ContentType.REG, entry.getContentType());
            assertEquals("TPTSolvencyII", entry.getReportingType(),
                    "ReportingType should be parsed from Type element");
        }
    }

    @Nested
    @DisplayName("DownloadedInformation - multiple entries and DataSuppliers")
    class DownloadedInfoMultipleTests {

        @Test
        @DisplayName("parses multiple DownloadedInformation entries")
        void parsesMultipleEntries() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-01-01</ContentDate>"
                    + "    <Owner>OWNER</Owner>"
                    + "  </DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>DOC</ContentType>"
                    + "    <ContentDate>2024-02-01</ContentDate>"
                    + "    <AccessRuleID>AR-100</AccessRuleID>"
                    + "    <DocumentType><ListedType>AIFMD</ListedType><Language>de</Language><Format>PDF</Format></DocumentType>"
                    + "  </DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>REG</ContentType>"
                    + "    <ContentDate>2024-03-01</ContentDate>"
                    + "    <RegulatoryReporting><Type>EMIR</Type></RegulatoryReporting>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(3, result.size(), "Should parse all three entries");

            assertEquals(DownloadedInformationEntry.ContentType.FUND, result.get(0).getContentType());
            assertTrue(result.get(0).isOwner());

            assertEquals(DownloadedInformationEntry.ContentType.DOC, result.get(1).getContentType());
            assertEquals("AR-100", result.get(1).getAccessRuleId());
            assertEquals("AIFMD", result.get(1).getDocumentType());

            assertEquals(DownloadedInformationEntry.ContentType.REG, result.get(2).getContentType());
            assertEquals("EMIR", result.get(2).getReportingType());
        }

        @Test
        @DisplayName("parses multiple DataSuppliers")
        void parsesMultipleDataSuppliers() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>DL_A</Short></DataSupplier>"
                    + "      <DataSupplier><Short>DL_B</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            List<String> suppliers = result.get(0).getDataSuppliers();
            assertEquals(2, suppliers.size(), "Should have two data suppliers");
            assertTrue(suppliers.contains("DL_A"), "Should contain DL_A");
            assertTrue(suppliers.contains("DL_B"), "Should contain DL_B");
        }

        @Test
        @DisplayName("parses multiple profiles")
        void parsesMultipleProfiles() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <Profiles>"
                    + "      <Profile>ProfileX</Profile>"
                    + "      <Profile>ProfileY</Profile>"
                    + "    </Profiles>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            List<String> profiles = result.get(0).getProfiles();
            assertEquals(2, profiles.size(), "Should have two profiles");
            assertTrue(profiles.contains("ProfileX"), "Should contain ProfileX");
            assertTrue(profiles.contains("ProfileY"), "Should contain ProfileY");
        }
    }

    @Nested
    @DisplayName("DownloadedInformation - edge cases")
    class DownloadedInfoEdgeCaseTests {

        @Test
        @DisplayName("entry without FundOrShareclass leaves identifiers null/empty")
        void noFundOrShareclass() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getLei(), "LEI should be null when FundOrShareclass is absent");
            assertNull(result.get(0).getOenbIdentnr(),
                    "OeNB_Identnr should be null when FundOrShareclass is absent");
            assertTrue(result.get(0).getIsins().isEmpty(),
                    "ISINs should be empty when FundOrShareclass is absent");
        }

        @Test
        @DisplayName("missing ContentDate leaves contentDate null")
        void missingContentDate() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getContentDate(), "ContentDate should be null when missing");
        }

        @Test
        @DisplayName("missing DownloadDateTime leaves downloadDateTime null")
        void missingDownloadDateTime() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getDownloadDateTime(), "DownloadDateTime should be null when missing");
        }

        @Test
        @DisplayName("unknown ContentType string results in null enum value")
        void unknownContentType() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>INVALID</ContentType>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getContentType(),
                    "ContentType should be null for unknown string value");
        }

        @Test
        @DisplayName("comprehensive entry with all fields populated")
        void comprehensiveEntry() {
            String xml = "<FundsXML_DownloadedInformation>"
                    + "  <DownloadedInformation>"
                    + "    <ContentType>FUND</ContentType>"
                    + "    <ContentDate>2024-09-01</ContentDate>"
                    + "    <DownloadDateTime>2024-09-02T14:00:00</DownloadDateTime>"
                    + "    <DataSuppliers>"
                    + "      <DataSupplier><Short>KAG_FULL</Short></DataSupplier>"
                    + "    </DataSuppliers>"
                    + "    <AccessRuleID>AR-999</AccessRuleID>"
                    + "    <FundOrShareclass>"
                    + "      <LEI>FULL_LEI_12345</LEI>"
                    + "      <OeNB_Identnr>99999</OeNB_Identnr>"
                    + "      <ISIN>AT0000FULL01</ISIN>"
                    + "    </FundOrShareclass>"
                    + "    <Profiles>"
                    + "      <Profile>FullProfile</Profile>"
                    + "    </Profiles>"
                    + "  </DownloadedInformation>"
                    + "</FundsXML_DownloadedInformation>";

            List<DownloadedInformationEntry> result = downloadedInformation.parseDownloadedInformationXml(xml);

            assertEquals(1, result.size());
            DownloadedInformationEntry entry = result.get(0);

            assertEquals(DownloadedInformationEntry.ContentType.FUND, entry.getContentType());
            assertEquals(LocalDate.of(2024, 9, 1), entry.getContentDate());
            assertEquals(LocalDateTime.of(2024, 9, 2, 14, 0, 0), entry.getDownloadDateTime());
            assertEquals(1, entry.getDataSuppliers().size());
            assertEquals("KAG_FULL", entry.getDataSuppliers().get(0));
            assertEquals("AR-999", entry.getAccessRuleId());
            assertFalse(entry.isOwner());
            assertEquals("FULL_LEI_12345", entry.getLei());
            assertEquals("99999", entry.getOenbIdentnr());
            assertEquals(1, entry.getIsins().size());
            assertEquals("AT0000FULL01", entry.getIsins().get(0));
            assertEquals(1, entry.getProfiles().size());
            assertEquals("FullProfile", entry.getProfiles().get(0));
        }
    }
}
