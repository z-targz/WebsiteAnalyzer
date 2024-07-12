package edu.odu.cs.cs350;

import edu.odu.cs.cs350.data.Website;
import edu.odu.cs.cs350.data.WebsiteBuilder;
import edu.odu.cs.cs350.report.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Main {
    public static void main(String[] args) {
        try {
            File root = new File(args[0]);

            Website website = new WebsiteBuilder().withRoot(root.toPath()).build();

            ReportManager manager = new ReportManager(website);
            manager.writeAll();
            manager.writeReportNames(new BufferedWriter(new OutputStreamWriter(System.out)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
