package edu.odu.cs.cs350;

public class Util {

    //Test ready
    public static String trimRootURI(String URI, String rootURI) {
        if(URI.startsWith(rootURI)) {
            return URI.substring(rootURI.length());
        }
        return URI;
    }
}
