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

import dao.FundEnhancer;

import java.util.Objects;

public final class RuleRow implements Comparable<RuleRow> {
    private String id;
    private String contentType;
    private String profile;

    private String dataSupplierCreatorShort;
    private String dataSupplierCreatorName;
    private String dataSuppliersGivenShort;

    private String creationTime;
    private String accessDelayInDays;
    private String dateFrom;
    private String dateTo;
    private String frequency;
    private Boolean costsByDataSupplier;

    private String lei;
    private String oenbId;
    private String shareclassIsin;
    private String segmentIsin;

    private Boolean rootRow;

    // Default constructor
    public RuleRow() {
    }

    // Full constructor (15 params - for AccessRightsReceived)
    public RuleRow(String id, String contentType, String profile, String dataSupplierCreatorShort,
            String dataSupplierCreatorName, String dataSuppliersGivenShort, String creationTime,
            String accessDelayInDays, String dateFrom, String dateTo, String frequency, String lei, String oenbId,
            String shareclassIsin, String segmentIsin) {
        this.id = id;
        this.contentType = contentType;
        this.profile = profile;
        this.dataSupplierCreatorShort = dataSupplierCreatorShort;
        this.dataSupplierCreatorName = dataSupplierCreatorName;
        this.dataSuppliersGivenShort = dataSuppliersGivenShort;
        this.creationTime = creationTime;
        this.accessDelayInDays = accessDelayInDays;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.frequency = frequency;
        this.lei = lei;
        this.oenbId = oenbId;
        this.shareclassIsin = shareclassIsin;
        this.segmentIsin = segmentIsin;
    }

    // Extended constructor with Boolean costsByDataSupplier (17 params - for
    // AccessRightGrant)
    // Note: param12 can be Boolean or String depending on usage
    public RuleRow(String id, String contentType, String profile, String dataSupplierCreatorShort,
            String dataSupplierCreatorName, String dataSuppliersGivenShort, String creationTime,
            String accessDelayInDays, String dateFrom, String dateTo, String frequency, Object costsByDataSupplier,
            String lei, String oenbId, String shareclassIsin, String segmentIsin, boolean rootRow) {
        this.id = id;
        this.contentType = contentType;
        this.profile = profile;
        this.dataSupplierCreatorShort = dataSupplierCreatorShort;
        this.dataSupplierCreatorName = dataSupplierCreatorName;
        this.dataSuppliersGivenShort = dataSuppliersGivenShort;
        this.creationTime = creationTime;
        this.accessDelayInDays = accessDelayInDays;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.frequency = frequency;
        if (costsByDataSupplier instanceof Boolean) {
            this.costsByDataSupplier = (Boolean) costsByDataSupplier;
        } else if (costsByDataSupplier instanceof String) {
            this.costsByDataSupplier = Boolean.parseBoolean((String) costsByDataSupplier);
        }
        this.lei = lei;
        this.oenbId = oenbId;
        this.shareclassIsin = shareclassIsin;
        this.segmentIsin = segmentIsin;
        this.rootRow = rootRow;
    }

    public String getFundName() {
        String myId = null;

        if (lei != null) {
            myId = lei;
        } else if (oenbId != null) {
            myId = oenbId;
        } else if (shareclassIsin != null) {
            myId = shareclassIsin;
        } else if (segmentIsin != null) {
            myId = segmentIsin;
        }

        if (myId != null) {
            return FundEnhancer.getInstance().getFundNameByID(myId);
        }
        return null;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDataSupplierCreatorShort() {
        return dataSupplierCreatorShort;
    }

    public void setDataSupplierCreatorShort(String dataSupplierCreatorShort) {
        this.dataSupplierCreatorShort = dataSupplierCreatorShort;
    }

    public String getDataSupplierCreatorName() {
        return dataSupplierCreatorName;
    }

    public void setDataSupplierCreatorName(String dataSupplierCreatorName) {
        this.dataSupplierCreatorName = dataSupplierCreatorName;
    }

    public String getDataSuppliersGivenShort() {
        return dataSuppliersGivenShort;
    }

    public void setDataSuppliersGivenShort(String dataSuppliersGivenShort) {
        this.dataSuppliersGivenShort = dataSuppliersGivenShort;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getAccessDelayInDays() {
        return accessDelayInDays;
    }

    public void setAccessDelayInDays(String accessDelayInDays) {
        this.accessDelayInDays = accessDelayInDays;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isCostsByDataSupplier() {
        return Boolean.TRUE.equals(costsByDataSupplier);
    }

    public void setCostsByDataSupplier(Boolean costsByDataSupplier) {
        this.costsByDataSupplier = costsByDataSupplier;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public String getOenbId() {
        return oenbId;
    }

    public void setOenbId(String oenbId) {
        this.oenbId = oenbId;
    }

    public String getShareclassIsin() {
        return shareclassIsin;
    }

    public void setShareclassIsin(String shareclassIsin) {
        this.shareclassIsin = shareclassIsin;
    }

    public String getSegmentIsin() {
        return segmentIsin;
    }

    public void setSegmentIsin(String segmentIsin) {
        this.segmentIsin = segmentIsin;
    }

    public boolean isRootRow() {
        return Boolean.TRUE.equals(rootRow);
    }

    public void setRootRow(Boolean rootRow) {
        this.rootRow = rootRow;
    }

    @Override
    public int compareTo(RuleRow other) {
        if (this.id == null && other.id == null) {
            return 0;
        }
        if (this.id == null) {
            return -1;
        }
        if (other.id == null) {
            return 1;
        }
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RuleRow ruleRow = (RuleRow) o;
        return Objects.equals(id, ruleRow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RuleRow{" + "id='" + id + '\'' + ", contentType='" + contentType + '\'' + ", profile='" + profile + '\''
                + ", dataSupplierCreatorShort='" + dataSupplierCreatorShort + '\'' + ", dataSupplierCreatorName='"
                + dataSupplierCreatorName + '\'' + ", dataSuppliersGivenShort='" + dataSuppliersGivenShort + '\''
                + ", creationTime='" + creationTime + '\'' + ", accessDelayInDays='" + accessDelayInDays + '\''
                + ", dateFrom='" + dateFrom + '\'' + ", dateTo='" + dateTo + '\'' + ", frequency='" + frequency + '\''
                + ", costsByDataSupplier=" + costsByDataSupplier + ", lei='" + lei + '\'' + ", oenbId='" + oenbId + '\''
                + ", shareclassIsin='" + shareclassIsin + '\'' + ", segmentIsin='" + segmentIsin + '\'' + ", rootRow="
                + rootRow + '}';
    }
}
