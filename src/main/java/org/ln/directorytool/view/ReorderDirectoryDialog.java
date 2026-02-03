package org.ln.directorytool.view;

import java.nio.file.Path;

import org.ln.directorytool.DirectoryToolController.ReorderDialogResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ReorderDirectoryDialog extends Dialog<ReorderDialogResult> {

    public ReorderDirectoryDialog(Stage owner, Path operationRoot, Path selectedPath) {

        setTitle("Riorganizza directory");
        setHeaderText("Riorganizza il percorso");
        initOwner(owner);

        ButtonType okType = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        // --- combo segmenti ---
        ComboBox<String> referenceCombo = new ComboBox<>();
        referenceCombo.setMaxWidth(Double.MAX_VALUE);

        ObservableList<String> segments = buildSegments(operationRoot, selectedPath);
        referenceCombo.setItems(segments);
        if (!segments.isEmpty()) {
            referenceCombo.getSelectionModel().selectFirst();
        }

        // --- nuovo segmento ---
        TextField insertField = new TextField();
        insertField.setPromptText("es. 2026, AgenziaEntrate, ...");

        // --- prima/dopo ---
        ToggleGroup group = new ToggleGroup();
        RadioButton before = new RadioButton("Inserisci prima");
        RadioButton after  = new RadioButton("Inserisci dopo");
        before.setToggleGroup(group);
        after.setToggleGroup(group);
        before.setSelected(true);

        // --- layout ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        int r = 0;
        grid.add(new Label("Root operativa:"), 0, r);
        grid.add(new Label(operationRoot.toString()), 1, r++, 2, 1);

        grid.add(new Label("Directory selezionata:"), 0, r);
        grid.add(new Label(selectedPath.toString()), 1, r++, 2, 1);

        grid.add(new Label("Segmento di riferimento:"), 0, r);
        grid.add(referenceCombo, 1, r++, 2, 1);

        grid.add(new Label("Nuovo segmento:"), 0, r);
        grid.add(insertField, 1, r++, 2, 1);

        grid.add(before, 1, r);
        grid.add(after, 2, r++);

        getDialogPane().setContent(grid);

        // --- validazione (ok abilitato solo se insertField non vuoto e combo selezionata) ---
        Node okBtn = getDialogPane().lookupButton(okType);
        okBtn.setDisable(true);

        Runnable validate = () -> okBtn.setDisable(
                referenceCombo.getSelectionModel().getSelectedItem() == null ||
                insertField.getText().trim().isBlank()
        );

        referenceCombo.valueProperty().addListener((obs, o, n) -> validate.run());
        insertField.textProperty().addListener((obs, o, n) -> validate.run());
        validate.run();

        // --- result ---
        setResultConverter(bt -> {
            if (bt != okType) return null;

            String ref = referenceCombo.getSelectionModel().getSelectedItem();
            String ins = insertField.getText().trim();

            return new ReorderDialogResult(
                    ref,
                    ins,
                    before.isSelected()
            );
        });
    }

    private static ObservableList<String> buildSegments(Path operationRoot, Path selectedPath) {
        Path rel = operationRoot.normalize().toAbsolutePath()
                .relativize(selectedPath.normalize().toAbsolutePath());

        var list = FXCollections.<String>observableArrayList();
        for (int i = 0; i < rel.getNameCount(); i++) {
            list.add(rel.getName(i).toString());
        }
        return list;
    }
}
