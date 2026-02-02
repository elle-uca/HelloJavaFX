package org.ln.directorytool.view;


import java.nio.file.Path;

import org.ln.directorytool.DirectoryToolController.ReorderDialogResult;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ReorderDirectoryDialog
        extends Dialog<ReorderDialogResult> {

    public ReorderDirectoryDialog(
            Stage owner,
            Path operationRoot,
            Path selectedPath
    ) {

        setTitle("Riorganizza directory");
        setHeaderText("Riorganizza il percorso");

        initOwner(owner);

        ButtonType okButtonType =
                new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes()
                       .addAll(okButtonType, ButtonType.CANCEL);

        // ---------- UI ----------

        TextField referenceField = new TextField();
        TextField insertField = new TextField();

        ToggleGroup group = new ToggleGroup();
        RadioButton before = new RadioButton("Inserisci prima");
        RadioButton after  = new RadioButton("Inserisci dopo");
        before.setToggleGroup(group);
        after.setToggleGroup(group);
        before.setSelected(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        int r = 0;

        grid.add(new Label("Root operativa:"), 0, r);
        grid.add(new Label(operationRoot.toString()), 1, r++);

        grid.add(new Label("Directory selezionata:"), 0, r);
        grid.add(new Label(selectedPath.toString()), 1, r++);

        grid.add(new Label("Segmento di riferimento:"), 0, r);
        grid.add(referenceField, 1, r++);

        grid.add(new Label("Nuovo segmento:"), 0, r);
        grid.add(insertField, 1, r++);

        grid.add(before, 1, r);
        grid.add(after, 2, r++);

        getDialogPane().setContent(grid);

        // ---------- Validation ----------

        Node okButton = getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        referenceField.textProperty().addListener((obs, o, n) ->
                validate(okButton, referenceField, insertField)
        );
        insertField.textProperty().addListener((obs, o, n) ->
                validate(okButton, referenceField, insertField)
        );

        // ---------- Result ----------

        setResultConverter(button -> {
            if (button != okButtonType) {
                return null;
            }

            return new ReorderDialogResult(
                    referenceField.getText().trim(),
                    insertField.getText().trim(),
                    before.isSelected()
            );
        });
    }

    private void validate(
            Node okButton,
            TextField ref,
            TextField ins
    ) {
        okButton.setDisable(
                ref.getText().isBlank() ||
                ins.getText().isBlank()
        );
    }
}
