package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.Website;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelReportWriter extends ReportWriter {
    public ExcelReportWriter(Website website) {
        super(website);
    }
    public HSSFWorkbook getExcelReport() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();

        HSSFRow firstRow = sheet.createRow(0);
        HSSFCell column0 = firstRow.createCell(0);
        column0.setCellType(CellType.STRING);
        column0.setCellValue("Page Name");

        HSSFCell column1 = firstRow.createCell(1);
        column1.setCellType(CellType.STRING);
        column1.setCellValue("# of Images");

        HSSFCell column2 = firstRow.createCell(2);
        column2.setCellType(CellType.STRING);
        column2.setCellValue("# of Stylesheets");

        HSSFCell column3 = firstRow.createCell(3);
        column3.setCellType(CellType.STRING);
        column3.setCellValue("# of Scripts");

        HSSFCell column4 = firstRow.createCell(4);
        column4.setCellType(CellType.STRING);
        column4.setCellValue("# of Intra-Page Links");

        HSSFCell column5 = firstRow.createCell(5);
        column5.setCellType(CellType.STRING);
        column5.setCellValue("# of Internal Links");

        HSSFCell column6 = firstRow.createCell(6);
        column6.setCellType(CellType.STRING);
        column6.setCellValue("# of External Links");

        AtomicInteger i = new AtomicInteger();
        i.set(1);
        website.getDocumentRegistry().keySet().stream().sorted().forEach(x->{
            ExcelEntry entry = website.getDocumentRegistry().get(x).getExcelEntry();
            HSSFRow row = sheet.createRow(i.get());


            HSSFCell cell0 = row.createCell(0);
            cell0.setCellType(CellType.NUMERIC);
            cell0.setCellValue(entry.getPageName());

            HSSFCell cell1 = row.createCell(1);
            cell1.setCellType(CellType.NUMERIC);
            cell1.setCellValue(entry.getNumImages());

            HSSFCell cell2 = row.createCell(2);
            cell2.setCellType(CellType.NUMERIC);
            cell2.setCellValue(entry.getNumStylesheets());

            HSSFCell cell3 = row.createCell(3);
            cell3.setCellType(CellType.NUMERIC);
            cell3.setCellValue(entry.getNumScripts());

            HSSFCell cell4 = row.createCell(4);
            cell4.setCellType(CellType.NUMERIC);
            cell4.setCellValue(entry.getNumLinksIntraPage());

            HSSFCell cell5 = row.createCell(5);
            cell5.setCellType(CellType.NUMERIC);
            cell5.setCellValue(entry.getNumLinksInternal());

            HSSFCell cell6 = row.createCell(6);
            cell6.setCellType(CellType.NUMERIC);
            cell6.setCellValue(entry.getNumLinksExternal());

            i.incrementAndGet();
        });
        for(int j=0;j<7;++j) {
            sheet.autoSizeColumn(j);
        }

        return workbook;
    }

    @Override
    public void write(String fileName) throws IOException {
        FileOutputStream excel = new FileOutputStream(fileName);
        getExcelReport().write(excel);
        excel.close();
    }
}
