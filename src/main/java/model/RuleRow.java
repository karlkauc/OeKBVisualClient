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

    private String LEI;
    private String OENB_ID;
    private String SHARECLASS_ISIN;
    private String SEGMENT_ISIN;

    private Boolean rootRow;

    // Default constructor
    public RuleRow() {
    }

    // Full constructor (15 params - for AccessRightsReceived)
    public RuleRow(String id, String contentType, String profile,
                   String dataSupplierCreatorShort, String dataSupplierCreatorName,
                   String dataSuppliersGivenShort, String creationTime,
                   String accessDelayInDays, String dateFrom, String dateTo,
                   String frequency, String LEI, String OENB_ID,
                   String SHARECLASS_ISIN, String SEGMENT_ISIN) {
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
        this.LEI = LEI;
        this.OENB_ID = OENB_ID;
        this.SHARECLASS_ISIN = SHARECLASS_ISIN;
        this.SEGMENT_ISIN = SEGMENT_ISIN;
    }

    // Extended constructor with Boolean costsByDataSupplier (17 params - for AccessRightGrant)
    // Note: param12 can be Boolean or String depending on usage
    public RuleRow(String id, String contentType, String profile,
                   String dataSupplierCreatorShort, String dataSupplierCreatorName,
                   String dataSuppliersGivenShort, String creationTime,
                   String accessDelayInDays, String dateFrom, String dateTo,
                   String frequency, Object costsByDataSupplier,
                   String LEI, String OENB_ID,
                   String SHARECLASS_ISIN, String SEGMENT_ISIN,
                   boolean rootRow) {
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
        this.LEI = LEI;
        this.OENB_ID = OENB_ID;
        this.SHARECLASS_ISIN = SHARECLASS_ISIN;
        this.SEGMENT_ISIN = SEGMENT_ISIN;
        this.rootRow = rootRow;
    }

    public String getFundName() {
        String myId = null;

        if (LEI != null) {
            myId = LEI;
        } else if (OENB_ID != null) {
            myId = OENB_ID;
        } else if (SHARECLASS_ISIN != null) {
            myId = SHARECLASS_ISIN;
        } else if (SEGMENT_ISIN != null) {
            myId = SEGMENT_ISIN;
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

    public Boolean getCostsByDataSupplier() {
        return costsByDataSupplier;
    }

    public boolean isCostsByDataSupplier() {
        return Boolean.TRUE.equals(costsByDataSupplier);
    }

    public void setCostsByDataSupplier(Boolean costsByDataSupplier) {
        this.costsByDataSupplier = costsByDataSupplier;
    }

    public String getLEI() {
        return LEI;
    }

    public void setLEI(String LEI) {
        this.LEI = LEI;
    }

    public String getOENB_ID() {
        return OENB_ID;
    }

    public void setOENB_ID(String OENB_ID) {
        this.OENB_ID = OENB_ID;
    }

    public String getSHARECLASS_ISIN() {
        return SHARECLASS_ISIN;
    }

    public void setSHARECLASS_ISIN(String SHARECLASS_ISIN) {
        this.SHARECLASS_ISIN = SHARECLASS_ISIN;
    }

    public String getSEGMENT_ISIN() {
        return SEGMENT_ISIN;
    }

    public void setSEGMENT_ISIN(String SEGMENT_ISIN) {
        this.SEGMENT_ISIN = SEGMENT_ISIN;
    }

    public Boolean getRootRow() {
        return rootRow;
    }

    public boolean isRootRow() {
        return Boolean.TRUE.equals(rootRow);
    }

    public void setRootRow(Boolean rootRow) {
        this.rootRow = rootRow;
    }

    @Override
    public int compareTo(RuleRow other) {
        if (this.id == null && other.id == null) return 0;
        if (this.id == null) return -1;
        if (other.id == null) return 1;
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "RuleRow{" +
                "id='" + id + '\'' +
                ", contentType='" + contentType + '\'' +
                ", profile='" + profile + '\'' +
                ", dataSupplierCreatorShort='" + dataSupplierCreatorShort + '\'' +
                ", dataSupplierCreatorName='" + dataSupplierCreatorName + '\'' +
                ", dataSuppliersGivenShort='" + dataSuppliersGivenShort + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", accessDelayInDays='" + accessDelayInDays + '\'' +
                ", dateFrom='" + dateFrom + '\'' +
                ", dateTo='" + dateTo + '\'' +
                ", frequency='" + frequency + '\'' +
                ", costsByDataSupplier=" + costsByDataSupplier +
                ", LEI='" + LEI + '\'' +
                ", OENB_ID='" + OENB_ID + '\'' +
                ", SHARECLASS_ISIN='" + SHARECLASS_ISIN + '\'' +
                ", SEGMENT_ISIN='" + SEGMENT_ISIN + '\'' +
                ", rootRow=" + rootRow +
                '}';
    }
}
