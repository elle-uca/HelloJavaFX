package org.ln.directorytool.view;

import org.ln.directorytool.DirectoryToolController.DeleteAction;
import org.ln.directorytool.DirectoryToolController.DeleteContext;
import org.ln.directorytool.DirectoryToolController.DirectoryContent;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class DeleteDirectoryDialog extends Dialog<DeleteAction> {

    public DeleteDirectoryDialog(DeleteContext ctx) {

        setTitle("Elimina directory");
        setHeaderText(ctx.dir().toString());

        ButtonType ok =
            new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes()
                       .addAll(ok, ButtonType.CANCEL);

        ToggleGroup group = new ToggleGroup();

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));

        // messaggio
        Label info = new Label(messageFor(ctx.content()));
        contentBox.getChildren().add(info);

        // DELETE_ALL
        RadioButton deleteAll =
            new RadioButton("Cancella directory e tutto il contenuto");
        deleteAll.setToggleGroup(group);
        contentBox.getChildren().add(deleteAll);

        // MOVE_CONTENT_UP
        RadioButton moveUp = null;
        if (ctx.hasParent()) {
            moveUp = new RadioButton(
                "Sposta il contenuto nella directory superiore"
            );
            moveUp.setToggleGroup(group);
            contentBox.getChildren().add(moveUp);
        }

        // ABORT
        RadioButton abort =
            new RadioButton("Annulla operazione");
        abort.setToggleGroup(group);
        abort.setSelected(true);
        contentBox.getChildren().add(abort);

        getDialogPane().setContent(contentBox);

        // abilita OK solo se scelta != ABORT
        Node okBtn = getDialogPane().lookupButton(ok);
        okBtn.disableProperty().bind(
            group.selectedToggleProperty().isNull()
        );

        RadioButton finalMoveUp = moveUp;

        setResultConverter(bt -> {
            if (bt != ok) return DeleteAction.ABORT;
            if (deleteAll.isSelected()) return DeleteAction.DELETE_ALL;
            if (finalMoveUp != null && finalMoveUp.isSelected())
                return DeleteAction.MOVE_CONTENT_UP;
            return DeleteAction.ABORT;
        });
    }

    private String messageFor(DirectoryContent c) {
        return switch (c) {
            case EMPTY -> "La directory è vuota.";
            case FILES_ONLY -> "La directory contiene file.";
            case DIRS_ONLY -> "La directory contiene sottodirectory.";
            case FILES_AND_DIRS ->
                "La directory contiene file e sottodirectory.";
        };
    }
}
