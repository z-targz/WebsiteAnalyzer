package edu.odu.cs.cs350.report;

import edu.odu.cs.cs350.data.Website;

import java.io.IOException;

public abstract class ReportWriter {
    protected Website website;

    protected ReportWriter(Website website) {
        this.website = website;
    }

    public abstract void write(String fileName) throws IOException;
}
