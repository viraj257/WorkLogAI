package com.viraj.aiexcellogger.service;

import com.viraj.aiexcellogger.model.WorkLog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
            workbook = new XSSFWorkbook(new FileInputStream(file));
            sheet = workbook.getSheetAt(0);
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Logs");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Date");
            header.createCell(1).setCellValue("Project Name");
            header.createCell(2).setCellValue("Task/Item/Activity Summary");
            header.createCell(3).setCellValue("Description of Work");
            header.createCell(4).setCellValue("Total Hours Spent");
            header.createCell(5).setCellValue("Next Action Summary");
            header.createCell(6).setCellValue("In Progress");
            header.createCell(7).setCellValue("Due Date");
            header.createCell(8).setCellValue("Reviewed By");
            header.createCell(9).setCellValue("Remark");
        }

        // 🔥 Fix newline formatting before writing
        log.setDescription(log.getDescription().replace("\\n", "\n"));
        log.setTaskSummary(log.getTaskSummary().replace("\\n", "\n"));

        int lastRow = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRow);

        // 🔥 Create wrap style
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        // Fill data
        row.createCell(0).setCellValue(log.getDate());
        row.createCell(1).setCellValue(log.getProjectName());

        // Task Summary
        Cell taskCell = row.createCell(2);
        taskCell.setCellValue(log.getTaskSummary());
        taskCell.setCellStyle(style);

        // Description
        Cell descCell = row.createCell(3);
        descCell.setCellValue(log.getDescription());
        descCell.setCellStyle(style);

        row.createCell(4).setCellValue(log.getHours());
        row.createCell(5).setCellValue(log.getNextAction());
        row.createCell(6).setCellValue(log.getInProgress());
        row.createCell(7).setCellValue(log.getDueDate());
        row.createCell(8).setCellValue(log.getReviewedBy());
        row.createCell(9).setCellValue(log.getRemark());

        //  Auto-size columns (optional but nice)
        for (int i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }
}