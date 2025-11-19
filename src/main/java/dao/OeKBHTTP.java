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

import model.ApplicationSettings;
import model.DownloadParameters;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OeKBHTTP {
    private static final Logger log = LogManager.getLogger(OeKBHTTP.class);
    private static ApplicationSettings applicationSettings = ApplicationSettings.getInstance();

    private static CloseableHttpClient getOekbConnection() {
        applicationSettings.readSettingsFromFile();

        String proxyHost = "";
        Integer proxyPort = 0;
        boolean useCustomProxy = false;

        if (applicationSettings.isConnectionUseSystemSettings()) {
            log.debug("using system proxy Settings");
            System.setProperty("java.net.useSystemProxies", "true");
            useCustomProxy = false;
        } else {
            if (applicationSettings.getConnectionProxyHost() != null &&
                !applicationSettings.getConnectionProxyHost().isEmpty()) {
                proxyHost = applicationSettings.getConnectionProxyHost();
                proxyPort = applicationSettings.getConnectionProxyPort();
            }
        }

        if (proxyHost != null && proxyHost.length() > 1 && proxyPort != null && proxyPort > 1) {
            useCustomProxy = true;
            log.debug("proxy settings: " + proxyHost + ":" + proxyPort);
        } else {
            log.info("no valid proxy settings found!");
        }

        if (useCustomProxy) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            return HttpClients.custom()
                    .setRoutePlanner(routePlanner)
                    .build();
        } else {
            return HttpClients.createDefault();
        }
    }

    public static String uploadAccessRule(File file) {
        applicationSettings.readSettingsFromFile();
        String outputString = "";

        // Check if in FileSystem mode (offline mode)
        if (applicationSettings.isFileSystem()) {
            try {
                String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                saveToBackup(fileContent, "ACCESS_RULE_UPLOAD_OFFLINE");
                log.info("OFFLINE MODE: Access rule saved to backup instead of uploading to server");
                return "SUCCESS (OFFLINE MODE)\n\nAccess rule saved to backup directory.\nNo actual upload to server performed.";
            } catch (Exception e) {
                log.error("Error saving access rule in offline mode", e);
                return "ERROR: " + e.getMessage();
            }
        }

        // Real mode: HTTP POST to server
        try (CloseableHttpClient httpClient = getOekbConnection()) {
            HttpPost httpPost = new HttpPost(applicationSettings.getServerURL());

            // Set headers
            String authHeader = "Basic " + applicationSettings.getAuthCredentialsBasic();
            httpPost.setHeader("Authorization", authHeader);
            httpPost.setHeader("User-Agent", "(OeKBVisualClient)");
            httpPost.setHeader("Accept-Encoding", "gzip,deflate");

            // Build multipart entity
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("mode", "UPLOAD_ACCESS_RULE");
            builder.addTextBody("server", applicationSettings.isUseProdServer() ? "prod" : "test");
            builder.addTextBody("user", applicationSettings.getOekbUserName());
            builder.addTextBody("datasupplier", applicationSettings.getDataSupplierList());
            builder.addTextBody("clientversion", "4.4.0");
            builder.addTextBody("fileToUploadName", file.getName());
            builder.addBinaryBody("fileToUpload", file);

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            log.info("Uploading access rule to server: {}", file.getName());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                log.debug("Server response status: {}", statusCode);

                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);

                    if (statusCode != 200) {
                        log.warn("Server returned non-OK status code: {}. Response: {}",
                            statusCode,
                            outputString.length() > 200 ? outputString.substring(0, 200) + "..." : outputString);
                    } else {
                        log.info("Access rule uploaded successfully");
                    }
                } else {
                    log.warn("No response entity received from server");
                    outputString = "ERROR: No response from server";
                }
            }

            // Save backup of both request and response
            String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            saveToBackup(fileContent, "ACCESS_RULE_UPLOAD_REQUEST");
            if (!outputString.isEmpty()) {
                saveToBackup(outputString, "ACCESS_RULE_UPLOAD_RESPONSE");
            }

        } catch (java.net.UnknownHostException e) {
            log.error("Cannot reach server: {}. Check network connection and server URL.", e.getMessage());
            outputString = "ERROR: Cannot reach server - " + e.getMessage();
        } catch (java.net.ConnectException e) {
            log.error("Connection refused: {}. Check if proxy settings are correct and server is reachable.", e.getMessage());
            outputString = "ERROR: Connection refused - " + e.getMessage();
        } catch (javax.net.ssl.SSLException e) {
            log.error("SSL/TLS error: {}. Check certificate configuration.", e.getMessage());
            outputString = "ERROR: SSL/TLS error - " + e.getMessage();
        } catch (java.net.SocketTimeoutException e) {
            log.error("Connection timeout: {}. Server may be slow or unreachable.", e.getMessage());
            outputString = "ERROR: Connection timeout - " + e.getMessage();
        } catch (Exception e) {
            log.error("Error uploading access rule: {}. Check credentials, proxy settings, and network connection.",
                e.getMessage());
            log.debug("Full exception details", e);
            outputString = "ERROR: " + e.getMessage();
        }

        return outputString;
    }

    public static String uploadDataFile(File file) {
        applicationSettings.readSettingsFromFile();
        String outputString = "";

        try (CloseableHttpClient httpClient = getOekbConnection()) {
            HttpPost httpPost = new HttpPost(applicationSettings.getServerURL());

            // Set headers
            String authHeader = "Basic " + applicationSettings.getAuthCredentialsBasic();
            httpPost.setHeader("Authorization", authHeader);
            httpPost.setHeader("User-Agent", "(KarlK)");
            httpPost.setHeader("Accept-Encoding", "gzip,deflate");

            // Build multipart entity
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("mode", "UPLOAD_DATA");
            builder.addTextBody("server", "test");
            builder.addTextBody("user", applicationSettings.getOekbUserName());
            builder.addTextBody("datasupplier", applicationSettings.getDataSupplierList());
            builder.addTextBody("clientversion", "meine");
            builder.addTextBody("fileToUploadName", file.getName());
            builder.addTextBody("upload_xml", file.getName());
            builder.addBinaryBody("fileToUpload", file);

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                }
            }

            String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            saveToBackup(fileContent, file.getName());
            saveToBackup(outputString, "UPLOAD_DATA_REPLY");

        } catch (Exception e) {
            log.error("Error uploading data file", e);
        }

        return outputString;
    }

    public static String downloadAccessRules() {
        applicationSettings.readSettingsFromFile();
        String outputString = "";

        try (CloseableHttpClient httpClient = getOekbConnection()) {
            HttpPost httpPost = new HttpPost(applicationSettings.getServerURL());

            // Set headers
            String authHeader = "Basic " + applicationSettings.getAuthCredentialsBasic();
            httpPost.setHeader("Authorization", authHeader);
            httpPost.setHeader("User-Agent", "(KarlK)");
            httpPost.setHeader("Accept-Encoding", "gzip,deflate");

            // Build form parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("mode", "DOWNLOAD_AR_RECEIVED"));
            params.add(new BasicNameValuePair("server", "test"));
            params.add(new BasicNameValuePair("user", applicationSettings.getOekbUserName()));
            params.add(new BasicNameValuePair("datasupplier", applicationSettings.getDataSupplierList()));
            params.add(new BasicNameValuePair("clientversion", "meine"));

            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            log.debug("Requesting access rules from: {}", applicationSettings.getServerURL());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                log.debug("Server response status: {}", statusCode);

                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);

                    if (outputString == null || outputString.trim().isEmpty()) {
                        log.warn("Server returned empty response. Check credentials and server availability.");
                    } else if (statusCode != 200) {
                        log.warn("Server returned non-OK status code: {}. Response: {}",
                            statusCode,
                            outputString.length() > 200 ? outputString.substring(0, 200) + "..." : outputString);
                    } else {
                        log.debug("Received {} bytes from server", outputString.length());
                    }
                } else {
                    log.warn("No response entity received from server");
                }
            }

            if (outputString != null && !outputString.trim().isEmpty()) {
                saveToBackup(outputString, "DOWNLOAD_ACCESS_RULE");
            }

        } catch (java.net.UnknownHostException e) {
            log.error("Cannot reach server: {}. Check network connection and server URL.", e.getMessage());
        } catch (java.net.ConnectException e) {
            log.error("Connection refused: {}. Check if proxy settings are correct and server is reachable.", e.getMessage());
        } catch (javax.net.ssl.SSLException e) {
            log.error("SSL/TLS error: {}. Check certificate configuration.", e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            log.error("Connection timeout: {}. Server may be slow or unreachable.", e.getMessage());
        } catch (Exception e) {
            log.error("Error downloading access rules: {}. Check credentials, proxy settings, and network connection.",
                e.getMessage());
            log.debug("Full exception details", e);
        }

        return outputString;
    }

    public static String downloadGivenAccessRules() {
        applicationSettings.readSettingsFromFile();
        String outputString = "";

        try (CloseableHttpClient httpClient = getOekbConnection()) {
            HttpPost httpPost = new HttpPost(applicationSettings.getServerURL());

            // Set headers
            String authHeader = "Basic " + applicationSettings.getAuthCredentialsBasic();
            httpPost.setHeader("Authorization", authHeader);
            httpPost.setHeader("User-Agent", "(KarlK)");
            httpPost.setHeader("Accept-Encoding", "gzip,deflate");

            // Build form parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("mode", "DOWNLOAD_AR_ASSIGNED"));
            params.add(new BasicNameValuePair("server", "test"));
            params.add(new BasicNameValuePair("user", applicationSettings.getOekbUserName()));
            params.add(new BasicNameValuePair("datasupplier", applicationSettings.getDataSupplierList()));
            params.add(new BasicNameValuePair("clientversion", "meine"));

            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            log.debug("Requesting access rules from: {}", applicationSettings.getServerURL());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                log.debug("Server response status: {}", statusCode);

                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);

                    if (outputString == null || outputString.trim().isEmpty()) {
                        log.warn("Server returned empty response. Check credentials and server availability.");
                    } else if (statusCode != 200) {
                        log.warn("Server returned non-OK status code: {}. Response: {}",
                            statusCode,
                            outputString.length() > 200 ? outputString.substring(0, 200) + "..." : outputString);
                    } else {
                        log.debug("Received {} bytes from server", outputString.length());
                    }
                } else {
                    log.warn("No response entity received from server");
                }
            }

            if (outputString != null && !outputString.trim().isEmpty()) {
                saveToBackup(outputString, "DOWNLOAD_AR_ASSIGNED");
            }

        } catch (java.net.UnknownHostException e) {
            log.error("Cannot reach server: {}. Check network connection and server URL.", e.getMessage());
        } catch (java.net.ConnectException e) {
            log.error("Connection refused: {}. Check if proxy settings are correct and server is reachable.", e.getMessage());
        } catch (javax.net.ssl.SSLException e) {
            log.error("SSL/TLS error: {}. Check certificate configuration.", e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            log.error("Connection timeout: {}. Server may be slow or unreachable.", e.getMessage());
        } catch (Exception e) {
            log.error("Error downloading given access rules: {}. Check credentials, proxy settings, and network connection.",
                e.getMessage());
            log.debug("Full exception details", e);
        }

        return outputString;
    }

    public static void saveToBackup(String xml, String fileName) {
        try {
            String filePraefix = applicationSettings.isUseProdServer() ? "PROD" : "DEV";
            filePraefix += "_" + applicationSettings.getDataSupplierList();

            new File(applicationSettings.getBackupDirectory()).mkdirs();

            String filePath = applicationSettings.getBackupDirectory() + File.separator +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) +
                    "__" + filePraefix + "_" + fileName + ".xml";

            // Fix double .xml.xml extension
            filePath = filePath.replace(".xml.xml", ".xml");

            // Parse and pretty print XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            File outputFile = new File(filePath);
            try (FileWriter writer = new FileWriter(outputFile)) {
                transformer.transform(new DOMSource(doc), new StreamResult(writer));
            }

            log.info("Saving Backup File: " + filePath);

        } catch (Exception e) {
            log.error("Error saving backup file", e);
        }
    }

    /**
     * Generic download method for all download modes
     * @param params Download parameters
     * @return XML response as string
     */
    private static String genericDownload(Map<String, String> params) {
        applicationSettings.readSettingsFromFile();
        String outputString = "";

        try (CloseableHttpClient httpClient = getOekbConnection()) {
            HttpPost httpPost = new HttpPost(applicationSettings.getServerURL());

            // Set headers
            String authHeader = "Basic " + applicationSettings.getAuthCredentialsBasic();
            httpPost.setHeader("Authorization", authHeader);
            httpPost.setHeader("User-Agent", "(OeKBVisualClient)");
            httpPost.setHeader("Accept-Encoding", "gzip,deflate");

            // Build form parameters
            List<NameValuePair> formParams = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                }
            }

            // Save backup if mode is available
            String mode = params.get("mode");
            if (mode != null) {
                saveToBackup(outputString, mode + "_RESPONSE");
            }

        } catch (Exception e) {
            log.error("Error in generic download", e);
        }

        return outputString;
    }

    /**
     * DOWNLOAD_FUND - Download fund data by LEI or OeNB-ID
     */
    public static String downloadFund(DownloadParameters params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_FUND");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (params.getDate() != null) {
            requestParams.put("date", params.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (params.getProfile() != null) {
            requestParams.put("profile", params.getProfile());
        }

        if (params.hasLeiOenIds()) {
            requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
        }

        if (params.getRequestBlockSize() != null) {
            requestParams.put("requestblock-size", params.getRequestBlockSize().toString());
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_SHARECLASS_SEGMENT - Download shareclass/segment data by ISIN
     */
    public static String downloadShareClass(DownloadParameters params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_SHARECLASS_SEGMENT");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (params.getDate() != null) {
            requestParams.put("date", params.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (params.getProfile() != null) {
            requestParams.put("profile", params.getProfile());
        }

        if (params.hasIsins()) {
            requestParams.put("isin", String.join(" ", params.getIsins()));
        }

        if (params.getRequestBlockSize() != null) {
            requestParams.put("requestblock-size", params.getRequestBlockSize().toString());
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_OENB_AGGREGIERUNG - Download OeNB aggregated data
     */
    public static String downloadOeNBAggregierung(DownloadParameters params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_OENB_AGGREGIERUNG");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (params.getDate() != null) {
            requestParams.put("date", params.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (params.hasLeiOenIds()) {
            requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
        }

        if (params.isExcludeInvalid()) {
            requestParams.put("excludeinvalid", "true");
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_OENB_SECBYSEC - Download OeNB Security-by-Security data
     */
    public static String downloadOeNBSecBySec(DownloadParameters params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_OENB_SECBYSEC");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (params.getDate() != null) {
            requestParams.put("date", params.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (params.hasLeiOenIds()) {
            requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
        }

        if (params.isExcludeInvalid()) {
            requestParams.put("excludeinvalid", "true");
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_OENB_CHECK - Download OeNB aggregation check
     */
    public static String downloadOeNBCheck(LocalDate date, String validFilter) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_OENB_CHECK");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (date != null) {
            requestParams.put("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (validFilter != null) {
            requestParams.put("valid", validFilter); // "true" or "false"
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_JOURNAL - Download journal entries
     */
    public static String downloadJournal(LocalDateTime timeFrom, LocalDateTime timeTo,
                                        String action, String type, String userJournal,
                                        String uniqueId, boolean excludeEmptyDownloads) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_JOURNAL");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (timeFrom != null) {
            requestParams.put("time_from", timeFrom.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (timeTo != null) {
            requestParams.put("time_to", timeTo.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (action != null) {
            requestParams.put("action", action); // UL, DL
        }

        if (type != null) {
            requestParams.put("type", type); // AR, FXML_DATA, etc.
        }

        if (userJournal != null) {
            requestParams.put("user_journal", userJournal);
        }

        if (uniqueId != null) {
            requestParams.put("unique_id", uniqueId);
        }

        if (excludeEmptyDownloads) {
            requestParams.put("exclude_empty_dl", "true");
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_DOCUMENTS - Download documents
     */
    public static String downloadDocuments(DownloadParameters params, String documentType) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_DOCUMENTS");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (params.getDate() != null) {
            requestParams.put("date", params.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (params.getProfile() != null) {
            requestParams.put("profile", params.getProfile());
        }

        if (params.hasLeiOenIds()) {
            requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
        }

        if (params.hasIsins()) {
            requestParams.put("isin", String.join(" ", params.getIsins()));
        }

        if (documentType != null) {
            // Check if it's a listed or unlisted document type
            if (isListedDocumentType(documentType)) {
                requestParams.put("listed_doc_type", documentType);
            } else {
                requestParams.put("unlisted_doc_type", documentType);
            }
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_REG_REPORTINGS - Download regulatory reportings
     */
    public static String downloadRegulatoryReportings(DownloadParameters params, String reportingType) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_REG_REPORTINGS");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (params.getDate() != null) {
            requestParams.put("date", params.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (params.getProfile() != null) {
            requestParams.put("profile", params.getProfile());
        }

        if (params.hasLeiOenIds()) {
            requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
        }

        if (params.hasIsins()) {
            requestParams.put("isin", String.join(" ", params.getIsins()));
        }

        if (reportingType != null) {
            requestParams.put("reg_reporting_type", reportingType);
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_AVAILABLE_DATA - Download available data information
     */
    public static String downloadAvailableData(LocalDate contentDate, LocalDateTime uploadTimeFrom,
                                               LocalDateTime uploadTimeTo, String fdpContent,
                                               DownloadParameters params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_AVAILABLE_DATA");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params != null && params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (contentDate != null) {
            requestParams.put("date", contentDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (uploadTimeFrom != null) {
            requestParams.put("upload_time_from",
                    uploadTimeFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        }

        if (uploadTimeTo != null) {
            requestParams.put("upload_time_to",
                    uploadTimeTo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        }

        if (fdpContent != null) {
            requestParams.put("fdp_content", fdpContent); // FUND, REG, DOC
        }

        if (params != null) {
            if (params.hasLeiOenIds()) {
                requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
            }
            if (params.hasIsins()) {
                requestParams.put("isin", String.join(" ", params.getIsins()));
            }
        }

        return genericDownload(requestParams);
    }

    /**
     * DOWNLOAD_OWN_DATA_DOWNLOADED - Download information about own data downloaded by others
     */
    public static String downloadOwnDataDownloaded(LocalDate dateFrom, LocalDate dateTo,
                                                   String fdpContent, DownloadParameters params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("mode", "DOWNLOAD_OWN_DATA_DOWNLOADED");
        requestParams.put("server", applicationSettings.isUseProdServer() ? "prod" : "test");
        requestParams.put("user", applicationSettings.getOekbUserName());
        requestParams.put("datasupplier", params != null && params.getDataSupplier() != null ?
                params.getDataSupplier() : applicationSettings.getDataSupplierList());
        requestParams.put("clientversion", "4.4.0");

        if (dateFrom != null) {
            requestParams.put("date_from", dateFrom.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (dateTo != null) {
            requestParams.put("date_to", dateTo.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (fdpContent != null) {
            requestParams.put("fdp_content", fdpContent); // FUND, REG, DOC
        }

        if (params != null) {
            if (params.hasLeiOenIds()) {
                requestParams.put("lei_oen_id", String.join(" ", params.getLeiOenIds()));
            }
            if (params.hasIsins()) {
                requestParams.put("isin", String.join(" ", params.getIsins()));
            }
        }

        return genericDownload(requestParams);
    }

    /**
     * Helper method to check if document type is listed
     */
    private static boolean isListedDocumentType(String docType) {
        return docType.equals("AIFMD") || docType.equals("AnnualReport") ||
               docType.equals("AuditReport") || docType.equals("Factsheet") ||
               docType.equals("KID") || docType.equals("Prospectus") ||
               docType.equals("PRIIPS-KID");
    }

    /**
     * Batch upload multiple files
     */
    public static List<String> uploadDataFiles(List<File> files) {
        List<String> results = new ArrayList<>();

        for (File file : files) {
            log.info("Uploading file: " + file.getName());
            String result = uploadDataFile(file);
            results.add(result);
        }

        return results;
    }
}
