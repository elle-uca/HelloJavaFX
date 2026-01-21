package org.ln.directorytool;

import java.util.List;

import org.ln.directorytool.action.ChooseRootDirCommand;
import org.ln.directorytool.action.FxActionAdapter;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DirectoryToolFXView extends BorderPane {

   private final DirectoryToolController controller;

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
    private Stage stage;
    
    
    private final ObservableList<DirectoryScanResult> tableItems =
            FXCollections.observableArrayList();


	private final TableView<DirectoryScanResult> table =
            new TableView<>(tableItems);
    
    public DirectoryToolFXView(Stage stage) {
		this.controller = new DirectoryToolController(this);
		this.stage = stage;
		buildLayout();
		initTable();
		
		ContextMenu menu = new ContextMenu();

		MenuItem add = new MenuItem("Add New Dir");
		MenuItem rename = new MenuItem("Rename Current Dir");
		MenuItem move = new MenuItem("Move Files To...");
		MenuItem delete = new MenuItem("Delete Directory");
		MenuItem deleteIntermediate = new MenuItem("Delete Intermediate Directory");
		MenuItem reorder = new MenuItem("Reorder Directory...");

		menu.getItems().addAll(reorder, add, rename, move, delete, deleteIntermediate);

		table.setContextMenu(menu);

		rootDirButton.setOnAction(new FxActionAdapter(new ChooseRootDirCommand(controller)));
		
//		add.setOnAction(e -> controller.addDirectory());
//		rename.setOnAction(e -> controller.renameDirectory());
//		delete.setOnAction(e -> controller.deleteDirectory());

	}

    

	private void buildLayout() {

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        form.add(new Label("Root dir"), 	0, 0);
        form.add(rootDirField, 				1, 0, 2, 1 );
        form.add(rootDirButton, 			3, 0);

        form.add(new Label("Dir search"), 	0, 1);
        form.add(searchDirField, 			1, 1, 2, 1 );
        form.add(searchDirButton, 			3, 1);

        emptyButton.setToggleGroup(actionGroup);
        cancelButton.setToggleGroup(actionGroup);
        emptyButton.setSelected(true);

        form.add(emptyButton, 				1, 2);
        form.add(cancelButton, 				2, 2);
        form.add(actionButton, 				3, 2);
        
//        HBox radios = new HBox(10, emptyButton, cancelButton, actionButton);
//        form.add(radios, 1, 2, 2, 1);
        rootDirButton.setPrefWidth(100.0);
        searchDirButton.setPrefWidth(100.0);
        actionButton.setPrefWidth(100.0);
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        ColumnConstraints col4 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col3.setHgrow(Priority.ALWAYS);
        col4.setFillWidth(true);
        form.getColumnConstraints().addAll(col1, col2, col3, col4);
        

        setTop(form);
        setCenter(table);

        VBox status = new VBox(5, globalReportLabel, progress, selectedLabel, detailSelLabel);
        status.setPadding(new Insets(10));
        setBottom(status);
    }

    
    private void initTable() {
       
        TableColumn<DirectoryScanResult, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<DirectoryScanResult, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(c -> c.getValue().pathProperty());

        TableColumn<DirectoryScanResult, Number> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(c -> c.getValue().sizeProperty());
 
        table.getColumns().setAll(List.of(nameCol, pathCol, sizeCol) );
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
           // controller.onDirectorySelected();
        });
    }



	public ProgressBar getProgress() {
		return progress;
	}



	public TextField getRootDirField() {
		return rootDirField;
	}
	
    public ObservableList<DirectoryScanResult> getTableItems() {
		return tableItems;
	}


	public Stage getStage() {
		return stage;
	}

	public Object setGlobalReport(String msg) {
		// TODO Auto-generated method stub
		return "";
	}

}
