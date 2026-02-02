package org.ln.directorytool.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Performs basic filesystem operations with minimal safeguards.
 *
 * @author Luca Noale
 */
public class FXFilesystemService {

    /**
     * Moves a path to a new location, creating missing parent directories.
     *
     * @param from source path
     * @param to   destination path
     * @throws IOException if the move fails
     */
    public void move(Path from, Path to) throws IOException {
        Path parent = to.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Files.move(from, to);
    }
}
	