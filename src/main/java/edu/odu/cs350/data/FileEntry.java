package edu.odu.data;

import edu.odu.Main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;

public class FileEntry
{
    enum ResourceType {
        HTML,
        IMAGE,
        ARCHIVE,
        VIDEO,
        AUDIO,
        SCRIPT,
        OTHER,
    }
    private boolean isDirectory;
    private HashMap<String, FileEntry> children;
    private String label;

    private Path filePath;

    public FileEntry(Path p) {
        this.children = new HashMap<>();
        this.label = p.getFileName().toString();
        this.isDirectory = Files.isDirectory(p);
        this.filePath = p;
    }

    public FileEntry(Path p, HashMap<String, FileEntry> children) {
        this.isDirectory = true;
        this.label = p.getFileName().toString();
        this.children = children;
        this.filePath = p;
    }

    public HashMap<String, FileEntry> getChildren() {
        return (HashMap<String, FileEntry>) children.clone();
    }

    //adapted from https://stackoverflow.com/a/8948691
    protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(label);
        if(!isDirectory) {
            buffer.append(" : ");
            try {
                buffer.append(Files.probeContentType(filePath));
            } catch (Exception e) {
                System.exit(-1);
            }
        }
        buffer.append('\n');
        for (Iterator<FileEntry> it = children.values().iterator(); it.hasNext();) {
            FileEntry next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├─ ", childrenPrefix + "│  ");
            } else {
                next.print(buffer, childrenPrefix + "└─ ", childrenPrefix + "   ");
            }
        }
    }

    private String generateHtmlReport() throws Exception {
        StringBuilder buffer = new StringBuilder();
        File theFile = filePath.toFile();

        final String URI = theFile.getCanonicalFile().toURI().toString();
        final String relativePath = URI.replaceFirst(Main.getRootURI());

        Document doc = Jsoup.parse(theFile, "UTF-8", URI);
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

    }
}


