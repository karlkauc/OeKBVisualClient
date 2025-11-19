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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing new information notification from OeKB FDP.
 * Indicates newly available data for download.
 * Based on FundsXML_NewInformation_1.0.2.xsd
 */
public class NewInformationEntry {

    public enum ContentType {
        FUND("Fund Data"),
        DOC("Documents"),
        REG("Regulatory Reporting");

        private final String description;

        ContentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static ContentType fromString(String value) {
            if (value == null) return null;
            return switch (value) {
                case "FUND" -> FUND;
                case "DOC" -> DOC;
                case "REG" -> REG;
                default -> null;
            };
        }
    }

    private ContentType contentType;
    private LocalDate contentDate;
    private LocalDateTime uploadDateTime;

    // Data suppliers who uploaded this data
    private List<String> dataSuppliers;

    // Fund/ShareClass identifiers
    private String lei;
    private List<String> isins;

    // For documents
    private String documentType;
    private String documentLanguage;
    private String documentFormat;

    // For regulatory reporting
    private String reportingType;

    // Profiles
    private List<String> profiles;

    public NewInformationEntry() {
        this.dataSuppliers = new ArrayList<>();
        this.isins = new ArrayList<>();
        this.profiles = new ArrayList<>();
    }

    public NewInformationEntry(ContentType contentType, LocalDate contentDate, LocalDateTime uploadDateTime) {
        this();
        this.contentType = contentType;
        this.contentDate = contentDate;
        this.uploadDateTime = uploadDateTime;
    }

    // Getters and Setters
    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public LocalDate getContentDate() {
        return contentDate;
    }

    public void setContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public List<String> getDataSuppliers() {
        return dataSuppliers;
    }

    public void setDataSuppliers(List<String> dataSuppliers) {
        this.dataSuppliers = dataSuppliers;
    }

    public void addDataSupplier(String dataSupplier) {
        if (dataSupplier != null && !dataSupplier.isEmpty()) {
            this.dataSuppliers.add(dataSupplier);
        }
    }

    public String getDataSuppliersString() {
        return String.join(", ", dataSuppliers);
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public List<String> getIsins() {
        return isins;
    }

    public void setIsins(List<String> isins) {
        this.isins = isins;
    }

    public void addIsin(String isin) {
        if (isin != null && !isin.isEmpty()) {
            this.isins.add(isin);
        }
    }

    public String getIsinsString() {
        return String.join(", ", isins);
    }

    public int getIsinCount() {
        return isins.size();
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentLanguage() {
        return documentLanguage;
    }

    public void setDocumentLanguage(String documentLanguage) {
        this.documentLanguage = documentLanguage;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getReportingType() {
        return reportingType;
    }

    public void setReportingType(String reportingType) {
        this.reportingType = reportingType;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(String profile) {
        if (profile != null && !profile.isEmpty()) {
            this.profiles.add(profile);
        }
    }

    public String getProfilesString() {
        return String.join(", ", profiles);
    }

    /**
     * Get formatted upload date time for display
     */
    public String getFormattedUploadDateTime() {
        if (uploadDateTime != null) {
            return uploadDateTime.toString();
        }
        return "";
    }

    /**
     * Get formatted content date for display
     */
    public String getFormattedContentDate() {
        if (contentDate != null) {
            return contentDate.toString();
        }
        return "";
    }

    /**
     * Get identifier summary for display
     */
    public String getIdentifierSummary() {
        StringBuilder sb = new StringBuilder();
        if (lei != null && !lei.isEmpty()) {
            sb.append("LEI: ").append(lei);
        }
        if (!isins.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(isins.size()).append(" ISIN(s)");
        }
        return sb.toString();
    }

    /**
     * Get detail string based on content type
     */
    public String getDetails() {
        return switch (contentType) {
            case FUND -> getIdentifierSummary();
            case DOC -> documentType != null ? documentType + " (" + documentLanguage + ")" : "";
            case REG -> reportingType != null ? reportingType : "";
        };
    }

    @Override
    public String toString() {
        return "NewInformationEntry{" +
                "contentType=" + contentType +
                ", contentDate=" + contentDate +
                ", uploadDateTime=" + uploadDateTime +
                ", dataSuppliers=" + dataSuppliers.size() +
                ", lei='" + lei + '\'' +
                ", isins=" + isins.size() +
                '}';
    }
}
