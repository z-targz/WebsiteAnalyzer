package edu.odu.cs.cs350.data;

import edu.odu.cs.cs350.Util;
import edu.odu.cs.cs350.ser.ExcelEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class HTMLDocument
{


    private String label;
    private String rootURI;
    private Set<String> intraPageLinks;
    private Set<String> internalLinks;
    private Set<String> externalLinks;
    private Set<String> internalImages;
    private Set<String> externalImages;
    private Set<String> styleSheets;
    private Set<String> scripts;


    public HTMLDocument(Path filePath, Website parent) throws IOException {
        File theFile = filePath.toFile();

        this.rootURI = parent.getRootURI();

        this.label = theFile.getCanonicalFile().toURI().toString();

        this.intraPageLinks = new HashSet<>();
        this.internalLinks = new HashSet<>();
        this.externalLinks = new HashSet<>();

        this.styleSheets = new HashSet<>();
        this.internalImages = new HashSet<>();
        this.externalImages = new HashSet<>();
        this.scripts = new HashSet<>();

        parseDocument(theFile, parent);
    }

    public void parseDocument(File theFile, Website parent) throws IOException {

        Document doc = Jsoup.parse(theFile, "UTF-8", label);
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        for (Element the_import : imports) {
            if(the_import.attr("rel").equals("stylesheet")) {
                this.styleSheets.add(the_import.attr("abs:href"));
            }
        }

        for (Element src : media) {
            if (src.nameIs("img")) {
                String src_uri = src.attr("abs:src");
                if(src_uri.startsWith(rootURI)) {
                    this.internalImages.add(src.attr("abs:src"));
                    parent.registerLinkToImage(src_uri, label);
                } else {
                    this.externalImages.add(src.attr("abs:src"));
                    parent.registerLinkToExternalImage(src_uri, label);
                }
            }
            else if (src.tagName().equals("script")) {
                this.scripts.add(src.attr("abs:src"));
            }
        }

        for (Element link : links) {
            String link_uri = link.attr("abs:href");
            if(link_uri.startsWith(rootURI) || parent.getURLs().stream().anyMatch(s->link_uri.startsWith(s))) {
                if(link_uri.startsWith(label) && !link_uri.equals(label)) {
                    this.intraPageLinks.add(link_uri);
                } else {
                    this.internalLinks.add(link_uri);
                }
            } else {
                this.externalLinks.add(link_uri);
            }
        }
    }

    public String getLabel() {
        return label;
    }

    public ExcelEntry getExcelEntry() {
        return new ExcelEntry(
            Util.trimRootURI(label, rootURI),
            internalImages.size() + externalImages.size(),
            styleSheets.size(),
            scripts.size(),
            intraPageLinks.size(),
            internalLinks.size(),
            externalLinks.size()
        );
    }

    public Set<String> getStyleSheets() {
        return styleSheets;
    }

    public Set<String> getInternalImages() {
        return internalImages;
    }

    public Set<String> getExternalImages() {
        return externalImages;
    }

    public Set<String> getScripts() {
        return scripts;
    }

    public Set<String> getIntraPageLinks() { return intraPageLinks; }

    public Set<String> getInternalLinks() { return internalLinks; }

    public Set<String> getExternalLinks() { return externalLinks; }
}
