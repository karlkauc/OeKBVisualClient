package model;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationSettingsSecurityTest {

    private static final String TEST_SETTINGS_FILE = "test_settings_security.xml";

    @BeforeEach
    void setUp() {
        // Reset singleton for each test
        resetSingleton();
    }

    @AfterEach
    void tearDown() {
        new File(TEST_SETTINGS_FILE).delete();
    }

    private void resetSingleton() {
        try {
            var field = ApplicationSettings.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singleton: " + e.getMessage());
        }
    }

    // ========== K3: Credential Encoding ==========

    @Test
    @DisplayName("getAuthCredentialsBasic should correctly encode ASCII credentials")
    void authCredentials_asciiCredentials() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.setOekbUserName("testuser");
        settings.setOekbPasswort("testpass");

        String encoded = settings.getAuthCredentialsBasic();
        String decoded = new String(Base64.decodeBase64(encoded), StandardCharsets.UTF_8);

        assertEquals("testuser:testpass", decoded);
    }

    @Test
    @DisplayName("getAuthCredentialsBasic should preserve umlauts (UTF-8)")
    void authCredentials_preservesUmlauts() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.setOekbUserName("müller");
        settings.setOekbPasswort("paßwört");

        String encoded = settings.getAuthCredentialsBasic();
        String decoded = new String(Base64.decodeBase64(encoded), StandardCharsets.UTF_8);

        assertEquals("müller:paßwört", decoded);
    }

    @Test
    @DisplayName("getAuthCredentialsBasic should handle special characters")
    void authCredentials_specialCharacters() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.setOekbUserName("user@domain.at");
        settings.setOekbPasswort("p@$$w0rd!#€");

        String encoded = settings.getAuthCredentialsBasic();
        String decoded = new String(Base64.decodeBase64(encoded), StandardCharsets.UTF_8);

        assertEquals("user@domain.at:p@$$w0rd!#€", decoded);
    }

    @Test
    @DisplayName("getAuthCredentialsBasic should produce valid Base64")
    void authCredentials_validBase64() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.setOekbUserName("user");
        settings.setOekbPasswort("pass");

        String encoded = settings.getAuthCredentialsBasic();

        assertTrue(Base64.isBase64(encoded), "Output should be valid Base64");
        assertFalse(encoded.contains(":"), "Base64 output should not contain raw colon");
    }

    // ========== Settings XML write/read roundtrip ==========

    @Test
    @DisplayName("Settings roundtrip should preserve all fields")
    void settingsRoundtrip_preservesFields() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.setOekbUserName("testuser");
        settings.setOekbPasswort("testpass");
        settings.setUseProdServer(true);
        settings.setConnectionProxyHost("proxy.example.com");
        settings.setConnectionProxyPort(8080);
        settings.setDataSupplierList("FUM");
        settings.setBackupDirectory("backup");

        assertTrue(settings.saveSettingsDataToFile(TEST_SETTINGS_FILE));

        // Reset and re-read
        resetSingleton();
        ApplicationSettings reloaded = ApplicationSettings.getInstance();
        reloaded.readSettingsFromFile(TEST_SETTINGS_FILE);

        assertEquals("testuser", reloaded.getOekbUserName());
        assertEquals("testpass", reloaded.getOekbPasswort());
        assertTrue(reloaded.isUseProdServer());
        assertEquals("proxy.example.com", reloaded.getConnectionProxyHost());
        assertEquals(8080, reloaded.getConnectionProxyPort());
        assertEquals("FUM", reloaded.getDataSupplierList());
        assertEquals("backup", reloaded.getBackupDirectory());
    }

    @Test
    @DisplayName("Settings file should not contain XXE-exploitable DOCTYPE")
    void settingsFile_noDoctype() throws Exception {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.setOekbUserName("user");
        settings.setOekbPasswort("pass");
        settings.saveSettingsDataToFile(TEST_SETTINGS_FILE);

        String content = Files.readString(new File(TEST_SETTINGS_FILE).toPath(), StandardCharsets.UTF_8);

        assertFalse(content.contains("<!DOCTYPE"), "Settings file should not contain DOCTYPE declaration");
        assertFalse(content.contains("<!ENTITY"), "Settings file should not contain ENTITY declaration");
    }

    @Test
    @DisplayName("readSettingsFromFile should reject XXE in settings.xml")
    void readSettings_rejectsXxe() throws Exception {
        // Write a malicious settings.xml with XXE
        String xxeSettings = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE settings [ <!ENTITY xxe SYSTEM \"file:///etc/passwd\"> ]>" + "<settings>"
                + "  <OeKBUserName>&xxe;</OeKBUserName>" + "  <OeKBUserPassword>test</OeKBUserPassword>"
                + "  <useProdServer>false</useProdServer>" + "</settings>";
        Files.writeString(new File(TEST_SETTINGS_FILE).toPath(), xxeSettings, StandardCharsets.UTF_8);

        ApplicationSettings settings = ApplicationSettings.getInstance();
        // Should not crash, should not load /etc/passwd content
        settings.readSettingsFromFile(TEST_SETTINGS_FILE);

        // Username should NOT contain contents of /etc/passwd
        String username = settings.getOekbUserName();
        assertTrue(username == null || username.isEmpty() || !username.contains("root"),
                "XXE should not have been resolved — username should not contain file content");
    }
}
