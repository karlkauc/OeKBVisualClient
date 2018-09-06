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
package dao

import groovy.util.logging.Log4j2
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import model.AccessRule
import model.ApplicationSettings
import model.RuleRow

@Log4j2
class AccesRights {

    def applicationSettings = ApplicationSettings.getInstance()

    List<AccessRule> getAccesRightsRecievedFromOEKB() {
        def outputString

        if (applicationSettings.fileSystem) {
            // Daten sollen aus Filesystem gelesen werden
            def potentialFiles = new File(applicationSettings.backupDirectory).listFiles().findAll {
                it.name.toString().contains("DOWNLOAD_ACCESS_RULE.xml")
            }
            log.debug potentialFiles
            def dasIstEs = potentialFiles.sort().max()
            log.debug "Das ist es: " + dasIstEs
            outputString = dasIstEs.text
        } else {
            log.debug "lese von OeKB Server"
            outputString = OeKBHTTP.downloadAccessRules()
        }

        def xml = new XmlSlurper().parseText(outputString)
        def accessRules = []

        xml.AccessRule.each { rule ->
            def LEI = []
            def OENB_ID = []
            def ISIN_SEGMENT = []
            def ISIN_SHARECLASS = []

            rule.AccessObjects.AccessObject.each { AO ->
                if (AO.Fund.LEI.text()) {
                    LEI.add(AO.Fund.LEI.text())
                }
                if (AO.Fund.OeNB_Identnr.text()) {
                    OENB_ID.add(AO.Fund.OeNB_Identnr.text())
                }
                if (AO.ShareClass.ISIN.text()) {
                    ISIN_SHARECLASS.add(AO.ShareClass.ISIN.text())
                }
                if (AO.Segment.ISIN.text()) {
                    ISIN_SEGMENT.add(AO.Segment.ISIN.text())
                }
            }

            accessRules.add(
                    new AccessRule(id: rule.@id.text(),
                            contentType: rule.ContentType.text(),
                            profile: rule.Profiles.Profile.text(),
                            dataSupplierCreatorShort: rule.DataSupplier_Creator.Short.text(),
                            dataSupplierCreatorName: rule.DataSupplier_Creator.Name.text(),
                            creationTime: rule.CreationTime.text(),
                            accessDelayInDays: rule.Schedule.AccessDelayInDays.text(),
                            dateFrom: rule.Schedule.DataAccessRange.DateFrom.text(),
                            dateTo: rule.Schedule.DataAccessRange.DateTo.text(),
                            frequency: rule.Schedule.DataAccessRange.Frequency.text(),
                            costsByDataSupplier: rule.CostsByDataSupplier.text().toBoolean(),
                            LEI: LEI,
                            OENB_ID: OENB_ID,
                            ISIN_SEGMENT: ISIN_SEGMENT,
                            ISIN_SHARECLASS: ISIN_SHARECLASS
                    )
            )
        }

        // DatatypeConverter.printBase64Binary(accessRules) // Java7
        // String base64str = Base64.getEncoder().encodeToString(data) // Java8
        return accessRules

    }

    List<AccessRule> getAccessRightsGivenFromOEKB() {
        def outputString

        if (applicationSettings.fileSystem) {
            // Daten sollen aus Filesystem gelesen werden
            def potentialFiles = new File(applicationSettings.backupDirectory).listFiles().findAll {
                it.name.toString().contains("DOWNLOAD_AR_ASSIGNED.xml")
            }
            log.debug potentialFiles
            def dasIstEs = potentialFiles.sort().max()
            log.debug "Das ist es: " + dasIstEs
            outputString = dasIstEs.text
        } else {
            outputString = OeKBHTTP.downloadGivenAccessRules()
        }

        def xml = new XmlSlurper().parseText(outputString)
        def accessRules = []

        xml.AccessRule.each { rule ->
            def LEI = []
            def OENB_ID = []
            def ISIN_SEGMENT = []
            def ISIN_SHARECLASS = []

            rule.AccessObjects.AccessObject.each { AO ->
                if (AO.Fund.LEI.text()) {
                    LEI.add(AO.Fund.LEI.text())
                }
                if (AO.Fund.OeNB_Identnr.text()) {
                    OENB_ID.add(AO.Fund.OeNB_Identnr.text())
                }
                if (AO.ShareClass.ISIN.text()) {
                    ISIN_SHARECLASS.add(AO.ShareClass.ISIN.text())
                }
                if (AO.Segment.ISIN.text()) {
                    ISIN_SEGMENT.add(AO.Segment.ISIN.text())
                }
            }

            def kagShort = []
            rule.DataSuppliers.DataSupplier.each { ds ->
                kagShort.add(ds.Short.text())
            }

            accessRules.add(
                    new AccessRule(id: rule.@id.text(),
                            contentType: rule.ContentType.text(),
                            profile: rule.Profiles.Profile.text(),
                            dataSupplierCreatorShort: rule.DataSupplier_Creator.Short.text(),
                            dataSupplierCreatorName: rule.DataSupplier_Creator.Name.text(),
                            dataSuppliersGivenShort: kagShort,
                            creationTime: rule.CreationTime.text(),
                            accessDelayInDays: rule.Schedule.AccessDelayInDays.text(),
                            dateFrom: rule.Schedule.DataAccessRange.DateFrom.text(),
                            dateTo: rule.Schedule.DataAccessRange.DateTo.text(),
                            frequency: rule.Schedule.DataAccessRange.Frequency.text(),
                            costsByDataSupplier: rule.CostsByDataSupplier.text().toBoolean(),
                            LEI: LEI,
                            OENB_ID: OENB_ID,
                            ISIN_SHARECLASS: ISIN_SHARECLASS,
                            ISIN_SEGMENT: ISIN_SEGMENT
                    )
            )
        }
        return accessRules
    }


    static String deleteRule(AccessRule rule) {
        log.debug "Delte Rule: " + rule.id.toString()

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.FundsXML_AccessRules(["xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "https://fdp-service.oekb.at/FundsXML_AccessRules_2.3.0.xsd"]) {
            Task('delete')
            DataSupplier {
                Short(rule.dataSupplierCreatorShort)
                Name(rule.dataSupplierCreatorName)
                Contact {
                    Name('Karl Kauc')
                    Phone('050100 19850')
                    Email('karl.kauc@sparinvest.com')
                }
            }
            AccessRule(id: rule.id) {
                ContentType(rule.contentType)
                DataSuppliers {
                    DataSupplier {
                        rule.dataSuppliersGivenShort.each { dds ->
                            Short(dds)
                        }
                    }
                }
                Profiles {
                    Profile(rule.profile)
                }
                AccessObjects {

                    rule.LEI.each { oneLei ->
                        AccessObject {
                            Fund {
                                LEI(oneLei.toString())
                            }
                        }
                    }
                    rule.OENB_ID.each { oneOenbId ->
                        AccessObject {
                            Fund {
                                OeNB_Identnr(oneOenbId.toString())
                            }
                        }
                    }
                    rule.ISIN_SEGMENT.each { ISIN_SEGMENT ->
                        AccessObject {
                            Segment {
                                ISIN(ISIN_SEGMENT)
                            }
                        }
                    }
                    rule.ISIN_SHARECLASS.each { ISIN_SHARECLASS ->
                        AccessObject {
                            ShareClass {
                                ISIN(ISIN_SHARECLASS)
                            }
                        }
                    }
                }
                Schedule {
                    AccessDelayInDays(rule.accessDelayInDays)
                    DataAccessRange {
                        DateFrom(rule.dateFrom)
                        DateTo(rule.dateTo)
                        Frequency(rule.frequency)
                    }
                }
                CostsByDataSupplier(rule.costsByDataSupplier)
            }
        }

        log.debug XmlUtil.serialize(writer.toString())
        return writer.toString()

    }

    def deleteFundFromRule(RuleRow rule) {
        log.debug "im in deleteFundFromFule"
        log.debug "lösche Fund aus Rule: " + rule.id + ": " + rule.LEI + "/" + rule.OENB_ID + "/" + rule.SHARECLASS_ISIN + "/" + rule.SEGMENT_ISIN

        def given = accesRightsRecievedFromOEKB
        def idToDelete = given.find { it.id == rule.id }

        log.debug"LEI vorher: " + idToDelete.LEI
        idToDelete.LEI.remove(rule.LEI)
        log.debug "LEI nachher: " + idToDelete.LEI

        def newRule1 = deleteRule(idToDelete)
        newRule(newRule1)
    }

    def newRule(String newRule) {
        def xml = new XmlSlurper().parseText(newRule)
        xml.Task = "import"

        log.debug XmlUtil.serialize(xml)
    }

}
