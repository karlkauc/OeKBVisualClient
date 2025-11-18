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
 * Model class for Regulatory Reporting data
 */
public class RegulatoryReporting {
    public enum ReportingType {
        EMIR("EMIR"),
        KIIDS("KIIDs"),
        EMT("EMT"),
        TRIPARTITE_SOLVENCY_II("TripartiteTemplateSolvencyII"),
        PRIIPS("PRIIPS"),
        ALL("all");

        private final String value;

        ReportingType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private String identifier; // LEI, OeNB-ID or ISIN
    private String identifierType; // LEI, OENB, ISIN
    private ReportingType reportingType;
    private LocalDate contentDate;
    private String profile;
    private String xmlContent;
    private String fundName;
    private String status;

    public RegulatoryReporting() {
    }

    public RegulatoryReporting(String identifier, ReportingType reportingType, LocalDate contentDate) {
        this.identifier = identifier;
        this.reportingType = reportingType;
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

    public ReportingType getReportingType() {
        return reportingType;
    }

    public void setReportingType(ReportingType reportingType) {
        this.reportingType = reportingType;
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

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
