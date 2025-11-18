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
    private String ISIN;
    private String Fondsbezeichnung;
    private String KAG;
    private String OENB_ID;
    private String LEI;
    private String LEIStatus;
    private String WRAPPER;
    private String ISINStatus;

    public String getISIN() {
        return ISIN;
    }

    public void setISIN(String ISIN) {
        this.ISIN = ISIN;
    }

    public String getFondsbezeichnung() {
        return Fondsbezeichnung;
    }

    public void setFondsbezeichnung(String Fondsbezeichnung) {
        this.Fondsbezeichnung = Fondsbezeichnung;
    }

    public String getKAG() {
        return KAG;
    }

    public void setKAG(String KAG) {
        this.KAG = KAG;
    }

    public String getOENB_ID() {
        return OENB_ID;
    }

    public void setOENB_ID(String OENB_ID) {
        this.OENB_ID = OENB_ID;
    }

    public String getLEI() {
        return LEI;
    }

    public void setLEI(String LEI) {
        this.LEI = LEI;
    }

    public String getLEIStatus() {
        return LEIStatus;
    }

    public void setLEIStatus(String LEIStatus) {
        this.LEIStatus = LEIStatus;
    }

    public String getWRAPPER() {
        return WRAPPER;
    }

    public void setWRAPPER(String WRAPPER) {
        this.WRAPPER = WRAPPER;
    }

    public String getISINStatus() {
        return ISINStatus;
    }

    public void setISINStatus(String ISINStatus) {
        this.ISINStatus = ISINStatus;
    }

    @Override
    public String toString() {
        return "FundEnhancerData{" +
                "ISIN='" + ISIN + '\'' +
                ", Fondsbezeichnung='" + Fondsbezeichnung + '\'' +
                ", KAG='" + KAG + '\'' +
                ", OENB_ID='" + OENB_ID + '\'' +
                ", LEI='" + LEI + '\'' +
                ", LEIStatus='" + LEIStatus + '\'' +
                ", WRAPPER='" + WRAPPER + '\'' +
                ", ISINStatus='" + ISINStatus + '\'' +
                '}';
    }
}
