package dao;

import model.JournalEntry;
import model.JournalEntry.ActionType;
import model.JournalEntry.JournalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Journal XML parsing. Tests the package-private parseJournalXml
 * method directly, without network or filesystem interaction.
 */
class JournalParseTest {

    private Journal journal;

    @BeforeEach
    void setUp() {
        journal = new Journal();
    }

    // ---------------------------------------------------------------
    // parseJournalXml - null / empty / invalid input
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - null/empty/invalid input")
    class InvalidInputTests {

        @Test
        @DisplayName("null XML returns empty list")
        void nullXmlReturnsEmptyList() {
            List<JournalEntry> result = journal.parseJournalXml(null);
            assertNotNull(result, "Result should never be null");
            assertTrue(result.isEmpty(), "Null XML should produce empty list");
        }

        @Test
        @DisplayName("empty string returns empty list")
        void emptyStringReturnsEmptyList() {
            List<JournalEntry> result = journal.parseJournalXml("");
            assertTrue(result.isEmpty(), "Empty string should produce empty list");
        }

        @Test
        @DisplayName("blank string returns empty list")
        void blankStringReturnsEmptyList() {
            List<JournalEntry> result = journal.parseJournalXml("   \n\t  ");
            assertTrue(result.isEmpty(), "Blank string should produce empty list");
        }

        @Test
        @DisplayName("malformed XML returns empty list without exception")
        void malformedXmlReturnsEmptyList() {
            List<JournalEntry> result = journal.parseJournalXml("<broken><xml");
            assertTrue(result.isEmpty(), "Malformed XML should produce empty list");
        }

        @Test
        @DisplayName("valid XML without JournalEntry elements returns empty list")
        void validXmlNoJournalEntriesReturnsEmptyList() {
            String xml = "<?xml version=\"1.0\"?><FundsXMLJournal><Other>data</Other></FundsXMLJournal>";
            List<JournalEntry> result = journal.parseJournalXml(xml);
            assertTrue(result.isEmpty(), "XML without JournalEntry elements should produce empty list");
        }
    }

    // ---------------------------------------------------------------
    // parseJournalXml - Upload entries
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - Upload (UL) entries")
    class UploadEntryTests {

        @Test
        @DisplayName("parses UL action type")
        void parsesUploadActionType() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T14:30:00</ActionTime>"
                            + "<User>testuser</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size(), "Should parse one entry");
            assertEquals(ActionType.UL, result.get(0).getAction(), "Action should be UL (Upload)");
        }

        @Test
        @DisplayName("parses Upload Status and UniqueDocumentID")
        void parsesUploadStatusAndDocId() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T14:30:00</ActionTime>"
                            + "<User>testuser</User>"
                            + "<Upload>"
                            + "  <Status>OK</Status>"
                            + "  <UniqueDocumentID>DOC-2024-001</UniqueDocumentID>"
                            + "</Upload>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            JournalEntry entry = result.get(0);
            assertEquals("Status: OK", entry.getDetails(), "Details should contain upload status");
            assertEquals("DOC-2024-001", entry.getUniqueId(), "UniqueId should be parsed");
        }

        @Test
        @DisplayName("parses Upload with status only (no UniqueDocumentID)")
        void parsesUploadStatusOnly() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T14:30:00</ActionTime>"
                            + "<User>testuser</User>"
                            + "<Upload>"
                            + "  <Status>ERROR</Status>"
                            + "</Upload>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            JournalEntry entry = result.get(0);
            assertEquals("Status: ERROR", entry.getDetails(), "Details should contain error status");
            assertNull(entry.getUniqueId(), "UniqueId should be null when not present");
        }
    }

    // ---------------------------------------------------------------
    // parseJournalXml - Download entries
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - Download (DL) entries")
    class DownloadEntryTests {

        @Test
        @DisplayName("parses DL action type")
        void parsesDownloadActionType() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T15:00:00</ActionTime>"
                            + "<User>dluser</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size(), "Should parse one entry");
            assertEquals(ActionType.DL, result.get(0).getAction(), "Action should be DL (Download)");
        }

        @Test
        @DisplayName("parses empty download detection (IsEmpty=true)")
        void parsesEmptyDownload() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T15:00:00</ActionTime>"
                            + "<User>dluser</User>"
                            + "<Download>"
                            + "  <Data IsEmpty=\"true\"/>"
                            + "</Download>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            JournalEntry entry = result.get(0);
            assertTrue(entry.isEmpty(), "Entry should be marked as empty download");
            assertEquals("Empty download (no data available)", entry.getDetails(),
                    "Details should describe empty download");
        }

        @Test
        @DisplayName("non-empty download (IsEmpty=false) does not set empty flag")
        void parsesNonEmptyDownload() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T15:00:00</ActionTime>"
                            + "<User>dluser</User>"
                            + "<Download>"
                            + "  <Data IsEmpty=\"false\"/>"
                            + "</Download>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isEmpty(), "Entry should not be marked as empty when IsEmpty=false");
        }

        @Test
        @DisplayName("download without IsEmpty attribute does not set empty flag")
        void parsesDownloadWithoutIsEmpty() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action>"
                            + "<Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-03-15T15:00:00</ActionTime>"
                            + "<User>dluser</User>"
                            + "<Download>"
                            + "  <Data/>"
                            + "</Download>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isEmpty(),
                    "Entry should not be marked as empty when IsEmpty attribute is absent");
        }
    }

    // ---------------------------------------------------------------
    // parseJournalXml - JournalType parsing
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - JournalType parsing")
    class JournalTypeTests {

        @Test
        @DisplayName("parses FXML_DATA type")
        void parsesFxmlDataType() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(JournalType.FXML_DATA, result.get(0).getType(), "Type should be FXML_DATA");
        }

        @Test
        @DisplayName("parses AR type")
        void parsesArType() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>AR</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(JournalType.AR, result.get(0).getType(), "Type should be AR");
        }

        @Test
        @DisplayName("parses FXML_DOC type")
        void parsesFxmlDocType() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action><Type>FXML_DOC</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(JournalType.FXML_DOC, result.get(0).getType(), "Type should be FXML_DOC");
        }

        @Test
        @DisplayName("parses FXML_REG type")
        void parsesFxmlRegType() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_REG</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(JournalType.FXML_REG, result.get(0).getType(), "Type should be FXML_REG");
        }

        @Test
        @DisplayName("parses DATEN_VORH type")
        void parsesDatenVorhType() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action><Type>DATEN_VORH</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(JournalType.DATEN_VORH, result.get(0).getType(), "Type should be DATEN_VORH");
        }

        @Test
        @DisplayName("parses OENB_AGG type")
        void parsesOenbAggType() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>OENB_AGG</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(JournalType.OENB_AGG, result.get(0).getType(), "Type should be OENB_AGG");
        }

        @Test
        @DisplayName("unknown type does not throw, entry still created with null type")
        void unknownTypeDoesNotThrow() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>UNKNOWN_TYPE_XYZ</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size(), "Entry should still be created for unknown type");
            assertNull(result.get(0).getType(), "Type should be null for unknown type value");
        }
    }

    // ---------------------------------------------------------------
    // parseJournalXml - DateTime and DataSupplier
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - DateTime and DataSupplier parsing")
    class DateTimeAndDataSupplierTests {

        @Test
        @DisplayName("parses ActionTime as ISO date-time")
        void parsesActionTime() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-06-20T09:45:30</ActionTime><User>admin</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            LocalDateTime expected = LocalDateTime.of(2024, 6, 20, 9, 45, 30);
            assertEquals(expected, result.get(0).getTimestamp(), "ActionTime should be parsed as LocalDateTime");
        }

        @Test
        @DisplayName("missing ActionTime leaves timestamp null")
        void missingActionTimeLeavesNull() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type><User>admin</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getTimestamp(), "Timestamp should be null when ActionTime is missing");
        }

        @Test
        @DisplayName("parses DataSupplier Short")
        void parsesDataSupplierShort() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime>"
                            + "<DataSupplier><Short>SPARKAG</Short><Name>Sparinvest KAG</Name></DataSupplier>"
                            + "<User>admin</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertEquals("SPARKAG", result.get(0).getDataSupplier(),
                    "DataSupplier should be the Short value");
        }

        @Test
        @DisplayName("parses User name")
        void parsesUserName() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime>"
                            + "<User>karl.kauc</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertEquals("karl.kauc", result.get(0).getUserName(), "User name should be parsed");
        }
    }

    // ---------------------------------------------------------------
    // parseJournalXml - multiple entries
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - multiple entries")
    class MultipleEntriesTests {

        @Test
        @DisplayName("parses multiple journal entries of mixed types")
        void parsesMultipleEntries() {
            String xml = "<FundsXMLJournal>"
                    + "  <JournalEntry>"
                    + "    <Action>UL</Action>"
                    + "    <Type>FXML_DATA</Type>"
                    + "    <ActionTime>2024-03-01T08:00:00</ActionTime>"
                    + "    <User>uploader</User>"
                    + "    <Upload><Status>OK</Status><UniqueDocumentID>UID-001</UniqueDocumentID></Upload>"
                    + "  </JournalEntry>"
                    + "  <JournalEntry>"
                    + "    <Action>DL</Action>"
                    + "    <Type>FXML_DOC</Type>"
                    + "    <ActionTime>2024-03-02T09:00:00</ActionTime>"
                    + "    <User>downloader</User>"
                    + "    <Download><Data IsEmpty=\"true\"/></Download>"
                    + "  </JournalEntry>"
                    + "  <JournalEntry>"
                    + "    <Action>UL</Action>"
                    + "    <Type>AR</Type>"
                    + "    <ActionTime>2024-03-03T10:00:00</ActionTime>"
                    + "    <DataSupplier><Short>DDS_XYZ</Short></DataSupplier>"
                    + "    <User>ruleadmin</User>"
                    + "  </JournalEntry>"
                    + "</FundsXMLJournal>";

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(3, result.size(), "Should parse all three entries");

            // First entry - upload
            assertEquals(ActionType.UL, result.get(0).getAction());
            assertEquals(JournalType.FXML_DATA, result.get(0).getType());
            assertEquals("UID-001", result.get(0).getUniqueId());
            assertEquals("uploader", result.get(0).getUserName());

            // Second entry - empty download
            assertEquals(ActionType.DL, result.get(1).getAction());
            assertEquals(JournalType.FXML_DOC, result.get(1).getType());
            assertTrue(result.get(1).isEmpty());
            assertEquals("downloader", result.get(1).getUserName());

            // Third entry - access rule upload
            assertEquals(ActionType.UL, result.get(2).getAction());
            assertEquals(JournalType.AR, result.get(2).getType());
            assertEquals("DDS_XYZ", result.get(2).getDataSupplier());
            assertEquals("ruleadmin", result.get(2).getUserName());
        }
    }

    // ---------------------------------------------------------------
    // parseJournalXml - edge cases
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("parseJournalXml - edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("empty Upload element does not set details or uniqueId")
        void emptyUploadElement() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime>"
                            + "<User>u</User><Upload/>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertNull(result.get(0).getDetails(), "Details should be null for empty Upload");
            assertNull(result.get(0).getUniqueId(), "UniqueId should be null for empty Upload");
        }

        @Test
        @DisplayName("empty Download element without Data child does not set empty flag")
        void emptyDownloadElement() {
            String xml = wrapJournalEntry(
                    "<Action>DL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime>"
                            + "<User>u</User><Download/>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isEmpty(),
                    "Entry should not be marked empty when Download has no Data child");
        }

        @Test
        @DisplayName("unknown action type does not crash, entry still created")
        void unknownActionType() {
            String xml = wrapJournalEntry(
                    "<Action>XX</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size(), "Entry should still be created for unknown action");
            assertNull(result.get(0).getAction(), "Action should be null for unknown action type");
        }

        @Test
        @DisplayName("entry without DataSupplier element leaves dataSupplier as empty string")
        void missingDataSupplier() {
            String xml = wrapJournalEntry(
                    "<Action>UL</Action><Type>FXML_DATA</Type>"
                            + "<ActionTime>2024-01-01T00:00:00</ActionTime><User>u</User>");

            List<JournalEntry> result = journal.parseJournalXml(xml);

            assertEquals(1, result.size());
            // getElementText returns "" for missing elements in Journal
            assertNull(result.get(0).getDataSupplier(),
                    "DataSupplier should be null when element is missing");
        }
    }

    // ---------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------

    private static String wrapJournalEntry(String entryContent) {
        return "<FundsXMLJournal><JournalEntry>" + entryContent + "</JournalEntry></FundsXMLJournal>";
    }
}
