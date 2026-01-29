package org.ln.directorytool;

import org.ln.directorytool.view.DirectoryToolFXView;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DirectoryToolApp extends Application{
	
	 private static HostServices hostServices;

	@Override
	public void start(Stage stage) throws Exception {
       // DirectoryToolController controller = new DirectoryToolController(); // lo stesso tuo
        DirectoryToolFXView view = new DirectoryToolFXView(stage);

        hostServices = getHostServices();
        Scene scene = new Scene(view, 900, 600);
//        scene.getStylesheets().add(
//            getClass().getResource("/flat-light.css").toExternalForm()
//        );

        stage.setTitle("CrocodileFX");
        stage.setScene(scene);
        stage.show();
		
	}
	
    public static HostServices getHS() {
        return hostServices;
    }
	
    public static void main(String[] args) {
        launch();
    }

}
