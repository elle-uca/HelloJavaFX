package org.ln.directorytool.action;

import org.ln.directorytool.DirectoryToolController;

public class ChooseRootDirCommand implements Runnable {

    private final DirectoryToolController controller;

    public ChooseRootDirCommand(DirectoryToolController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.chooseRootDirectory();
    }
}
