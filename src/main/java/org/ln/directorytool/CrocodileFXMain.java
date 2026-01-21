package org.ln.directorytool;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CrocodileFXMain extends Application {

    @Override
    public void start(Stage stage) {

      //  DirectoryToolController controller = new DirectoryToolController(); // lo stesso tuo
        DirectoryToolFXView view = new DirectoryToolFXView();

        Scene scene = new Scene(view, 900, 600);
//        scene.getStylesheets().add(
//            getClass().getResource("/noor-dark.css").toExternalForm()
//        );
        
        stage.setTitle("CrocodileFX");
        stage.setScene(scene);
        stage.show();
        System.out.println(System.getProperty("javafx.version"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
