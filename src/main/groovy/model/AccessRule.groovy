package model

import dao.FundEnhancer
import groovy.transform.Sortable
import groovy.transform.ToString


@ToString(includeNames = true)
class AccessRule {
    def id
    def contentType
    def profile

    def dataSupplierCreatorShort
    def dataSupplierCreatorName

    List<String> dataSuppliersGivenShort

    def creationTime
    def accessDelayInDays
    def dateFrom
    def dateTo
    def frequency
    def costsByDataSupplier

    List<String> LEI
    List<String> OENB_ID
    List<String> ISIN_SEGMENT
    List<String> ISIN_SHARECLASS
}

@Sortable
@ToString(includeFields = true)
final class RuleRow {
    String id
    String origId
    String contentType
    String profile

    String dataSupplierCreatorShort
    String dataSupplierCreatorName

    String dataSuppliersGivenShort

    String creationTime
    String accessDelayInDays
    String dateFrom
    String dateTo
    String frequency
    Boolean costsByDataSupplier

    String LEI
    String OENB_ID
    String SHARECLASS_ISIN
    String SEGMENT_ISIN

    Boolean deleteRule
    Boolean rootRow

    // String fundName

    String getFundName() {
        def myId = null

        if (LEI != null) {
            myId = LEI
        } else {
            if (OENB_ID != null) {
                myId = OENB_ID
            } else {
                if (SHARECLASS_ISIN != null) {
                    myId = SHARECLASS_ISIN
                } else {
                    if (SEGMENT_ISIN != null) {
                        myId = SEGMENT_ISIN
                    }
                }
            }
        }

        if (myId != null) {
            return FundEnhancer.getInstance().getFundNameByID(myId)
        }
        return null
    }
}

