package edu.odu.cs.cs350.data;

import edu.odu.cs.cs350.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Website {
    private final FileEntry rootNode;

    private final String rootURI;
    private Map<String, String> pageEntries;
    private Map<String, Image> imageRegistry;
    private Map<String, Set<String>> externalImageRegistry;
    private Map<String, String> imageEntries;
    private Map<String, String> externalImageEntries;
    private Map<String, String> archiveEntries;
    private Map<String, String> videoEntries;
    private Map<String, String> audioEntries;
    private Map<String, String> otherEntries;
    private Map<String, Page> pageRegistry;

    public Website(Path root) throws IOException {
        rootURI = root.toFile().getCanonicalFile().toURI().toString();
        rootNode = generateRecursive(root);
        pageEntries = new HashMap<>();
        imageRegistry = new HashMap<>();
        externalImageRegistry = new HashMap<>();
        imageEntries = new HashMap<>();
        externalImageEntries = new HashMap<>();
        archiveEntries = new HashMap<>();
        videoEntries = new HashMap<>();
        audioEntries = new HashMap<>();
        otherEntries = new HashMap<>();
        pageRegistry = new HashMap<>();
    }

    public Map<String, Page> getPageRegistry() {
        return pageRegistry;
    }

    public Map<String, Image> getImageRegistry() {
        return imageRegistry;
    }

    //Test ready
    public FileEntry generateRecursive(Path path) {
        try {
            HashMap<String, FileEntry> children = new HashMap<>();
            Files.list(path)
                .forEach(p -> {
                    if (Files.isDirectory(p)) {
                        children.put(p.getFileName().toString(), generateRecursive(p));
                    } else {
                        try {
                            children.put(p.getFileName().toString(), new FileEntry(p, this));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            return new FileEntry(path, children, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    // Move to excel report writer


    private enum FileType {
        PAGE,
        IMAGE,
        ARCHIVE,
        AUDIO,
        VIDEO,
    }

    public boolean matchMimeType(String mimeType, FileType fileType) {
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

    public String getResourceEntries() throws IOException {
        generateResourceEntries();
        StringBuilder buffer = new StringBuilder(1000);
        buffer.append(getImageEntries());
        buffer.append(getArchiveEntries());
        buffer.append(getVideoEntries());
        buffer.append(getAudioEntries());
        buffer.append(getOtherEntries());
        return buffer.toString();
    }

    public void generateResourceEntries() throws IOException {
        for(FileEntry child : rootNode) {
            String mimeType = child.getMimeType();
            if(matchMimeType(mimeType, FileType.IMAGE)) {
                generateImageEntries(child);
            } else if (matchMimeType(mimeType, FileType.ARCHIVE)) {
                generateArchiveEntries(child);
            } else if (matchMimeType(mimeType, FileType.VIDEO)) {
                generateVideoEntries(child);
            } else if (matchMimeType(mimeType, FileType.AUDIO)) {
                generateAudioEntries(child);
            } else if (!matchMimeType(mimeType, FileType.PAGE)) {
                generateOtherEntries(child);
            }
        }
        for(String externalURI : externalImageRegistry.keySet()) {
            StringBuilder buffer = new StringBuilder(1000);
            buffer.append(String.format("  Embed (%d):\n", externalImageRegistry.get(externalURI).size()));
            externalImageRegistry.get(externalURI).forEach(l->{
                buffer.append(String.format("\t%s\n", trimRootURI(l, rootURI)));
            });
            externalImageEntries.put(externalURI, buffer.toString());
        }
    }

    public String getOtherEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        otherEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", trimRootURI(x, rootURI)));
            buffer.append(otherEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    public void generateOtherEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s\n", FileBase.printFileSize(new OtherFile(URI).getSizeBytes())));
        otherEntries.put(URI, buffer.toString());
    }

    public String getAudioEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        audioEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", trimRootURI(x, rootURI)));
            buffer.append(audioEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    public void generateAudioEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s", FileBase.printFileSize(new Audio(URI).getSizeBytes())));
        audioEntries.put(URI, buffer.toString());
    }

    public String getVideoEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        videoEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", trimRootURI(x, rootURI)));
            buffer.append(videoEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    public void generateVideoEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s", FileBase.printFileSize(new Video(URI).getSizeBytes())));
        videoEntries.put(URI, buffer.toString());
    }

    public String getArchiveEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        archiveEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", trimRootURI(x, rootURI)));
            buffer.append(archiveEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    public void generateArchiveEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String URI = child.getURI();
        buffer.append(String.format("  %s", FileBase.printFileSize(new Archive(URI).getSizeBytes())));
        archiveEntries.put(URI, buffer.toString());
    }

    public String getImageEntries() {

        StringBuilder buffer = new StringBuilder(1000);

        imageEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", x));
            buffer.append(imageEntries.get(x));
        });

        externalImageEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", x));
            buffer.append(externalImageEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    public void generateImageEntries(FileEntry child) throws IOException {
        StringBuilder buffer = new StringBuilder(1000);
        String URI = child.getURI();
        if(imageRegistry.containsKey(URI)) {
            buffer.append(String.format("  Embed (%d):\n", imageRegistry.get(URI).numPagesWithMe()));
            imageRegistry.get(URI).getPagesWithMe().forEach(l->{
                buffer.append(String.format("\t%s\n", trimRootURI(l, rootURI)));
            });
        } else {
            buffer.append("  Embed (0)\n");
        }
        imageEntries.put(trimRootURI(URI, rootURI), buffer.toString());
    }


    public String getPageEntries() throws IOException {
        generatePageEntries();
        StringBuilder buffer = new StringBuilder(1000);

        pageEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n",x));
            buffer.append(pageEntries.get(x));
        });
        buffer.append("\n");
        return buffer.toString();
    }

    //Must execute first pass
    public void generatePageEntries() throws IOException {
        for (FileEntry child : rootNode) {
            if (matchMimeType(child.getMimeType(), FileType.PAGE)) {
                Page page = new Page(child.getFilePath(), this);
                pageRegistry.put(page.getLabel(), page);

                StringBuilder buffer = new StringBuilder(1000);

                buffer.append(String.format("  Stylesheets (%d):\n", page.getStyleSheets().size()));
                page.getStyleSheets().stream().sorted().forEach(s->{
                    buffer.append(String.format("\tcss: %s\n", trimRootURI(s, rootURI)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Scripts (%d):\n", page.getScripts().size()));
                page.getScripts().stream().sorted().forEach(s->{
                    buffer.append(String.format("\tscript: %s\n", trimRootURI(s, rootURI)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Internal Images (%d):\n", page.getInternalImages().size()));
                page.getInternalImages().stream().sorted().forEach(i->{
                    buffer.append(String.format("\timg: %s\n", trimRootURI(i, rootURI)));
                });
                buffer.append("\n");

                buffer.append(String.format("  External Images (%d):\n", page.getExternalImages().size()));
                page.getExternalImages().stream().sorted().forEach(i->{
                    buffer.append(String.format("\timg: %s\n", trimRootURI(i, rootURI)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Intra-Page Links (%d):\n", page.getIntraPageLinks().size()));
                page.getIntraPageLinks().stream().sorted().forEach(l->{
                    buffer.append(String.format("\tlink: %s\n", trimRootURI(l, rootURI)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Internal Links (%d):\n", page.getInternalLinks().size()));
                page.getInternalLinks().stream().sorted().forEach(l->{
                    buffer.append(String.format("\tlink: %s\n", trimRootURI(l, rootURI)));
                });
                buffer.append("\n");

                buffer.append(String.format("  External Links (%d):\n", page.getExternalLinks().size()));
                page.getExternalLinks().stream().sorted().forEach(l->{
                    buffer.append(String.format("\tlink: %s\n", trimRootURI(l, rootURI)));
                });
                buffer.append("\n");

                pageEntries.put(trimRootURI(page.getLabel(), rootURI), buffer.toString());
            }
        }
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
    public static String trimRootURI(String URI, String rootURI) {
        if(URI.startsWith(rootURI)) {
            return URI.substring(rootURI.length());
        }
        return URI;
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

