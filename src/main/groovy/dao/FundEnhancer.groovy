package dao

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.transform.ToString
import groovy.util.logging.Log4j2

@Log4j2
@Singleton
@CompileStatic
class FundEnhancer {
    List<FundEnhancerData> data = new ArrayList<FundEnhancerData>()

    static void main(args) {
        getInstance().readData()

        println "LEI: " + getFundNameByID("529900RNEPM2AX88SJ32")
        println "ISIN: " + getFundNameByID("AT0000A20FX4")
        // println "OENB ID: " + getInstance().getFundNameByID("7517734")
        println getFundNameByID("529900RNEPM2AX88SJ32").collect().first()["Fondsbezeichnung"]
        println getFundNameByID("AT0000A20FX4").collect().first()["Fondsbezeichnung"]
        println getFundNameByID("529900S56OD7UE7H1V04").collect().first()["Fondsbezeichnung"]
    }

    @Memoized
    def readData() {
        File file
        def filePfad = "resources" + File.separator + "isinlei.csv"

        try {
            if (new File(filePfad).exists()) {
                file = new File(filePfad)
                log.debug "ISIN-LEI File [" + file.getAbsolutePath() + "] loaded with " + file.readLines().size() + " records"

                file.eachLine { line ->
                    def t = line.split(';')

                    data.add(new FundEnhancerData(
                            [ISIN            : t[0],
                             Fondsbezeichnung: t[1].trim(),
                             KAG             : t[2].trim(),
                             OENB_ID         : t[3].trim(),
                             LEI             : t[4].trim(),
                             LEIStatus       : t[5].trim(),
                             WRAPPER         : t[6].trim(),
                             ISINStatus      : t[7].trim()
                            ]))
                }
            } else {
                log.info "ISINLEI CSV File not found under 'resources' folder"
            }
        }
        catch (FileNotFoundException ignored) {
            log.info "internal ISIN LEI FILE NOT FOUND"
        }
    }

    def getAllFundDataByID(String id) {
        if (data.size() == 0) {
            readData()
        }

        def leiReturn = data.findAll { row -> row["LEI"] == id }
        def oenbReturn = data.findAll { row -> row["OENB_ID"] == id }
        def isinReturn = data.findAll { row -> row["ISIN"] == id }

        def ignore = ["(A)", "(T)", "(VT)", "(V)", "(VA)", "(R)", "(I)", "(S)", "(VS)", "(RT)", "(VI)", "(IT)"]
        def combine = leiReturn + oenbReturn + isinReturn
        combine.each { fundName ->
            ignore.each { i ->
                if (fundName['Fondsbezeichnung'].toString().endsWith(i)) {
                    // println "remove "+ fundName + ": " + i
                    fundName['Fondsbezeichnung'] = fundName['Fondsbezeichnung'].toString().replace(i, "").trim()
                } else {
                    // println "OK " + fundName + ": " + i
                }
            }
        }

        return combine
    }

    @Memoized
    static
    def getFundNameByID(String id) {
        def temp = getInstance().getAllFundDataByID(id)

        if (temp)
            return temp.collect().first()["Fondsbezeichnung"]
        else
            return "not found"
    }
}

@ToString(includeNames = true)
class FundEnhancerData {
    def ISIN
    def Fondsbezeichnung
    def KAG
    def OENB_ID
    def LEI
    def LEIStatus
    def WRAPPER
    def ISINStatus
}
