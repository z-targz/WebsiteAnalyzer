package edu.odu.cs.cs350;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WebsiteBuilder {

    Path root;
    Set<String> URLs;

    public WebsiteBuilder() {
        URLs = new HashSet<>();
    }

    public WebsiteBuilder withRoot(Path root) {
        this.root = root;
        return this;
    }

    public WebsiteBuilder withURLs(Set<String> urls) {
        this.URLs = urls;
        return this;
    }

    public Website build() throws IOException {
        return new Website(root, walkDirectory(root), URLs);
    }

    public FileEntry walkDirectory(Path path) {
        try {
            HashMap<String, FileEntry> children = new HashMap<>();
            Files.list(path)
                .forEach(p -> {
                    if (Files.isDirectory(p)) {
                        children.put(p.getFileName().toString(), walkDirectory(p));
                    } else {
                        try {
                            children.put(p.getFileName().toString(), new FileEntry(p));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            return new FileEntry(path, children);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
