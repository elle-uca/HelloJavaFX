package org.ln.directorytool.service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.ln.directorytool.DirectoryScanResult;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class NetworkDirectoryScanner extends Task<Void> {

    private final Path root;
    private final ObservableList<DirectoryScanResult> items;
    private final Predicate<Path> dirFilter;
    private final Consumer<String> onReport;
    private final Runnable onDone;

    private final List<DirectoryScanResult> buffer = new ArrayList<>();

    private int scanned = 0;
    private long lastUiUpdate = 0;

    public NetworkDirectoryScanner(
            Path root,
            ObservableList<DirectoryScanResult> items,
            Predicate<Path> dirFilter,
            Consumer<String> onReport,
            Runnable onDone) {

        this.root = root;
        this.items = items;
        this.dirFilter = dirFilter;
        this.onReport = onReport;
        this.onDone = onDone;
    }

    @Override
    protected Void call() {

        Deque<Path> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty() && !isCancelled()) {

            Path dir = queue.poll();
            scanned++;

            if (isCancelled()) break;

            DirectoryScanResult r = new DirectoryScanResult(dir);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path p : stream) {
                    if (Files.isDirectory(p)) {
                        r.subDirs++;
                        queue.add(p); // BFS SEMPRE COMPLETA
                    } else {
                        r.files++;
                    }
                }
            } catch (Exception ignored) {
                // rete / permessi: ignora
            }

            r.updateSize();
            r.completed = true;

            // ✅ QUI E SOLO QUI DECIDI SE MOSTRARLA
            if (dirFilter == null || dirFilter.test(dir)) {
                buffer.add(r);
            }

            // Report UI throttled
            long now = System.currentTimeMillis();
            if (onReport != null && now - lastUiUpdate > 200) {
                int count = scanned;
                Platform.runLater(() -> {
                    if (!isCancelled()) {
                        onReport.accept("Scansionate " + count + " directory...");
                    }
                });
                lastUiUpdate = now;
            }
        }

        return null;
    }

    @Override
    protected void succeeded() {

        if (isCancelled()) return;

        Platform.runLater(() -> {
            items.setAll(buffer);   // 🔥 UNA SOLA UPDATE UI
            if (onDone != null) {
                onDone.run();
            }
        });
    }
}
