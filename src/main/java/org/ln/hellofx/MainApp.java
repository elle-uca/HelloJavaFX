package org.ln.hellofx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) { 
        Button b = new Button("Hello JavaFX");
        b.setOnAction(e -> b.setText("NOOR ready 😎"));

        VBox root = new VBox(20, b);
        root.setStyle("-fx-padding: 40; -fx-alignment: center;");

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Hello JavaFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
