package org.ln.directorytool.util;


import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Directory utilities based on java.nio.file.
 *
 * Notes:
 * - Does NOT follow symlinks (safer across platforms).
 * - Operations are best-effort with clear exceptions on failures.
 *
 * @author Luca Noale
 */
public final class DirectoryUtils {

    private DirectoryUtils() {}

    /**
     * Deletes a directory recursively (directory + all children).
     * If the directory does not exist, does nothing.
     *
     * @param dir directory to delete
     * @throws IOException if an I/O error occurs
     */
    public static void deleteDirectoryRecursively(Path dir) throws IOException {
        Objects.requireNonNull(dir, "dir");
        if (!Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        Files.walkFileTree(dir, deletingVisitor());
    }

    /**
     * Empties a directory: deletes all files and subdirectories inside it,
     * but keeps the directory itself.
     *
     * If the directory does not exist, does nothing.
     *
     * @param dir directory to empty
     * @throws IOException if an I/O error occurs
     */
    public static void emptyDirectory(Path dir) throws IOException {
        Objects.requireNonNull(dir, "dir");
        if (!Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalArgumentException("Path is not a directory: " + dir);
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path child : ds) {
                deleteDirectoryRecursively(child); // works for files too
            }
        }
    }

    /**
     * Inserts a directory segment inside an existing path, right AFTER a given segment.
     *
     * Example:
     *  input:  pippo/pluto/minni
     *  after:  pippo
     *  insert: topolino
     *  result: pippo/topolino/pluto/minni
     *
     * Works with absolute and relative paths.
     *
     * @param original original path
     * @param afterSegment segment after which to insert
     * @param segmentToInsert segment to insert
     * @return new Path with inserted segment
     * @throws IllegalArgumentException if afterSegment is not found
     */
    public static Path insertDirectoryAfter(Path original, String afterSegment, String segmentToInsert) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(afterSegment, "afterSegment");
        Objects.requireNonNull(segmentToInsert, "segmentToInsert");

        if (afterSegment.isBlank()) {
            throw new IllegalArgumentException("afterSegment cannot be blank");
        }
        if (segmentToInsert.isBlank()) {
            throw new IllegalArgumentException("segmentToInsert cannot be blank");
        }

        Path root = original.getRoot(); // null if relative
        int nameCount = original.getNameCount();

        int insertAfterIndex = -1;
        for (int i = 0; i < nameCount; i++) {
            if (afterSegment.equals(original.getName(i).toString())) {
                insertAfterIndex = i;
                break;
            }
        }

        if (insertAfterIndex < 0) {
            throw new IllegalArgumentException("Segment not found: " + afterSegment + " in " + original);
        }

        // rebuild: root + names[0..insertAfterIndex] + segmentToInsert + names[insertAfterIndex+1..end]
        Path rebuilt = (root != null) ? root : Path.of("");

        for (int i = 0; i <= insertAfterIndex; i++) {
            rebuilt = rebuilt.resolve(original.getName(i));
        }

        rebuilt = rebuilt.resolve(segmentToInsert);

        for (int i = insertAfterIndex + 1; i < nameCount; i++) {
            rebuilt = rebuilt.resolve(original.getName(i));
        }

        return rebuilt;
    }

    /**
     * Moves ONLY regular files contained directly in sourceDir into targetDir.
     * - Does not move subdirectories (they are ignored)
     * - Creates targetDir if missing
     * - Overwrites same-named files in targetDir
     *
     * If sourceDir does not exist, does nothing.
     *
     * @param sourceDir source directory
     * @param targetDir target directory
     * @throws IOException if an I/O error occurs
     */
    public static void moveFiles(Path sourceDir, Path targetDir) throws IOException {
        Objects.requireNonNull(sourceDir, "sourceDir");
        Objects.requireNonNull(targetDir, "targetDir");

        if (!Files.exists(sourceDir, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        if (!Files.isDirectory(sourceDir, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalArgumentException("sourceDir is not a directory: " + sourceDir);
        }

        Files.createDirectories(targetDir);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(sourceDir)) {
            for (Path child : ds) {
                if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
                    Path dest = targetDir.resolve(child.getFileName());
                    moveWithFallback(child, dest);
                }
            }
        }
    }

    /**
     * Empties all directories whose final name equals {@code dirName}, recursively under {@code root}.
     * The matching directories are kept, only their contents are removed.
     *
     * If root does not exist, does nothing.
     *
     * @param root root folder to scan
     * @param dirName directory name to match (exact match)
     * @throws IOException if an I/O error occurs
     */
    public static void emptyAllDirectoriesNamed(Path root, String dirName) throws IOException {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(dirName, "dirName");

        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }

        List<Path> matches = findDirectoriesNamed(root, dirName);

        // Empty deeper ones first (safer when nested matches exist).
        matches.sort(Comparator.comparingInt(Path::getNameCount).reversed());

        for (Path p : matches) {
            emptyDirectory(p);
        }
    }
    
    /**
     * Moves only subdirectories (not files) from sourceDir to targetDir.
     * Subdirectories are moved as whole trees.
     *
     * @param sourceDir directory whose immediate subdirectories will be moved
     * @param targetDir destination directory that will receive moved subdirectories
     * @throws IOException if an I/O error occurs during move
     */
    public static void moveDirectories(Path sourceDir, Path targetDir) throws IOException {
        Objects.requireNonNull(sourceDir);
        Objects.requireNonNull(targetDir);

        if (!Files.exists(sourceDir)) return;

        Files.createDirectories(targetDir);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(sourceDir)) {
            for (Path child : ds) {
                if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                    Path dest = targetDir.resolve(child.getFileName());
                    moveWithFallback(child, dest);
                }
            }
        }
    }


    /**
     * Moves files and subdirectories from sourceDir to targetDir.
     *
     * @param sourceDir directory whose contents will be moved
     * @param targetDir destination directory that will receive files and subdirectories
     * @throws IOException if an I/O error occurs during move
     */
    public static void moveAll(Path sourceDir, Path targetDir) throws IOException {
        Objects.requireNonNull(sourceDir);
        Objects.requireNonNull(targetDir);

        if (!Files.exists(sourceDir)) return;

        Files.createDirectories(targetDir);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(sourceDir)) {
            for (Path child : ds) {
                Path dest = targetDir.resolve(child.getFileName());
                moveWithFallback(child, dest);
            }
        }
    }


    /**
     * Deletes all directories whose final name equals {@code dirName}, recursively under {@code root}.
     * Matching directories are deleted along with all their contents.
     *
     * If root does not exist, does nothing.
     *
     * @param root root folder to scan
     * @param dirName directory name to match (exact match)
     * @throws IOException if an I/O error occurs
     */
    public static void deleteAllDirectoriesNamed(Path root, String dirName) throws IOException {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(dirName, "dirName");

        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }

        List<Path> matches = findDirectoriesNamed(root, dirName);

        // Delete deeper ones first (required: you can't delete a parent before its child).
        matches.sort(Comparator.comparingInt(Path::getNameCount).reversed());

        for (Path p : matches) {
            deleteDirectoryRecursively(p);
        }
    }

    // -------------------------
    // Internals
    // -------------------------

    private static List<Path> findDirectoriesNamed(Path root, String dirName) throws IOException {
        if (!Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalArgumentException("root is not a directory: " + root);
        }

        List<Path> matches = new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                Path name = dir.getFileName();
                if (name != null && dirName.equals(name.toString())) {
                    matches.add(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return matches;
    }

    private static FileVisitor<Path> deletingVisitor() {
        return new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // includes symlinks as "files" in practice when not following links
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                try {
                    Files.deleteIfExists(dir);
                } catch (DirectoryNotEmptyException e) {
                    // Extremely rare race: try once more by emptying then deleting.
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                        for (Path child : ds) {
                            deleteDirectoryRecursively(child);
                        }
                    }
                    Files.deleteIfExists(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        };
    }

    private static void moveWithFallback(Path source, Path dest) throws IOException {
        try {
            Files.move(source, dest,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicOrCrossFsFail) {
            // ATOMIC_MOVE may fail on some FS, also cross-filesystem moves may fail: retry without atomic.
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
