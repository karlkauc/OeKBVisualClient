package common

import groovy.util.logging.Log4j2

@Log4j2
class XMLHelper {

    enum FileTypes {
        OFI,
        ACCESS_RIGHTS,
        FUND_DATA
    }

    static FileTypes getFileType(String fileData) {
        def xmlData = new XmlSlurper().parseText(fileData)

        if (xmlData.Funds.Fund.CountrySpecificData.AT.OeNB.Meldungstyp == "OFI") {
            log.debug "Ofi Fonds gefunden "
            return FileTypes.OFI
        }

    }

    static boolean isOfiFile(String fileData) {
        def xmlData = new XmlSlurper().parseText(fileData)

        if (xmlData.Funds.Fund.CountrySpecificData.AT.OeNB.Meldungstyp == "OFI") {
            log.debug "Ofi Fonds gefunden "
            return true
        }
        else {
            log.debug "KEIN Ofi Fonds gefunden "
            return false
        }
    }

    static boolean isOfiResponseOk(String fileData) {
        return true
    }
}
