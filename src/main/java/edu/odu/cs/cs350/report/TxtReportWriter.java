package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.Util;
import edu.odu.cs.cs350.data.Resource;
import edu.odu.cs.cs350.data.HTMLDocument;
import edu.odu.cs.cs350.data.Website;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TxtReportWriter extends ReportWriter {
    public TxtReportWriter(Website website) {
        super(website);
    }

    public Map<String, Long> determinePageSizes() {
        Map<String, Long> pageSizeRegistry = new HashMap<>();
        website.getDocumentRegistry().keySet().forEach(x->{
            HTMLDocument p = website.getDocumentRegistry().get(x);
            long sizeBytes =
                p.getInternalImages()
                    .stream()
                    .map(uri->website.getImageRegistry().get(uri).getSizeBytes())
                    .mapToLong(Long::longValue)
                    .sum();
            pageSizeRegistry.put(x, sizeBytes);
        });
        return pageSizeRegistry;
    }

    public String getTxtReport() {
        Map<String, Long> pageSizeRegistry = determinePageSizes();
        StringBuilder buffer = new StringBuilder(1000);
        pageSizeRegistry.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\t%s\n", Resource.printFileSize(pageSizeRegistry.get(x)), Util.trimRootURI(x, website.getRootURI())));
        });
        return buffer.toString();
    }

    @Override
    public void write(String fileName) throws IOException {
        File report_txt = new File(fileName);
        BufferedWriter writer_txt = new BufferedWriter(new FileWriter(report_txt));

        writer_txt.write(getTxtReport());

        writer_txt.close();
    }
}
