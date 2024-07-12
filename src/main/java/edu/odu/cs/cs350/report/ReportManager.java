package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.data.Website;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {
    private Website site;
    private String baseFileName;
    public ReportManager(Website site) {
        this.site = site;
        determineBaseFilename();
    }

    public void setSourceData(Website sourceData) {
        this.site = sourceData;
    }
    public void determineBaseFilename() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        baseFileName = String.format("%s-summary", dateFormat.format(date));
    }

    public void writeReportNames(BufferedWriter nameWriter) throws IOException {
        nameWriter.write(String.format("%s.txt", this.baseFileName));
        nameWriter.write(String.format("%s-debug.txt", this.baseFileName));
        nameWriter.write(String.format("%s.xlsx", this.baseFileName));
    }
    public void writeAll() throws IOException {
        ReportWriter writer = null;
        writer = new TxtReportWriter(site);
        writer.write(String.format("%s.txt", this.baseFileName));

        writer = new JSONReportWriter(site);
        writer.write(String.format("%s-debug.txt", this.baseFileName));

        writer = new ExcelReportWriter(site);
        writer.write(String.format("%s.xlsx", this.baseFileName));
    }
}
