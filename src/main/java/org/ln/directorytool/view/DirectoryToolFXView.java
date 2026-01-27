package org.ln.directorytool.view;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.ln.directorytool.DirectoryScanResult;
import org.ln.directorytool.DirectoryToolController;
import org.ln.directorytool.action.ChooseRootDirCommand;
import org.ln.directorytool.action.ExecuteCommandAction;
import org.ln.directorytool.action.FxActionAdapter;
import org.ln.directorytool.action.RefreshSearchCommand;

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

    private final Label globalReportLabel = new Label("global");
    private final Label selectedLabel = new Label("selected");
    private final Label detailSelLabel = new Label("detail");

    private final ProgressBar progress = new ProgressBar();
    private Stage stage;
    
    
    private final ObservableList<DirectoryScanResult> tableItems =
            FXCollections.observableArrayList();


	private final TableView<DirectoryScanResult> table =
            new TableView<>(tableItems);
    
    public DirectoryToolFXView(Stage stage) {
		this.controller = new DirectoryToolController(this);
		this.stage = stage;
	    progress.setProgress(0);
	    progress.setVisible(false);
		rootDirField.setEditable(false);
		rootDirButton.setOnAction(new FxActionAdapter(
				new ChooseRootDirCommand(controller)));
		searchDirButton.setOnAction(new FxActionAdapter(
				new RefreshSearchCommand(controller)));
		actionButton.setOnAction(new FxActionAdapter(
				new ExecuteCommandAction(controller)));		
		buildLayout();
		initTable();
		initPopup();
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
         
        GridPane status = new GridPane();
        status.setPadding(new Insets(10));
        ColumnConstraints colLeft = new ColumnConstraints();
        colLeft.setHgrow(Priority.ALWAYS);  

        ColumnConstraints colRight = new ColumnConstraints();
        colRight.setHgrow(Priority.NEVER);  

        status.getColumnConstraints().addAll(colLeft, colRight);

        status.add(globalReportLabel, 		0, 0);
        status.add(progress,          		1, 0);

        status.add(selectedLabel,     		0, 1, 2, 1);
        status.add(detailSelLabel,    		0, 2, 2, 1);
        
        setTop(form);
        setCenter(table);
        setBottom(status);
    }

    
    private void initTable() {
        TableColumn<DirectoryScanResult, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<DirectoryScanResult, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(c -> c.getValue().pathProperty());

        TableColumn<DirectoryScanResult, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(c -> c.getValue().sizeProperty());
 
        table.getColumns().setAll(List.of(pathCol, nameCol, sizeCol) );
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
           controller.onDirectorySelected();
        });
    }

    private void initPopup() {
		ContextMenu menu = new ContextMenu();

		MenuItem add = new MenuItem("Add New Dir");
		MenuItem rename = new MenuItem("Rename Current Dir");
		MenuItem move = new MenuItem("Move Files To...");
		MenuItem delete = new MenuItem("Delete Directory");
		MenuItem deleteIntermediate = new MenuItem("Delete Intermediate Directory");
		MenuItem reorder = new MenuItem("Reorder Directory...");

		menu.getItems().addAll(reorder, add, rename, move, delete, deleteIntermediate);

		table.setContextMenu(menu);
		
		add.setOnAction(e -> 	controller.addNewDir());
		rename.setOnAction(e -> controller.renameCurrentDir());
		delete.setOnAction(e -> controller.deleteSelectedDirectory());
		move.setOnAction(e -> 	controller.moveFilesFromSelectedDir());
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

	public void setGlobalReport(String st) {
		globalReportLabel.setText(st);
	}

	public void setSelected(String st) {
		selectedLabel.setText(st);
	}

	public void setDetail(String st) {
		detailSelLabel.setText(st);
	}
	
	public DirectoryScanResult getSelectedItem() {
		return table.getSelectionModel().getSelectedItem();
	}

	public boolean isCancelSelected() {
		return cancelButton.isSelected();
		
	}

	public Path getSelectedDir() {
		return getSelectedItem().getDir();
	}

	public Path getRootDir() {
     	String dir = getRootDirField().getText();
    	Path root = new File(dir).toPath();
		return root;
	}



	public String getSearchDir() {
		return searchDirField.getText().trim();
	}
	
}
