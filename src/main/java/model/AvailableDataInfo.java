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
import java.time.LocalDateTime;

/**
 * Model class for available data information
 */
public class AvailableDataInfo {
    public enum FDPContentType {
        FUND("Fund Data"),
        REG("Regulatory Reporting"),
        DOC("Documents");

        private final String description;

        FDPContentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private String identifier; // LEI, OeNB-ID or ISIN
    private String identifierType;
    private LocalDate contentDate;
    private LocalDateTime uploadTime;
    private FDPContentType contentType;
    private String dataSupplierProvider;
    private String fundName;
    private boolean hasData;

    public AvailableDataInfo() {
    }

    public AvailableDataInfo(String identifier, LocalDate contentDate, FDPContentType contentType) {
        this.identifier = identifier;
        this.contentDate = contentDate;
        this.contentType = contentType;
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

    public LocalDate getContentDate() {
        return contentDate;
    }

    public void setContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public FDPContentType getContentType() {
        return contentType;
    }

    public void setContentType(FDPContentType contentType) {
        this.contentType = contentType;
    }

    public String getDataSupplierProvider() {
        return dataSupplierProvider;
    }

    public void setDataSupplierProvider(String dataSupplierProvider) {
        this.dataSupplierProvider = dataSupplierProvider;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
}
