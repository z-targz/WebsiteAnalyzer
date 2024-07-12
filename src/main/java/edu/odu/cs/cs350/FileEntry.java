package edu.odu.cs.cs350;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;


/*
    A tree structure representing a directory tree with utility functions
 */
public class FileEntry implements Iterable<FileEntry>
{
    private class FileEntryIterator implements Iterator<FileEntry>
    {

        private Iterator<FileEntry> selfIterator;
        private FileEntry currentChild;
        private FileEntryIterator childIterator;

        private FileEntry parent;

        private boolean consumed;

        public FileEntryIterator(FileEntry parent) {
            this.parent = parent;
            this.consumed = false;

            this.selfIterator = parent.getChildren().values().iterator();
            if(selfIterator.hasNext()) {
                currentChild = selfIterator.next();
                childIterator = currentChild.iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if(consumed) {
                return false;
            }

            if(parent.isDirectory()) {
                if(currentChild == null) {
                    return false;
                }

                if (!childIterator.hasNext()) {
                    while (!childIterator.hasNext()) {
                        if (selfIterator.hasNext()) {
                            currentChild = selfIterator.next();
                            childIterator = currentChild.iterator();
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public FileEntry next() {
            if(parent.isDirectory()) {
                if(currentChild == null) {
                    return null;
                }
                if (!childIterator.hasNext()) {
                    while (!childIterator.hasNext()) {
                        if (selfIterator.hasNext()) {
                            currentChild = selfIterator.next();
                            childIterator = currentChild.iterator();
                        } else {
                            return null;
                        }
                    }
                }
                return childIterator.next();
            } else {
                consumed = true;
                return parent;
            }
        }
    }

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



    private final String mimetype;



    public FileEntry(Path p) throws IOException {
        this(p, new HashMap<>(), Files.isDirectory(p));
    }

    public FileEntry(Path p, HashMap<String, FileEntry> children) throws IOException {
        this(p, children, true);
    }

    private FileEntry(Path p, HashMap<String, FileEntry> children, boolean is_directory) throws IOException {
        this.is_directory = is_directory;
        this.label = p.getFileName().toString();
        this.children = children;
        this.filePath = p;

        this.mimetype = Files.probeContentType(filePath);
    }

    public boolean isDirectory() {
        return is_directory;
    }

    public HashMap<String, FileEntry> getChildren() {
        return (HashMap<String, FileEntry>) children.clone();
    }

    public Path getFilePath() {
        return filePath;
    }

    //Test Ready
    public String getMimeType() {
        return mimetype;
    }

    //Test ready
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
}



