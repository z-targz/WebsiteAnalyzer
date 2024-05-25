package edu.odu.cs350.data;

import edu.odu.cs350.ser.ExcelEntry;

import java.util.HashSet;
import java.util.Set;

public class Page
{
    private String label;


    private Set<String> intraPageLinks;
    private Set<String> internalLinks;
    private Set<String> externalLinks;

    private Set<String> internalImages;
    private Set<String> externalImages;

    private Set<String> styleSheets;

    private Set<String> scripts;

    public Page(String label) {
        this.label = label;

        this.intraPageLinks = new HashSet<>();
        this.internalLinks = new HashSet<>();
        this.externalLinks = new HashSet<>();

        this.styleSheets = new HashSet<>();
        this.internalImages = new HashSet<>();
        this.externalImages = new HashSet<>();
        this.scripts = new HashSet<>();
    }

    public String getLabel() {
        return label;
    }

    public ExcelEntry getExcelEntry() {
        return new ExcelEntry(
            Website.trimRootURI(label),
            internalImages.size() + externalImages.size(),
            styleSheets.size(),
            scripts.size(),
            intraPageLinks.size(),
            internalLinks.size(),
            externalLinks.size()
        );
    }



    public boolean addStyleSheet(String styleSheetURI) {
        return styleSheets.add(styleSheetURI);
    }

    public Set<String> getStyleSheets() {
        return styleSheets;
    }

    public boolean addInternalImage(String imageURI) {
        return internalImages.add(imageURI);
    }

    public Set<String> getInternalImages() {
        return internalImages;
    }

    public boolean addExternalImage(String imageURI) {
        return externalImages.add(imageURI);
    }

    public Set<String> getExternalImages() {
        return externalImages;
    }

    public boolean addScript(String scriptURI) {
        return scripts.add(scriptURI);
    }

    public Set<String> getScripts() {
        return scripts;
    }

    public boolean addIntraPageLink(String linkURI) {
        return intraPageLinks.add(linkURI);
    }

    public Set<String> getIntraPageLinks() {
        return intraPageLinks;
    }

    public boolean addInternalLink(String linkURI) {
        return internalLinks.add(linkURI);
    }

    public Set<String> getInternalLinks() {
        return internalLinks;
    }

    public boolean addExternalLink(String linkURI) {
        return externalLinks.add(linkURI);
    }

    public Set<String> getExternalLinks() {
        return externalLinks;
    }
}
