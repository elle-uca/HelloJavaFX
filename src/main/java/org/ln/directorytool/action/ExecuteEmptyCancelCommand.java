package org.ln.directorytool.action;

import org.ln.directorytool.DirectoryToolController;

public class ExecuteEmptyCancelCommand implements Runnable {

    private final DirectoryToolController controller;

    public ExecuteEmptyCancelCommand(DirectoryToolController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.processDirectory();
    }
}
