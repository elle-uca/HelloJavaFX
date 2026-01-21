package org.ln.directorytool;

import java.nio.file.Path;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DirectoryToolFXView extends BorderPane {

   // private final DirectoryToolController controller;

    private final TextField rootDirField = new TextField();
    private final TextField searchDirField = new TextField();

    private final Button rootDirButton = new Button("Cerca");
    private final Button searchDirButton = new Button("Refresh");
    private final Button actionButton = new Button("Go");

    private final RadioButton emptyButton = new RadioButton("Svuota");
    private final RadioButton cancelButton = new RadioButton("Cancella");

    private final ToggleGroup actionGroup = new ToggleGroup();

    private final Label globalReportLabel = new Label();
    private final Label selectedLabel = new Label();
    private final Label detailSelLabel = new Label();

    private final ProgressBar progress = new ProgressBar();

    private final ObservableList<Path> dirList = FXCollections.observableArrayList();

    private final TableView<Path> table = new TableView<>(dirList);
    
    
    
    public DirectoryToolFXView() {
		super();
		buildLayout();
		initTable();
	}

    private void initTable() {

        TableColumn<Path, String> nameCol = new TableColumn<>("Directory");
        nameCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getFileName().toString())
        );

        TableColumn<Path, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().toString())
        );

        table.getColumns().setAll(nameCol, pathCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            //controller.onDirectorySelected();
        });
        
        ContextMenu menu = new ContextMenu();

        MenuItem add = new MenuItem("Add New Dir");
        MenuItem rename = new MenuItem("Rename Current Dir");
        MenuItem move = new MenuItem("Move Files To...");
        MenuItem delete = new MenuItem("Delete Directory");
        MenuItem deleteIntermediate = new MenuItem("Delete Intermediate Directory");
        MenuItem reorder = new MenuItem("Reorder Directory...");

        menu.getItems().addAll(reorder, add, rename, move, delete, deleteIntermediate);

        table.setContextMenu(menu);
    }

	private void buildLayout() {

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        form.add(new Label("Root dir"), 0, 0);
        form.add(rootDirField, 1, 0);
        form.add(rootDirButton, 2, 0);

        form.add(new Label("Dir da cercare"), 0, 1);
        form.add(searchDirField, 1, 1);
        form.add(searchDirButton, 2, 1);
        
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();

        // La colonna 2 (indice 1) deve crescere sempre
        col2.setHgrow(Priority.ALWAYS);

        form.getColumnConstraints().addAll(col1, col2);

        emptyButton.setToggleGroup(actionGroup);
        cancelButton.setToggleGroup(actionGroup);
        emptyButton.setSelected(true);

        HBox radios = new HBox(10, emptyButton, cancelButton, actionButton);
        form.add(radios, 1, 2, 2, 1);

        setTop(form);
        setCenter(table);

        VBox status = new VBox(5, globalReportLabel, progress, selectedLabel, detailSelLabel);
        status.setPadding(new Insets(10));
        setBottom(status);
    }

}
