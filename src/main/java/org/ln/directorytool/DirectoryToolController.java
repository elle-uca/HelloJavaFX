package org.ln.directorytool;

import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javafx.scene.control.ProgressBar;
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

    /* -------------------------------------------------
     *  TABLE / DIRECTORY SCAN
     * ------------------------------------------------- */

 
    public void refreshTable() {
    	
    	String dir = view.getRootDirField().getText();
    	Path root = new File(dir).toPath();
        if (root == null) return;
//
        var items = view.getTableItems();
        items.clear();

        view.getProgress().setVisible(true);
        view.getProgress().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
//        view.setGlobalReport("Scansione rete in corso...");

        NetworkDirectoryScanner task = new NetworkDirectoryScanner(
                root,
                items,
                msg -> view.setGlobalReport(msg),
                () -> {
                    view.getProgress().setVisible(false);
                    view.setGlobalReport("Caricate " + items.size() + " directory");
                }
        );
        

        Thread t = new Thread(task, "network-scan");
        t.setDaemon(true);
        t.start();
    }
//
//
//
//
//    void displayDirectory(Path root) {
//        try {
//            Files.walk(root)
//                 .filter(Files::isDirectory)
//                 .filter(p -> !p.equals(root))
//                 .forEach(crocodileView.getDirList()::add);
//        } catch (IOException ex) {
//            showError("Errore lettura directory", ex);
//        }
//    }
//
//    void displayDirectorySearch(Path root) {
//        String searchName = crocodileView.getSearchDir();
//        crocodileView.getDirList().clear();
//
//        try {
//            Files.walk(root)
//                 .filter(Files::isDirectory)
//                 .filter(p -> p.getFileName().toString().equals(searchName))
//                 .forEach(crocodileView.getDirList()::add);
//        } catch (IOException ex) {
//            showError("Errore ricerca directory", ex);
//        }
//    }
//
//    /* -------------------------------------------------
//     *  ACTIONS
//     * ------------------------------------------------- */
//
//
//    
//    /**
//     * Executes a filtered search based on the text field and updates the table.
//     */
//    public void refreshSearch() {
//        if (crocodileView.getSelectedDir() == null) return;
//
//        crocodileView.setSearchDir(
//                crocodileView.getSearchDirField().getText());
//
//        displayDirectorySearch(crocodileView.getSelectedDir());
//        crocodileView.getActionButton().setEnabled(true);
//    }
//
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

    	    // 🔹 Aggiorna stato applicazione
    	    //view.setSelectedDir(root);
    	    view.getRootDirField().setText(root.toString());
    	    prefs.put(LAST_DIR_KEY, root.toString());

    	    // 🔹 Avvia scansione
    	   // refreshTable();
    	 
        refreshTable();
//        view.getSearchDirButton().setEnabled(true);
        

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
//
//    /* -------------------------------------------------
//     *  MOVE
//     * ------------------------------------------------- */
//
//    /**
//     * Moves files or subdirectories from the selected directory to a chosen target.
//     */
//    public void moveFilesFromSelectedDir() {
//
//        int row = crocodileView.getSelectedRow();
//        if (row < 0) return;
//
//        //Path source = crocodileView.getModel().getDirectoryAt(row);
//        DirectoryScanResult r = crocodileView.getModel().getRow(row);
//        Path source = r.dir;
//
//        Object[] options = {
//                "Solo file",
//                "Solo directory",
//                "File + directory"
//        };
//
//        int choice = JOptionPane.showOptionDialog(
//                crocodileView,
//                "Cosa vuoi spostare?",
//                "Modalità spostamento",
//                JOptionPane.DEFAULT_OPTION,
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                options,
//                options[0]
//        );
//
//        if (choice < 0) return;
//
//        FileMode mode = switch (choice) {
//            case 0 -> FileMode.FILES_ONLY;
//            case 1 -> FileMode.DIRS_ONLY;
//            case 2 -> FileMode.FILES_AND_DIRS;
//            default -> throw new IllegalStateException();
//        };
//
//        JFileChooser fc = new JFileChooser(source.toFile());
//        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//
//        if (fc.showOpenDialog(crocodileView) != JFileChooser.APPROVE_OPTION) {
//            return;
//        }
//
//        Path target = fc.getSelectedFile().toPath();
//        if (source.equals(target)) {
//            showWarning("Origine e destinazione coincidono");
//            return;
//        }
//
//        if (!confirm("Confermi lo spostamento?")) return;
//
//        try {
//            switch (mode) {
//                case FILES_ONLY -> DirectoryUtils.moveFiles(source, target);
//                case DIRS_ONLY -> DirectoryUtils.moveDirectories(source, target);
//                case FILES_AND_DIRS -> DirectoryUtils.moveAll(source, target);
//            }
//        } catch (Exception ex) {
//            showError("Errore spostamento", ex);
//            return;
//        }
//
//        refreshTable();
//    }
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
//
//    /* -------------------------------------------------
//     *  DELETE SINGLE DIRECTORY (popup)
//     * ------------------------------------------------- */
//
//    /**
//     * Deletes the selected directory after user confirmation and statistics checks.
//     */
//   public  void deleteSelectedDirectory() {
//
//        int row = crocodileView.getSelectedRow();
//        if (row < 0) {
//            showWarning("Seleziona una directory.");
//            return;
//        }
//
//        //Path dir = crocodileView.getModel().getDirectoryAt(row);
//        DirectoryScanResult r = crocodileView.getModel().getRow(row);
//        Path dir = r.dir;
//
//        // First confirmation: deletion of the directory itself
//        int confirmDir = JOptionPane.showConfirmDialog(
//                crocodileView,
//                "Vuoi cancellare la directory?\n\n" + dir.toAbsolutePath(),
//                "Conferma cancellazione",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE
//        );
//
//        if (confirmDir != JOptionPane.YES_OPTION) {
//            return;
//        }
//
//        // Compute statistics only when the directory is not empty
//        DirectoryStatsService.DirStats stats;
//        try {
//            stats = statsService.countRecursive(dir);
//        } catch (IOException ex) {
//            showError("Errore nel conteggio del contenuto", ex);
//            return;
//        }
//
//        // Second confirmation: warn about deleting contents
//        if (stats.files > 0 || stats.directories > 0) {
//
//            int confirmContent = JOptionPane.showConfirmDialog(
//                    crocodileView,
//                    "ATTENZIONE: la directory non è vuota.\n\n" +
//                    "Verranno eliminati:\n" +
//                    "(" + stats.files + " file, " +
//                    stats.directories + " directory)\n\n" +
//                    "Vuoi cancellare ANCHE tutto il contenuto?",
//                    "Conferma cancellazione contenuto",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.WARNING_MESSAGE
//            );
//
//            if (confirmContent != JOptionPane.YES_OPTION) {
//                return;
//            }
//        }
//
//        // Execute the deletion after confirmations
//        try {
//            DirectoryUtils.deleteDirectoryRecursively(dir);
//        } catch (Exception ex) {
//            showError("Errore durante la cancellazione", ex);
//            return;
//        }
//
//        refreshTable();
//        crocodileView.setGlobalReport("Caricate "+crocodileView.getDirList().size()+" directory" );
//    }
//
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
//
//
//
//
//
//
//
//    /* -------------------------------------------------
//     *  UTIL
//     * ------------------------------------------------- */
//
//    private boolean confirm(String msg) {
//        return JOptionPane.showConfirmDialog(
//                crocodileView,
//                msg,
//                "Conferma",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE
//        ) == JOptionPane.YES_OPTION;
//    }
//
//    private void showError(String title, Exception ex) {
//        JOptionPane.showMessageDialog(
//                crocodileView,
//                title + "\n\n" + ex.getMessage(),
//                "Errore",
//                JOptionPane.ERROR_MESSAGE
//        );
//    }
//
//    private void showWarning(String msg) {
//        JOptionPane.showMessageDialog(
//                crocodileView,
//                msg,
//                "Attenzione",
//                JOptionPane.WARNING_MESSAGE
//        );
//    }
//
////    private static class DirCount {
////        int files;
////        int dirs;
////    }
////
////    private DirCount countFilesAndDirs(Path root) throws IOException {
////        DirCount c = new DirCount();
////        Files.walk(root).forEach(p -> {
////            if (p.equals(root)) return;
////            if (Files.isDirectory(p)) c.dirs++;
////            else c.files++;
////        });
////        return c;
////    }
//
//
//
//
//    /**
//     * Adds a new subdirectory inside the currently selected directory.
//     */
//    public void addNewDir() {
//        int row = crocodileView.getSelectedRow();
//        if (row < 0) {
//            showWarning("Seleziona una directory.");
//            return;
//        }
//
//        DirectoryScanResult r = crocodileView.getModel().getRow(row);
//        Path parentDir = r.dir;
//       // Path parentDir = crocodileView.getModel().getDirectoryAt(row);
//
//        FileNameDialog dialog = new FileNameDialog(crocodileView, "");
//        dialog.setVisible(true);
//
//        if (dialog.getReturnStatus() == FileNameDialog.RET_CANCEL) {
//            return;
//        }
//
//        String name = dialog.getText();
//        if (name == null || name.isBlank()) {
//            showWarning("Nome directory non valido.");
//            return;
//        }
//
//        Path newDir = parentDir.resolve(name);
//
//        try {
//            Files.createDirectory(newDir);
//        } catch (IOException ex) {
//            showError("Errore creazione directory", ex);
//            return;
//        }
//
//        refreshTable();
//    }
//
//    /**
//     * Renames the currently selected directory after validation.
//     */
//    public void renameCurrentDir() {
//
//        int row = crocodileView.getSelectedRow();
//        if (row < 0) {
//            showWarning("Seleziona una directory.");
//            return;
//        }
//
//        DirectoryScanResult r = crocodileView.getModel().getRow(row);
//        Path dir = r.dir;
//        //Path dir = crocodileView.getModel().getDirectoryAt(row);
//        Path parent = dir.getParent();
//
//        if (parent == null) {
//            showWarning("Impossibile rinominare la directory root.");
//            return;
//        }
//
//        FileNameDialog dialog =
//                new FileNameDialog(crocodileView, dir.getFileName().toString());
//        dialog.setVisible(true);
//
//        if (dialog.getReturnStatus() == FileNameDialog.RET_CANCEL) {
//            return;
//        }
//
//        String newName = dialog.getText();
//        if (newName == null || newName.isBlank()) {
//            showWarning("Nome directory non valido.");
//            return;
//        }
//
//        Path target = parent.resolve(newName);
//
//        if (Files.exists(target)) {
//            showWarning("Esiste già una directory con questo nome.");
//            return;
//        }
//
//        try {
//            Files.move(dir, target);
//        } catch (IOException ex) {
//            showError("Errore rinomina directory", ex);
//            return;
//        }
//
//        refreshTable();
//    }
//
//    /**
//     * Updates labels and optional actions when a directory row is selected.
//     */
//    public void onDirectorySelected() {
//
//        int row = crocodileView.getSelectedRow();
//        if (row < 0) {
//            clearDirectoryInfo();
//            return;
//        }
//
//        //Path dir = crocodileView.getModel().getDirectoryAt(row);
//    	DirectoryScanResult r = crocodileView.getModel().getRow(row);
//    	Path dir = r.dir;
//
//        // Label 1: info directory
//        crocodileView.setSelected("Directory selezionata: " + dir.toAbsolutePath());
//
//        // Label 2: statistiche
//        try {
//            DirectoryStatsService.DirStats stats =
//                    statsService.countRecursive(dir);
//
//            crocodileView.setDetail(
//                    "Contenuto: " +
//                    stats.files + " file, " +
//                    stats.directories + " directory"
//            );
//
//        } catch (IOException ex) {
////            crocodileView.getInfoLabel()
////                    .setText("Errore lettura contenuto");
//        }
//
//        // opzionale: aggiorna menu Move Files
//       // updateMoveFilesAvailability();
//    }
//
//    private void clearDirectoryInfo() {
////        crocodileView.getReportLabel().setText("");
////        crocodileView.getInfoLabel().setText("");
//    }



}
