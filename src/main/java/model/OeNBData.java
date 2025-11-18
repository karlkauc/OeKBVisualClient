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
 * Model class for OeNB specific data (Aggregation, SecBySec, Check)
 */
public class OeNBData {
    public enum OeNBMode {
        AGGREGIERUNG,
        SECBYSEC,
        CHECK
    }

    private String oenbId;
    private LocalDate contentDate;
    private OeNBMode mode;
    private String xmlContent;
    private boolean valid;
    private String checkStatus;
    private String fundName;

    public OeNBData() {
    }

    public OeNBData(String oenbId, LocalDate contentDate, OeNBMode mode) {
        this.oenbId = oenbId;
        this.contentDate = contentDate;
        this.mode = mode;
    }

    // Getters and Setters
    public String getOenbId() {
        return oenbId;
    }

    public void setOenbId(String oenbId) {
        this.oenbId = oenbId;
    }

    public LocalDate getContentDate() {
        return contentDate;
    }

    public void setContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public OeNBMode getMode() {
        return mode;
    }

    public void setMode(OeNBMode mode) {
        this.mode = mode;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }
}
