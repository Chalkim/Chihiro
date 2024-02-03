package org.chihiro.index.scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chihiro.exception.InvalidDirectoryException;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class SubtitleScanner {
    private LinkedList<String> formats;
    private Boolean recursive;

    private static final Log log = LogFactory.getLog(SubtitleScanner.class);

    public SubtitleScanner() {
        this(Arrays.asList("srt", "ass"),
                false);
    }

    public SubtitleScanner(List<String> formats, boolean recursive) {
        this.formats = new LinkedList<>(formats);
        this.recursive = recursive;
    }

    public LinkedList<String> getFormats() {
        return formats;
    }

    public void setFormats(LinkedList<String> formats) {
        this.formats = formats;
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    private boolean isSupportFormat(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1);
            return formats.contains(extension);
        }
        return false;
    }

    public List<Path> scanDirectory(Path directoryPath) throws InvalidDirectoryException {
        File directory = directoryPath.toFile();

        if (!directory.isDirectory()) {
            throw new InvalidDirectoryException(directoryPath.toString());
        }

        Queue<File> queue = new LinkedList<>();
        queue.add(directory);

        List<Path> subtitleFilePaths = new LinkedList<>();
        while (!queue.isEmpty()) {
            File currentFolder = queue.poll();
            File[] files = currentFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (recursive && file.isDirectory()) {
                        queue.add(file);
                    } else if (isSupportFormat(file)) {
                        subtitleFilePaths.add(file.toPath());
                    } else {
                        log.warn("Unsupported format: " + file.getPath());
                    }
                }
            }
        }

        return subtitleFilePaths;
    }

    public List<Path> scanDirectories(List<Path> directoryPaths) throws InvalidDirectoryException {
        List<Path> subtitleFilePaths = new LinkedList<>();

        for (Path path : directoryPaths) {
            List<Path> files = scanDirectory(path);
            subtitleFilePaths.addAll(files);
        }

        return subtitleFilePaths;
    }
}
