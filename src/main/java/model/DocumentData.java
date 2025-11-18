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
package model;

import java.time.LocalDate;

/**
 * Model class for Document download data
 */
public class DocumentData {
    public enum DocumentType {
        AIFMD("AIFMD"),
        ANNUAL_REPORT("AnnualReport"),
        AUDIT_REPORT("AuditReport"),
        FACTSHEET("Factsheet"),
        KID("KID"),
        PROSPECTUS("Prospectus"),
        PRIIPS_KID("PRIIPS-KID");

        private final String value;

        DocumentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private String identifier; // LEI, OeNB-ID or ISIN
    private String identifierType; // LEI, OENB, ISIN
    private DocumentType documentType;
    private String customDocumentType; // for unlisted types
    private LocalDate contentDate;
    private String profile;
    private String documentUrl;
    private String fileName;
    private String fundName;
    private long fileSize;

    public DocumentData() {
    }

    public DocumentData(String identifier, DocumentType documentType, LocalDate contentDate) {
        this.identifier = identifier;
        this.documentType = documentType;
        this.contentDate = contentDate;
    }

    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getCustomDocumentType() {
        return customDocumentType;
    }

    public void setCustomDocumentType(String customDocumentType) {
        this.customDocumentType = customDocumentType;
    }

    public LocalDate getContentDate() {
        return contentDate;
    }

    public void setContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDocumentTypeString() {
        return documentType != null ? documentType.getValue() : customDocumentType;
    }
}
