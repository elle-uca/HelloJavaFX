package org.ln.hellofx;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class BaseConverterFormFX extends Application {

    private static final String DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public void start(Stage stage) {

        // ---- Model ----
        IntegerProperty baseIn = new SimpleIntegerProperty(10);
        IntegerProperty baseOut = new SimpleIntegerProperty(16);

        // ---- Controls ----
        ComboBox<Integer> baseInBox = new ComboBox<>();
        ComboBox<Integer> baseOutBox = new ComboBox<>();
        TextField numberField = new TextField();
        Label resultLabel = new Label("-");
        Button convertBtn = new Button("Converti");

        // ---- Bases 2–36 ----
        for (int i = 2; i <= 36; i++) {
            baseInBox.getItems().add(i);
            baseOutBox.getItems().add(i);
        }

        baseInBox.setValue(10);
        baseOutBox.setValue(16);

        baseIn.bind(baseInBox.valueProperty());
        baseOut.bind(baseOutBox.valueProperty());

        // ---- Dynamic validation filter ----
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText().toUpperCase();

            // allow empty
            if (text.isEmpty()) return change;

            // handle sign
            if (text.startsWith("-")) {
                text = text.substring(1);
            }

            int base = baseIn.get();
            String allowed = DIGITS.substring(0, base);

            return text.matches("[" + allowed + "]*")
                    ? change
                    : null;
        };

        numberField.setTextFormatter(new TextFormatter<>(filter));

        // Re-validate when base changes
        baseIn.addListener((obs, oldV, newV) -> {
            numberField.setText(numberField.getText());
        });

        // ---- Conversion ----
        convertBtn.setOnAction(e -> {
            try {
                NumberConverter converter = new NumberConverter();
                String result = converter.convert(
                        numberField.getText(),
                        baseIn.get(),
                        baseOut.get()
                );
                resultLabel.setText(result);
            } catch (Exception ex) {
                resultLabel.setText("Errore");
            }
        });

        // ---- Layout ----
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Base di partenza:"), 0, 0);
        grid.add(baseInBox, 1, 0);

        grid.add(new Label("Base di arrivo:"), 0, 1);
        grid.add(baseOutBox, 1, 1);

        grid.add(new Label("Numero:"), 0, 2);
        grid.add(numberField, 1, 2);

        grid.add(convertBtn, 1, 3);
        grid.add(new Label("Risultato:"), 0, 4);
        grid.add(resultLabel, 1, 4);

        stage.setScene(new Scene(grid));
        stage.setTitle("Conversione basi numeriche");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

