package org.ln.directorytool;

import javafx.concurrent.Task;

public class StartupTask extends Task<Void> {

    @Override
    protected Void call() throws Exception {

        updateMessage("Inizializzazione...");
        updateProgress(0, 100);
        Thread.sleep(400);

        updateMessage("Caricamento preferenze...");
        updateProgress(20, 100);
        Thread.sleep(400);

        updateMessage("Preparazione servizi...");
        updateProgress(50, 100);
        Thread.sleep(500);

        updateMessage("Avvio interfaccia...");
        updateProgress(80, 100);
        Thread.sleep(300);

        updateProgress(100, 100);
        updateMessage("Pronto");

        return null;
    }
}
