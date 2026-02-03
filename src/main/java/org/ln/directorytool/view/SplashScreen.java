package org.ln.directorytool.view;

import java.util.Objects;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen extends Stage {

    private final ProgressBar progressBar = new ProgressBar();
    private final Label statusLabel = new Label("Avvio...");
    private final ImageView logoView;

    public SplashScreen() {

        Image logo = new Image(
            Objects.requireNonNull(
                getClass().getResourceAsStream("/images/logo.png")
            )
        );

        logoView = new ImageView(logo);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(220);

        progressBar.setPrefWidth(300);

        VBox root = new VBox(20,
                logoView,
                statusLabel,
                progressBar
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(root);
        setScene(scene);
        setResizable(false);
        initStyle(StageStyle.UNDECORATED); // 🔥 splash vero
    }

    public void bind(Task<?> task) {
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
    }
}


