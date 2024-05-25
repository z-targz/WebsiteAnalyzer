package edu.odu.cs350.data;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class HtmlEntry
{
    private String label;


    private List<String> intraPageLinks;
    private List<String> internalLinks;
    private List<String> externalLinks;

    public HtmlEntry(String label) {
        this.label = label;


        this.intraPageLinks = new ArrayList<>();
        this.internalLinks = new ArrayList<>();
        this.externalLinks = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }
    public String getReport() {
        return report;
    }
}
