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
@ToString(includeNames = true)
final class RuleRow {
    String id
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

