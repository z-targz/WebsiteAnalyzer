package edu.odu.cs350.data;

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
}
