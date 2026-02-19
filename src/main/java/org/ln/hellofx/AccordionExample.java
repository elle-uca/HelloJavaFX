package org.ln.hellofx;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AccordionExample extends Application {

	 private final Accordion accordion = new Accordion();
	    private final Map<String, TitledPane> panes = new HashMap<>();

	    @Override
	    public void start(Stage stage) {

	        accordion.setPadding(new Insets(10));

	        Button btnSearch  = new Button("Add Search");
	        Button btnFilters = new Button("Add Filters");
	        Button btnDetails = new Button("Add Details");

	        btnSearch.setOnAction(e -> addPane("search"));
	        btnFilters.setOnAction(e -> addPane("filters"));
	        btnDetails.setOnAction(e -> addPane("details"));

	        HBox buttons = new HBox(10, btnSearch, btnFilters, btnDetails);
	        buttons.setPadding(new Insets(10));

	        BorderPane root = new BorderPane();
	        root.setTop(buttons);
	        root.setCenter(accordion);

	        stage.setScene(new Scene(root, 500, 400));
	        stage.setTitle("Dynamic Accordion");
	        stage.show();
	    }

	    private void addPane(String key) {

	        // Se già esiste, espandilo
	        if (panes.containsKey(key)) {
	            accordion.setExpandedPane(panes.get(key));
	            return;
	        }

	        TitledPane pane = createPane(key);

	        panes.put(key, pane);
	        accordion.getPanes().add(pane);

	        // Apri automaticamente il nuovo
	        accordion.setExpandedPane(pane);
	    }

	    private TitledPane createPane(String key) {

	        TitledPane pane = new TitledPane();

	        switch (key) {
	            case "search" -> {
	                pane.setText("Search");
	                pane.setContent(new VBox(10,
	                        new Label("Search field:"),
	                        new TextField()
	                ));
	            }

	            case "filters" -> {
	                pane.setText("Filters");
	                pane.setContent(new VBox(10,
	                        new CheckBox("Only active"),
	                        new CheckBox("Only duplicates")
	                ));
	            }

	            case "details" -> {
	                pane.setText("Details");
	                pane.setContent(new TextArea("Details here..."));
	            }
	        }

	        return pane;
	    }

	    public static void main(String[] args) {
	        launch();
	    }
	}
