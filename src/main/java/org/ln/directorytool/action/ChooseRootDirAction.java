package org.ln.directorytool.action;


import org.ln.directorytool.DirectoryToolController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Action that opens a dialog for selecting the root directory.
 *
 * @author Luca Noale
 */
public class ChooseRootDirAction implements EventHandler<ActionEvent> {

    private final DirectoryToolController controller;

    /**
     * Creates the action with a reference to the controller.
     *
     * @param controller the controller coordinating directory selection
     */
    public ChooseRootDirAction(DirectoryToolController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {
        controller.chooseRootDirectory();
    }
}
