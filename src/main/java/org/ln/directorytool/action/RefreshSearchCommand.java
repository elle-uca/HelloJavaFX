package org.ln.directorytool.action;

import org.ln.directorytool.DirectoryToolController;

public class RefreshSearchCommand implements Runnable {

    private final DirectoryToolController controller;

    public RefreshSearchCommand(DirectoryToolController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.refreshSearch();
    }
}
