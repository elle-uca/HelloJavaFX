package org.ln.directorytool.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FxActionAdapter implements EventHandler<ActionEvent> {

    private final Runnable command;

    public FxActionAdapter(Runnable command) {
        this.command = command;
    }

    @Override
    public void handle(ActionEvent event) {
        command.run();
    }
}