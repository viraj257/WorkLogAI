package com.viraj.aiexcellogger.service;

import com.viraj.aiexcellogger.model.WorkLog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ExcelService {

    private static final String FILE_PATH = "worklog.xlsx";

    public void writeToExcel(WorkLog log) throws Exception {

        Workbook workbook;
        Sheet sheet;

        File file = new File(FILE_PATH);

        if (file.exists()) {
            // ✅ Fix: use try-with-resources to close FileInputStream
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
            sheet = workbook.getSheetAt(0);
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Logs");

            // ✅ Row 0: Title row matching Citadel sheet
            Row titleRow = sheet.createRow(0);
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 13);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Citadel  TimeSheet - Internal");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // ✅ Row 1: Column headers matching Citadel sheet
            Row header = sheet.createRow(1);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);

            String[] columns = {
                    "Date", "Project Name", "Task/Item/Activity Summary",
                    "Description of Work", "Total Hours Spent",
                    "Next Action Summary", "Task Status",
                    "Due Date", "Reviewed By", "Remarks"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
        }

        // ✅ Data rows start from row 2 onward
        int lastRow = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRow);

        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

        row.createCell(0).setCellValue(log.getDate());
        row.createCell(1).setCellValue(log.getProjectName());

        Cell taskCell = row.createCell(2);
        taskCell.setCellValue(log.getTaskSummary());
        taskCell.setCellStyle(wrapStyle);

        Cell descCell = row.createCell(3);
        descCell.setCellValue(log.getDescription());
        descCell.setCellStyle(wrapStyle);

        row.createCell(4).setCellValue(log.getHours());

        Cell nextCell = row.createCell(5);
        nextCell.setCellValue(log.getNextAction());
        nextCell.setCellStyle(wrapStyle);

        // ✅ "Task Status" column — In Progress / Completed
        row.createCell(6).setCellValue(log.isInProgress() ? "In Progress" : "Completed");
        row.createCell(7).setCellValue(log.getDueDate() != null ? log.getDueDate() : "");
        row.createCell(8).setCellValue(log.getReviewedBy() != null ? log.getReviewedBy() : "");
        row.createCell(9).setCellValue(log.getRemark() != null ? log.getRemark() : "");

        for (int i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }

        // ✅ Fix: try-with-resources for output stream
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public void exportToPDF(WorkLog log) throws Exception {
        com.itextpdf.kernel.pdf.PdfWriter writer =
                new com.itextpdf.kernel.pdf.PdfWriter("worklog.pdf");
        com.itextpdf.kernel.pdf.PdfDocument pdf =
                new com.itextpdf.kernel.pdf.PdfDocument(writer);
        com.itextpdf.layout.Document document =
                new com.itextpdf.layout.Document(pdf);

        document.add(new com.itextpdf.layout.element.Paragraph("Citadel TimeSheet - Internal")
                .setBold().setFontSize(14));
        document.add(new com.itextpdf.layout.element.Paragraph("Date: " + log.getDate()));
        document.add(new com.itextpdf.layout.element.Paragraph("Project: " + log.getProjectName()));
        document.add(new com.itextpdf.layout.element.Paragraph("Task Summary:\n" + log.getTaskSummary()));
        document.add(new com.itextpdf.layout.element.Paragraph("Description:\n" + log.getDescription()));
        document.add(new com.itextpdf.layout.element.Paragraph("Hours: " + log.getHours()));
        document.add(new com.itextpdf.layout.element.Paragraph("Next Action: " + log.getNextAction()));
        document.add(new com.itextpdf.layout.element.Paragraph(
                "Status: " + (log.isInProgress() ? "In Progress" : "Completed")));

        document.close();
    }
}