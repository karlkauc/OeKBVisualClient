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

import model.UploadReply.AccessRuleStatus;
import model.UploadReply.ElementStatus;
import model.UploadReply.OverallStatus;
import model.UploadReply.StatusInfo;
import model.UploadReply.StatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UploadReply model class and its inner classes.
 */
class UploadReplyTest {

    private UploadReply reply;

    @BeforeEach
    void setUp() {
        reply = new UploadReply();
    }

    // -----------------------------------------------------------------------
    // OverallStatus.fromString()
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("OverallStatus.fromString()")
    class OverallStatusFromStringTests {

        @Test
        @DisplayName("Should parse 'OK' to OverallStatus.OK")
        void fromString_ok() {
            assertEquals(OverallStatus.OK, OverallStatus.fromString("OK"),
                    "String 'OK' should map to OverallStatus.OK");
        }

        @Test
        @DisplayName("Should parse 'OK_INFOS' to OverallStatus.OK_INFOS")
        void fromString_okInfos() {
            assertEquals(OverallStatus.OK_INFOS, OverallStatus.fromString("OK_INFOS"),
                    "String 'OK_INFOS' should map to OverallStatus.OK_INFOS");
        }

        @Test
        @DisplayName("Should parse 'ERROR' to OverallStatus.ERROR")
        void fromString_error() {
            assertEquals(OverallStatus.ERROR, OverallStatus.fromString("ERROR"),
                    "String 'ERROR' should map to OverallStatus.ERROR");
        }

        @Test
        @DisplayName("Should parse 'FILE NOT PROCESSED' to OverallStatus.FILE_NOT_PROCESSED")
        void fromString_fileNotProcessed() {
            assertEquals(OverallStatus.FILE_NOT_PROCESSED, OverallStatus.fromString("FILE NOT PROCESSED"),
                    "String 'FILE NOT PROCESSED' should map to OverallStatus.FILE_NOT_PROCESSED");
        }

        @Test
        @DisplayName("Should return null for null input")
        void fromString_null() {
            assertNull(OverallStatus.fromString(null),
                    "Null input should return null");
        }

        @Test
        @DisplayName("Should return null for unknown string")
        void fromString_unknown() {
            assertNull(OverallStatus.fromString("UNKNOWN_STATUS"),
                    "Unknown string should return null");
        }

        @Test
        @DisplayName("Should return null for empty string")
        void fromString_empty() {
            assertNull(OverallStatus.fromString(""),
                    "Empty string should return null");
        }

        @Test
        @DisplayName("Should be case-sensitive (lowercase 'ok' returns null)")
        void fromString_caseSensitive() {
            assertNull(OverallStatus.fromString("ok"),
                    "Lowercase 'ok' should not match (case-sensitive)");
        }

        @Test
        @DisplayName("OverallStatus descriptions should not be empty")
        void descriptions_notEmpty() {
            for (OverallStatus status : OverallStatus.values()) {
                assertNotNull(status.getDescription(),
                        "Description should not be null for " + status);
                assertFalse(status.getDescription().isEmpty(),
                        "Description should not be empty for " + status);
            }
        }
    }

    // -----------------------------------------------------------------------
    // isSuccessful(), hasErrors(), hasWarnings()
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("UploadReply predicates")
    class PredicateTests {

        @Test
        @DisplayName("isSuccessful() should return true for OK status")
        void isSuccessful_ok() {
            reply.setOverallStatus(OverallStatus.OK);
            assertTrue(reply.isSuccessful(), "OK status should be successful");
        }

        @Test
        @DisplayName("isSuccessful() should return true for OK_INFOS status")
        void isSuccessful_okInfos() {
            reply.setOverallStatus(OverallStatus.OK_INFOS);
            assertTrue(reply.isSuccessful(), "OK_INFOS status should be successful");
        }

        @Test
        @DisplayName("isSuccessful() should return false for ERROR status")
        void isSuccessful_error() {
            reply.setOverallStatus(OverallStatus.ERROR);
            assertFalse(reply.isSuccessful(), "ERROR status should not be successful");
        }

        @Test
        @DisplayName("isSuccessful() should return false for FILE_NOT_PROCESSED status")
        void isSuccessful_fileNotProcessed() {
            reply.setOverallStatus(OverallStatus.FILE_NOT_PROCESSED);
            assertFalse(reply.isSuccessful(), "FILE_NOT_PROCESSED status should not be successful");
        }

        @Test
        @DisplayName("isSuccessful() should return false when status is null")
        void isSuccessful_null() {
            reply.setOverallStatus(null);
            assertFalse(reply.isSuccessful(), "Null status should not be successful");
        }

        @Test
        @DisplayName("hasErrors() should return true for ERROR status")
        void hasErrors_error() {
            reply.setOverallStatus(OverallStatus.ERROR);
            assertTrue(reply.hasErrors(), "ERROR status should indicate errors");
        }

        @Test
        @DisplayName("hasErrors() should return true for FILE_NOT_PROCESSED status")
        void hasErrors_fileNotProcessed() {
            reply.setOverallStatus(OverallStatus.FILE_NOT_PROCESSED);
            assertTrue(reply.hasErrors(), "FILE_NOT_PROCESSED status should indicate errors");
        }

        @Test
        @DisplayName("hasErrors() should return false for OK status")
        void hasErrors_ok() {
            reply.setOverallStatus(OverallStatus.OK);
            assertFalse(reply.hasErrors(), "OK status should not indicate errors");
        }

        @Test
        @DisplayName("hasErrors() should return false for OK_INFOS status")
        void hasErrors_okInfos() {
            reply.setOverallStatus(OverallStatus.OK_INFOS);
            assertFalse(reply.hasErrors(), "OK_INFOS status should not indicate errors");
        }

        @Test
        @DisplayName("hasWarnings() should return true only for OK_INFOS status")
        void hasWarnings_okInfos() {
            reply.setOverallStatus(OverallStatus.OK_INFOS);
            assertTrue(reply.hasWarnings(), "OK_INFOS status should indicate warnings");
        }

        @Test
        @DisplayName("hasWarnings() should return false for OK status")
        void hasWarnings_ok() {
            reply.setOverallStatus(OverallStatus.OK);
            assertFalse(reply.hasWarnings(), "OK status should not indicate warnings");
        }

        @Test
        @DisplayName("hasWarnings() should return false for ERROR status")
        void hasWarnings_error() {
            reply.setOverallStatus(OverallStatus.ERROR);
            assertFalse(reply.hasWarnings(), "ERROR status should not indicate warnings");
        }
    }

    // -----------------------------------------------------------------------
    // getErrorCount(), getInfoCount()
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("Stream counting: getErrorCount() and getInfoCount()")
    class StreamCountingTests {

        @Test
        @DisplayName("getErrorCount() should return 0 when no status infos exist")
        void errorCount_empty() {
            assertEquals(0, reply.getErrorCount(), "Empty reply should have 0 errors");
        }

        @Test
        @DisplayName("getInfoCount() should return 0 when no status infos exist")
        void infoCount_empty() {
            assertEquals(0, reply.getInfoCount(), "Empty reply should have 0 infos");
        }

        @Test
        @DisplayName("getErrorCount() should count only ERROR status infos")
        void errorCount_mixedStatuses() {
            reply.addStatusInfo(new StatusInfo(StatusType.ERROR, "Error 1", "ctx1"));
            reply.addStatusInfo(new StatusInfo(StatusType.INFO, "Info 1", "ctx2"));
            reply.addStatusInfo(new StatusInfo(StatusType.ERROR, "Error 2", "ctx3"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK, "OK 1", "ctx4"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK_INFO, "OKInfo 1", "ctx5"));

            assertEquals(2, reply.getErrorCount(), "Should count exactly 2 errors");
        }

        @Test
        @DisplayName("getInfoCount() should count INFO and OK_INFO status infos")
        void infoCount_mixedStatuses() {
            reply.addStatusInfo(new StatusInfo(StatusType.ERROR, "Error 1", "ctx1"));
            reply.addStatusInfo(new StatusInfo(StatusType.INFO, "Info 1", "ctx2"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK_INFO, "OKInfo 1", "ctx3"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK, "OK 1", "ctx4"));
            reply.addStatusInfo(new StatusInfo(StatusType.INFO, "Info 2", "ctx5"));

            assertEquals(3, reply.getInfoCount(), "Should count INFO + OK_INFO = 3 infos");
        }

        @Test
        @DisplayName("getErrorCount() should return 0 when only info statuses present")
        void errorCount_noErrors() {
            reply.addStatusInfo(new StatusInfo(StatusType.INFO, "Info 1", "ctx1"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK, "OK 1", "ctx2"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK_INFO, "OKInfo 1", "ctx3"));

            assertEquals(0, reply.getErrorCount(), "Should have 0 errors when none present");
        }

        @Test
        @DisplayName("getInfoCount() should return 0 when only error statuses present")
        void infoCount_noInfos() {
            reply.addStatusInfo(new StatusInfo(StatusType.ERROR, "Error 1", "ctx1"));
            reply.addStatusInfo(new StatusInfo(StatusType.OK, "OK 1", "ctx2"));

            assertEquals(0, reply.getInfoCount(), "Should have 0 infos when only ERROR and OK present");
        }
    }

    // -----------------------------------------------------------------------
    // StatusInfo predicates
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("StatusInfo.isError() and StatusInfo.isInfo()")
    class StatusInfoPredicateTests {

        @Test
        @DisplayName("isError() should return true for ERROR status")
        void isError_true() {
            StatusInfo info = new StatusInfo(StatusType.ERROR, "msg", "ctx");
            assertTrue(info.isError(), "ERROR status should be error");
        }

        @Test
        @DisplayName("isError() should return false for INFO status")
        void isError_info() {
            StatusInfo info = new StatusInfo(StatusType.INFO, "msg", "ctx");
            assertFalse(info.isError(), "INFO status should not be error");
        }

        @Test
        @DisplayName("isError() should return false for OK status")
        void isError_ok() {
            StatusInfo info = new StatusInfo(StatusType.OK, "msg", "ctx");
            assertFalse(info.isError(), "OK status should not be error");
        }

        @Test
        @DisplayName("isError() should return false for OK_INFO status")
        void isError_okInfo() {
            StatusInfo info = new StatusInfo(StatusType.OK_INFO, "msg", "ctx");
            assertFalse(info.isError(), "OK_INFO status should not be error");
        }

        @Test
        @DisplayName("isInfo() should return true for INFO status")
        void isInfo_info() {
            StatusInfo info = new StatusInfo(StatusType.INFO, "msg", "ctx");
            assertTrue(info.isInfo(), "INFO status should be info");
        }

        @Test
        @DisplayName("isInfo() should return true for OK_INFO status")
        void isInfo_okInfo() {
            StatusInfo info = new StatusInfo(StatusType.OK_INFO, "msg", "ctx");
            assertTrue(info.isInfo(), "OK_INFO status should be info");
        }

        @Test
        @DisplayName("isInfo() should return false for ERROR status")
        void isInfo_error() {
            StatusInfo info = new StatusInfo(StatusType.ERROR, "msg", "ctx");
            assertFalse(info.isInfo(), "ERROR status should not be info");
        }

        @Test
        @DisplayName("isInfo() should return false for OK status")
        void isInfo_ok() {
            StatusInfo info = new StatusInfo(StatusType.OK, "msg", "ctx");
            assertFalse(info.isInfo(), "OK status should not be info");
        }

        @Test
        @DisplayName("StatusInfo getters and setters should work correctly")
        void statusInfo_gettersSetters() {
            StatusInfo info = new StatusInfo();
            info.setStatus(StatusType.ERROR);
            info.setMessage("Some error message");
            info.setContext("ErrorContext");
            info.setKategorie("VALIDATION");

            assertEquals(StatusType.ERROR, info.getStatus(), "Status should match");
            assertEquals("Some error message", info.getMessage(), "Message should match");
            assertEquals("ErrorContext", info.getContext(), "Context should match");
            assertEquals("VALIDATION", info.getKategorie(), "Kategorie should match");
        }
    }

    // -----------------------------------------------------------------------
    // AccessRuleStatus
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("AccessRuleStatus")
    class AccessRuleStatusTests {

        @Test
        @DisplayName("hasErrors() should return false when no element statuses exist")
        void hasErrors_empty() {
            AccessRuleStatus ars = new AccessRuleStatus("rule-1");
            assertFalse(ars.hasErrors(), "Empty AccessRuleStatus should have no errors");
        }

        @Test
        @DisplayName("hasErrors() should return true when at least one element has ERROR")
        void hasErrors_withError() {
            AccessRuleStatus ars = new AccessRuleStatus("rule-1");
            ars.addElementStatus(new ElementStatus(StatusType.OK, "All good"));
            ars.addElementStatus(new ElementStatus(StatusType.ERROR, "Something failed"));

            assertTrue(ars.hasErrors(), "Should detect error among element statuses");
        }

        @Test
        @DisplayName("hasErrors() should return false when all elements are OK")
        void hasErrors_allOk() {
            AccessRuleStatus ars = new AccessRuleStatus("rule-1");
            ars.addElementStatus(new ElementStatus(StatusType.OK, "OK 1"));
            ars.addElementStatus(new ElementStatus(StatusType.OK, "OK 2"));

            assertFalse(ars.hasErrors(), "All-OK elements should have no errors");
        }

        @Test
        @DisplayName("getElementStatuses() should return a defensive copy")
        void getElementStatuses_defensiveCopy() {
            AccessRuleStatus ars = new AccessRuleStatus("rule-1");
            ars.addElementStatus(new ElementStatus(StatusType.OK, "OK"));

            List<ElementStatus> copy = ars.getElementStatuses();
            copy.add(new ElementStatus(StatusType.ERROR, "Injected"));

            assertEquals(1, ars.getElementStatuses().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("setElementStatuses(null) should initialize empty list")
        void setElementStatuses_null() {
            AccessRuleStatus ars = new AccessRuleStatus("rule-1");
            ars.setElementStatuses(null);

            assertNotNull(ars.getElementStatuses(), "Should never be null after setting null");
            assertTrue(ars.getElementStatuses().isEmpty(), "Should be empty after setting null");
        }

        @Test
        @DisplayName("setElementStatuses() should defensively copy the input list")
        void setElementStatuses_defensiveCopy() {
            AccessRuleStatus ars = new AccessRuleStatus();
            List<ElementStatus> input = new ArrayList<>();
            input.add(new ElementStatus(StatusType.OK, "ok"));
            ars.setElementStatuses(input);

            input.add(new ElementStatus(StatusType.ERROR, "injected"));

            assertEquals(1, ars.getElementStatuses().size(),
                    "Modifying the input list should not affect the AccessRuleStatus");
        }

        @Test
        @DisplayName("getRuleId() and setRuleId() should work correctly")
        void ruleId_getterSetter() {
            AccessRuleStatus ars = new AccessRuleStatus();
            ars.setRuleId("rule-42");
            assertEquals("rule-42", ars.getRuleId(), "Rule ID should match");
        }
    }

    // -----------------------------------------------------------------------
    // ElementStatus
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("ElementStatus")
    class ElementStatusTests {

        @Test
        @DisplayName("isError() should return true for ERROR status")
        void isError_true() {
            ElementStatus es = new ElementStatus(StatusType.ERROR, "fail");
            assertTrue(es.isError(), "ERROR status should be error");
        }

        @Test
        @DisplayName("isError() should return false for OK status")
        void isError_ok() {
            ElementStatus es = new ElementStatus(StatusType.OK, "good");
            assertFalse(es.isError(), "OK status should not be error");
        }

        @Test
        @DisplayName("isOk() should return true for OK status")
        void isOk_true() {
            ElementStatus es = new ElementStatus(StatusType.OK, "good");
            assertTrue(es.isOk(), "OK status should be ok");
        }

        @Test
        @DisplayName("isOk() should return false for ERROR status")
        void isOk_error() {
            ElementStatus es = new ElementStatus(StatusType.ERROR, "fail");
            assertFalse(es.isOk(), "ERROR status should not be ok");
        }

        @Test
        @DisplayName("isOk() should return false for INFO status")
        void isOk_info() {
            ElementStatus es = new ElementStatus(StatusType.INFO, "note");
            assertFalse(es.isOk(), "INFO status should not be ok");
        }

        @Test
        @DisplayName("isOk() should return false for OK_INFO status")
        void isOk_okInfo() {
            ElementStatus es = new ElementStatus(StatusType.OK_INFO, "note");
            assertFalse(es.isOk(), "OK_INFO status should not be ok");
        }

        @Test
        @DisplayName("Default constructor should allow setting fields via setters")
        void defaultConstructor_setters() {
            ElementStatus es = new ElementStatus();
            es.setStatus(StatusType.ERROR);
            es.setMessage("Something broke");

            assertEquals(StatusType.ERROR, es.getStatus(), "Status should match");
            assertEquals("Something broke", es.getMessage(), "Message should match");
        }
    }

    // -----------------------------------------------------------------------
    // UploadReply list methods and defensive copying
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("UploadReply list operations and defensive copying")
    class ListOperationTests {

        @Test
        @DisplayName("getStatusInfos() should return a defensive copy")
        void getStatusInfos_defensiveCopy() {
            reply.addStatusInfo(new StatusInfo(StatusType.OK, "ok", "ctx"));

            List<StatusInfo> copy = reply.getStatusInfos();
            copy.add(new StatusInfo(StatusType.ERROR, "injected", "ctx"));

            assertEquals(1, reply.getStatusInfos().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("setStatusInfos(null) should initialize empty list")
        void setStatusInfos_null() {
            reply.setStatusInfos(null);
            assertNotNull(reply.getStatusInfos(), "Should never be null after setting null");
            assertTrue(reply.getStatusInfos().isEmpty(), "Should be empty after setting null");
        }

        @Test
        @DisplayName("setStatusInfos() should defensively copy the input list")
        void setStatusInfos_defensiveCopy() {
            List<StatusInfo> input = new ArrayList<>();
            input.add(new StatusInfo(StatusType.OK, "ok", "ctx"));
            reply.setStatusInfos(input);

            input.add(new StatusInfo(StatusType.ERROR, "injected", "ctx"));

            assertEquals(1, reply.getStatusInfos().size(),
                    "Modifying the input list should not affect UploadReply");
        }

        @Test
        @DisplayName("getAccessRuleStatuses() should return a defensive copy")
        void getAccessRuleStatuses_defensiveCopy() {
            reply.addAccessRuleStatus(new AccessRuleStatus("rule-1"));

            List<AccessRuleStatus> copy = reply.getAccessRuleStatuses();
            copy.add(new AccessRuleStatus("injected"));

            assertEquals(1, reply.getAccessRuleStatuses().size(),
                    "Original list should not be modified by mutating the returned copy");
        }

        @Test
        @DisplayName("setAccessRuleStatuses(null) should initialize empty list")
        void setAccessRuleStatuses_null() {
            reply.setAccessRuleStatuses(null);
            assertNotNull(reply.getAccessRuleStatuses(), "Should never be null after setting null");
            assertTrue(reply.getAccessRuleStatuses().isEmpty(), "Should be empty after setting null");
        }

        @Test
        @DisplayName("setAccessRuleStatuses() should defensively copy the input list")
        void setAccessRuleStatuses_defensiveCopy() {
            List<AccessRuleStatus> input = new ArrayList<>();
            input.add(new AccessRuleStatus("rule-1"));
            reply.setAccessRuleStatuses(input);

            input.add(new AccessRuleStatus("injected"));

            assertEquals(1, reply.getAccessRuleStatuses().size(),
                    "Modifying the input list should not affect UploadReply");
        }
    }

    // -----------------------------------------------------------------------
    // UploadReply simple getters/setters and toString
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("UploadReply getters, setters, and toString")
    class GetterSetterTests {

        @Test
        @DisplayName("Should store and retrieve dataSupplierShort")
        void dataSupplierShort() {
            reply.setDataSupplierShort("DDS001");
            assertEquals("DDS001", reply.getDataSupplierShort(), "DataSupplierShort should match");
        }

        @Test
        @DisplayName("Should store and retrieve dataSupplierName")
        void dataSupplierName() {
            reply.setDataSupplierName("Test Supplier");
            assertEquals("Test Supplier", reply.getDataSupplierName(), "DataSupplierName should match");
        }

        @Test
        @DisplayName("Should store and retrieve contentDate")
        void contentDate() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            reply.setContentDate(date);
            assertEquals(date, reply.getContentDate(), "ContentDate should match");
        }

        @Test
        @DisplayName("Should store and retrieve uniqueDocumentId")
        void uniqueDocumentId() {
            reply.setUniqueDocumentId("DOC-12345");
            assertEquals("DOC-12345", reply.getUniqueDocumentId(), "UniqueDocumentId should match");
        }

        @Test
        @DisplayName("Should store and retrieve additionalInformation")
        void additionalInformation() {
            reply.setAdditionalInformation("Some additional info");
            assertEquals("Some additional info", reply.getAdditionalInformation(),
                    "AdditionalInformation should match");
        }

        @Test
        @DisplayName("toString() should contain relevant fields")
        void toStringContainsFields() {
            reply.setDataSupplierShort("DDS001");
            reply.setUniqueDocumentId("DOC-001");
            reply.setOverallStatus(OverallStatus.OK);

            String result = reply.toString();
            assertTrue(result.contains("DDS001"), "toString should contain data supplier short");
            assertTrue(result.contains("DOC-001"), "toString should contain unique document id");
            assertTrue(result.contains("OK"), "toString should contain overall status");
        }
    }
}
