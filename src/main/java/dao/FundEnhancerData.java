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
package dao;

public class FundEnhancerData {
    private String isin;
    private String fondsbezeichnung;
    private String kag;
    private String oenbId;
    private String lei;
    private String leiStatus;
    private String wrapper;
    private String isinStatus;

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getFondsbezeichnung() {
        return fondsbezeichnung;
    }

    public void setFondsbezeichnung(String fondsbezeichnung) {
        this.fondsbezeichnung = fondsbezeichnung;
    }

    public String getKag() {
        return kag;
    }

    public void setKag(String kag) {
        this.kag = kag;
    }

    public String getOenbId() {
        return oenbId;
    }

    public void setOenbId(String oenbId) {
        this.oenbId = oenbId;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public String getLeiStatus() {
        return leiStatus;
    }

    public void setLeiStatus(String leiStatus) {
        this.leiStatus = leiStatus;
    }

    public String getWrapper() {
        return wrapper;
    }

    public void setWrapper(String wrapper) {
        this.wrapper = wrapper;
    }

    public String getIsinStatus() {
        return isinStatus;
    }

    public void setIsinStatus(String isinStatus) {
        this.isinStatus = isinStatus;
    }

    @Override
    public String toString() {
        return "FundEnhancerData{" + "isin='" + isin + '\'' + ", fondsbezeichnung='" + fondsbezeichnung + '\''
                + ", kag='" + kag + '\'' + ", oenbId='" + oenbId + '\'' + ", lei='" + lei + '\'' + ", leiStatus='"
                + leiStatus + '\'' + ", wrapper='" + wrapper + '\'' + ", isinStatus='" + isinStatus + '\'' + '}';
    }
}
