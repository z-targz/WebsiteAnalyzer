package edu.odu.cs.cs350;

import edu.odu.cs.cs350.data.Website;
import edu.odu.cs.cs350.report.ExcelReportWriter;
import edu.odu.cs.cs350.report.JSONReportWriter;
import edu.odu.cs.cs350.report.TxtReportWriter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Main {
    public static void main(String[] args) {
        try {
            File root = new File(args[0]);
            Website website = new Website(root.toPath());
            //System.out.println(website);

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();

            JSONReportWriter.writeJSONReport(String.format("%s-summary-debug.txt", dateFormat.format(date)), website);
            TxtReportWriter.writeTxtReport(String.format("%s-summary.txt", dateFormat.format(date)), website);
            ExcelReportWriter.writeExcelReport(String.format("%s-summary.xlsx", dateFormat.format(date)), website);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
