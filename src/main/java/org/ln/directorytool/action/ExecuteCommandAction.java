package org.ln.directorytool.action;

import org.ln.directorytool.DirectoryToolController;

public class ExecuteCommandAction implements Runnable {

    private final DirectoryToolController controller;

    public ExecuteCommandAction(DirectoryToolController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.processDirectory();
    }
}
