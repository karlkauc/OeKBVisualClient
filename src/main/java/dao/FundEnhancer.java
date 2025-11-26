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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FundEnhancer {
    private static final Logger log = LogManager.getLogger(FundEnhancer.class);
    private static FundEnhancer instance;

    private List<FundEnhancerData> data = new ArrayList<>();
    private Map<String, String> fundNameCache = new HashMap<>();
    private boolean dataLoaded = false;

    private FundEnhancer() {
        // Private constructor for singleton
    }

    public static synchronized FundEnhancer getInstance() {
        if (instance == null) {
            instance = new FundEnhancer();
        }
        return instance;
    }

    public static void main(String[] args) {
        getInstance().readData();

        log.debug("LEI: {}", getFundNameByID("529900RNEPM2AX88SJ32"));
        log.debug("ISIN: {}", getFundNameByID("AT0000A20FX4"));
        log.debug("Fund name by LEI: {}", getInstance().getAllFundDataByID("529900RNEPM2AX88SJ32").get(0).getFondsbezeichnung());
        log.debug("Fund name by ISIN: {}", getInstance().getAllFundDataByID("AT0000A20FX4").get(0).getFondsbezeichnung());
        log.debug("Fund name by LEI: {}", getInstance().getAllFundDataByID("529900S56OD7UE7H1V04").get(0).getFondsbezeichnung());
    }

    public synchronized void readData() {
        if (dataLoaded) {
            return;
        }

        String filePath = "resources" + File.separator + "isinlei.csv";
        File file = new File(filePath);

        if (file.exists()) {
            log.debug("ISIN-LEI File [{}] will be loaded", file.getAbsolutePath());
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader br = new BufferedReader(fileReader)) {
                loadDataFromReader(br);
            } catch (IOException e) {
                log.error("Error reading ISIN LEI file", e);
            }
        } else {
            log.info("ISIN-LEI CSV File not found under 'resources' folder: {}", file.getAbsolutePath());
        }
    }

    // Made public for testing purposes
    public synchronized void loadDataFromReader(BufferedReader br) throws IOException {
        if (dataLoaded) {
            return;
        }
        
        String line;
        int recordCount = 0;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length >= 8) {
                FundEnhancerData fed = new FundEnhancerData();
                fed.setISIN(parts[0]);
                fed.setFondsbezeichnung(parts[1].trim());
                fed.setKAG(parts[2].trim());
                fed.setOENB_ID(parts[3].trim());
                fed.setLEI(parts[4].trim());
                fed.setLEIStatus(parts[5].trim());
                fed.setWRAPPER(parts[6].trim());
                fed.setISINStatus(parts[7].trim());
                data.add(fed);
                recordCount++;
            }
        }
        log.debug("Loaded {} records from reader", recordCount);
        dataLoaded = true;
    }

    /**
     * Resets the singleton instance. Used for testing.
     */
    public static synchronized void reset() {
        if (instance != null) {
            instance.data.clear();
            instance.fundNameCache.clear();
            instance.dataLoaded = false;
        }
    }

    public List<FundEnhancerData> getAllFundDataByID(String id) {
        if (data.size() == 0) {
            readData();
        }

        List<FundEnhancerData> leiReturn = data.stream()
                .filter(row -> id.equals(row.getLEI()))
                .collect(Collectors.toList());

        List<FundEnhancerData> oenbReturn = data.stream()
                .filter(row -> id.equals(row.getOENB_ID()))
                .collect(Collectors.toList());

        List<FundEnhancerData> isinReturn = data.stream()
                .filter(row -> id.equals(row.getISIN()))
                .collect(Collectors.toList());

        List<String> ignore = List.of("(A)", "(T)", "(VT)", "(V)", "(VA)", "(R)", "(I)", "(S)", "(VS)", "(RT)", "(VI)", "(IT)");

        List<FundEnhancerData> combine = new ArrayList<>();
        combine.addAll(leiReturn);
        combine.addAll(oenbReturn);
        combine.addAll(isinReturn);

        for (FundEnhancerData fundData : combine) {
            for (String suffix : ignore) {
                if (fundData.getFondsbezeichnung() != null && fundData.getFondsbezeichnung().endsWith(suffix)) {
                    fundData.setFondsbezeichnung(fundData.getFondsbezeichnung().replace(suffix, "").trim());
                }
            }
        }

        return combine;
    }

    public static String getFundNameByID(String id) {
        // Check cache first
        FundEnhancer instance = getInstance();
        if (instance.fundNameCache.containsKey(id)) {
            return instance.fundNameCache.get(id);
        }

        List<FundEnhancerData> temp = instance.getAllFundDataByID(id);
        String result;

        if (temp != null && !temp.isEmpty()) {
            result = temp.get(0).getFondsbezeichnung();
        } else {
            result = "not found";
        }

        // Cache the result
        instance.fundNameCache.put(id, result);
        return result;
    }
}
