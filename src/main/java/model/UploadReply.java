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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing upload reply/validation results from OeKB FDP.
 * Based on FundsXML_Reply_3.0.1.xsd
 */
public class UploadReply {

    public enum OverallStatus {
        OK("OK - All data processed successfully"),
        OK_INFOS("OK with Infos - Data processed, please review notes"),
        ERROR("ERROR - Some data could not be processed"),
        FILE_NOT_PROCESSED("FILE NOT PROCESSED - Global error occurred");

        private final String description;

        OverallStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static OverallStatus fromString(String value) {
            if (value == null) return null;
            return switch (value) {
                case "OK" -> OK;
                case "OK_INFOS" -> OK_INFOS;
                case "ERROR" -> ERROR;
                case "FILE NOT PROCESSED" -> FILE_NOT_PROCESSED;
                default -> null;
            };
        }
    }

    public enum StatusType {
        OK, OK_INFO, ERROR, INFO
    }

    private String dataSupplierShort;
    private String dataSupplierName;

    // Document info (for FundsXML data uploads)
    private LocalDate contentDate;
    private String uniqueDocumentId;
    private List<StatusInfo> statusInfos;

    // Access Rule info (for AccessRule uploads)
    private List<AccessRuleStatus> accessRuleStatuses;

    // Overall status
    private OverallStatus overallStatus;
    private String additionalInformation;

    public UploadReply() {
        this.statusInfos = new ArrayList<>();
        this.accessRuleStatuses = new ArrayList<>();
    }

    // Inner class for StatusInfo
    public static class StatusInfo {
        private StatusType status;
        private String message;
        private String context;
        private String kategorie;

        public StatusInfo() {}

        public StatusInfo(StatusType status, String message, String context) {
            this.status = status;
            this.message = message;
            this.context = context;
        }

        public StatusType getStatus() {
            return status;
        }

        public void setStatus(StatusType status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getKategorie() {
            return kategorie;
        }

        public void setKategorie(String kategorie) {
            this.kategorie = kategorie;
        }

        public boolean isError() {
            return status == StatusType.ERROR;
        }

        public boolean isInfo() {
            return status == StatusType.INFO || status == StatusType.OK_INFO;
        }
    }

    // Inner class for AccessRule status
    public static class AccessRuleStatus {
        private String ruleId;
        private List<ElementStatus> elementStatuses;

        public AccessRuleStatus() {
            this.elementStatuses = new ArrayList<>();
        }

        public AccessRuleStatus(String ruleId) {
            this.ruleId = ruleId;
            this.elementStatuses = new ArrayList<>();
        }

        public String getRuleId() {
            return ruleId;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public List<ElementStatus> getElementStatuses() {
            return elementStatuses;
        }

        public void setElementStatuses(List<ElementStatus> elementStatuses) {
            this.elementStatuses = elementStatuses;
        }

        public void addElementStatus(ElementStatus status) {
            this.elementStatuses.add(status);
        }

        public boolean hasErrors() {
            return elementStatuses.stream().anyMatch(ElementStatus::isError);
        }
    }

    // Inner class for Element status
    public static class ElementStatus {
        private StatusType status;
        private String message;

        public ElementStatus() {}

        public ElementStatus(StatusType status, String message) {
            this.status = status;
            this.message = message;
        }

        public StatusType getStatus() {
            return status;
        }

        public void setStatus(StatusType status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isError() {
            return status == StatusType.ERROR;
        }

        public boolean isOk() {
            return status == StatusType.OK;
        }
    }

    // Getters and Setters
    public String getDataSupplierShort() {
        return dataSupplierShort;
    }

    public void setDataSupplierShort(String dataSupplierShort) {
        this.dataSupplierShort = dataSupplierShort;
    }

    public String getDataSupplierName() {
        return dataSupplierName;
    }

    public void setDataSupplierName(String dataSupplierName) {
        this.dataSupplierName = dataSupplierName;
    }

    public LocalDate getContentDate() {
        return contentDate;
    }

    public void setContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public String getUniqueDocumentId() {
        return uniqueDocumentId;
    }

    public void setUniqueDocumentId(String uniqueDocumentId) {
        this.uniqueDocumentId = uniqueDocumentId;
    }

    public List<StatusInfo> getStatusInfos() {
        return statusInfos;
    }

    public void setStatusInfos(List<StatusInfo> statusInfos) {
        this.statusInfos = statusInfos;
    }

    public void addStatusInfo(StatusInfo info) {
        this.statusInfos.add(info);
    }

    public List<AccessRuleStatus> getAccessRuleStatuses() {
        return accessRuleStatuses;
    }

    public void setAccessRuleStatuses(List<AccessRuleStatus> accessRuleStatuses) {
        this.accessRuleStatuses = accessRuleStatuses;
    }

    public void addAccessRuleStatus(AccessRuleStatus status) {
        this.accessRuleStatuses.add(status);
    }

    public OverallStatus getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(OverallStatus overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    // Helper methods
    public boolean isSuccessful() {
        return overallStatus == OverallStatus.OK || overallStatus == OverallStatus.OK_INFOS;
    }

    public boolean hasErrors() {
        return overallStatus == OverallStatus.ERROR || overallStatus == OverallStatus.FILE_NOT_PROCESSED;
    }

    public boolean hasWarnings() {
        return overallStatus == OverallStatus.OK_INFOS;
    }

    public int getErrorCount() {
        return (int) statusInfos.stream().filter(StatusInfo::isError).count();
    }

    public int getInfoCount() {
        return (int) statusInfos.stream().filter(StatusInfo::isInfo).count();
    }

    @Override
    public String toString() {
        return "UploadReply{" +
                "dataSupplier='" + dataSupplierShort + '\'' +
                ", uniqueDocumentId='" + uniqueDocumentId + '\'' +
                ", overallStatus=" + overallStatus +
                ", errors=" + getErrorCount() +
                ", infos=" + getInfoCount() +
                '}';
    }
}
