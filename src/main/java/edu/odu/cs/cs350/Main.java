package edu.odu.cs.cs350;

import edu.odu.cs.cs350.report.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;




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
