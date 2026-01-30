package org.ln.directorytool;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.ln.directorytool.DirectoryToolController.DirectoryContent;

public final class DirectoryContentAnalyzer {

    public static DirectoryContent analyze(Path dir) throws IOException {

        boolean hasFiles = false;
        boolean hasDirs  = false;

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    hasDirs = true;
                } else {
                    hasFiles = true;
                }
                if (hasFiles && hasDirs) break;
            }
        }

        if (!hasFiles && !hasDirs) return DirectoryContent.EMPTY;
        if (hasFiles && hasDirs)   return DirectoryContent.FILES_AND_DIRS;
        if (hasFiles)              return DirectoryContent.FILES_ONLY;
        return DirectoryContent.DIRS_ONLY;
    }
}