package edu.odu.cs.cs350.data;

import java.io.File;
import java.net.URI;

public class Resource {

    private long mySize;
    private String myURI;

    protected Resource(String uri) {
        myURI = uri;
        mySize = new File(URI.create(myURI)).length();
    }
    public String getURI() {
        return myURI;
    }

    public long getSizeBytes() {
        return mySize;
    }

    public static String printFileSize(long size) {
        double size_kilo = size / 1024d;
        double size_mega = size_kilo / 1024d;
        double size_giga = size_mega / 1024d;

        if(size_giga >= 1d) {
            return String.format("%.2f GiB", size_giga);
        } else if(size_mega >= 1d) {
            return String.format("%.2f MiB", size_mega);
        } else if(size_kilo >= 1d) {
            return String.format("%.2f KiB", size_kilo);
        } else {
            return String.format("%d B", size);
        }
    }
}
