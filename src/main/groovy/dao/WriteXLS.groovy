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
package dao

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import model.AccessRule
import model.ApplicationSettings
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Log4j2
class WriteXLS {

    def static writeAccessRights(String filename, List<AccessRule> accessRules) {
        log.debug "writing " + accessRules.size() + " access rules to excel file"
        def fe = FundEnhancer.getInstance()
        fe.readData()

        def builder = new WorkbookBuilder()
        builder.workbook {
            sheet("Access Rights") {
                row(["Rule ID", "Profile", "Content Type", "Creator From", "Creator To", "LEI", "OENB_ID", "ISIN",
                     "Fund Name", "Date from", "Date to", "frequency", "Costs by data supplier"])

                accessRules.each { rule ->
                    rule.LEI.each { lei ->
                        row([rule.id, rule.profile, rule.contentType, rule.dataSupplierCreatorShort,
                             rule.dataSuppliersGivenShort?.join(';'), lei, null, null, fe.getFundNameByID(lei.toString()),
                             rule.dateFrom, rule.dateTo, rule.frequency, rule.costsByDataSupplier])
                    }
                    rule.OENB_ID.each { oenb_id ->
                        row([rule.id, rule.profile, rule.contentType, rule.dataSupplierCreatorShort,
                             rule.dataSuppliersGivenShort?.join(';'), null, oenb_id, null, fe.getFundNameByID(oenb_id.toString()),
                             rule.dateFrom, rule.dateTo, rule.frequency, rule.costsByDataSupplier])
                    }
                    rule.ISIN_SHARECLASS.each { isin ->
                        row([rule.id, rule.profile, rule.contentType, rule.dataSupplierCreatorShort,
                             rule.dataSuppliersGivenShort?.join(';'), null, null, isin, fe.getFundNameByID(isin.toString()),
                             rule.dateFrom, rule.dateTo, rule.frequency, rule.costsByDataSupplier])
                    }
                    rule.ISIN_SEGMENT.each { isin ->
                        row([rule.id, rule.profile, rule.contentType, rule.dataSupplierCreatorShort,
                             rule.dataSuppliersGivenShort?.join(';'), null, null, isin, fe.getFundNameByID(isin.toString()),
                             rule.dateFrom, rule.dateTo, rule.frequency, rule.costsByDataSupplier])
                    }
                }
            }
            saveFile {
                fileName = filename
            }
        }
        log.debug "schreiben fertig"
    }
}

@CompileStatic
@Log4j2
class WorkbookBuilder {
    XSSFWorkbook workbook
    Sheet sheet
    int rows
    int col
    Row currentRow

    String filename

    Workbook workbook(@DelegatesTo(WorkbookBuilder.class) Closure closure) {
        log.debug "erstelle workbook"
        workbook = new XSSFWorkbook()

        closure.delegate = this
        closure.call()
        return workbook
    }

    void sheet(String name, Closure closure) {
        log.debug "erstelle sheet: " + name

        sheet = workbook.createSheet(name)
        rows = 0
        closure.delegate = this
        closure.call()
    }

    /**
     * "leere" Row mit Cell closure
     * @param closure
     */
    void row(Closure closure) {
        log.debug "row closure"

        currentRow = sheet.createRow(rows)
        col = 0
        closure.delegate = this
        closure.call()
        rows++
    }

    /**
     * Cell closure
     * @param value
     */
    void cell(value) {
        Cell cell = currentRow.createCell(col)
        col++
        switch (value) {
            case Date: cell.setCellValue((Date) value); break
            case Double: cell.setCellValue((Double) value); break
            case BigDecimal: cell.setCellValue(((BigDecimal) value).doubleValue()); break
            case null: cell.setCellValue(""); break
            default: cell.setCellValue(new XSSFRichTextString("" + value.toString())); break
        }

    }

    /**
     * row mit Liste von Daten (ohne cell)
     */
    void row(List values) {
        log.debug "erstelle row mit List: " + values.toString()

        Row row = sheet.createRow(rows++ as int)
        values.eachWithIndex { value, col ->
            Cell cell = row.createCell(col as int)
            switch (value) {
                case Date:
                    cell.setCellValue(value as Date)
                    break
                case Double:
                    cell.setCellValue(value as Double)
                    break
                case BigDecimal:
                    cell.setCellValue((value as BigDecimal).doubleValue())
                    break
                case null:
                    cell.setCellValue("")
                    break
                default:
                    cell.setCellValue(new XSSFRichTextString("" + value as String))
                    break
            }
        }
    }

    void setFileName(final String filename) {
        this.filename = filename
    }

    void saveFile(Closure closure) {
        closure.delegate = this
        closure.call()

        if (filename.size() == 0) {
            log.warn "kein filename gesetzt!"
            filename = ApplicationSettings.getInstance().backupDirectory + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "__" + "export" + ".xslx"
        }

        new File(filename).delete()
        FileOutputStream out = new FileOutputStream(new File(filename))
        workbook.write(out)
        out.close()
    }
}

