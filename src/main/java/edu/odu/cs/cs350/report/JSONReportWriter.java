package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.data.Website;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONReportWriter {

    public static void writeJSONReport(String fileName, Website website) throws IOException {
        File report = new File(fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(report));

        writer.write(website.getPageEntries());
        writer.write(website.getResourceEntries());

        writer.close();
    }
}
