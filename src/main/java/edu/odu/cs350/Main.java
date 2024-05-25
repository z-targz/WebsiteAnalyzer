package edu.odu;

import edu.odu.data.DirectoryStructure;

import java.io.File;

public class Main {
    private static String rootURI;
    public static void main(String[] args) {
        try {
            File root = new File(args[0]);
            rootURI = root.getCanonicalFile().toURI().toString();
            System.out.println(rootURI);
            DirectoryStructure directoryStructure = new DirectoryStructure(root.toPath());
            System.out.println(directoryStructure.toString());
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    public static String getRootURI() {
        return rootURI;
    }
}
