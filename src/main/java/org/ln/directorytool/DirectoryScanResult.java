package org.ln.directorytool;

import java.nio.file.Path;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DirectoryScanResult {


	public final Path dir;
    public int files = 0;
    public int subDirs = 0;
    public boolean completed = false;
    
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty path = new SimpleStringProperty();
    private final StringProperty size = new SimpleStringProperty();

    public DirectoryScanResult(Path dir) {
        this.dir = dir;
        this.name.set(dir.toFile().getName());
        this.path.set(dir.toFile().getAbsolutePath());
    }
    
    public void updateSize() {
        size.set(files + " file, " + subDirs + " dir");
    }

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty pathProperty() {
		return path;
	}

	public StringProperty sizeProperty() {
		return size;
	}

    public Path getDir() {
		return dir;
	}
    
}