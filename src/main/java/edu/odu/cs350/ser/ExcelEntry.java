package edu.odu.cs350.ser;

public class ExcelEntry
{
    private String pageName;
    private int numImages;
    private int numStylesheets;
    private int numScripts;
    private int numLinksIntraPage;
    private int numLinksInternal;
    private int numLinksExternal;

    public ExcelEntry(String pageName, int numImages, int numStylesheets, int numScripts, int numLinksIntraPage, int numLinksInternal, int numLinksExternal) {
        this.pageName = pageName;
        this.numImages = numImages;
        this.numStylesheets = numStylesheets;
        this.numScripts = numScripts;
        this.numLinksIntraPage = numLinksIntraPage;
        this.numLinksInternal = numLinksInternal;
        this.numLinksExternal = numLinksExternal;
    }

    public String getPageName() {
        return pageName;
    }

    public int getNumImages() {
        return numImages;
    }

    public int getNumStylesheets() {
        return numStylesheets;
    }

    public int getNumScripts() {
        return numScripts;
    }

    public int getNumLinksIntraPage() {
        return numLinksIntraPage;
    }

    public int getNumLinksInternal() {
        return numLinksInternal;
    }

    public int getNumLinksExternal() {
        return numLinksExternal;
    }
}
