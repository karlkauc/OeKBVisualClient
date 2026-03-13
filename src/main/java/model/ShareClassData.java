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
 * Model class for ShareClass/Segment download data
 */
public class ShareClassData {
    private String isin;
    private String shareClassName;
    private LocalDate contentDate;
    private String profile;
    private String xmlContent;
    private String fundName;
    private String status;

    public ShareClassData() {
    }

    public ShareClassData(String isin, String shareClassName, LocalDate contentDate) {
        this.isin = isin;
        this.shareClassName = shareClassName;
        this.contentDate = contentDate;
    }

    // Getters and Setters
    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getShareClassName() {
        return shareClassName;
    }

    public void setShareClassName(String shareClassName) {
        this.shareClassName = shareClassName;
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
