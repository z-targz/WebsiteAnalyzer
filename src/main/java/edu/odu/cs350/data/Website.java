package edu.odu.cs350.data;

import edu.odu.cs350.Main;
import edu.odu.cs350.ser.ExcelEntry;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Website {
    private final FileEntry rootNode;
    private HashMap<String, String> pageEntries;
    private HashMap<String, Image> imageRegistry;
    private HashMap<String, Set<String>> externalImageRegistry;
    private HashMap<String, String> imageEntries;
    private HashMap<String, String> externalImageEntries;
    private HashMap<String, String> archiveEntries;
    private HashMap<String, String> videoEntries;
    private HashMap<String, String> audioEntries;
    private HashMap<String, String> otherEntries;
    private HashMap<String, Page> pageRegistry;
    private HashMap<String, Long> pageSizeRegistry;

    public Website(Path root) {
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
        pageSizeRegistry = new HashMap<>();
    }

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

    public void determinePageSizes() {
        pageRegistry.keySet().forEach(x->{
            Page p = pageRegistry.get(x);
            long sizeBytes =
                p.getInternalImages()
                    .stream()
                    .map(uri->imageRegistry.get(uri).getSizeBytes())
                    .mapToLong(Long::longValue)
                    .sum();
            pageSizeRegistry.put(x, sizeBytes);
        });
    }

    public String getTxtReport() {
        determinePageSizes();
        StringBuilder buffer = new StringBuilder(1000);
        pageSizeRegistry.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\t%s\n", FileBase.printFileSize(pageSizeRegistry.get(x)), trimRootURI(x)));
        });
        return buffer.toString();
    }

    public HSSFWorkbook getExcelReport() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();

        HSSFRow firstRow = sheet.createRow(0);
        HSSFCell column0 = firstRow.createCell(0);
        column0.setCellType(CellType.STRING);
        column0.setCellValue("Page Name");

        HSSFCell column1 = firstRow.createCell(1);
        column1.setCellType(CellType.STRING);
        column1.setCellValue("# of Images");

        HSSFCell column2 = firstRow.createCell(2);
        column2.setCellType(CellType.STRING);
        column2.setCellValue("# of Stylesheets");

        HSSFCell column3 = firstRow.createCell(3);
        column3.setCellType(CellType.STRING);
        column3.setCellValue("# of Scripts");

        HSSFCell column4 = firstRow.createCell(4);
        column4.setCellType(CellType.STRING);
        column4.setCellValue("# of Intra-Page Links");

        HSSFCell column5 = firstRow.createCell(5);
        column5.setCellType(CellType.STRING);
        column5.setCellValue("# of Internal Links");

        HSSFCell column6 = firstRow.createCell(6);
        column6.setCellType(CellType.STRING);
        column6.setCellValue("# of External Links");

        AtomicInteger i = new AtomicInteger();
        i.set(1);
        pageRegistry.keySet().stream().sorted().forEach(x->{
            ExcelEntry entry = pageRegistry.get(x).getExcelEntry();
            HSSFRow row = sheet.createRow(i.get());


            HSSFCell cell0 = row.createCell(0);
            cell0.setCellType(CellType.NUMERIC);
            cell0.setCellValue(entry.getPageName());

            HSSFCell cell1 = row.createCell(1);
            cell1.setCellType(CellType.NUMERIC);
            cell1.setCellValue(entry.getNumImages());

            HSSFCell cell2 = row.createCell(2);
            cell2.setCellType(CellType.NUMERIC);
            cell2.setCellValue(entry.getNumStylesheets());

            HSSFCell cell3 = row.createCell(3);
            cell3.setCellType(CellType.NUMERIC);
            cell3.setCellValue(entry.getNumScripts());

            HSSFCell cell4 = row.createCell(4);
            cell4.setCellType(CellType.NUMERIC);
            cell4.setCellValue(entry.getNumLinksIntraPage());

            HSSFCell cell5 = row.createCell(5);
            cell5.setCellType(CellType.NUMERIC);
            cell5.setCellValue(entry.getNumLinksInternal());

            HSSFCell cell6 = row.createCell(6);
            cell6.setCellType(CellType.NUMERIC);
            cell6.setCellValue(entry.getNumLinksExternal());

            i.incrementAndGet();
        });
        for(int j=0;j<7;++j) {
            sheet.autoSizeColumn(j);
        }

        return workbook;
    }

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
                buffer.append(String.format("\t%s\n", trimRootURI(l)));
            });
            externalImageEntries.put(externalURI, buffer.toString());
        }
    }

    public String getOtherEntries() {
        StringBuilder buffer = new StringBuilder(1000);

        otherEntries.keySet().stream().sorted().forEach(x->{
            buffer.append(String.format("%s\n", trimRootURI(x)));
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
            buffer.append(String.format("%s\n", trimRootURI(x)));
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
            buffer.append(String.format("%s\n", trimRootURI(x)));
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
            buffer.append(String.format("%s\n", trimRootURI(x)));
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
                buffer.append(String.format("\t%s\n", trimRootURI(l)));
            });
        } else {
            buffer.append("  Embed (0)\n");
        }
        imageEntries.put(trimRootURI(URI), buffer.toString());
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
                Page page = child.parsePage();
                pageRegistry.put(page.getLabel(), page);


                StringBuilder buffer = new StringBuilder(1000);

                buffer.append(String.format("  Stylesheets (%d):\n", page.getStyleSheets().size()));
                page.getStyleSheets().stream().sorted().forEach(s->{
                    buffer.append(String.format("\tcss: %s\n", trimRootURI(s)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Scripts (%d):\n", page.getScripts().size()));
                page.getScripts().stream().sorted().forEach(s->{
                    buffer.append(String.format("\tscript: %s\n", trimRootURI(s)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Internal Images (%d):\n", page.getInternalImages().size()));
                page.getInternalImages().stream().sorted().forEach(i->{
                    buffer.append(String.format("\timg: %s\n", trimRootURI(i)));
                });
                buffer.append("\n");

                buffer.append(String.format("  External Images (%d):\n", page.getExternalImages().size()));
                page.getExternalImages().stream().sorted().forEach(i->{
                    buffer.append(String.format("\timg: %s\n", trimRootURI(i)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Intra-Page Links (%d):\n", page.getIntraPageLinks().size()));
                page.getIntraPageLinks().stream().sorted().forEach(l->{
                    buffer.append(String.format("\tlink: %s\n", trimRootURI(l)));
                });
                buffer.append("\n");

                buffer.append(String.format("  Internal Links (%d):\n", page.getInternalLinks().size()));
                page.getInternalLinks().stream().sorted().forEach(l->{
                    buffer.append(String.format("\tlink: %s\n", trimRootURI(l)));
                });
                buffer.append("\n");

                buffer.append(String.format("  External Links (%d):\n", page.getExternalLinks().size()));
                page.getExternalLinks().stream().sorted().forEach(l->{
                    buffer.append(String.format("\tlink: %s\n", trimRootURI(l)));
                });
                buffer.append("\n");

                pageEntries.put(trimRootURI(page.getLabel()), buffer.toString());
            }
        }
    }

    public boolean registerLinkToImage(String imageURI, String pageURI) throws IOException {
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

    public boolean registerLinkToExternalImage(String imageURI, String pageURI) throws IOException {
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

    public static String trimRootURI(String URI) {
        if(URI.startsWith(Main.getRootURI())) {
            return URI.substring(Main.getRootURI().length());
        }
        return URI;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(1000);
        rootNode.print(buffer, "", "");
        return buffer.toString();
    }
}

