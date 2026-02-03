package org.ln.directorytool;

import org.ln.directorytool.view.DirectoryToolFXView;
import org.ln.directorytool.view.SplashScreen;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DirectoryToolApp extends Application{
	
	private static HostServices hostServices;

	@Override
	public void start(Stage stage) throws Exception {
		
		SplashScreen splash = new SplashScreen();
	    StartupTask task = new StartupTask();

	    splash.bind(task);
	    splash.show();
	    
	    task.setOnSucceeded(e -> {
	        splash.close();
		
        DirectoryToolFXView view = new DirectoryToolFXView(stage);

        hostServices = getHostServices();
        Scene scene = new Scene(view, 900, 600);
//        scene.getStylesheets().add(
//            getClass().getResource("/flat-light.css").toExternalForm()
//        );

        stage.setTitle("CrocodileFX");
        stage.setScene(scene);
        stage.show();
        
	    });

	    Thread t = new Thread(task, "startup-task");
	    t.setDaemon(true);
	    t.start();
	}
	
    public static HostServices getHS() {
        return hostServices;
    }
	
    public static void main(String[] args) {
        launch();
    }

}
