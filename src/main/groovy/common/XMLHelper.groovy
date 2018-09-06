/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
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

        return FileTypes.FUND_DATA // TODO: FIXEN

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
