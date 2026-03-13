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
package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NewInformationEntry, DownloadedInformationEntry, and RuleRow
 * model classes.
 */
class EntryModelTest {

    // =======================================================================
    // NewInformationEntry
    // =======================================================================
    @Nested
    @DisplayName("NewInformationEntry")
    class NewInformationEntryTests {

        private NewInformationEntry entry;

        @BeforeEach
        void setUp() {
            entry = new NewInformationEntry();
        }

        // -- ContentType.fromString() --

        @Test
        @DisplayName("ContentType.fromString('FUND') should return FUND")
        void fromString_fund() {
            assertEquals(NewInformationEntry.ContentType.FUND,
                    NewInformationEntry.ContentType.fromString("FUND"),
                    "String 'FUND' should map to ContentType.FUND");
        }

        @Test
        @DisplayName("ContentType.fromString('DOC') should return DOC")
        void fromString_doc() {
            assertEquals(NewInformationEntry.ContentType.DOC,
                    NewInformationEntry.ContentType.fromString("DOC"),
                    "String 'DOC' should map to ContentType.DOC");
        }

        @Test
        @DisplayName("ContentType.fromString('REG') should return REG")
        void fromString_reg() {
            assertEquals(NewInformationEntry.ContentType.REG,
                    NewInformationEntry.ContentType.fromString("REG"),
                    "String 'REG' should map to ContentType.REG");
        }

        @Test
        @DisplayName("ContentType.fromString(null) should return null")
        void fromString_null() {
            assertNull(NewInformationEntry.ContentType.fromString(null),
                    "Null input should return null");
        }

        @Test
        @DisplayName("ContentType.fromString('UNKNOWN') should return null")
        void fromString_unknown() {
            assertNull(NewInformationEntry.ContentType.fromString("UNKNOWN"),
                    "Unknown string should return null");
        }

        @Test
        @DisplayName("ContentType.fromString('') should return null")
        void fromString_empty() {
            assertNull(NewInformationEntry.ContentType.fromString(""),
                    "Empty string should return null");
        }

        @Test
        @DisplayName("ContentType.fromString should be case-sensitive")
        void fromString_caseSensitive() {
            assertNull(NewInformationEntry.ContentType.fromString("fund"),
                    "Lowercase 'fund' should not match (case-sensitive)");
        }

        @Test
        @DisplayName("ContentType descriptions should not be empty")
        void descriptions_notEmpty() {
            for (NewInformationEntry.ContentType ct : NewInformationEntry.ContentType.values()) {
                assertNotNull(ct.getDescription(), "Description should not be null for " + ct);
                assertFalse(ct.getDescription().isEmpty(), "Description should not be empty for " + ct);
            }
        }

        // -- addDataSupplier with null guards --

        @Test
        @DisplayName("addDataSupplier should add valid supplier")
        void addDataSupplier_valid() {
            entry.addDataSupplier("DDS001");
            assertEquals(1, entry.getDataSuppliers().size(), "Should have 1 supplier");
            assertEquals("DDS001", entry.getDataSuppliers().get(0), "Supplier should match");
        }

        @Test
        @DisplayName("addDataSupplier should reject null")
        void addDataSupplier_null() {
            entry.addDataSupplier(null);
            assertTrue(entry.getDataSuppliers().isEmpty(), "Null supplier should be rejected");
        }

        @Test
        @DisplayName("addDataSupplier should reject empty string")
        void addDataSupplier_empty() {
            entry.addDataSupplier("");
            assertTrue(entry.getDataSuppliers().isEmpty(), "Empty supplier should be rejected");
        }

        // -- addIsin with null guards --

        @Test
        @DisplayName("addIsin should add valid ISIN")
        void addIsin_valid() {
            entry.addIsin("AT0000A12345");
            assertEquals(1, entry.getIsins().size(), "Should have 1 ISIN");
        }

        @Test
        @DisplayName("addIsin should reject null")
        void addIsin_null() {
            entry.addIsin(null);
            assertTrue(entry.getIsins().isEmpty(), "Null ISIN should be rejected");
        }

        @Test
        @DisplayName("addIsin should reject empty string")
        void addIsin_empty() {
            entry.addIsin("");
            assertTrue(entry.getIsins().isEmpty(), "Empty ISIN should be rejected");
        }

        // -- addProfile with null guards --

        @Test
        @DisplayName("addProfile should add valid profile")
        void addProfile_valid() {
            entry.addProfile("FundsXML_Full");
            assertEquals(1, entry.getProfiles().size(), "Should have 1 profile");
        }

        @Test
        @DisplayName("addProfile should reject null")
        void addProfile_null() {
            entry.addProfile(null);
            assertTrue(entry.getProfiles().isEmpty(), "Null profile should be rejected");
        }

        @Test
        @DisplayName("addProfile should reject empty string")
        void addProfile_empty() {
            entry.addProfile("");
            assertTrue(entry.getProfiles().isEmpty(), "Empty profile should be rejected");
        }

        // -- getIdentifierSummary() --

        @Test
        @DisplayName("getIdentifierSummary() with LEI only")
        void identifierSummary_leiOnly() {
            entry.setLei("529900HNOAA1KXQJUQ27");
            assertEquals("LEI: 529900HNOAA1KXQJUQ27", entry.getIdentifierSummary(),
                    "Should show LEI when only LEI is set");
        }

        @Test
        @DisplayName("getIdentifierSummary() with ISINs only")
        void identifierSummary_isinsOnly() {
            entry.addIsin("AT0000A12345");
            entry.addIsin("AT0000A67890");
            assertEquals("2 ISIN(s)", entry.getIdentifierSummary(),
                    "Should show ISIN count when only ISINs are set");
        }

        @Test
        @DisplayName("getIdentifierSummary() with LEI and ISINs")
        void identifierSummary_leiAndIsins() {
            entry.setLei("529900HNOAA1KXQJUQ27");
            entry.addIsin("AT0000A12345");
            String summary = entry.getIdentifierSummary();
            assertTrue(summary.contains("LEI:"), "Should contain LEI prefix");
            assertTrue(summary.contains("1 ISIN(s)"), "Should contain ISIN count");
            assertTrue(summary.contains(", "), "Should use comma separator");
        }

        @Test
        @DisplayName("getIdentifierSummary() with nothing set should return empty string")
        void identifierSummary_empty() {
            assertEquals("", entry.getIdentifierSummary(),
                    "Should return empty string when no identifiers set");
        }

        @Test
        @DisplayName("getIdentifierSummary() with empty LEI should not show LEI")
        void identifierSummary_emptyLei() {
            entry.setLei("");
            assertEquals("", entry.getIdentifierSummary(),
                    "Empty LEI should not appear in summary");
        }

        // -- getDetails() --

        @Test
        @DisplayName("getDetails() for FUND should return identifier summary")
        void details_fund() {
            entry.setContentType(NewInformationEntry.ContentType.FUND);
            entry.setLei("529900HNOAA1KXQJUQ27");
            assertEquals("LEI: 529900HNOAA1KXQJUQ27", entry.getDetails(),
                    "FUND details should be the identifier summary");
        }

        @Test
        @DisplayName("getDetails() for DOC should return documentType and language")
        void details_doc() {
            entry.setContentType(NewInformationEntry.ContentType.DOC);
            entry.setDocumentType("KIID");
            entry.setDocumentLanguage("DE");
            assertEquals("KIID (DE)", entry.getDetails(),
                    "DOC details should be 'type (language)'");
        }

        @Test
        @DisplayName("getDetails() for DOC with null documentType should return empty string")
        void details_doc_nullType() {
            entry.setContentType(NewInformationEntry.ContentType.DOC);
            assertEquals("", entry.getDetails(),
                    "DOC details should be empty when documentType is null");
        }

        @Test
        @DisplayName("getDetails() for REG should return reportingType")
        void details_reg() {
            entry.setContentType(NewInformationEntry.ContentType.REG);
            entry.setReportingType("OeNB_FULL");
            assertEquals("OeNB_FULL", entry.getDetails(),
                    "REG details should be the reporting type");
        }

        @Test
        @DisplayName("getDetails() for REG with null reportingType should return empty string")
        void details_reg_nullType() {
            entry.setContentType(NewInformationEntry.ContentType.REG);
            assertEquals("", entry.getDetails(),
                    "REG details should be empty when reportingType is null");
        }

        // -- getIsinCount() --

        @Test
        @DisplayName("getIsinCount() should return 0 when no ISINs added")
        void isinCount_empty() {
            assertEquals(0, entry.getIsinCount(), "Should return 0 for empty ISINs");
        }

        @Test
        @DisplayName("getIsinCount() should return correct count")
        void isinCount_multiple() {
            entry.addIsin("AT0000A12345");
            entry.addIsin("AT0000A67890");
            entry.addIsin("AT0000A11111");
            assertEquals(3, entry.getIsinCount(), "Should return 3 for three ISINs");
        }

        // -- getFormattedUploadDateTime / getFormattedContentDate --

        @Test
        @DisplayName("getFormattedUploadDateTime() should return ISO string when set")
        void formattedUploadDateTime_set() {
            LocalDateTime dt = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
            entry.setUploadDateTime(dt);
            assertEquals("2024-06-15T14:30", entry.getFormattedUploadDateTime(),
                    "Should return ISO-8601 formatted datetime");
        }

        @Test
        @DisplayName("getFormattedUploadDateTime() should return empty string when null")
        void formattedUploadDateTime_null() {
            assertEquals("", entry.getFormattedUploadDateTime(),
                    "Should return empty string when uploadDateTime is null");
        }

        @Test
        @DisplayName("getFormattedContentDate() should return ISO string when set")
        void formattedContentDate_set() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            entry.setContentDate(date);
            assertEquals("2024-06-15", entry.getFormattedContentDate(),
                    "Should return ISO-8601 formatted date");
        }

        @Test
        @DisplayName("getFormattedContentDate() should return empty string when null")
        void formattedContentDate_null() {
            assertEquals("", entry.getFormattedContentDate(),
                    "Should return empty string when contentDate is null");
        }

        // -- String join methods --

        @Test
        @DisplayName("getDataSuppliersString() should join with comma separator")
        void dataSuppliersString() {
            entry.addDataSupplier("DDS001");
            entry.addDataSupplier("DDS002");
            assertEquals("DDS001, DDS002", entry.getDataSuppliersString(),
                    "Suppliers should be joined with ', '");
        }

        @Test
        @DisplayName("getDataSuppliersString() should return empty string when empty")
        void dataSuppliersString_empty() {
            assertEquals("", entry.getDataSuppliersString(),
                    "Empty list should produce empty string");
        }

        @Test
        @DisplayName("getIsinsString() should join with comma separator")
        void isinsString() {
            entry.addIsin("AT0000A12345");
            entry.addIsin("AT0000A67890");
            assertEquals("AT0000A12345, AT0000A67890", entry.getIsinsString(),
                    "ISINs should be joined with ', '");
        }

        @Test
        @DisplayName("getProfilesString() should join with comma separator")
        void profilesString() {
            entry.addProfile("FundsXML_Full");
            entry.addProfile("FundsXML_Light");
            assertEquals("FundsXML_Full, FundsXML_Light", entry.getProfilesString(),
                    "Profiles should be joined with ', '");
        }

        // -- Defensive copy on getters --

        @Test
        @DisplayName("getDataSuppliers() should return a defensive copy")
        void dataSuppliers_defensiveCopy() {
            entry.addDataSupplier("DDS001");
            List<String> copy = entry.getDataSuppliers();
            copy.add("INJECTED");
            assertEquals(1, entry.getDataSuppliers().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("getIsins() should return a defensive copy")
        void isins_defensiveCopy() {
            entry.addIsin("AT0000A12345");
            List<String> copy = entry.getIsins();
            copy.add("INJECTED");
            assertEquals(1, entry.getIsins().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("getProfiles() should return a defensive copy")
        void profiles_defensiveCopy() {
            entry.addProfile("FundsXML_Full");
            List<String> copy = entry.getProfiles();
            copy.add("INJECTED");
            assertEquals(1, entry.getProfiles().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        // -- setters with null should initialize empty lists --

        @Test
        @DisplayName("setDataSuppliers(null) should initialize empty list")
        void setDataSuppliers_null() {
            entry.setDataSuppliers(null);
            assertNotNull(entry.getDataSuppliers(), "Should never be null after setting null");
            assertTrue(entry.getDataSuppliers().isEmpty(), "Should be empty after setting null");
        }

        @Test
        @DisplayName("setIsins(null) should initialize empty list")
        void setIsins_null() {
            entry.setIsins(null);
            assertNotNull(entry.getIsins(), "Should never be null after setting null");
            assertTrue(entry.getIsins().isEmpty(), "Should be empty after setting null");
        }

        @Test
        @DisplayName("setProfiles(null) should initialize empty list")
        void setProfiles_null() {
            entry.setProfiles(null);
            assertNotNull(entry.getProfiles(), "Should never be null after setting null");
            assertTrue(entry.getProfiles().isEmpty(), "Should be empty after setting null");
        }

        // -- setters should defensively copy input --

        @Test
        @DisplayName("setDataSuppliers() should defensively copy the input list")
        void setDataSuppliers_defensiveCopy() {
            List<String> input = new ArrayList<>();
            input.add("DDS001");
            entry.setDataSuppliers(input);
            input.add("INJECTED");
            assertEquals(1, entry.getDataSuppliers().size(),
                    "Modifying the input list should not affect the entry");
        }

        @Test
        @DisplayName("setIsins() should defensively copy the input list")
        void setIsins_defensiveCopy() {
            List<String> input = new ArrayList<>();
            input.add("AT0000A12345");
            entry.setIsins(input);
            input.add("INJECTED");
            assertEquals(1, entry.getIsins().size(),
                    "Modifying the input list should not affect the entry");
        }

        @Test
        @DisplayName("setProfiles() should defensively copy the input list")
        void setProfiles_defensiveCopy() {
            List<String> input = new ArrayList<>();
            input.add("FundsXML_Full");
            entry.setProfiles(input);
            input.add("INJECTED");
            assertEquals(1, entry.getProfiles().size(),
                    "Modifying the input list should not affect the entry");
        }

        // -- Parameterized constructor --

        @Test
        @DisplayName("Constructor with parameters should set fields correctly")
        void parameterizedConstructor() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
            NewInformationEntry e = new NewInformationEntry(
                    NewInformationEntry.ContentType.FUND, date, dateTime);

            assertEquals(NewInformationEntry.ContentType.FUND, e.getContentType(),
                    "ContentType should match");
            assertEquals(date, e.getContentDate(), "ContentDate should match");
            assertEquals(dateTime, e.getUploadDateTime(), "UploadDateTime should match");
            assertTrue(e.getDataSuppliers().isEmpty(), "DataSuppliers should be initialized empty");
            assertTrue(e.getIsins().isEmpty(), "ISINs should be initialized empty");
            assertTrue(e.getProfiles().isEmpty(), "Profiles should be initialized empty");
        }

        // -- toString --

        @Test
        @DisplayName("toString() should contain key fields")
        void toStringContainsFields() {
            entry.setContentType(NewInformationEntry.ContentType.FUND);
            entry.setLei("529900HNOAA1KXQJUQ27");
            String result = entry.toString();
            assertTrue(result.contains("FUND"), "toString should contain content type");
            assertTrue(result.contains("529900HNOAA1KXQJUQ27"), "toString should contain LEI");
        }
    }

    // =======================================================================
    // DownloadedInformationEntry
    // =======================================================================
    @Nested
    @DisplayName("DownloadedInformationEntry")
    class DownloadedInformationEntryTests {

        private DownloadedInformationEntry entry;

        @BeforeEach
        void setUp() {
            entry = new DownloadedInformationEntry();
        }

        // -- ContentType.fromString() --

        @Test
        @DisplayName("ContentType.fromString('FUND') should return FUND")
        void fromString_fund() {
            assertEquals(DownloadedInformationEntry.ContentType.FUND,
                    DownloadedInformationEntry.ContentType.fromString("FUND"),
                    "String 'FUND' should map to ContentType.FUND");
        }

        @Test
        @DisplayName("ContentType.fromString('DOC') should return DOC")
        void fromString_doc() {
            assertEquals(DownloadedInformationEntry.ContentType.DOC,
                    DownloadedInformationEntry.ContentType.fromString("DOC"),
                    "String 'DOC' should map to ContentType.DOC");
        }

        @Test
        @DisplayName("ContentType.fromString('REG') should return REG")
        void fromString_reg() {
            assertEquals(DownloadedInformationEntry.ContentType.REG,
                    DownloadedInformationEntry.ContentType.fromString("REG"),
                    "String 'REG' should map to ContentType.REG");
        }

        @Test
        @DisplayName("ContentType.fromString(null) should return null")
        void fromString_null() {
            assertNull(DownloadedInformationEntry.ContentType.fromString(null),
                    "Null input should return null");
        }

        @Test
        @DisplayName("ContentType.fromString('INVALID') should return null")
        void fromString_invalid() {
            assertNull(DownloadedInformationEntry.ContentType.fromString("INVALID"),
                    "Invalid string should return null");
        }

        @Test
        @DisplayName("ContentType.fromString('') should return null")
        void fromString_empty() {
            assertNull(DownloadedInformationEntry.ContentType.fromString(""),
                    "Empty string should return null");
        }

        @Test
        @DisplayName("ContentType descriptions should not be empty")
        void descriptions_notEmpty() {
            for (DownloadedInformationEntry.ContentType ct : DownloadedInformationEntry.ContentType.values()) {
                assertNotNull(ct.getDescription(), "Description should not be null for " + ct);
                assertFalse(ct.getDescription().isEmpty(), "Description should not be empty for " + ct);
            }
        }

        // -- getAccessInfo() priority --

        @Test
        @DisplayName("getAccessInfo() should return 'OWNER' when isOwner is true")
        void accessInfo_owner() {
            entry.setOwner(true);
            entry.setAccessRuleId("RULE-123");
            assertEquals("OWNER", entry.getAccessInfo(),
                    "Owner should take priority over access rule ID");
        }

        @Test
        @DisplayName("getAccessInfo() should return 'Rule: ...' when not owner but has ruleId")
        void accessInfo_rule() {
            entry.setOwner(false);
            entry.setAccessRuleId("RULE-123");
            assertEquals("Rule: RULE-123", entry.getAccessInfo(),
                    "Should show rule ID when not owner");
        }

        @Test
        @DisplayName("getAccessInfo() should return empty string when not owner and no ruleId")
        void accessInfo_empty() {
            entry.setOwner(false);
            entry.setAccessRuleId(null);
            assertEquals("", entry.getAccessInfo(),
                    "Should return empty string when no access info available");
        }

        @Test
        @DisplayName("getAccessInfo() should return empty string when ruleId is empty")
        void accessInfo_emptyRuleId() {
            entry.setOwner(false);
            entry.setAccessRuleId("");
            assertEquals("", entry.getAccessInfo(),
                    "Should return empty string when ruleId is empty");
        }

        // -- getIdentifierSummary() with OeNB --

        @Test
        @DisplayName("getIdentifierSummary() with LEI only")
        void identifierSummary_leiOnly() {
            entry.setLei("529900HNOAA1KXQJUQ27");
            assertEquals("LEI: 529900HNOAA1KXQJUQ27", entry.getIdentifierSummary(),
                    "Should show LEI when only LEI is set");
        }

        @Test
        @DisplayName("getIdentifierSummary() with OeNB only")
        void identifierSummary_oenbOnly() {
            entry.setOenbIdentnr("12345");
            assertEquals("OeNB: 12345", entry.getIdentifierSummary(),
                    "Should show OeNB when only OeNB ID is set");
        }

        @Test
        @DisplayName("getIdentifierSummary() with LEI, OeNB, and ISINs")
        void identifierSummary_allIdentifiers() {
            entry.setLei("529900HNOAA1KXQJUQ27");
            entry.setOenbIdentnr("12345");
            entry.addIsin("AT0000A12345");

            String summary = entry.getIdentifierSummary();
            assertTrue(summary.contains("LEI: 529900HNOAA1KXQJUQ27"), "Should contain LEI");
            assertTrue(summary.contains("OeNB: 12345"), "Should contain OeNB");
            assertTrue(summary.contains("1 ISIN(s)"), "Should contain ISIN count");
        }

        @Test
        @DisplayName("getIdentifierSummary() with nothing set should return empty string")
        void identifierSummary_empty() {
            assertEquals("", entry.getIdentifierSummary(),
                    "Should return empty string when no identifiers set");
        }

        @Test
        @DisplayName("getIdentifierSummary() with empty LEI and empty OeNB")
        void identifierSummary_emptyStrings() {
            entry.setLei("");
            entry.setOenbIdentnr("");
            assertEquals("", entry.getIdentifierSummary(),
                    "Empty strings should not appear in summary");
        }

        @Test
        @DisplayName("getIdentifierSummary() with OeNB and ISINs but no LEI")
        void identifierSummary_oenbAndIsins() {
            entry.setOenbIdentnr("12345");
            entry.addIsin("AT0000A12345");
            entry.addIsin("AT0000A67890");

            String summary = entry.getIdentifierSummary();
            assertTrue(summary.startsWith("OeNB: 12345"), "Should start with OeNB");
            assertTrue(summary.contains("2 ISIN(s)"), "Should contain ISIN count");
        }

        // -- getDetails() --

        @Test
        @DisplayName("getDetails() for FUND should return identifier summary")
        void details_fund() {
            entry.setContentType(DownloadedInformationEntry.ContentType.FUND);
            entry.setLei("529900HNOAA1KXQJUQ27");
            entry.setOenbIdentnr("12345");
            String details = entry.getDetails();
            assertTrue(details.contains("LEI:"), "FUND details should contain LEI");
            assertTrue(details.contains("OeNB:"), "FUND details should contain OeNB");
        }

        @Test
        @DisplayName("getDetails() for DOC should return documentType and language")
        void details_doc() {
            entry.setContentType(DownloadedInformationEntry.ContentType.DOC);
            entry.setDocumentType("KIID");
            entry.setDocumentLanguage("EN");
            assertEquals("KIID (EN)", entry.getDetails(),
                    "DOC details should be 'type (language)'");
        }

        @Test
        @DisplayName("getDetails() for DOC with null documentType should return empty")
        void details_doc_nullType() {
            entry.setContentType(DownloadedInformationEntry.ContentType.DOC);
            assertEquals("", entry.getDetails(),
                    "DOC details should be empty when documentType is null");
        }

        @Test
        @DisplayName("getDetails() for REG should return reportingType")
        void details_reg() {
            entry.setContentType(DownloadedInformationEntry.ContentType.REG);
            entry.setReportingType("OeNB_FULL");
            assertEquals("OeNB_FULL", entry.getDetails(),
                    "REG details should be the reporting type");
        }

        @Test
        @DisplayName("getDetails() for REG with null reportingType should return empty")
        void details_reg_nullType() {
            entry.setContentType(DownloadedInformationEntry.ContentType.REG);
            assertEquals("", entry.getDetails(),
                    "REG details should be empty when reportingType is null");
        }

        // -- Formatted date/time methods --

        @Test
        @DisplayName("getFormattedDownloadDateTime() should return ISO string when set")
        void formattedDownloadDateTime_set() {
            LocalDateTime dt = LocalDateTime.of(2024, 3, 10, 9, 15, 0);
            entry.setDownloadDateTime(dt);
            assertEquals("2024-03-10T09:15", entry.getFormattedDownloadDateTime(),
                    "Should return ISO-8601 formatted datetime");
        }

        @Test
        @DisplayName("getFormattedDownloadDateTime() should return empty string when null")
        void formattedDownloadDateTime_null() {
            assertEquals("", entry.getFormattedDownloadDateTime(),
                    "Should return empty string when downloadDateTime is null");
        }

        @Test
        @DisplayName("getFormattedContentDate() should return ISO string when set")
        void formattedContentDate_set() {
            entry.setContentDate(LocalDate.of(2024, 3, 10));
            assertEquals("2024-03-10", entry.getFormattedContentDate(),
                    "Should return ISO-8601 formatted date");
        }

        @Test
        @DisplayName("getFormattedContentDate() should return empty string when null")
        void formattedContentDate_null() {
            assertEquals("", entry.getFormattedContentDate(),
                    "Should return empty string when contentDate is null");
        }

        // -- add methods with null guards --

        @Test
        @DisplayName("addDataSupplier should reject null")
        void addDataSupplier_null() {
            entry.addDataSupplier(null);
            assertTrue(entry.getDataSuppliers().isEmpty(), "Null supplier should be rejected");
        }

        @Test
        @DisplayName("addDataSupplier should reject empty string")
        void addDataSupplier_empty() {
            entry.addDataSupplier("");
            assertTrue(entry.getDataSuppliers().isEmpty(), "Empty supplier should be rejected");
        }

        @Test
        @DisplayName("addIsin should reject null")
        void addIsin_null() {
            entry.addIsin(null);
            assertTrue(entry.getIsins().isEmpty(), "Null ISIN should be rejected");
        }

        @Test
        @DisplayName("addIsin should reject empty string")
        void addIsin_empty() {
            entry.addIsin("");
            assertTrue(entry.getIsins().isEmpty(), "Empty ISIN should be rejected");
        }

        @Test
        @DisplayName("addProfile should reject null")
        void addProfile_null() {
            entry.addProfile(null);
            assertTrue(entry.getProfiles().isEmpty(), "Null profile should be rejected");
        }

        @Test
        @DisplayName("addProfile should reject empty string")
        void addProfile_empty() {
            entry.addProfile("");
            assertTrue(entry.getProfiles().isEmpty(), "Empty profile should be rejected");
        }

        // -- String join methods --

        @Test
        @DisplayName("getDataSuppliersString() should join with comma separator")
        void dataSuppliersString() {
            entry.addDataSupplier("DDS001");
            entry.addDataSupplier("DDS002");
            assertEquals("DDS001, DDS002", entry.getDataSuppliersString(),
                    "Suppliers should be joined with ', '");
        }

        @Test
        @DisplayName("getIsinsString() should join with comma separator")
        void isinsString() {
            entry.addIsin("AT0000A12345");
            entry.addIsin("AT0000A67890");
            assertEquals("AT0000A12345, AT0000A67890", entry.getIsinsString(),
                    "ISINs should be joined with ', '");
        }

        @Test
        @DisplayName("getProfilesString() should join with comma separator")
        void profilesString() {
            entry.addProfile("FundsXML_Full");
            entry.addProfile("FundsXML_Light");
            assertEquals("FundsXML_Full, FundsXML_Light", entry.getProfilesString(),
                    "Profiles should be joined with ', '");
        }

        // -- Defensive copy tests --

        @Test
        @DisplayName("getDataSuppliers() should return a defensive copy")
        void dataSuppliers_defensiveCopy() {
            entry.addDataSupplier("DDS001");
            entry.getDataSuppliers().add("INJECTED");
            assertEquals(1, entry.getDataSuppliers().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("getIsins() should return a defensive copy")
        void isins_defensiveCopy() {
            entry.addIsin("AT0000A12345");
            entry.getIsins().add("INJECTED");
            assertEquals(1, entry.getIsins().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("getProfiles() should return a defensive copy")
        void profiles_defensiveCopy() {
            entry.addProfile("FundsXML_Full");
            entry.getProfiles().add("INJECTED");
            assertEquals(1, entry.getProfiles().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        // -- Setter null safety and defensive copy --

        @Test
        @DisplayName("setDataSuppliers(null) should initialize empty list")
        void setDataSuppliers_null() {
            entry.setDataSuppliers(null);
            assertNotNull(entry.getDataSuppliers(), "Should not be null");
            assertTrue(entry.getDataSuppliers().isEmpty(), "Should be empty");
        }

        @Test
        @DisplayName("setIsins(null) should initialize empty list")
        void setIsins_null() {
            entry.setIsins(null);
            assertNotNull(entry.getIsins(), "Should not be null");
            assertTrue(entry.getIsins().isEmpty(), "Should be empty");
        }

        @Test
        @DisplayName("setProfiles(null) should initialize empty list")
        void setProfiles_null() {
            entry.setProfiles(null);
            assertNotNull(entry.getProfiles(), "Should not be null");
            assertTrue(entry.getProfiles().isEmpty(), "Should be empty");
        }

        @Test
        @DisplayName("setDataSuppliers() should defensively copy input")
        void setDataSuppliers_defensiveCopy() {
            List<String> input = new ArrayList<>();
            input.add("DDS001");
            entry.setDataSuppliers(input);
            input.add("INJECTED");
            assertEquals(1, entry.getDataSuppliers().size(),
                    "Modifying input list should not affect entry");
        }

        // -- toString --

        @Test
        @DisplayName("toString() should contain key fields")
        void toStringContainsFields() {
            entry.setContentType(DownloadedInformationEntry.ContentType.DOC);
            entry.setOwner(true);
            entry.setAccessRuleId("R-001");
            String result = entry.toString();
            assertTrue(result.contains("DOC"), "toString should contain content type");
            assertTrue(result.contains("true"), "toString should contain isOwner");
            assertTrue(result.contains("R-001"), "toString should contain access rule id");
        }
    }

    // =======================================================================
    // RuleRow
    // =======================================================================
    @Nested
    @DisplayName("RuleRow")
    class RuleRowTests {

        // -- Default constructor --

        @Test
        @DisplayName("Default constructor should create object with null fields")
        void defaultConstructor() {
            RuleRow row = new RuleRow();
            assertNull(row.getId(), "Id should be null");
            assertNull(row.getContentType(), "ContentType should be null");
            assertNull(row.getLei(), "LEI should be null");
            assertFalse(row.isRootRow(), "RootRow should default to false (null Boolean)");
            assertFalse(row.isCostsByDataSupplier(),
                    "CostsByDataSupplier should default to false (null Boolean)");
        }

        // -- Full constructor (15 params) --

        @Test
        @DisplayName("Full constructor should set all 15 fields correctly")
        void fullConstructor() {
            RuleRow row = new RuleRow("ID-1", "FUND", "FundsXML_Full",
                    "DDS001", "Test Supplier", "DDS002",
                    "2024-06-15T10:00:00", "3", "2024-01-01", "2024-12-31",
                    "DAILY", "529900HNOAA1KXQJUQ27", "12345",
                    "AT0000A12345", "AT0000A67890");

            assertEquals("ID-1", row.getId(), "Id should match");
            assertEquals("FUND", row.getContentType(), "ContentType should match");
            assertEquals("FundsXML_Full", row.getProfile(), "Profile should match");
            assertEquals("DDS001", row.getDataSupplierCreatorShort(), "CreatorShort should match");
            assertEquals("Test Supplier", row.getDataSupplierCreatorName(), "CreatorName should match");
            assertEquals("DDS002", row.getDataSuppliersGivenShort(), "GivenShort should match");
            assertEquals("2024-06-15T10:00:00", row.getCreationTime(), "CreationTime should match");
            assertEquals("3", row.getAccessDelayInDays(), "AccessDelayInDays should match");
            assertEquals("2024-01-01", row.getDateFrom(), "DateFrom should match");
            assertEquals("2024-12-31", row.getDateTo(), "DateTo should match");
            assertEquals("DAILY", row.getFrequency(), "Frequency should match");
            assertEquals("529900HNOAA1KXQJUQ27", row.getLei(), "LEI should match");
            assertEquals("12345", row.getOenbId(), "OeNB ID should match");
            assertEquals("AT0000A12345", row.getShareclassIsin(), "ShareclassIsin should match");
            assertEquals("AT0000A67890", row.getSegmentIsin(), "SegmentIsin should match");
        }

        // -- Extended constructor (17 params) with Boolean coercion --

        @Test
        @DisplayName("Extended constructor should accept Boolean costsByDataSupplier")
        void extendedConstructor_booleanCosts() {
            RuleRow row = new RuleRow("ID-1", "FUND", "Full",
                    "DDS001", "Supplier", "DDS002",
                    "2024-06-15", "3", "2024-01-01", "2024-12-31",
                    "DAILY", Boolean.TRUE, "LEI-123", "OeNB-123",
                    "ISIN-SC", "ISIN-SEG", true);

            assertTrue(row.isCostsByDataSupplier(),
                    "Boolean.TRUE should be stored as true for costsByDataSupplier");
            assertTrue(row.isRootRow(), "RootRow should be true");
        }

        @Test
        @DisplayName("Extended constructor should accept String 'true' as costsByDataSupplier")
        void extendedConstructor_stringTrueCosts() {
            RuleRow row = new RuleRow("ID-1", "FUND", "Full",
                    "DDS001", "Supplier", "DDS002",
                    "2024-06-15", "3", "2024-01-01", "2024-12-31",
                    "DAILY", "true", "LEI-123", "OeNB-123",
                    "ISIN-SC", "ISIN-SEG", false);

            assertTrue(row.isCostsByDataSupplier(),
                    "String 'true' should be parsed as true for costsByDataSupplier");
        }

        @Test
        @DisplayName("Extended constructor should accept String 'false' as costsByDataSupplier")
        void extendedConstructor_stringFalseCosts() {
            RuleRow row = new RuleRow("ID-1", "FUND", "Full",
                    "DDS001", "Supplier", "DDS002",
                    "2024-06-15", "3", "2024-01-01", "2024-12-31",
                    "DAILY", "false", "LEI-123", "OeNB-123",
                    "ISIN-SC", "ISIN-SEG", false);

            assertFalse(row.isCostsByDataSupplier(),
                    "String 'false' should be parsed as false for costsByDataSupplier");
        }

        @Test
        @DisplayName("Extended constructor should handle Boolean.FALSE for costsByDataSupplier")
        void extendedConstructor_booleanFalseCosts() {
            RuleRow row = new RuleRow("ID-1", "FUND", "Full",
                    "DDS001", "Supplier", "DDS002",
                    "2024-06-15", "3", "2024-01-01", "2024-12-31",
                    "DAILY", Boolean.FALSE, "LEI-123", "OeNB-123",
                    "ISIN-SC", "ISIN-SEG", false);

            assertFalse(row.isCostsByDataSupplier(),
                    "Boolean.FALSE should be stored as false for costsByDataSupplier");
        }

        @Test
        @DisplayName("Extended constructor should handle null costsByDataSupplier (no Boolean, no String)")
        void extendedConstructor_nullCosts() {
            RuleRow row = new RuleRow("ID-1", "FUND", "Full",
                    "DDS001", "Supplier", "DDS002",
                    "2024-06-15", "3", "2024-01-01", "2024-12-31",
                    "DAILY", null, "LEI-123", "OeNB-123",
                    "ISIN-SC", "ISIN-SEG", false);

            assertFalse(row.isCostsByDataSupplier(),
                    "Null should result in false for isCostsByDataSupplier");
        }

        @Test
        @DisplayName("Extended constructor should handle Integer costsByDataSupplier (neither Boolean nor String)")
        void extendedConstructor_integerCosts() {
            RuleRow row = new RuleRow("ID-1", "FUND", "Full",
                    "DDS001", "Supplier", "DDS002",
                    "2024-06-15", "3", "2024-01-01", "2024-12-31",
                    "DAILY", Integer.valueOf(42), "LEI-123", "OeNB-123",
                    "ISIN-SC", "ISIN-SEG", false);

            assertFalse(row.isCostsByDataSupplier(),
                    "Non-Boolean, non-String type should leave costsByDataSupplier as null/false");
        }

        // -- compareTo with nulls --

        @Test
        @DisplayName("compareTo should return 0 when both IDs are null")
        void compareTo_bothNull() {
            RuleRow a = new RuleRow();
            RuleRow b = new RuleRow();
            assertEquals(0, a.compareTo(b), "Both null IDs should compare as equal");
        }

        @Test
        @DisplayName("compareTo should return -1 when this.id is null and other.id is not")
        void compareTo_thisNull() {
            RuleRow a = new RuleRow();
            RuleRow b = new RuleRow();
            b.setId("B");
            assertEquals(-1, a.compareTo(b),
                    "Null ID should come before non-null ID");
        }

        @Test
        @DisplayName("compareTo should return 1 when this.id is not null and other.id is null")
        void compareTo_otherNull() {
            RuleRow a = new RuleRow();
            a.setId("A");
            RuleRow b = new RuleRow();
            assertEquals(1, a.compareTo(b),
                    "Non-null ID should come after null ID");
        }

        @Test
        @DisplayName("compareTo should delegate to String.compareTo when both IDs are non-null")
        void compareTo_bothNonNull() {
            RuleRow a = new RuleRow();
            a.setId("A");
            RuleRow b = new RuleRow();
            b.setId("B");
            assertTrue(a.compareTo(b) < 0, "'A' should come before 'B'");
            assertTrue(b.compareTo(a) > 0, "'B' should come after 'A'");
        }

        @Test
        @DisplayName("compareTo should return 0 for equal IDs")
        void compareTo_equal() {
            RuleRow a = new RuleRow();
            a.setId("SAME");
            RuleRow b = new RuleRow();
            b.setId("SAME");
            assertEquals(0, a.compareTo(b), "Equal IDs should compare as 0");
        }

        // -- equals/hashCode contract --

        @Test
        @DisplayName("equals should return true for same object")
        void equals_sameObject() {
            RuleRow row = new RuleRow();
            row.setId("ID-1");
            assertEquals(row, row, "Object should be equal to itself");
        }

        @Test
        @DisplayName("equals should return true for objects with same ID")
        void equals_sameId() {
            RuleRow a = new RuleRow();
            a.setId("ID-1");
            a.setContentType("FUND");

            RuleRow b = new RuleRow();
            b.setId("ID-1");
            b.setContentType("DOC"); // different content type, same ID

            assertEquals(a, b, "Objects with same ID should be equal regardless of other fields");
        }

        @Test
        @DisplayName("equals should return false for objects with different IDs")
        void equals_differentId() {
            RuleRow a = new RuleRow();
            a.setId("ID-1");
            RuleRow b = new RuleRow();
            b.setId("ID-2");
            assertNotEquals(a, b, "Objects with different IDs should not be equal");
        }

        @Test
        @DisplayName("equals should return true for both null IDs")
        void equals_bothNullId() {
            RuleRow a = new RuleRow();
            RuleRow b = new RuleRow();
            assertEquals(a, b, "Objects with both null IDs should be equal");
        }

        @Test
        @DisplayName("equals should return false when compared with null")
        void equals_null() {
            RuleRow row = new RuleRow();
            row.setId("ID-1");
            assertNotEquals(null, row, "Object should not be equal to null");
        }

        @Test
        @DisplayName("equals should return false when compared with different type")
        void equals_differentType() {
            RuleRow row = new RuleRow();
            row.setId("ID-1");
            assertNotEquals("ID-1", row, "Object should not be equal to a String");
        }

        @Test
        @DisplayName("hashCode should be equal for objects with same ID")
        void hashCode_sameId() {
            RuleRow a = new RuleRow();
            a.setId("ID-1");
            RuleRow b = new RuleRow();
            b.setId("ID-1");
            assertEquals(a.hashCode(), b.hashCode(),
                    "Equal objects must have equal hash codes");
        }

        @Test
        @DisplayName("hashCode should be consistent for null ID")
        void hashCode_nullId() {
            RuleRow a = new RuleRow();
            RuleRow b = new RuleRow();
            assertEquals(a.hashCode(), b.hashCode(),
                    "Both null-ID objects should have same hash code");
        }

        @Test
        @DisplayName("hashCode should generally differ for different IDs")
        void hashCode_differentIds() {
            RuleRow a = new RuleRow();
            a.setId("ID-1");
            RuleRow b = new RuleRow();
            b.setId("ID-2");
            // Hash codes may theoretically collide, but for these simple strings they
            // should not
            assertNotEquals(a.hashCode(), b.hashCode(),
                    "Different IDs should generally produce different hash codes");
        }

        // -- isRootRow() and isCostsByDataSupplier() with null Boolean --

        @Test
        @DisplayName("isRootRow() should return false when rootRow is null")
        void isRootRow_null() {
            RuleRow row = new RuleRow();
            assertFalse(row.isRootRow(), "Null rootRow should return false");
        }

        @Test
        @DisplayName("isRootRow() should return true when rootRow is Boolean.TRUE")
        void isRootRow_true() {
            RuleRow row = new RuleRow();
            row.setRootRow(Boolean.TRUE);
            assertTrue(row.isRootRow(), "Boolean.TRUE rootRow should return true");
        }

        @Test
        @DisplayName("isRootRow() should return false when rootRow is Boolean.FALSE")
        void isRootRow_false() {
            RuleRow row = new RuleRow();
            row.setRootRow(Boolean.FALSE);
            assertFalse(row.isRootRow(), "Boolean.FALSE rootRow should return false");
        }

        @Test
        @DisplayName("isCostsByDataSupplier() should return false when null")
        void isCostsByDataSupplier_null() {
            RuleRow row = new RuleRow();
            assertFalse(row.isCostsByDataSupplier(),
                    "Null costsByDataSupplier should return false");
        }

        @Test
        @DisplayName("isCostsByDataSupplier() should return true when set to true")
        void isCostsByDataSupplier_true() {
            RuleRow row = new RuleRow();
            row.setCostsByDataSupplier(Boolean.TRUE);
            assertTrue(row.isCostsByDataSupplier(),
                    "Boolean.TRUE costsByDataSupplier should return true");
        }

        // -- toString --

        @Test
        @DisplayName("toString() should contain key fields")
        void toStringContainsFields() {
            RuleRow row = new RuleRow();
            row.setId("ID-42");
            row.setContentType("FUND");
            row.setLei("LEI-123");

            String result = row.toString();
            assertTrue(result.contains("ID-42"), "toString should contain id");
            assertTrue(result.contains("FUND"), "toString should contain contentType");
            assertTrue(result.contains("LEI-123"), "toString should contain lei");
        }

        // -- Setter/getter round-trip for fields not covered above --

        @Test
        @DisplayName("setProfile/getProfile round-trip")
        void profile_roundTrip() {
            RuleRow row = new RuleRow();
            row.setProfile("FundsXML_Full");
            assertEquals("FundsXML_Full", row.getProfile(), "Profile should match");
        }

        @Test
        @DisplayName("setAccessDelayInDays/getAccessDelayInDays round-trip")
        void accessDelay_roundTrip() {
            RuleRow row = new RuleRow();
            row.setAccessDelayInDays("5");
            assertEquals("5", row.getAccessDelayInDays(), "AccessDelayInDays should match");
        }

        @Test
        @DisplayName("setFrequency/getFrequency round-trip")
        void frequency_roundTrip() {
            RuleRow row = new RuleRow();
            row.setFrequency("DAILY");
            assertEquals("DAILY", row.getFrequency(), "Frequency should match");
        }

        @Test
        @DisplayName("setShareclassIsin/getShareclassIsin round-trip")
        void shareclassIsin_roundTrip() {
            RuleRow row = new RuleRow();
            row.setShareclassIsin("AT0000A12345");
            assertEquals("AT0000A12345", row.getShareclassIsin(), "ShareclassIsin should match");
        }

        @Test
        @DisplayName("setSegmentIsin/getSegmentIsin round-trip")
        void segmentIsin_roundTrip() {
            RuleRow row = new RuleRow();
            row.setSegmentIsin("AT0000A67890");
            assertEquals("AT0000A67890", row.getSegmentIsin(), "SegmentIsin should match");
        }
    }
}
