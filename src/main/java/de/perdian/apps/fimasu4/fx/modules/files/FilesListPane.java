package de.perdian.apps.fimasu4.fx.modules.files;

import java.io.File;

import de.perdian.apps.fimasu4.fx.FimasuPreferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;

public class FilesListPane extends BorderPane {

    private ObservableList<File> files = null;

    public FilesListPane(FimasuPreferences preferences) {

        ObservableList<File> files = FXCollections.observableArrayList();
        this.setFiles(files);

        this.setMinHeight(150);

    }

    public ObservableList<File> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<File> files) {
        this.files = files;
    }

}
