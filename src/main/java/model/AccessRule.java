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

import java.util.ArrayList;
import java.util.List;

public class AccessRule {
    private String id;
    private String contentType;
    private List<String> profiles;

    private String dataSupplierCreatorShort;
    private String dataSupplierCreatorName;

    private List<String> dataSuppliersGivenShort;

    private String creationTime;
    private String accessDelayInDays;
    private String dateFrom;
    private String dateTo;
    private String frequency;
    private String costsByDataSupplier;

    private List<String> LEI;
    private List<String> OENB_ID;
    private List<String> ISIN_SEGMENT;
    private List<String> ISIN_SHARECLASS;

    // Advanced options (Tab 4)
    private List<String> documentTypes;         // For ContentType=DOC
    private List<String> regulatoryReportings;  // For ContentType=REG

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

    /**
     * Get all profiles for this access rule.
     * @return List of profile names
     */
    public List<String> getProfiles() {
        return profiles;
    }

    /**
     * Set all profiles for this access rule.
     * @param profiles List of profile names
     */
    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    /**
     * Get the first profile (for backwards compatibility).
     * @return The first profile or null if no profiles exist
     */
    public String getProfile() {
        return (profiles != null && !profiles.isEmpty()) ? profiles.get(0) : null;
    }

    /**
     * Set a single profile (for backwards compatibility).
     * Clears existing profiles and adds the given profile.
     * @param profile The profile to set
     */
    public void setProfile(String profile) {
        if (this.profiles == null) {
            this.profiles = new ArrayList<>();
        }
        this.profiles.clear();
        if (profile != null && !profile.isEmpty()) {
            this.profiles.add(profile);
        }
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

    public List<String> getDataSuppliersGivenShort() {
        return dataSuppliersGivenShort;
    }

    public void setDataSuppliersGivenShort(List<String> dataSuppliersGivenShort) {
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

    public String getCostsByDataSupplier() {
        return costsByDataSupplier;
    }

    public void setCostsByDataSupplier(String costsByDataSupplier) {
        this.costsByDataSupplier = costsByDataSupplier;
    }

    public List<String> getLEI() {
        return LEI;
    }

    public void setLEI(List<String> LEI) {
        this.LEI = LEI;
    }

    public List<String> getOENB_ID() {
        return OENB_ID;
    }

    public void setOENB_ID(List<String> OENB_ID) {
        this.OENB_ID = OENB_ID;
    }

    public List<String> getISIN_SEGMENT() {
        return ISIN_SEGMENT;
    }

    public void setISIN_SEGMENT(List<String> ISIN_SEGMENT) {
        this.ISIN_SEGMENT = ISIN_SEGMENT;
    }

    public List<String> getISIN_SHARECLASS() {
        return ISIN_SHARECLASS;
    }

    public void setISIN_SHARECLASS(List<String> ISIN_SHARECLASS) {
        this.ISIN_SHARECLASS = ISIN_SHARECLASS;
    }

    /**
     * Get document types (for ContentType=DOC).
     * @return List of document type names (e.g., AIFMD, AnnualReport, KID, Prospectus)
     */
    public List<String> getDocumentTypes() {
        return documentTypes;
    }

    /**
     * Set document types (for ContentType=DOC).
     * @param documentTypes List of document type names
     */
    public void setDocumentTypes(List<String> documentTypes) {
        this.documentTypes = documentTypes;
    }

    /**
     * Get regulatory reportings (for ContentType=REG).
     * @return List of regulatory reporting names (e.g., EMIR, KIID, EMT, TPTSolvencyII, PRIIPS)
     */
    public List<String> getRegulatoryReportings() {
        return regulatoryReportings;
    }

    /**
     * Set regulatory reportings (for ContentType=REG).
     * @param regulatoryReportings List of regulatory reporting names
     */
    public void setRegulatoryReportings(List<String> regulatoryReportings) {
        this.regulatoryReportings = regulatoryReportings;
    }

    @Override
    public String toString() {
        return "AccessRule{" +
                "id='" + id + '\'' +
                ", contentType='" + contentType + '\'' +
                ", profiles=" + profiles +
                ", dataSupplierCreatorShort='" + dataSupplierCreatorShort + '\'' +
                ", dataSupplierCreatorName='" + dataSupplierCreatorName + '\'' +
                ", dataSuppliersGivenShort=" + dataSuppliersGivenShort +
                ", creationTime='" + creationTime + '\'' +
                ", accessDelayInDays='" + accessDelayInDays + '\'' +
                ", dateFrom='" + dateFrom + '\'' +
                ", dateTo='" + dateTo + '\'' +
                ", frequency='" + frequency + '\'' +
                ", costsByDataSupplier='" + costsByDataSupplier + '\'' +
                ", LEI=" + LEI +
                ", OENB_ID=" + OENB_ID +
                ", ISIN_SEGMENT=" + ISIN_SEGMENT +
                ", ISIN_SHARECLASS=" + ISIN_SHARECLASS +
                ", documentTypes=" + documentTypes +
                ", regulatoryReportings=" + regulatoryReportings +
                '}';
    }
}
