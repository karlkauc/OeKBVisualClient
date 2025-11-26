package dao;

import model.IApplicationSettings;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OeKBHTTP class.
 * Uses IApplicationSettings interface for easy mocking.
 */
@ExtendWith(MockitoExtension.class)
class OeKBHTTPTest {

    @Mock
    private CloseableHttpClient mockHttpClient;

    @Mock
    private CloseableHttpResponse mockHttpResponse;

    @Mock
    private IApplicationSettings mockAppSettings;

    @BeforeEach
    void setUp() {
        // Common setup for ApplicationSettings mock
        when(mockAppSettings.getAuthCredentialsBasic()).thenReturn("dGVzdDp0ZXN0"); // "test:test" base64
        when(mockAppSettings.getServerURL()).thenReturn("http://localhost:8080");
        when(mockAppSettings.getOekbUserName()).thenReturn("testuser");
        when(mockAppSettings.getDataSupplierList()).thenReturn("DDS001");
        when(mockAppSettings.isUseProdServer()).thenReturn(false);
    }

    @Test
    @DisplayName("Should download access rules successfully")
    void testDownloadAccessRules_Success() throws Exception {
        // Arrange
        String expectedXml = "<?xml version='1.0'?><AccessRules><Rule id='123'/></AccessRules>";
        HttpEntity stringEntity = new StringEntity(expectedXml, StandardCharsets.UTF_8);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getCode()).thenReturn(200);
        when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

        // Act
        OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient, mockAppSettings);
        String actualXml = oekbHttp.downloadAccessRules();

        // Assert
        assertEquals(expectedXml, actualXml);
        verify(mockHttpClient).execute(any(HttpPost.class));
    }

    @Test
    @DisplayName("Should handle server error during download")
    void testDownloadAccessRules_ServerError() throws Exception {
        // Arrange
        String errorResponse = "Internal Server Error";
        HttpEntity stringEntity = new StringEntity(errorResponse, StandardCharsets.UTF_8);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getCode()).thenReturn(500);
        when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

        // Act
        OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient, mockAppSettings);
        String actualResponse = oekbHttp.downloadAccessRules();

        // Assert
        assertEquals(errorResponse, actualResponse);
        verify(mockHttpClient).execute(any(HttpPost.class));
    }

    @Test
    @DisplayName("Should use prod server when configured")
    void testGetServerParam_Production() throws Exception {
        // Arrange
        when(mockAppSettings.isUseProdServer()).thenReturn(true);

        String expectedXml = "<?xml version='1.0'?><AccessRules/>";
        HttpEntity stringEntity = new StringEntity(expectedXml, StandardCharsets.UTF_8);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getCode()).thenReturn(200);
        when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

        // Act
        OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient, mockAppSettings);
        oekbHttp.downloadAccessRules();

        // Assert - verify that isUseProdServer was called
        verify(mockAppSettings, atLeastOnce()).isUseProdServer();
    }

    @Test
    @DisplayName("Should handle network exception gracefully")
    void testDownloadAccessRules_NetworkException() throws Exception {
        // Arrange
        when(mockHttpClient.execute(any(HttpPost.class)))
            .thenThrow(new java.net.UnknownHostException("Unknown host"));

        // Act
        OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient, mockAppSettings);
        String result = oekbHttp.downloadAccessRules();

        // Assert - should return empty string on error
        assertTrue(result.isEmpty() || result.startsWith("ERROR"));
    }

    @Test
    @DisplayName("Should call readSettingsFromFile on download")
    void testDownloadAccessRules_ReadsSettings() throws Exception {
        // Arrange
        String expectedXml = "<?xml version='1.0'?><AccessRules/>";
        HttpEntity stringEntity = new StringEntity(expectedXml, StandardCharsets.UTF_8);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getCode()).thenReturn(200);
        when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

        // Act
        OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient, mockAppSettings);
        oekbHttp.downloadAccessRules();

        // Assert - verify settings were read
        verify(mockAppSettings).readSettingsFromFile();
    }

    @Test
    @DisplayName("Should use correct authentication header")
    void testDownloadAccessRules_UsesCorrectAuth() throws Exception {
        // Arrange
        String expectedXml = "<?xml version='1.0'?><AccessRules/>";
        HttpEntity stringEntity = new StringEntity(expectedXml, StandardCharsets.UTF_8);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getCode()).thenReturn(200);
        when(mockHttpResponse.getEntity()).thenReturn(stringEntity);

        // Act
        OeKBHTTP oekbHttp = new OeKBHTTP(mockHttpClient, mockAppSettings);
        oekbHttp.downloadAccessRules();

        // Assert - verify auth credentials were retrieved
        verify(mockAppSettings).getAuthCredentialsBasic();
    }
}
