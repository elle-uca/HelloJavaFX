package org.ln.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides statistics about directories.
 *
 * @author Luca Noale
 */
public class DirectoryStatsService {

    /**
     * Counts the direct children (files + directories) of a directory.
     *
     * @param dir directory path
     * @return number of direct children, 0 if not accessible or not a directory
     */
    public static int countDirectChildren(Path dir) {
        if (dir == null || !Files.isDirectory(dir)) {
            return 0;
        }

        try (var stream = Files.list(dir)) {
            return (int) stream.count();
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Counts files and directories recursively under the given directory.
     * The root directory itself is excluded from the count.
     *
     * @param root directory path
     * @return Stats object with files and directories count
     * @throws IOException if walking fails
     */
    public static DirStats countRecursive(Path root) throws IOException {
        DirStats stats = new DirStats();

        Files.walk(root).forEach(p -> {
            // Skip counting the root directory itself
            if (p.equals(root)) return;

            if (Files.isDirectory(p)) {
                stats.directories++;
            } else {
                stats.files++;
            }
        });

        return stats;
    }

    /* -------------------------
     *  Value object
     * ------------------------- */

    /**
     * Simple container for directory statistics.
     *
     * @author Luca Noale
     */
    public static class DirStats {
        /** total number of files encountered */
        public int files;
        /** total number of directories encountered */
        public int directories;

        @Override
        public String toString() {
            return files + " file, " + directories + " directory";
        }
    }
}
