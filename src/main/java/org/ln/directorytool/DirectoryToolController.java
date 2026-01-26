package org.ln.directorytool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import org.ln.directorytool.service.DirectoryStatsService;
import org.ln.directorytool.service.NetworkDirectoryScanner;
import org.ln.directorytool.util.DirectoryUtils;
import org.ln.directorytool.view.DirectoryToolFXView;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;

/**
 * Coordinates UI interactions for directory operations such as scanning,
 * deletion, moving, and reordering.
 *
 * @author Luca Noale
 */
public class DirectoryToolController {

    private final DirectoryToolFXView view;
//    private final DirectoryStatsService statsService;

    private static final Preferences prefs =
            Preferences.userRoot().node("Crocodile");
    private static final String LAST_DIR_KEY = "lastDir";
    
    private NetworkDirectoryScanner currentTask;


//    private final DirectoryReorderService reorderService =
//            new DirectoryReorderService();
//
//    private final FilesystemService filesystemService =
//            new FilesystemService();


    /**
     * Creates a controller bound to the provided view instance.
     *
     * @param crocodileView the view component backing the controller
     */
    public DirectoryToolController(DirectoryToolFXView view) {
        this.view = view;
        //this.statsService = new DirectoryStatsService();
    }

    private void startScanner(NetworkDirectoryScanner task) {

        if (currentTask != null && currentTask.isRunning()) {
            currentTask.cancel();
        }

        currentTask = task;

        Thread t = new Thread(task, "network-scan");
        t.setDaemon(true);
        t.start();
    }
 
    
    
    /* -------------------------------------------------
     *  TABLE / DIRECTORY SCAN
     * ------------------------------------------------- */

 
    public void refreshTable() {
     	String dir = view.getRootDirField().getText();
    	Path root = new File(dir).toPath();
        if (root == null) return;

        var items = view.getTableItems();
        items.clear();

        view.getProgress().setVisible(true);
        view.getProgress().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        view.setGlobalReport("Scansione rete in corso...");

        NetworkDirectoryScanner task = new NetworkDirectoryScanner(
                root,
                items,
                null,
                msg -> view.setGlobalReport(msg),
                () -> {
                    view.getProgress().setVisible(false);
                    view.setGlobalReport("Caricate " + items.size() + " directory");
                }
        );
        startScanner(task);

//        Thread t = new Thread(task, "network-scan");
//        t.setDaemon(true);
//        t.start();
    }

    
    /**
     * Executes a filtered search based on the text field and updates the table.
     */
    public void refreshSearch() {

        Path root = view.getRootDir();
        if (root == null) return;

        String searchName = view.getSearchDir();
        if (searchName.isBlank()) {
            showWarning("Inserisci un nome di directory da cercare.");
            return;
        }
        
       // System.out.println("Refresh   "+searchName);

        ObservableList<DirectoryScanResult> items = view.getTableItems();
        items.clear();

        view.getProgress().setVisible(true);
        view.getProgress().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        view.setGlobalReport("Ricerca in corso...");

        String needle = searchName.trim().toLowerCase();

        Predicate<Path> filter = p -> {
            Path name = p.getFileName();
           // System.out.println("filter   "+name);
            return name != null &&
                   name.toString().toLowerCase().equals(needle);
        };
 
       // System.out.println("filter   "+filter);
        NetworkDirectoryScanner task =
            new NetworkDirectoryScanner(
                root,
                items,
                filter,
                msg -> view.setGlobalReport(msg),
                () -> {
                    view.getProgress().setVisible(false);
                    view.setGlobalReport(
                        "Trovate " + items.size() + " directory con nome \"" + searchName + "\""
                    );
                }
            );
        startScanner(task);

    }

    
    /**
     * Opens a chooser to pick the root directory and initializes the view state.
     */
    public void chooseRootDirectory() {
    	String lastPath = prefs.get(LAST_DIR_KEY, null);

    	DirectoryChooser chooser = new DirectoryChooser();
    	chooser.setTitle("Seleziona una directory");

    	if (lastPath != null) {
    		File lastDir = new File(lastPath);
    		if (lastDir.exists() && lastDir.isDirectory()) {
    			chooser.setInitialDirectory(lastDir);
    		}
    	}

    	File dir = chooser.showDialog(view.getStage());
    	if (dir == null) {
    		return;
    	}

    	Path root = dir.toPath();
    	view.getRootDirField().setText(root.toString());
    	prefs.put(LAST_DIR_KEY, root.toString());

    	refreshTable();
//        view.getSearchDirButton().setEnabled(true);
    }

    /* -------------------------------------------------
     *  ACTIONS
     * ------------------------------------------------- */

    

    /**
     * Updates labels and optional actions when a directory row is selected.
     */
    public void onDirectorySelected() {
    	DirectoryScanResult selected = view.getSelectedItem();
       if (selected == null) {
            clearDirectoryInfo();
            return;
        }
    	Path dir = selected.dir;

        // Label 1: info directory
    	view.setSelected("Directory selezionata: " + dir.toAbsolutePath());

        // Label 2: statistiche
        try {
            DirectoryStatsService.DirStats stats =
            		DirectoryStatsService.countRecursive(dir);

            view.setDetail(
                    "Contenuto: " +
                    stats.files + " file, " +
                    stats.directories + " directory"
            );

        } catch (IOException ex) {
        	 view.setDetail("Errore lettura contenuto");
        }
        // opzionale: aggiorna menu Move Files
       // updateMoveFilesAvailability();
    }
    
    
//
//    /**
//     * Performs the primary action depending on whether a selection exists.
//     */
//    public void executeMainAction() {
//
//        if (!confirm("Sei sicuro di procedere?")) return;
//
//       // List<Path> list = crocodileView.getModel().getDirectories();
//        
//        List<Path> list = crocodileView.getModel()
//                .getRows()
//                .stream()
//                .map(r -> r.dir)
//                .toList();
//
//        if (list != null && !list.isEmpty()) {
//            delete(list);
//        } else {
//            processAllDirectoriesByName();
//        }
//    }
//
//    /* -------------------------------------------------
//     *  DELETE / EMPTY
//     * ------------------------------------------------- */
//
//    /**
//     * Deletes or empties the provided directories according to user settings.
//     *
//     * @param list directories to process
//     */
//    public void delete(List<Path> list) {
//
//        boolean deleteDir = crocodileView.getCancelButton().isSelected();
//        boolean emptyDir  = crocodileView.getEmptyButton().isSelected();
//
//        for (Path dir : list) {
//            try {
//                if (deleteDir) {
//                    DirectoryUtils.deleteDirectoryRecursively(dir);
//                } else if (emptyDir) {
//                    DirectoryUtils.emptyDirectory(dir);
//                }
//            } catch (Exception ex) {
//                showError("Errore su directory:\n" + dir, ex);
//                return;
//            }
//        }
//
//        refreshTable();
//    }
//
//    /**
//     * Processes all directories matching the search name under the selected root.
//     */
//    public void processAllDirectoriesByName() {
//
//        Path root = crocodileView.getSelectedDir();
//        String name = crocodileView.getSearchDir();
//
//        if (root == null || name == null || name.isBlank()) return;
//
//        try {
//            if (crocodileView.getCancelButton().isSelected()) {
//                DirectoryUtils.deleteAllDirectoriesNamed(root, name);
//            } else {
//                DirectoryUtils.emptyAllDirectoriesNamed(root, name);
//            }
//        } catch (Exception ex) {
//            showError("Errore operazione globale", ex);
//            return;
//        }
//
//        refreshTable();
//    }

    /* -------------------------------------------------
     *  MOVE
     * ------------------------------------------------- */

    
    /**
     * Moves files or subdirectories from the selected directory to a chosen target.
     */
    public void moveFilesFromSelectedDir() {
        DirectoryScanResult selected = view.getSelectedItem();

        if (selected == null) {
            showWarning("Seleziona una directory.");
            return;
        }
        
          Path source = selected.getDir();

        // -------------------------------------------------
        // 1) Scelta modalità spostamento (ChoiceDialog)
        // -------------------------------------------------
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(
                "Solo file",
                "Solo file",
                "Solo directory",
                "File + directory"
        );

        choiceDialog.initOwner(view.getStage());
        choiceDialog.setTitle("Modalità spostamento");
        choiceDialog.setHeaderText("Cosa vuoi spostare?");
        choiceDialog.setContentText("Modalità:");

        Optional<String> choiceRes = choiceDialog.showAndWait();
        if (choiceRes.isEmpty()) {
            return;
        }

        FileMode mode = switch (choiceRes.get()) {
            case "Solo file" -> FileMode.FILES_ONLY;
            case "Solo directory" -> FileMode.DIRS_ONLY;
            case "File + directory" -> FileMode.FILES_AND_DIRS;
            default -> throw new IllegalStateException();
        };

        // -------------------------------------------------
        // 2) Scelta directory di destinazione
        // -------------------------------------------------
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleziona directory di destinazione");
        chooser.setInitialDirectory(source.toFile());

        File targetDir = chooser.showDialog(view.getStage());
        if (targetDir == null) {
            return;
        }

        Path target = targetDir.toPath();
        if (source.equals(target)) {
            showWarning("Origine e destinazione coincidono");
            return;
        }

        // -------------------------------------------------
        // 3) Conferma finale
        // -------------------------------------------------
        if (!confirm("Conferma", "Conferma spostamento", "Confermi lo spostamento?")) {
            return;
        }

        // -------------------------------------------------
        // 4) Spostamento effettivo
        // -------------------------------------------------
        try {
            switch (mode) {
                case FILES_ONLY ->
                        DirectoryUtils.moveFiles(source, target);
                case DIRS_ONLY ->
                        DirectoryUtils.moveDirectories(source, target);
                case FILES_AND_DIRS ->
                        DirectoryUtils.moveAll(source, target);
            }
        } catch (Exception ex) {
            showError("Errore spostamento", ex);
            return;
        }

        // -------------------------------------------------
        // 5) Refresh UI
        // -------------------------------------------------
        refreshTable();
    }

    
//    
//    /**
//     * Checks whether a directory has any entries.
//     *
//     * @param dir directory to inspect
//     * @return {@code true} if the directory is empty or unreadable
//     */
//    private boolean isDirectoryEmpty(Path dir) {
//        try (var stream = Files.list(dir)) {
//            return stream.findAny().isEmpty();
//        } catch (IOException e) {
//            return true; // prudenziale
//        }
//    }
//
//    /**
//     * Enables or disables the move menu item based on the current selection.
//     */
//    public void updateMoveMenuState() {
//
//        JMenuItem moveItem = crocodileView.getMenuItemMoveFiles();
//
//        int row = crocodileView.getSelectedRow();
//        if (row < 0) {
//            moveItem.setEnabled(false);
//            return;
//        }
//        DirectoryScanResult r = crocodileView.getModel().getRow(row);
//        Path dir = r.dir;
//        
//       // Path dir = crocodileView.getModel().getDirectoryAt(row);
//        boolean enabled = !isDirectoryEmpty(dir);
//
//        moveItem.setEnabled(enabled);
//    }

    /* -------------------------------------------------
     *  DELETE SINGLE DIRECTORY (popup)
     * ------------------------------------------------- */

    /**
     * Deletes the selected directory after user confirmation and statistics checks.
     */
    public void deleteSelectedDirectory() {
        DirectoryScanResult selected = view.getSelectedItem();

        if (selected == null) {
            showWarning("Seleziona una directory.");
            return;
        }

        Path dir = selected.getDir();

        // -------------------------------------------------
        // 1) Prima conferma: cancellazione directory
        // -------------------------------------------------
        if (!confirm("Conferma cancellazione", 
        		"Vuoi cancellare la directory?", 
        		dir.toAbsolutePath().toString())){
            return;
        }
        
        // -------------------------------------------------
        // 2) Conteggio contenuto (potenzialmente lento)
        // -------------------------------------------------
        DirectoryStatsService.DirStats stats;
        try {
        	stats = DirectoryStatsService.countRecursive(dir);
        } catch (IOException ex) {
            showError("Errore nel conteggio del contenuto", ex);
            return;
        }

        // -------------------------------------------------
        // 3) Seconda conferma: directory non vuota
        // -------------------------------------------------
        if (stats.files > 0 || stats.directories > 0) {

            String msg = "Verranno eliminati:\n" +
                    stats.files + " file\n" +
                    stats.directories + " directory\n\n" +
                    "Vuoi cancellare ANCHE tutto il contenuto?" ;
            
            if (!confirm("Conferma cancellazione contenuto", 
            		"ATTENZIONE: la directory non è vuota", msg)){
                return;
            }
         }

        // -------------------------------------------------
        // 4) Cancellazione effettiva
        // -------------------------------------------------
        try {
            DirectoryUtils.deleteDirectoryRecursively(dir);
        } catch (Exception ex) {
            showError("Errore durante la cancellazione", ex);
            return;
        }

        // -------------------------------------------------
        // 5) Refresh UI 
        // -------------------------------------------------
        refreshTable();
        view.setGlobalReport(
                "Caricate " + view.getTableItems().size() + " directory");
    }
    
    /* -------------------------------------------------
     *  RENAME DIRECTORY 
     * ------------------------------------------------- */    
    
    /**
     * Renames the currently selected directory after validation.
     */
    public void renameCurrentDir() {

        DirectoryScanResult selected = view.getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona una directory.");
            return;
        }

        Path dir = selected.getDir();
        Path parent = dir.getParent();

        if (parent == null) {
            showWarning("Impossibile rinominare la directory root del filesystem.");
            return;
        }

        TextInputDialog dialog =
                new TextInputDialog(dir.getFileName().toString());

        dialog.initOwner(view.getStage());
        dialog.setTitle("Rinomina directory");
        dialog.setHeaderText("Nuovo nome directory");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String newName = result.get().trim();
        if (newName.isBlank()) {
            showWarning("Nome directory non valido.");
            return;
        }

        Path target = parent.resolve(newName);
        if (Files.exists(target)) {
            showWarning("Esiste già una directory con questo nome.");
            return;
        }

        Path currentRoot = view.getRootDir();
        boolean isRoot = dir.equals(currentRoot);

        try {
            Files.move(dir, target);
        } catch (IOException ex) {
            showError("Errore rinomina directory", ex);
            return;
        }
        System.out.println(currentRoot);
        System.out.println(dir);
        //  Solo se era la root operativa
        if (isRoot) {
            //view.setSelectedDir(target);
            view.getRootDirField().setText(target.toString());
        }

        refreshTable();
    }



    /* -------------------------------------------------
     *  ADD NEW DIRECTORY 
     * ------------------------------------------------- */     
    
    /**
     * Adds a new subdirectory inside the currently selected directory.
     */
    public void addNewDir() {
    	DirectoryScanResult selected = view.getSelectedItem();

    	if (selected == null) {
    		showWarning("Seleziona una directory.");
    		return;
    	}

    	Path parentDir = selected.getDir();

    	// -------------------------------------------------
    	// Dialog JavaFX per inserire il nome della directory
    	// -------------------------------------------------
    	TextInputDialog dialog = new TextInputDialog("");
    	dialog.initOwner(view.getStage());
    	dialog.setTitle("Nuova directory");
    	dialog.setHeaderText("Crea nuova directory");
    	dialog.setContentText("Nome:");

    	Optional<String> result = dialog.showAndWait();
    	if (result.isEmpty()) {
    		return; // annullato
    	}

    	String name = result.get().trim();
    	if (name.isBlank()) {
    		showWarning("Nome directory non valido.");
    		return;
    	}

    	Path newDir = parentDir.resolve(name);

    	// -------------------------------------------------
    	// Creazione directory
    	// -------------------------------------------------
    	try {
    		Files.createDirectory(newDir);
    	} catch (IOException ex) {
    		showError("Errore creazione directory", ex);
    		return;
    	}

    	// -------------------------------------------------
    	// Refresh UI
    	// -------------------------------------------------
    	refreshTable();
    }

    
 
//    /**
//     * Removes an intermediate directory by flattening its contents into the parent.
//     */
//   public void flattenSelectedDirectory() {
//
//	    int row = crocodileView.getSelectedRow();
//	    if (row < 0) {
//	        showWarning("Seleziona una directory.");
//	        return;
//	    }
//	    DirectoryScanResult r = crocodileView.getModel().getRow(row);
//	    Path dir = r.dir;
//
//	    //Path dir = crocodileView.getModel().getDirectoryAt(row);
//	    Path parent = dir.getParent();
//
//	    if (parent == null) {
//	        showWarning("Impossibile cancellare la directory root.");
//	        return;
//	    }
//
//            FlattenDirectoryDialog dlg =
//                    new FlattenDirectoryDialog(
//                            crocodileView,
//	                    dir.getFileName().toString(),
//	                    parent.getFileName().toString()
//	            );
//
//	    dlg.setVisible(true);
//
//	    if (dlg.getReturnStatus() != FlattenDirectoryDialog.RET_OK) {
//	        return;
//	    }
//
//	    DirectoryFlattenService service = new DirectoryFlattenService();
//
//	    try {
//	        service.flatten(dir, dlg.getStrategy());
//	    } catch (Exception ex) {
//	        showError("Errore durante l'operazione", ex);
//	        return;
//	    }
//
//	    refreshTable();
//	    crocodileView.setGlobalReport("Caricate "+crocodileView.getDirList().size()+" directory" );
//        }
//
//
//    /**
//     * Reorders a directory by inserting or replacing a path segment under the root.
//     */
//   public void reorderSelectedDirectory() {
//
//	    int row = crocodileView.getSelectedRow();
//	    if (row < 0) {
//	        showWarning("Seleziona una directory.");
//	        return;
//	    }
//
//	    // Directory selezionata
////	    Path selectedPath = crocodileView.getModel()
////	            .getDirectoryAt(row)
////	            .normalize()
////	            .toAbsolutePath();
//	    
//	    DirectoryScanResult r = crocodileView.getModel().getRow(row);
//	    Path selectedPath = r.dir.normalize().toAbsolutePath();
//
//	    // Root operativa dell'app (campo "Root dir")
//	    Path operationRoot = crocodileView.getSelectedDir()
//	            .normalize()
//	            .toAbsolutePath();
//
//	    // Sanity check: la directory deve stare sotto la root operativa
//	    if (!selectedPath.startsWith(operationRoot)) {
//	        showWarning(
//	                "La directory selezionata non è sotto la Root dir:\n" +
//	                operationRoot
//	        );
//	        return;
//	    }
//
//            // Dialog only configures UI inputs
//            ReorderDirectoryDialog dlg =
//                    new ReorderDirectoryDialog(
//	                    crocodileView,
//	                    operationRoot,
//	                    selectedPath
//	            );
//
//	    dlg.setVisible(true);
//
//	    if (dlg.getReturnStatus() != ReorderDirectoryDialog.RET_OK) {
//	        return;
//	    }
//
//            // Plan the effective move based on dialog input
//            ReorderPlan plan;
//            try {
//	        plan = reorderService.planReorder(
//	                operationRoot,
//	                selectedPath,
//	                dlg.getReferenceSegment(),
//	                dlg.getInsertedSegment(),
//	                dlg.isInsertBefore()
//	        );
//	    } catch (IllegalArgumentException ex) {
//	        showWarning(ex.getMessage());
//	        return;
//	    }
//
//            // Safety check to keep operations inside the user home directory
//            Path securityRoot = Path.of(System.getProperty("user.home"))
//                    .normalize()
//                    .toAbsolutePath();
//
//	    if (!plan.targetDir().startsWith(securityRoot)) {
//	        showWarning(
//	                "Operazione non consentita:\n" +
//	                "la directory deve restare sotto:\n" +
//	                securityRoot
//	        );
//	        return;
//	    }
//
//            // Abort if the target already exists
//            if (Files.exists(plan.targetDir())) {
//                showWarning(
//	                "La directory di destinazione esiste già:\n" +
//	                plan.targetDir()
//	        );
//	        return;
//	    }
//
//            // Present a final preview before committing the move
//            int confirm = JOptionPane.showConfirmDialog(
//                    crocodileView,
//	            "Verrà spostata la directory:\n\n" +
//	            plan.operatedDir() +
//	            "\n\nNuovo percorso:\n\n" +
//	            plan.targetDir() +
//	            "\n\nConfermi lo spostamento?",
//	            "Conferma riorganizzazione",
//	            JOptionPane.YES_NO_OPTION,
//	            JOptionPane.WARNING_MESSAGE
//	    );
//
//	    if (confirm != JOptionPane.YES_OPTION) {
//	        return;
//	    }
//
//            // Perform the planned move
//            try {
//                filesystemService.move(
//                        plan.operatedDir(),
//                        plan.targetDir()
//                );
//            } catch (IOException ex) {
//                showError("Errore durante lo spostamento", ex);
//                return;
//            }
//
//            // If the operative root changed, synchronize the view root
//            if (plan.operatedDir().equals(operationRoot)) {
//                crocodileView.setSelectedDir(plan.targetDir());
//                crocodileView.getRootDirField().setText(plan.targetDir().toString());
//            }
//
//	    refreshTable();
//	}
//



    /* -------------------------------------------------
     *  UTIL
     * ------------------------------------------------- */

    private boolean confirm(String title, String head, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(view.getStage());
        alert.setTitle(title);
        alert.setHeaderText(head);
        alert.setContentText(msg);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    private void showError(String title, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(view.getStage());
        alert.setTitle("Errore");
        alert.setHeaderText(title);
        alert.setContentText(ex != null ? ex.getMessage() : null);
        alert.showAndWait();
    }
    
    private void showWarning(String msg) {
    	Alert alert = new Alert(Alert.AlertType.WARNING);
    	alert.initOwner(view.getStage());  
    	alert.setTitle("Attenzione");
    	alert.setHeaderText(null);
    	alert.setContentText(msg);
    	alert.showAndWait();
    }


    /**
     * Indicates which file system entries should be processed during move operations.
     *
     * @author Luca Noale
     */
    public enum FileMode {
        FILES_ONLY,
        DIRS_ONLY,
        FILES_AND_DIRS
    }




    private void clearDirectoryInfo() {
    	view.setSelected("");
    	view.setDetail("");
    }





}
