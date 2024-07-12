package edu.odu.cs.cs350;

import edu.odu.cs.cs350.data.Image;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


public class Website {
    private final FileEntry rootNode;
    private final String rootURI;
    private Set<String> URLs;
    private Map<String, Image> imageRegistry;
    private Map<String, Set<String>> externalImageRegistry;
    private Map<String, HTMLDocument> documentRegistry;

    protected Website(Path root, FileEntry rootNode, Set<String> URLs) throws IOException {
        rootURI = root.toFile().getCanonicalFile().toURI().toString();
        this.rootNode = rootNode;
        imageRegistry = new HashMap<>();
        externalImageRegistry = new HashMap<>();
        this.documentRegistry = new HashMap<>();
        this.URLs = URLs;
        registerPages();
    }

    //This must loop through and register all pages before looping through and registering any page resources
    public void registerPages() throws IOException {
        for (FileEntry child : rootNode) {
            if (matchMimeType(child.getMimeType(), FileType.PAGE)) {
                HTMLDocument page = new HTMLDocument(child.getFilePath(), this);
                documentRegistry.put(page.getLabel(), page);
            }
        }
    }

    public Map<String, HTMLDocument> getDocumentRegistry() {
        return documentRegistry;
    }

    public Map<String, Image> getImageRegistry() {
        return imageRegistry;
    }

    public Map<String, Set<String>> getExternalImageRegistry() {
        return externalImageRegistry;
    }

    public FileEntry getRootNode() {
        return rootNode;
    }

    public enum FileType {
        PAGE,
        IMAGE,
        ARCHIVE,
        AUDIO,
        VIDEO,
    }

    public static boolean matchMimeType(String mimeType, FileType fileType) {
        switch (fileType) {
            case PAGE: {
                return mimeType.equals("text/html") ||
                    mimeType.equals("application/x-httpd-php") ||
                    mimeType.equals("application/x-httpd-cgi");
            }
            case IMAGE: {
                return mimeType.startsWith("image");
            }
            case ARCHIVE: {
                return mimeType.equals("application/zip") ||
                    mimeType.equals("application/x-7z-compressed") ||
                    mimeType.equals("application/x-tar") ||
                    mimeType.equals("application/x-gtar") ||
                    mimeType.equals("application/x-compress") ||
                    mimeType.equals("application/x-compressed") ||
                    mimeType.equals("application/gzip") ||
                    mimeType.equals("application/x-gzip") ||
                    mimeType.equals("application/x-tgz");
            }
            case AUDIO: {
                return mimeType.startsWith("audio");
            }
            case VIDEO: {
                return mimeType.startsWith("video");
            }

            default: return false;
        }
    }

    public Set<String> getURLs() {
        return URLs;
    }

    public boolean registerLinkToImage(String imageURI, String pageURI) {
        if (imageRegistry.containsKey(imageURI)) {
            imageRegistry.get(imageURI).addLink(pageURI);
            return false;
        } else {
            Image img = new Image(imageURI);
            img.addLink(pageURI);
            imageRegistry.put(imageURI, img);
            return true;
        }
    }

    public boolean registerLinkToExternalImage(String imageURI, String pageURI) {
        if (externalImageRegistry.containsKey(imageURI)) {
            externalImageRegistry.get(imageURI).add(pageURI);
            return false;
        } else {
            Set<String> set = new HashSet<>();
            set.add(pageURI);
            externalImageRegistry.put(imageURI, set);
            return true;
        }
    }

    //Test ready
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(1000);
        rootNode.print(buffer, "", "");
        return buffer.toString();
    }

    public String getRootURI() {
        return rootURI;
    }
}

