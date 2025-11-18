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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
        String output = "";

        try {
            String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            saveToBackup(fileContent, file.getName());
        } catch (Exception e) {
            log.error("Error uploading access rule", e);
        }

        return output;
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

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                }
            }

            saveToBackup(outputString, "DOWNLOAD_ACCESS_RULE");

        } catch (Exception e) {
            log.error("Error downloading access rules", e);
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

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    outputString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                }
            }

            saveToBackup(outputString, "DOWNLOAD_AR_ASSIGNED");

        } catch (Exception e) {
            log.error("Error downloading given access rules", e);
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
}
