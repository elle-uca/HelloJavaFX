package org.ln.directorytool;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class NetworkDirectoryScanner extends Task<Void> {

    private final Path root;
    private final ObservableList<DirectoryScanResult> items;
    private final Consumer<String> onReport;
    private final Runnable onDone;

    private int scanned = 0;
    private long lastUiUpdate = 0;

    public NetworkDirectoryScanner(
            Path root,
            ObservableList<DirectoryScanResult> items,
            Consumer<String> onReport,
            Runnable onDone
            ) {

        this.root = root;
        this.items = items;
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

            DirectoryScanResult r = new DirectoryScanResult(dir);

            Platform.runLater(() -> items.add(r));

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path p : stream) {
                    if (Files.isDirectory(p)) {
                        r.subDirs++;
                        queue.add(p);
                    } else {
                        r.files++;
                    }
                }
            } catch (Exception ignored) {}

            r.completed = true;

            long now = System.currentTimeMillis();
            if (now - lastUiUpdate > 200) {
                int count = scanned;
                Platform.runLater(() ->
                        onReport.accept("Scansionate " + count + " directory...")
                );
                lastUiUpdate = now;
            }
        }
        return null;
    }

    @Override
    protected void succeeded() {
        if (onDone != null) {
            Platform.runLater(onDone);
        }
    }
}
