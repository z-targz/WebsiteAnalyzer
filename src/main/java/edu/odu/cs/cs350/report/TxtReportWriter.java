package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.data.FileBase;
import edu.odu.cs.cs350.data.Image;
import edu.odu.cs.cs350.data.Page;
import edu.odu.cs.cs350.data.Website;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TxtReportWriter {

    public static Map<String, Long> determinePageSizes(Map<String, Page> pageRegistry, Map<String, Image> imageRegistry) {
        Map<String, Long> pageSizeRegistry = new HashMap<>();
        pageRegistry.keySet().forEach(x->{
            Page p = pageRegistry.get(x);
            long sizeBytes =
                p.getInternalImages()
                    .stream()
                    .map(uri->imageRegistry.get(uri).getSizeBytes())
                    .mapToLong(Long::longValue)
                    .sum();
            pageSizeRegistry.put(x, sizeBytes);
        });
        return pageSizeRegistry;
    }

    public static String getTxtReport(Map<String, Page> pageRegistry, Map<String, Image> imageRegistry, String rootURI) {
        Map<String, Long> pageSizeRegistry = determinePageSizes(pageRegistry, imageRegistry);
        StringBuilder buffer = new StringBuilder(1000);
        pageSizeRegistry.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\t%s\n", FileBase.printFileSize(pageSizeRegistry.get(x)), Website.trimRootURI(x, rootURI)));
        });
        return buffer.toString();
    }

    public static void writeTxtReport(String fileName, Website website) throws IOException {
        File report_txt = new File(fileName);
        BufferedWriter writer_txt = new BufferedWriter(new FileWriter(report_txt));

        writer_txt.write(TxtReportWriter.getTxtReport(website.getPageRegistry(), website.getImageRegistry(), website.getRootURI()));

        writer_txt.close();
    }
}
