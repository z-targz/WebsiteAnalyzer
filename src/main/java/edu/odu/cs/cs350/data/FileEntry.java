package edu.odu.cs.cs350.data;

import edu.odu.cs.cs350.Main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class FileEntry implements Iterable<FileEntry>
{
    @Override
    public FileEntryIterator iterator()
    {
        return new FileEntryIterator(this);
    }

    @Override
    public void forEach(Consumer<? super FileEntry> consumer)
    {
        Iterable.super.forEach(consumer);
    }


    private final boolean is_directory;
    private final HashMap<String, FileEntry> children;
    private final String label;
    private final Path filePath;

    private final Website parent;

    private final String mimetype;

    public FileEntry(Path p, Website parent) throws IOException {
        this(p, new HashMap<>(), parent, Files.isDirectory(p));
    }

    public FileEntry(Path p, HashMap<String, FileEntry> children, Website parent) throws IOException {
        this(p, children, parent, true);
    }

    private FileEntry(Path p, HashMap<String, FileEntry> children, Website parent, boolean is_directory) throws IOException {
        this.is_directory = is_directory;
        this.label = p.getFileName().toString();
        this.children = children;
        this.filePath = p;
        this.parent = parent;
        this.mimetype = Files.probeContentType(filePath);
    }

    public boolean isDirectory() {
        return is_directory;
    }

    public HashMap<String, FileEntry> getChildren() {
        return (HashMap<String, FileEntry>) children.clone();
    }

    public String getMimeType() {
        return mimetype;
    }

    public String getURI() throws IOException {
        return filePath.toFile().getCanonicalFile().toURI().toString();
    }

    //adapted from https://stackoverflow.com/a/8948691
    protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(label);
        if(!is_directory) {
            buffer.append(" : ");

            buffer.append(mimetype);
        }
        buffer.append('\n');
        for (Iterator<FileEntry> it = children.values().iterator(); it.hasNext();) {
            FileEntry next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├─ ", childrenPrefix + "│  ");
            } else {
                next.print(buffer, childrenPrefix + "└─ ", childrenPrefix + "   ");
            }
        }
    }

    public Page parsePage() throws IOException {
        File theFile = filePath.toFile();

        final String URI = theFile.getCanonicalFile().toURI().toString();

        Page out = new Page(URI);

        Document doc = Jsoup.parse(theFile, "UTF-8", URI);
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        for (Element the_import : imports) {
            if(the_import.attr("rel").equals("stylesheet")) {
                out.addStyleSheet(the_import.attr("abs:href"));
            }
        }

        for (Element src : media) {
            if (src.nameIs("img")) {
                String src_uri = src.attr("abs:src");
                if(src_uri.startsWith(Main.getRootURI())) {
                    out.addInternalImage(src.attr("abs:src"));
                    parent.registerLinkToImage(src_uri, URI);
                } else {
                    out.addExternalImage(src.attr("abs:src"));
                    parent.registerLinkToExternalImage(src_uri, URI);
                }
            }
            else if (src.tagName().equals("script")) {
                out.addScript(src.attr("abs:src"));
            }
        }

        for (Element link : links) {
            String link_uri = link.attr("abs:href");
            if(link_uri.startsWith(Main.getRootURI())) {
                if(link_uri.startsWith(URI) && !link_uri.equals(URI)) {
                    out.addIntraPageLink(link_uri);
                } else {
                    out.addInternalLink(link_uri);
                }
            } else {
                out.addExternalLink(link_uri);
            }
        }

        return out;
    }
}



