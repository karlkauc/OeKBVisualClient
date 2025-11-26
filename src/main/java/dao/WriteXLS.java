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
package dao;

import model.AccessRule;
import model.ApplicationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class WriteXLS {
    private static final Logger log = LogManager.getLogger(WriteXLS.class);

    public static void writeAccessRights(String filename, List<AccessRule> accessRules) {
        log.debug("writing " + accessRules.size() + " access rules to excel file");
        FundEnhancer fe = FundEnhancer.getInstance();
        fe.readData();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Access Rights");
            int rowNum = 0;

            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Rule ID", "Profile", "Content Type", "Creator From", "Creator To", "LEI", "OENB_ID", "ISIN",
                    "Fund Name", "Date from", "Date to", "frequency", "Costs by data supplier"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(new XSSFRichTextString(headers[i]));
            }

            // Data rows
            for (AccessRule rule : accessRules) {
                if (rule.getLEI() != null) {
                    for (String lei : rule.getLEI()) {
                        Row row = sheet.createRow(rowNum++);
                        createAccessRightRow(row, rule, lei, null, null, FundEnhancer.getFundNameByID(lei));
                    }
                }
                if (rule.getOENB_ID() != null) {
                    for (String oenbId : rule.getOENB_ID()) {
                        Row row = sheet.createRow(rowNum++);
                        createAccessRightRow(row, rule, null, oenbId, null, FundEnhancer.getFundNameByID(oenbId));
                    }
                }
                if (rule.getISIN_SHARECLASS() != null) {
                    for (String isin : rule.getISIN_SHARECLASS()) {
                        Row row = sheet.createRow(rowNum++);
                        createAccessRightRow(row, rule, null, null, isin, FundEnhancer.getFundNameByID(isin));
                    }
                }
                if (rule.getISIN_SEGMENT() != null) {
                    for (String isin : rule.getISIN_SEGMENT()) {
                        Row row = sheet.createRow(rowNum++);
                        createAccessRightRow(row, rule, null, null, isin, FundEnhancer.getFundNameByID(isin));
                    }
                }
            }

            // Save file
            if (filename == null || filename.isEmpty()) {
                log.warn("kein filename gesetzt!");
                filename = ApplicationSettings.getInstance().getBackupDirectory() + File.separator +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "__export.xlsx";
            }

            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

            log.debug("schreiben fertig");
        } catch (IOException e) {
            log.error("Error writing Excel file", e);
        }
    }

    private static void createAccessRightRow(Row row, AccessRule rule, String lei, String oenbId, String isin, String fundName) {
        int col = 0;
        setCellValue(row.createCell(col++), rule.getId());
        setCellValue(row.createCell(col++), rule.getProfile());
        setCellValue(row.createCell(col++), rule.getContentType());
        setCellValue(row.createCell(col++), rule.getDataSupplierCreatorShort());
        setCellValue(row.createCell(col++), rule.getDataSuppliersGivenShort() != null ?
                String.join(";", rule.getDataSuppliersGivenShort()) : null);
        setCellValue(row.createCell(col++), lei);
        setCellValue(row.createCell(col++), oenbId);
        setCellValue(row.createCell(col++), isin);
        setCellValue(row.createCell(col++), fundName);
        setCellValue(row.createCell(col++), rule.getDateFrom());
        setCellValue(row.createCell(col++), rule.getDateTo());
        setCellValue(row.createCell(col++), rule.getFrequency());
        setCellValue(row.createCell(col++), rule.getCostsByDataSupplier());
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else {
            cell.setCellValue(new XSSFRichTextString(value.toString()));
        }
    }

    public static void writeJournal(String filename, List<model.JournalEntry> journalEntries) {
        log.debug("writing " + journalEntries.size() + " journal entries to excel file");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Journal");
            int rowNum = 0;

            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Timestamp", "Action", "Type", "Username", "Data Supplier", "Unique ID", "Details", "Is Empty"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(new XSSFRichTextString(headers[i]));
            }

            // Data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (model.JournalEntry entry : journalEntries) {
                Row row = sheet.createRow(rowNum++);
                int col = 0;
                setCellValue(row.createCell(col++), entry.getTimestamp() != null ? entry.getTimestamp().format(formatter) : null);
                setCellValue(row.createCell(col++), entry.getAction() != null ? entry.getAction().getDescription() : null);
                setCellValue(row.createCell(col++), entry.getType() != null ? entry.getType().getDescription() : null);
                setCellValue(row.createCell(col++), entry.getUserName());
                setCellValue(row.createCell(col++), entry.getDataSupplier());
                setCellValue(row.createCell(col++), entry.getUniqueId());
                setCellValue(row.createCell(col++), entry.getDetails());
                setCellValue(row.createCell(col++), entry.isEmpty());
            }

            // Save file
            String finalFilename = filename;
            if (finalFilename == null || finalFilename.isEmpty()) {
                log.warn("kein filename gesetzt!");
                finalFilename = ApplicationSettings.getInstance().getBackupDirectory() + File.separator +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s")) + "_journal_export.xlsx";
            }

            File file = new File(finalFilename);
            if (file.exists()) {
                file.delete();
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

            log.debug("schreiben fertig");
        } catch (IOException e) {
            log.error("Error writing Excel file", e);
        }
    }
}
