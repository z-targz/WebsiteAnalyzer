package edu.odu.cs.cs350.data;

import java.util.Iterator;

public class FileEntryIterator implements Iterator<FileEntry>
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

