package model

class ApplicationSettingsTest extends GroovyTestCase {
    void testReadSettingsFromFile() {
        def settings = ApplicationSettings.getInstance()
        settings.readSettingsFromFile()
        def userAuth = settings.authCredentialsBasic
        assertToString(userAuth, "c3lzdGVtdXNlcjYyNTcwMDI6SGs2ISNHcE45eEUh")
    }

}
