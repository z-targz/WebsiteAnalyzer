package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.Util;
import edu.odu.cs.cs350.data.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//TODO: Replace the output from plaintext to JSON format. All necessary data is in the generatePageEntries Function

public class JSONReportWriter extends ReportWriter {

    private Map<String, String> pageEntries;
    private Map<String, String> imageEntries;
    private Map<String, String> externalImageEntries;
    private Map<String, String> archiveEntries;
    private Map<String, String> videoEntries;
    private Map<String, String> audioEntries;
    private Map<String, String> otherEntries;

    public JSONReportWriter(Website website) {
        super(website);
        pageEntries = new HashMap<>();
        imageEntries = new HashMap<>();
        externalImageEntries = new HashMap<>();
        archiveEntries = new HashMap<>();
        videoEntries = new HashMap<>();
        audioEntries = new HashMap<>();
        otherEntries = new HashMap<>();

    }

    public void write(String fileName) throws IOException {
        File report = new File(fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(report));

        writer.write(getPageEntries());
        writer.write(getResourceEntries());

        writer.close();
    }

    public String getPageEntries() {
        generatePageEntries();
        StringBuilder buffer = new StringBuilder(1000);

        pageEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n",x));
            buffer.append(pageEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    //Must execute first pass
    public void generatePageEntries() {
        for (HTMLDocument page : website.getDocumentRegistry().values()) {
            StringBuilder buffer = new StringBuilder(1000);

            buffer.append(String.format("  Stylesheets (%d):\n", page.getStyleSheets().size()));
            page.getStyleSheets().stream().sorted().forEach(s->{
                buffer.append(String.format("\tcss: %s\n", Util.trimRootURI(s, website.getRootURI())));
            });
            buffer.append("\n");

            buffer.append(String.format("  Scripts (%d):\n", page.getScripts().size()));
            page.getScripts().stream().sorted().forEach(s->{
                buffer.append(String.format("\tscript: %s\n", Util.trimRootURI(s, website.getRootURI())));
            });
            buffer.append("\n");

            buffer.append(String.format("  Internal Images (%d):\n", page.getInternalImages().size()));
            page.getInternalImages().stream().sorted().forEach(i->{
                buffer.append(String.format("\timg: %s\n", Util.trimRootURI(i, website.getRootURI())));
            });
            buffer.append("\n");

            buffer.append(String.format("  External Images (%d):\n", page.getExternalImages().size()));
            page.getExternalImages().stream().sorted().forEach(i->{
                buffer.append(String.format("\timg: %s\n", Util.trimRootURI(i, website.getRootURI())));
            });
            buffer.append("\n");

            buffer.append(String.format("  Intra-Page Links (%d):\n", page.getIntraPageLinks().size()));
            page.getIntraPageLinks().stream().sorted().forEach(l->{
                buffer.append(String.format("\tlink: %s\n", Util.trimRootURI(l, website.getRootURI())));
            });
            buffer.append("\n");

            buffer.append(String.format("  Internal Links (%d):\n", page.getInternalLinks().size()));
            page.getInternalLinks().stream().sorted().forEach(l->{
                buffer.append(String.format("\tlink: %s\n", Util.trimRootURI(l, website.getRootURI())));
            });
            buffer.append("\n");

            buffer.append(String.format("  External Links (%d):\n", page.getExternalLinks().size()));
            page.getExternalLinks().stream().sorted().forEach(l->{
                buffer.append(String.format("\tlink: %s\n", Util.trimRootURI(l, website.getRootURI())));
            });
            buffer.append("\n");

            pageEntries.put(Util.trimRootURI(page.getLabel(), website.getRootURI()), buffer.toString());
        }
    }

    public String getResourceEntries() throws IOException {
        generateResourceEntries();
        StringBuilder buffer = new StringBuilder(1000);
        buffer.append(getImageEntries());
        buffer.append(getArchiveEntries());
        buffer.append(getVideoEntries());
        buffer.append(getAudioEntries());
        buffer.append(getOtherEntries());
        return buffer.toString();
    }

    private void generateResourceEntries() throws IOException {
        for(FileEntry child : website.getRootNode()) {
            String mimeType = child.getMimeType();
            if(Website.matchMimeType(mimeType, Website.FileType.IMAGE)) {
                generateImageEntries(child);
            } else if (Website.matchMimeType(mimeType, Website.FileType.ARCHIVE)) {
                generateArchiveEntries(child);
            } else if (Website.matchMimeType(mimeType, Website.FileType.VIDEO)) {
                generateVideoEntries(child);
            } else if (Website.matchMimeType(mimeType, Website.FileType.AUDIO)) {
                generateAudioEntries(child);
            } else if (!Website.matchMimeType(mimeType, Website.FileType.PAGE)) {
                generateOtherEntries(child);
            }
        }
        for(String externalURI : website.getExternalImageRegistry().keySet()) {
            StringBuilder buffer = new StringBuilder(1000);
            buffer.append(String.format("  Embed (%d):\n", website.getExternalImageRegistry().get(externalURI).size()));
            website.getExternalImageRegistry().get(externalURI).forEach(l->{
                buffer.append(String.format("\t%s\n", Util.trimRootURI(l, website.getRootURI())));
            });
            externalImageEntries.put(externalURI, buffer.toString());
        }
    }



    private String getOtherEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        otherEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", Util.trimRootURI(x, website.getRootURI())));
            buffer.append(otherEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    private void generateOtherEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s\n", Resource.printFileSize(new OtherFile(URI).getSizeBytes())));
        otherEntries.put(URI, buffer.toString());
    }

    private String getAudioEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        audioEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", Util.trimRootURI(x, website.getRootURI())));
            buffer.append(audioEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    private void generateAudioEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s", Resource.printFileSize(new Audio(URI).getSizeBytes())));
        audioEntries.put(URI, buffer.toString());
    }

    private String getVideoEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        videoEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", Util.trimRootURI(x, website.getRootURI())));
            buffer.append(videoEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    private void generateVideoEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s", Resource.printFileSize(new Video(URI).getSizeBytes())));
        videoEntries.put(URI, buffer.toString());
    }

    private String getArchiveEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        archiveEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", Util.trimRootURI(x, website.getRootURI())));
            buffer.append(archiveEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    private void generateArchiveEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s", Resource.printFileSize(new Archive(URI).getSizeBytes())));
        archiveEntries.put(URI, buffer.toString());
    }

    private String getImageEntries() {

        StringBuilder buffer = new StringBuilder(1000);

        imageEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", x));
            buffer.append(imageEntries.get(x));
        });

        externalImageEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", x));
            buffer.append(externalImageEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    private void generateImageEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder(1000);
        String URI = child.getURI();
        if(website.getImageRegistry().containsKey(URI)) {
            buffer.append(String.format("  Embed (%d):\n", website.getImageRegistry().get(URI).numPagesWithMe()));
            website.getImageRegistry().get(URI).getPagesWithMe().forEach(l->{
                buffer.append(String.format("\t%s\n", Util.trimRootURI(l, website.getRootURI())));
            });
        } else {
            buffer.append("  Embed (0)\n");
        }
        imageEntries.put(Util.trimRootURI(URI, website.getRootURI()), buffer.toString());
    }
}
