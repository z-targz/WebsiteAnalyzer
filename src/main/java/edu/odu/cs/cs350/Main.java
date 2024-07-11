package edu.odu.cs.cs350;

import edu.odu.cs.cs350.data.Website;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Main {
    private static String rootURI;
    public static void main(String[] args) {
        try {
            File root = new File(args[0]);
            rootURI = root.getCanonicalFile().toURI().toString();
            System.out.println(rootURI);
            Website website = new Website(root.toPath());
            System.out.println(website);

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();

            File report = new File(String.format("%s-summary-debug.txt", dateFormat.format(date)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(report));

            writer.write(website.getPageEntries());
            writer.write(website.getResourceEntries());

            writer.close();

            File report_txt = new File(String.format("%s-summary.txt", dateFormat.format(date)));
            BufferedWriter writer_txt = new BufferedWriter(new FileWriter(report_txt));

            writer_txt.write(website.getTxtReport());

            writer_txt.close();

            FileOutputStream excel = new FileOutputStream(String.format("%s-summary.xlsx", dateFormat.format(date)));
            website.getExcelReport().write(excel);
            excel.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRootURI() {
        return rootURI;
    }
}
