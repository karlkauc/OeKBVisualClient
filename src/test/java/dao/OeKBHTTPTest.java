package dao;

import model.ApplicationSettings;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OeKBHTTPTest {

    @Test
    @DisplayName("Should download access rules successfully")
    void testDownloadAccessRules_Success() throws Exception {
        // Arrange
        String expectedXml = "<?xml version='1.0'?><AccessRules><Rule id='123'/></AccessRules>";
        HttpEntity stringEntity = new StringEntity(expectedXml, StandardCharsets.UTF_8);

        // Mock the static ApplicationSettings singleton
        try (MockedStatic<ApplicationSettings> mockedSettings = mockStatic(ApplicationSettings.class)) {
            ApplicationSettings mockAppSettings = mock(ApplicationSettings.class);
            mockedSettings.when(ApplicationSettings::getInstance).thenReturn(mockAppSettings);
            when(mockAppSettings.getAuthCredentialsBasic()).thenReturn("dGVzdDp0ZXN0"); // "test:test" base64
            when(mockAppSettings.getServerURL()).thenReturn("http://localhost:8080");

            // Mock the HttpClient and its response
            CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
            CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
            when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
            when(mockHttpResponse.getCode()).thenReturn(200);
            when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

            // Act: Create instance of OeKBHTTP inside the static mock scope
            OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient);
            String actualXml = oekbHttp.downloadAccessRules();

            // Assert
            assertEquals(expectedXml, actualXml);
        }
    }

    @Test
    @DisplayName("Should handle server error during download")
    void testDownloadAccessRules_ServerError() throws Exception {
        // Arrange
        String errorResponse = "Internal Server Error";
        HttpEntity stringEntity = new StringEntity(errorResponse, StandardCharsets.UTF_8);

        // Mock the static ApplicationSettings singleton
        try (MockedStatic<ApplicationSettings> mockedSettings = mockStatic(ApplicationSettings.class)) {
            ApplicationSettings mockAppSettings = mock(ApplicationSettings.class);
            mockedSettings.when(ApplicationSettings::getInstance).thenReturn(mockAppSettings);
            when(mockAppSettings.getAuthCredentialsBasic()).thenReturn("dGVzdDp0ZXN0");
            when(mockAppSettings.getServerURL()).thenReturn("http://localhost:8080");

            // Mock the HttpClient and its response
            CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
            CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
            when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
            when(mockHttpResponse.getCode()).thenReturn(500);
            when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

            // Act
            OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient);
            String actualResponse = oekbHttp.downloadAccessRules();

            // Assert
            assertEquals(errorResponse, actualResponse);
        }
    }
}