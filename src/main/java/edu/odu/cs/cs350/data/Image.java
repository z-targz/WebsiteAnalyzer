package edu.odu.cs.cs350.data;

import java.util.HashSet;
import java.util.Set;

public class Image extends FileBase {
    private Set<String> pagesWithMe;

    public Image(String uri) {
        super(uri);
        pagesWithMe = new HashSet<>();
    }

    public boolean addLink(String pageURI) {
        return pagesWithMe.add(pageURI);
    }

    public Set<String> getPagesWithMe() {
        return pagesWithMe;
    }

    public int numPagesWithMe() {
        return pagesWithMe.size();
    }
}
