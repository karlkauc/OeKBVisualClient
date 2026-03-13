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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class FundEnhancer {
    private static final Logger LOG = LogManager.getLogger(FundEnhancer.class);
    private static FundEnhancer instance;

    private final List<FundEnhancerData> data = new ArrayList<>();
    private final Map<String, String> fundNameCache = new HashMap<>();
    private volatile boolean dataLoaded;

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

        LOG.debug("LEI: {}", getFundNameByID("529900RNEPM2AX88SJ32"));
        LOG.debug("ISIN: {}", getFundNameByID("AT0000A20FX4"));
        LOG.debug("Fund name by LEI: {}",
                getInstance().getAllFundDataByID("529900RNEPM2AX88SJ32").get(0).getFondsbezeichnung());
        LOG.debug("Fund name by ISIN: {}",
                getInstance().getAllFundDataByID("AT0000A20FX4").get(0).getFondsbezeichnung());
        LOG.debug("Fund name by LEI: {}",
                getInstance().getAllFundDataByID("529900S56OD7UE7H1V04").get(0).getFondsbezeichnung());
    }

    public synchronized void readData() {
        if (dataLoaded) {
            return;
        }

        String filePath = "resources" + File.separator + "isinlei.csv";
        File file = new File(filePath);

        if (file.exists()) {
            LOG.debug("ISIN-LEI File [{}] will be loaded", file.getAbsolutePath());
            try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                loadDataFromReader(br);
            } catch (IOException e) {
                LOG.error("Error reading ISIN LEI file", e);
            }
        } else {
            LOG.info("ISIN-LEI CSV File not found under 'resources' folder: {}", file.getAbsolutePath());
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
                fed.setIsin(parts[0]);
                fed.setFondsbezeichnung(parts[1].trim());
                fed.setKag(parts[2].trim());
                fed.setOenbId(parts[3].trim());
                fed.setLei(parts[4].trim());
                fed.setLeiStatus(parts[5].trim());
                fed.setWrapper(parts[6].trim());
                fed.setIsinStatus(parts[7].trim());
                data.add(fed);
                recordCount++;
            }
        }
        LOG.debug("Loaded {} records from reader", recordCount);
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

    public synchronized List<FundEnhancerData> getAllFundDataByID(String id) {
        if (data.isEmpty()) {
            readData();
        }

        List<FundEnhancerData> leiReturn = data.stream().filter(row -> id.equals(row.getLei()))
                .collect(Collectors.toList());

        List<FundEnhancerData> oenbReturn = data.stream().filter(row -> id.equals(row.getOenbId()))
                .collect(Collectors.toList());

        List<FundEnhancerData> isinReturn = data.stream().filter(row -> id.equals(row.getIsin()))
                .collect(Collectors.toList());

        List<String> ignore = List.of("(A)", "(T)", "(VT)", "(V)", "(VA)", "(R)", "(I)", "(S)", "(VS)", "(RT)", "(VI)",
                "(IT)");

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
        FundEnhancer inst = getInstance();

        // Check cache first
        synchronized (inst) {
            if (inst.fundNameCache.containsKey(id)) {
                return inst.fundNameCache.get(id);
            }
        }

        List<FundEnhancerData> temp = inst.getAllFundDataByID(id);
        String result;

        if (temp != null && !temp.isEmpty()) {
            result = temp.get(0).getFondsbezeichnung();
        } else {
            result = "not found";
        }

        // Cache the result
        synchronized (inst) {
            inst.fundNameCache.put(id, result);
        }
        return result;
    }
}
