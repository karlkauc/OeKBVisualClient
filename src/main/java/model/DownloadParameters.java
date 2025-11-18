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
import java.util.ArrayList;
import java.util.List;

/**
 * Common parameters for all download operations
 */
public class DownloadParameters {
    private String mode;
    private LocalDate date;
    private String profile;
    private String dataSupplier;
    private List<String> leiOenIds;
    private List<String> isins;
    private Integer requestBlockSize;
    private boolean excludeInvalid;
    private String outputFileName;

    public DownloadParameters() {
        this.leiOenIds = new ArrayList<>();
        this.isins = new ArrayList<>();
        this.requestBlockSize = 10; // Default max block size
        this.excludeInvalid = false;
    }

    public DownloadParameters(String mode) {
        this();
        this.mode = mode;
    }

    // Getters and Setters
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDataSupplier() {
        return dataSupplier;
    }

    public void setDataSupplier(String dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public List<String> getLeiOenIds() {
        return leiOenIds;
    }

    public void setLeiOenIds(List<String> leiOenIds) {
        this.leiOenIds = leiOenIds;
    }

    public void addLeiOenId(String id) {
        this.leiOenIds.add(id);
    }

    public List<String> getIsins() {
        return isins;
    }

    public void setIsins(List<String> isins) {
        this.isins = isins;
    }

    public void addIsin(String isin) {
        this.isins.add(isin);
    }

    public Integer getRequestBlockSize() {
        return requestBlockSize;
    }

    public void setRequestBlockSize(Integer requestBlockSize) {
        this.requestBlockSize = requestBlockSize;
    }

    public boolean isExcludeInvalid() {
        return excludeInvalid;
    }

    public void setExcludeInvalid(boolean excludeInvalid) {
        this.excludeInvalid = excludeInvalid;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public boolean hasLeiOenIds() {
        return leiOenIds != null && !leiOenIds.isEmpty();
    }

    public boolean hasIsins() {
        return isins != null && !isins.isEmpty();
    }
}
