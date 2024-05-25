package edu.odu.cs350.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class DirectoryStructure {
    private FileEntry rootNode;
    private HashMap<String, String> htmlEntries;

    public DirectoryStructure(Path root) throws Exception {
        rootNode = generateRecursive(root, true);
        htmlEntries = new HashMap<>();
    }

    public FileEntry generateRecursive(Path path, boolean isRootNode) {
        try {
            HashMap<String, FileEntry> children = new HashMap<>();

            Files.list(path)
                .forEach(p -> {
                    if (Files.isDirectory(p)) {
                        children.put(p.getFileName().toString(), generateRecursive(p, false));
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

    public void addHtmlEntry(String label, String entry) {
        htmlEntries.put(label, entry);
    }

    public String getHtmlEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        htmlEntries.keySet().forEach(x->{
            buffer.append(String.format("%s\n",x));
            buffer.append(htmlEntries.get(x));
        });
        return buffer.toString();
    }

    public void generateHtmlEntries() {
        try {
            for (FileEntry child : rootNode) {
                if (child.getMimetype().equals("text/html")) {
                    Page page = child.parsePage();
                    StringBuilder buffer = new StringBuilder(1000);

                    buffer.append(String.format("  Stylesheets (%d):\n", page.getStyleSheets().size()));
                    page.getStyleSheets().forEach(s->{
                        buffer.append(String.format("\tcss: %s\n", page.trimRootURI(s)));
                    });
                    buffer.append("\n");

                    buffer.append(String.format("  Scripts (%d):\n", page.getScripts().size()));
                    page.getStyleSheets().forEach(s->{
                        buffer.append(String.format("\tscript: %s\n", page.trimRootURI(s)));
                    });
                    buffer.append("\n");

                    buffer.append(String.format("  Internal Images (%d):\n", page.getInternalImages().size()));
                    page.getInternalImages().forEach(i->{
                        buffer.append(String.format("\timg: %s\n", page.trimRootURI(i)));
                    });
                    buffer.append("\n");

                    buffer.append(String.format("  External Images (%d):\n", page.getExternalImages().size()));
                    page.getExternalImages().forEach(i->{
                        buffer.append(String.format("\timg: %s\n", page.trimRootURI(i)));
                    });
                    buffer.append("\n");

                    buffer.append(String.format("  Intra-Page Links (%d):\n", page.getIntraPageLinks().size()));
                    page.getIntraPageLinks().forEach(l->{
                        buffer.append(String.format("\tlink: %s\n", page.trimRootURI(l)));
                    });
                    buffer.append("\n");

                    buffer.append(String.format("  Internal Links (%d):\n", page.getInternalLinks().size()));
                    page.getInternalLinks().forEach(l->{
                        buffer.append(String.format("\tlink: %s\n", page.trimRootURI(l)));
                    });
                    buffer.append("\n");

                    buffer.append(String.format("  External Links (%d):\n", page.getExternalLinks().size()));
                    page.getExternalLinks().forEach(l->{
                        buffer.append(String.format("\tlink: %s\n", page.trimRootURI(l)));
                    });
                    buffer.append("\n");

                    htmlEntries.put(page.trimRootURI(page.getLabel()), buffer.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(1000);
        rootNode.print(buffer, "", "");
        return buffer.toString();
    }
}

