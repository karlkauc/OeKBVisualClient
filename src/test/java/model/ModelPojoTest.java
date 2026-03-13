package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for model POJO classes. Focuses on logic, defensive
 * copying, enum behavior, and edge cases rather than trivial getter/setter
 * round-trips.
 */
class ModelPojoTest {

    // ========================================================================
    // DownloadParameters
    // ========================================================================

    @Nested
    @DisplayName("DownloadParameters")
    class DownloadParametersTests {

        @Test
        @DisplayName("Default constructor initialises empty lists and sensible defaults")
        void defaultConstructor_initialisesDefaults() {
            DownloadParameters dp = new DownloadParameters();

            assertNotNull(dp.getLeiOenIds(), "leiOenIds should never be null after default construction");
            assertTrue(dp.getLeiOenIds().isEmpty(), "leiOenIds should be empty by default");
            assertNotNull(dp.getIsins(), "isins should never be null after default construction");
            assertTrue(dp.getIsins().isEmpty(), "isins should be empty by default");
            assertEquals(10, dp.getRequestBlockSize(), "default block size should be 10");
            assertFalse(dp.isExcludeInvalid(), "excludeInvalid should default to false");
        }

        @Test
        @DisplayName("Mode constructor delegates to default then sets mode")
        void modeConstructor_setsMode() {
            DownloadParameters dp = new DownloadParameters("FUND");

            assertEquals("FUND", dp.getMode());
            assertNotNull(dp.getLeiOenIds(), "lists should still be initialised");
            assertEquals(10, dp.getRequestBlockSize());
        }

        // ---- hasLeiOenIds / hasIsins logic ----

        @Test
        @DisplayName("hasLeiOenIds returns false when list is empty")
        void hasLeiOenIds_emptyList_returnsFalse() {
            DownloadParameters dp = new DownloadParameters();
            assertFalse(dp.hasLeiOenIds(), "empty list should yield false");
        }

        @Test
        @DisplayName("hasLeiOenIds returns true after adding an ID")
        void hasLeiOenIds_afterAdd_returnsTrue() {
            DownloadParameters dp = new DownloadParameters();
            dp.addLeiOenId("LEI12345");
            assertTrue(dp.hasLeiOenIds(), "list with one element should yield true");
        }

        @Test
        @DisplayName("hasIsins returns false when list is empty")
        void hasIsins_emptyList_returnsFalse() {
            DownloadParameters dp = new DownloadParameters();
            assertFalse(dp.hasIsins(), "empty list should yield false");
        }

        @Test
        @DisplayName("hasIsins returns true after adding an ISIN")
        void hasIsins_afterAdd_returnsTrue() {
            DownloadParameters dp = new DownloadParameters();
            dp.addIsin("AT0000123456");
            assertTrue(dp.hasIsins(), "list with one element should yield true");
        }

        // ---- defensive copying ----

        @Test
        @DisplayName("getLeiOenIds returns a defensive copy")
        void getLeiOenIds_returnsDefensiveCopy() {
            DownloadParameters dp = new DownloadParameters();
            dp.addLeiOenId("A");

            List<String> returned = dp.getLeiOenIds();
            returned.add("B"); // mutate the returned list

            assertEquals(1, dp.getLeiOenIds().size(),
                    "mutating returned list should not affect internal state");
        }

        @Test
        @DisplayName("setLeiOenIds makes a defensive copy of the input")
        void setLeiOenIds_makesDefensiveCopy() {
            DownloadParameters dp = new DownloadParameters();
            List<String> input = new ArrayList<>(List.of("X", "Y"));
            dp.setLeiOenIds(input);

            input.add("Z"); // mutate original

            assertEquals(2, dp.getLeiOenIds().size(),
                    "modifying the original list after set should not affect internal state");
        }

        @Test
        @DisplayName("setLeiOenIds with null creates empty list")
        void setLeiOenIds_null_createsEmptyList() {
            DownloadParameters dp = new DownloadParameters();
            dp.addLeiOenId("something");
            dp.setLeiOenIds(null);

            assertNotNull(dp.getLeiOenIds(), "null input should result in empty list, not null");
            assertTrue(dp.getLeiOenIds().isEmpty());
            assertFalse(dp.hasLeiOenIds());
        }

        @Test
        @DisplayName("getIsins returns a defensive copy")
        void getIsins_returnsDefensiveCopy() {
            DownloadParameters dp = new DownloadParameters();
            dp.addIsin("ISIN1");

            List<String> returned = dp.getIsins();
            returned.clear();

            assertEquals(1, dp.getIsins().size(),
                    "clearing returned list should not affect internal state");
        }

        @Test
        @DisplayName("setIsins with null creates empty list")
        void setIsins_null_createsEmptyList() {
            DownloadParameters dp = new DownloadParameters();
            dp.setIsins(null);

            assertNotNull(dp.getIsins());
            assertFalse(dp.hasIsins());
        }

        @Test
        @DisplayName("addLeiOenId accumulates entries")
        void addLeiOenId_accumulates() {
            DownloadParameters dp = new DownloadParameters();
            dp.addLeiOenId("A");
            dp.addLeiOenId("B");
            dp.addLeiOenId("C");

            assertEquals(3, dp.getLeiOenIds().size());
            assertEquals(List.of("A", "B", "C"), dp.getLeiOenIds());
        }

        @Test
        @DisplayName("addIsin accumulates entries")
        void addIsin_accumulates() {
            DownloadParameters dp = new DownloadParameters();
            dp.addIsin("ISIN1");
            dp.addIsin("ISIN2");

            assertEquals(2, dp.getIsins().size());
            assertEquals(List.of("ISIN1", "ISIN2"), dp.getIsins());
        }
    }

    // ========================================================================
    // JournalEntry + enums
    // ========================================================================

    @Nested
    @DisplayName("JournalEntry")
    class JournalEntryTests {

        @Test
        @DisplayName("Parameterised constructor sets timestamp, action, type")
        void paramConstructor_setsFields() {
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 10, 30);
            JournalEntry entry = new JournalEntry(now,
                    JournalEntry.ActionType.UL, JournalEntry.JournalType.FXML_DATA);

            assertEquals(now, entry.getTimestamp());
            assertEquals(JournalEntry.ActionType.UL, entry.getAction());
            assertEquals(JournalEntry.JournalType.FXML_DATA, entry.getType());
        }

        @Test
        @DisplayName("Default constructor leaves all fields null/false")
        void defaultConstructor_fieldsNull() {
            JournalEntry entry = new JournalEntry();

            assertNull(entry.getTimestamp());
            assertNull(entry.getAction());
            assertNull(entry.getType());
            assertNull(entry.getUserName());
            assertFalse(entry.isEmpty());
        }

        // ---- ActionType enum descriptions ----

        @Test
        @DisplayName("ActionType.UL has description 'Upload'")
        void actionType_uploadDescription() {
            assertEquals("Upload", JournalEntry.ActionType.UL.getDescription());
        }

        @Test
        @DisplayName("ActionType.DL has description 'Download'")
        void actionType_downloadDescription() {
            assertEquals("Download", JournalEntry.ActionType.DL.getDescription());
        }

        @Test
        @DisplayName("ActionType valueOf round-trips correctly")
        void actionType_valueOf() {
            assertEquals(JournalEntry.ActionType.UL, JournalEntry.ActionType.valueOf("UL"));
            assertEquals(JournalEntry.ActionType.DL, JournalEntry.ActionType.valueOf("DL"));
        }

        @Test
        @DisplayName("ActionType valueOf with invalid name throws IllegalArgumentException")
        void actionType_valueOf_invalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> JournalEntry.ActionType.valueOf("INVALID"),
                    "valueOf with unknown name should throw");
        }

        @Test
        @DisplayName("ActionType values() contains exactly UL and DL")
        void actionType_values() {
            JournalEntry.ActionType[] values = JournalEntry.ActionType.values();
            assertEquals(2, values.length);
        }

        // ---- JournalType enum descriptions ----

        @Test
        @DisplayName("All JournalType enum constants have non-null descriptions")
        void journalType_allDescriptionsPresent() {
            for (JournalEntry.JournalType jt : JournalEntry.JournalType.values()) {
                assertNotNull(jt.getDescription(),
                        "Description should not be null for " + jt.name());
                assertFalse(jt.getDescription().isEmpty(),
                        "Description should not be empty for " + jt.name());
            }
        }

        @Test
        @DisplayName("JournalType has expected number of constants")
        void journalType_count() {
            assertEquals(12, JournalEntry.JournalType.values().length,
                    "JournalType should have 12 constants");
        }

        @Test
        @DisplayName("Selected JournalType descriptions match expected values")
        void journalType_specificDescriptions() {
            assertEquals("AccessRules", JournalEntry.JournalType.AR.getDescription());
            assertEquals("Permissions received", JournalEntry.JournalType.AR_RECEIVED.getDescription());
            assertEquals("FundsXML Data", JournalEntry.JournalType.FXML_DATA.getDescription());
            assertEquals("FundsXML Documents", JournalEntry.JournalType.FXML_DOC.getDescription());
            assertEquals("OeNB OFI Reporting (Aggregierung)", JournalEntry.JournalType.OENB_AGG.getDescription());
            assertEquals("OeNB Aggregation-Check", JournalEntry.JournalType.OENB_CHECK.getDescription());
            assertEquals("Journal Entries", JournalEntry.JournalType.JOURNAL.getDescription());
        }

        @Test
        @DisplayName("JournalType valueOf round-trips for every constant")
        void journalType_valueOf_allConstants() {
            for (JournalEntry.JournalType jt : JournalEntry.JournalType.values()) {
                assertEquals(jt, JournalEntry.JournalType.valueOf(jt.name()));
            }
        }

        @Test
        @DisplayName("isEmpty flag defaults to false and can be toggled")
        void isEmpty_toggle() {
            JournalEntry entry = new JournalEntry();
            assertFalse(entry.isEmpty());
            entry.setEmpty(true);
            assertTrue(entry.isEmpty());
        }
    }

    // ========================================================================
    // AccessRule - defensive copying in all list fields
    // ========================================================================

    @Nested
    @DisplayName("AccessRule")
    class AccessRuleTests {

        // ---- profiles defensive copy ----

        @Test
        @DisplayName("getProfiles returns null when profiles not set")
        void getProfiles_null_whenUnset() {
            AccessRule rule = new AccessRule();
            assertNull(rule.getProfiles(), "profiles should be null when never set");
        }

        @Test
        @DisplayName("setProfiles / getProfiles performs defensive copy")
        void profiles_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("PROFILE_A", "PROFILE_B"));
            rule.setProfiles(input);

            // mutate original
            input.add("PROFILE_C");
            assertEquals(2, rule.getProfiles().size(),
                    "mutating input list should not affect internal state");

            // mutate returned
            List<String> returned = rule.getProfiles();
            returned.clear();
            assertEquals(2, rule.getProfiles().size(),
                    "mutating returned list should not affect internal state");
        }

        @Test
        @DisplayName("setProfiles with null stores null, not empty list")
        void setProfiles_null_storesNull() {
            AccessRule rule = new AccessRule();
            rule.setProfiles(null);
            assertNull(rule.getProfiles());
        }

        // ---- getProfile / setProfile backwards-compat ----

        @Test
        @DisplayName("getProfile returns null when profiles list is null")
        void getProfile_nullList_returnsNull() {
            AccessRule rule = new AccessRule();
            assertNull(rule.getProfile());
        }

        @Test
        @DisplayName("getProfile returns null when profiles list is empty")
        void getProfile_emptyList_returnsNull() {
            AccessRule rule = new AccessRule();
            rule.setProfiles(new ArrayList<>());
            assertNull(rule.getProfile(), "empty list should yield null from getProfile()");
        }

        @Test
        @DisplayName("getProfile returns first profile from list")
        void getProfile_returnsFirst() {
            AccessRule rule = new AccessRule();
            rule.setProfiles(List.of("FIRST", "SECOND"));
            assertEquals("FIRST", rule.getProfile());
        }

        @Test
        @DisplayName("setProfile with non-empty value creates single-element list")
        void setProfile_nonEmpty_createsList() {
            AccessRule rule = new AccessRule();
            rule.setProfile("ONLY");

            List<String> profiles = rule.getProfiles();
            assertNotNull(profiles);
            assertEquals(1, profiles.size());
            assertEquals("ONLY", profiles.get(0));
        }

        @Test
        @DisplayName("setProfile with null clears profiles list")
        void setProfile_null_clearsProfiles() {
            AccessRule rule = new AccessRule();
            rule.setProfile("X");
            rule.setProfile(null);

            List<String> profiles = rule.getProfiles();
            assertNotNull(profiles);
            assertTrue(profiles.isEmpty(), "null profile should clear the list");
        }

        @Test
        @DisplayName("setProfile with empty string clears profiles list")
        void setProfile_empty_clearsProfiles() {
            AccessRule rule = new AccessRule();
            rule.setProfile("X");
            rule.setProfile("");

            List<String> profiles = rule.getProfiles();
            assertNotNull(profiles);
            assertTrue(profiles.isEmpty(), "empty profile should clear the list");
        }

        @Test
        @DisplayName("setProfile replaces existing profiles, not appends")
        void setProfile_replacesExisting() {
            AccessRule rule = new AccessRule();
            rule.setProfiles(List.of("OLD_A", "OLD_B"));
            rule.setProfile("NEW");

            assertEquals(1, rule.getProfiles().size());
            assertEquals("NEW", rule.getProfile());
        }

        // ---- lei defensive copy ----

        @Test
        @DisplayName("getLei / setLei performs defensive copy")
        void lei_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("LEI_1"));
            rule.setLei(input);

            input.add("LEI_2");
            assertEquals(1, rule.getLei().size(), "mutating input should not affect internal lei");

            rule.getLei().add("LEI_3");
            assertEquals(1, rule.getLei().size(), "mutating returned list should not affect internal lei");
        }

        @Test
        @DisplayName("setLei with null stores null")
        void setLei_null() {
            AccessRule rule = new AccessRule();
            rule.setLei(null);
            assertNull(rule.getLei());
        }

        // ---- oenbId defensive copy ----

        @Test
        @DisplayName("getOenbId / setOenbId performs defensive copy")
        void oenbId_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("OID_1", "OID_2"));
            rule.setOenbId(input);

            input.clear();
            assertEquals(2, rule.getOenbId().size());
        }

        @Test
        @DisplayName("setOenbId with null stores null")
        void setOenbId_null() {
            AccessRule rule = new AccessRule();
            rule.setOenbId(null);
            assertNull(rule.getOenbId());
        }

        // ---- isinSegment defensive copy ----

        @Test
        @DisplayName("isinSegment getter/setter performs defensive copy")
        void isinSegment_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("SEG1"));
            rule.setIsinSegment(input);
            input.add("SEG2");
            assertEquals(1, rule.getIsinSegment().size());
        }

        @Test
        @DisplayName("setIsinSegment with null stores null")
        void setIsinSegment_null() {
            AccessRule rule = new AccessRule();
            rule.setIsinSegment(null);
            assertNull(rule.getIsinSegment());
        }

        // ---- isinShareclass defensive copy ----

        @Test
        @DisplayName("isinShareclass getter/setter performs defensive copy")
        void isinShareclass_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("SC1"));
            rule.setIsinShareclass(input);
            input.add("SC2");
            assertEquals(1, rule.getIsinShareclass().size());
        }

        // ---- dataSuppliersGivenShort defensive copy ----

        @Test
        @DisplayName("dataSuppliersGivenShort getter/setter performs defensive copy")
        void dataSuppliersGivenShort_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("DS1", "DS2"));
            rule.setDataSuppliersGivenShort(input);
            input.add("DS3");
            assertEquals(2, rule.getDataSuppliersGivenShort().size());
        }

        @Test
        @DisplayName("setDataSuppliersGivenShort with null stores null")
        void setDataSuppliersGivenShort_null() {
            AccessRule rule = new AccessRule();
            rule.setDataSuppliersGivenShort(null);
            assertNull(rule.getDataSuppliersGivenShort());
        }

        // ---- documentTypes defensive copy ----

        @Test
        @DisplayName("documentTypes getter/setter performs defensive copy")
        void documentTypes_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("AIFMD", "KID"));
            rule.setDocumentTypes(input);
            input.remove(0);
            assertEquals(2, rule.getDocumentTypes().size());
        }

        @Test
        @DisplayName("setDocumentTypes with null stores null")
        void setDocumentTypes_null() {
            AccessRule rule = new AccessRule();
            rule.setDocumentTypes(null);
            assertNull(rule.getDocumentTypes());
        }

        // ---- regulatoryReportings defensive copy ----

        @Test
        @DisplayName("regulatoryReportings getter/setter performs defensive copy")
        void regulatoryReportings_defensiveCopy() {
            AccessRule rule = new AccessRule();
            List<String> input = new ArrayList<>(List.of("EMIR", "PRIIPS"));
            rule.setRegulatoryReportings(input);
            input.clear();
            assertEquals(2, rule.getRegulatoryReportings().size());
        }

        @Test
        @DisplayName("setRegulatoryReportings with null stores null")
        void setRegulatoryReportings_null() {
            AccessRule rule = new AccessRule();
            rule.setRegulatoryReportings(null);
            assertNull(rule.getRegulatoryReportings());
        }

        // ---- toString ----

        @Test
        @DisplayName("toString includes key fields and does not throw on defaults")
        void toString_doesNotThrowOnDefaults() {
            AccessRule rule = new AccessRule();
            String result = rule.toString();
            assertNotNull(result);
            assertTrue(result.startsWith("AccessRule{"), "should start with class name");
        }

        @Test
        @DisplayName("toString contains set field values")
        void toString_containsFieldValues() {
            AccessRule rule = new AccessRule();
            rule.setId("RULE-42");
            rule.setContentType("FUND");
            String result = rule.toString();
            assertTrue(result.contains("RULE-42"), "toString should contain id");
            assertTrue(result.contains("FUND"), "toString should contain contentType");
        }
    }

    // ========================================================================
    // DocumentData + DocumentType enum + getDocumentTypeString logic
    // ========================================================================

    @Nested
    @DisplayName("DocumentData")
    class DocumentDataTests {

        @Test
        @DisplayName("Parameterised constructor sets identifier, documentType, contentDate")
        void paramConstructor_setsFields() {
            LocalDate date = LocalDate.of(2025, 6, 15);
            DocumentData dd = new DocumentData("LEI999", DocumentData.DocumentType.PROSPECTUS, date);

            assertEquals("LEI999", dd.getIdentifier());
            assertEquals(DocumentData.DocumentType.PROSPECTUS, dd.getDocumentType());
            assertEquals(date, dd.getContentDate());
        }

        @Test
        @DisplayName("Default constructor leaves fields null/zero")
        void defaultConstructor() {
            DocumentData dd = new DocumentData();
            assertNull(dd.getIdentifier());
            assertNull(dd.getDocumentType());
            assertNull(dd.getContentDate());
            assertEquals(0L, dd.getFileSize());
        }

        // ---- getDocumentTypeString logic ----

        @Test
        @DisplayName("getDocumentTypeString returns enum value when documentType is set")
        void getDocumentTypeString_withEnum() {
            DocumentData dd = new DocumentData();
            dd.setDocumentType(DocumentData.DocumentType.KID);

            assertEquals("KID", dd.getDocumentTypeString(),
                    "should return the enum's getValue()");
        }

        @Test
        @DisplayName("getDocumentTypeString returns customDocumentType when documentType is null")
        void getDocumentTypeString_fallbackToCustom() {
            DocumentData dd = new DocumentData();
            dd.setCustomDocumentType("MyCustomType");

            assertEquals("MyCustomType", dd.getDocumentTypeString(),
                    "should fall back to customDocumentType when enum is null");
        }

        @Test
        @DisplayName("getDocumentTypeString returns null when both documentType and custom are null")
        void getDocumentTypeString_bothNull() {
            DocumentData dd = new DocumentData();
            assertNull(dd.getDocumentTypeString(),
                    "should return null when both documentType and customDocumentType are null");
        }

        @Test
        @DisplayName("getDocumentTypeString prefers enum over customDocumentType")
        void getDocumentTypeString_enumTakesPrecedence() {
            DocumentData dd = new DocumentData();
            dd.setDocumentType(DocumentData.DocumentType.AIFMD);
            dd.setCustomDocumentType("IgnoreMe");

            assertEquals("AIFMD", dd.getDocumentTypeString(),
                    "enum value should take precedence over customDocumentType");
        }

        // ---- DocumentType enum ----

        @Test
        @DisplayName("All DocumentType constants have non-empty values")
        void documentType_allValuesPresent() {
            for (DocumentData.DocumentType dt : DocumentData.DocumentType.values()) {
                assertNotNull(dt.getValue(), "getValue() should not be null for " + dt.name());
                assertFalse(dt.getValue().isEmpty(), "getValue() should not be empty for " + dt.name());
            }
        }

        @Test
        @DisplayName("DocumentType has expected number of constants")
        void documentType_count() {
            assertEquals(7, DocumentData.DocumentType.values().length,
                    "DocumentType should have 7 constants");
        }

        @Test
        @DisplayName("DocumentType specific values match expectations")
        void documentType_specificValues() {
            assertEquals("AnnualReport", DocumentData.DocumentType.ANNUAL_REPORT.getValue());
            assertEquals("AuditReport", DocumentData.DocumentType.AUDIT_REPORT.getValue());
            assertEquals("Factsheet", DocumentData.DocumentType.FACTSHEET.getValue());
            assertEquals("PRIIPS-KID", DocumentData.DocumentType.PRIIPS_KID.getValue());
        }

        @Test
        @DisplayName("DocumentType valueOf round-trips for every constant")
        void documentType_valueOf() {
            for (DocumentData.DocumentType dt : DocumentData.DocumentType.values()) {
                assertEquals(dt, DocumentData.DocumentType.valueOf(dt.name()));
            }
        }

        @Test
        @DisplayName("DocumentType valueOf with invalid name throws")
        void documentType_valueOf_invalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> DocumentData.DocumentType.valueOf("NONEXISTENT"));
        }
    }

    // ========================================================================
    // AvailableDataInfo + FDPContentType enum
    // ========================================================================

    @Nested
    @DisplayName("AvailableDataInfo")
    class AvailableDataInfoTests {

        @Test
        @DisplayName("Parameterised constructor sets identifier, contentDate, contentType")
        void paramConstructor_setsFields() {
            LocalDate date = LocalDate.of(2025, 3, 1);
            AvailableDataInfo info = new AvailableDataInfo("IDENT_1", date,
                    AvailableDataInfo.FDPContentType.REG);

            assertEquals("IDENT_1", info.getIdentifier());
            assertEquals(date, info.getContentDate());
            assertEquals(AvailableDataInfo.FDPContentType.REG, info.getContentType());
        }

        @Test
        @DisplayName("Default constructor leaves fields null/false")
        void defaultConstructor() {
            AvailableDataInfo info = new AvailableDataInfo();
            assertNull(info.getIdentifier());
            assertNull(info.getContentDate());
            assertNull(info.getUploadTime());
            assertNull(info.getContentType());
            assertFalse(info.isHasData());
        }

        @Test
        @DisplayName("hasData flag can be toggled")
        void hasData_toggle() {
            AvailableDataInfo info = new AvailableDataInfo();
            assertFalse(info.isHasData());
            info.setHasData(true);
            assertTrue(info.isHasData());
        }

        // ---- FDPContentType enum ----

        @Test
        @DisplayName("FDPContentType descriptions match expected values")
        void fdpContentType_descriptions() {
            assertEquals("Fund Data", AvailableDataInfo.FDPContentType.FUND.getDescription());
            assertEquals("Regulatory Reporting", AvailableDataInfo.FDPContentType.REG.getDescription());
            assertEquals("Documents", AvailableDataInfo.FDPContentType.DOC.getDescription());
        }

        @Test
        @DisplayName("FDPContentType has exactly 3 constants")
        void fdpContentType_count() {
            assertEquals(3, AvailableDataInfo.FDPContentType.values().length);
        }

        @Test
        @DisplayName("FDPContentType valueOf round-trips correctly")
        void fdpContentType_valueOf() {
            assertEquals(AvailableDataInfo.FDPContentType.FUND,
                    AvailableDataInfo.FDPContentType.valueOf("FUND"));
            assertEquals(AvailableDataInfo.FDPContentType.DOC,
                    AvailableDataInfo.FDPContentType.valueOf("DOC"));
        }

        @Test
        @DisplayName("FDPContentType valueOf with invalid name throws")
        void fdpContentType_valueOf_invalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> AvailableDataInfo.FDPContentType.valueOf("XYZ"));
        }

        @Test
        @DisplayName("uploadTime can be set and retrieved")
        void uploadTime_setAndGet() {
            AvailableDataInfo info = new AvailableDataInfo();
            LocalDateTime time = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
            info.setUploadTime(time);
            assertEquals(time, info.getUploadTime());
        }
    }

    // ========================================================================
    // RegulatoryReporting + ReportingType enum
    // ========================================================================

    @Nested
    @DisplayName("RegulatoryReporting")
    class RegulatoryReportingTests {

        @Test
        @DisplayName("Parameterised constructor sets fields correctly")
        void paramConstructor_setsFields() {
            LocalDate date = LocalDate.of(2025, 9, 30);
            RegulatoryReporting rr = new RegulatoryReporting("IDENT",
                    RegulatoryReporting.ReportingType.EMIR, date);

            assertEquals("IDENT", rr.getIdentifier());
            assertEquals(RegulatoryReporting.ReportingType.EMIR, rr.getReportingType());
            assertEquals(date, rr.getContentDate());
        }

        @Test
        @DisplayName("Default constructor leaves fields null")
        void defaultConstructor() {
            RegulatoryReporting rr = new RegulatoryReporting();
            assertNull(rr.getIdentifier());
            assertNull(rr.getReportingType());
            assertNull(rr.getContentDate());
            assertNull(rr.getXmlContent());
            assertNull(rr.getStatus());
        }

        // ---- ReportingType enum ----

        @Test
        @DisplayName("ReportingType values match expected strings")
        void reportingType_values() {
            assertEquals("EMIR", RegulatoryReporting.ReportingType.EMIR.getValue());
            assertEquals("KIIDs", RegulatoryReporting.ReportingType.KIIDS.getValue());
            assertEquals("EMT", RegulatoryReporting.ReportingType.EMT.getValue());
            assertEquals("TripartiteTemplateSolvencyII",
                    RegulatoryReporting.ReportingType.TRIPARTITE_SOLVENCY_II.getValue());
            assertEquals("PRIIPS", RegulatoryReporting.ReportingType.PRIIPS.getValue());
            assertEquals("all", RegulatoryReporting.ReportingType.ALL.getValue());
        }

        @Test
        @DisplayName("ReportingType has exactly 6 constants")
        void reportingType_count() {
            assertEquals(6, RegulatoryReporting.ReportingType.values().length);
        }

        @Test
        @DisplayName("ReportingType valueOf round-trips for every constant")
        void reportingType_valueOf() {
            for (RegulatoryReporting.ReportingType rt : RegulatoryReporting.ReportingType.values()) {
                assertEquals(rt, RegulatoryReporting.ReportingType.valueOf(rt.name()));
            }
        }

        @Test
        @DisplayName("ReportingType valueOf with invalid name throws")
        void reportingType_valueOf_invalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> RegulatoryReporting.ReportingType.valueOf("UNKNOWN"));
        }

        @Test
        @DisplayName("All ReportingType constants have non-empty values")
        void reportingType_allValuesNonEmpty() {
            for (RegulatoryReporting.ReportingType rt : RegulatoryReporting.ReportingType.values()) {
                assertNotNull(rt.getValue(), "getValue() should not be null for " + rt.name());
                assertFalse(rt.getValue().isEmpty(), "getValue() should not be empty for " + rt.name());
            }
        }
    }

    // ========================================================================
    // FundData
    // ========================================================================

    @Nested
    @DisplayName("FundData")
    class FundDataTests {

        @Test
        @DisplayName("Parameterised constructor sets leiOenId, fundName, contentDate")
        void paramConstructor_setsFields() {
            LocalDate date = LocalDate.of(2025, 12, 31);
            FundData fd = new FundData("LEI_ABC", "Test Fund", date);

            assertEquals("LEI_ABC", fd.getLeiOenId());
            assertEquals("Test Fund", fd.getFundName());
            assertEquals(date, fd.getContentDate());
        }

        @Test
        @DisplayName("Default constructor leaves all fields null")
        void defaultConstructor() {
            FundData fd = new FundData();
            assertNull(fd.getLeiOenId());
            assertNull(fd.getFundName());
            assertNull(fd.getContentDate());
            assertNull(fd.getProfile());
            assertNull(fd.getXmlContent());
            assertNull(fd.getStatus());
        }

        @Test
        @DisplayName("All setters update fields that getters return")
        void settersAndGetters() {
            FundData fd = new FundData();
            fd.setProfile("FULL");
            fd.setXmlContent("<fund/>");
            fd.setStatus("OK");

            assertEquals("FULL", fd.getProfile());
            assertEquals("<fund/>", fd.getXmlContent());
            assertEquals("OK", fd.getStatus());
        }
    }

    // ========================================================================
    // ShareClassData
    // ========================================================================

    @Nested
    @DisplayName("ShareClassData")
    class ShareClassDataTests {

        @Test
        @DisplayName("Parameterised constructor sets isin, shareClassName, contentDate")
        void paramConstructor_setsFields() {
            LocalDate date = LocalDate.of(2025, 7, 1);
            ShareClassData scd = new ShareClassData("AT0000123456", "Class A", date);

            assertEquals("AT0000123456", scd.getIsin());
            assertEquals("Class A", scd.getShareClassName());
            assertEquals(date, scd.getContentDate());
        }

        @Test
        @DisplayName("Default constructor leaves all fields null")
        void defaultConstructor() {
            ShareClassData scd = new ShareClassData();
            assertNull(scd.getIsin());
            assertNull(scd.getShareClassName());
            assertNull(scd.getContentDate());
            assertNull(scd.getProfile());
            assertNull(scd.getXmlContent());
            assertNull(scd.getFundName());
            assertNull(scd.getStatus());
        }

        @Test
        @DisplayName("All setters update fields that getters return")
        void settersAndGetters() {
            ShareClassData scd = new ShareClassData();
            scd.setFundName("Parent Fund");
            scd.setProfile("COMPACT");
            scd.setXmlContent("<shareclass/>");
            scd.setStatus("PENDING");

            assertEquals("Parent Fund", scd.getFundName());
            assertEquals("COMPACT", scd.getProfile());
            assertEquals("<shareclass/>", scd.getXmlContent());
            assertEquals("PENDING", scd.getStatus());
        }
    }

    // ========================================================================
    // OeNBData + OeNBMode enum
    // ========================================================================

    @Nested
    @DisplayName("OeNBData")
    class OeNBDataTests {

        @Test
        @DisplayName("Parameterised constructor sets oenbId, contentDate, mode")
        void paramConstructor_setsFields() {
            LocalDate date = LocalDate.of(2025, 4, 15);
            OeNBData data = new OeNBData("OENB_42", date, OeNBData.OeNBMode.AGGREGIERUNG);

            assertEquals("OENB_42", data.getOenbId());
            assertEquals(date, data.getContentDate());
            assertEquals(OeNBData.OeNBMode.AGGREGIERUNG, data.getMode());
        }

        @Test
        @DisplayName("Default constructor leaves fields null/false")
        void defaultConstructor() {
            OeNBData data = new OeNBData();
            assertNull(data.getOenbId());
            assertNull(data.getContentDate());
            assertNull(data.getMode());
            assertNull(data.getXmlContent());
            assertFalse(data.isValid());
            assertNull(data.getCheckStatus());
            assertNull(data.getFundName());
        }

        @Test
        @DisplayName("valid flag can be toggled")
        void valid_toggle() {
            OeNBData data = new OeNBData();
            assertFalse(data.isValid());
            data.setValid(true);
            assertTrue(data.isValid());
            data.setValid(false);
            assertFalse(data.isValid());
        }

        // ---- OeNBMode enum ----

        @Test
        @DisplayName("OeNBMode has exactly 3 constants")
        void oeNBMode_count() {
            assertEquals(3, OeNBData.OeNBMode.values().length);
        }

        @Test
        @DisplayName("OeNBMode constants are AGGREGIERUNG, SECBYSEC, CHECK")
        void oeNBMode_constants() {
            OeNBData.OeNBMode[] modes = OeNBData.OeNBMode.values();
            List<String> names = Arrays.stream(modes).map(Enum::name).toList();

            assertTrue(names.contains("AGGREGIERUNG"));
            assertTrue(names.contains("SECBYSEC"));
            assertTrue(names.contains("CHECK"));
        }

        @Test
        @DisplayName("OeNBMode valueOf round-trips correctly")
        void oeNBMode_valueOf() {
            assertEquals(OeNBData.OeNBMode.AGGREGIERUNG,
                    OeNBData.OeNBMode.valueOf("AGGREGIERUNG"));
            assertEquals(OeNBData.OeNBMode.SECBYSEC,
                    OeNBData.OeNBMode.valueOf("SECBYSEC"));
            assertEquals(OeNBData.OeNBMode.CHECK,
                    OeNBData.OeNBMode.valueOf("CHECK"));
        }

        @Test
        @DisplayName("OeNBMode valueOf with invalid name throws")
        void oeNBMode_valueOf_invalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> OeNBData.OeNBMode.valueOf("INVALID_MODE"));
        }
    }
}
