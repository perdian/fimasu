package de.perdian.apps.fimasu.fx.widgets.files;

import java.io.File;
import java.util.Optional;

import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilesPane extends VBox {

    private ObservableList<File> files = null;
    private ObjectProperty<File> selectedFile = null;
    private ObjectProperty<FileContentPane> fileContentPane = null;

    public FilesPane(ObservableList<File> files, ObjectProperty<File> selectedFile, Preferences preferences) {

        this.setFiles(files);
        this.setSelectedFile(selectedFile);

        FilesListPane filesListPane = new FilesListPane(files, selectedFile, preferences);
        filesListPane.setMinHeight(250);
        filesListPane.setMaxHeight(250);
        filesListPane.setPadding(new Insets(8, 8, 8, 8));
        TitledPane filesListTitledPane = new TitledPane("Files", filesListPane);
        filesListTitledPane.setCollapsible(false);

        ObjectProperty<FileContentPane> fileContentPane = new SimpleObjectProperty<>();
        FileContentWrapperPane fileContentWrapperPane = new FileContentWrapperPane(selectedFile, fileContentPane, preferences);
        TitledPane fileContentWrapperTitledPane = new TitledPane("Content", fileContentWrapperPane);
        fileContentWrapperTitledPane.setCollapsible(false);
        fileContentWrapperTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(fileContentWrapperTitledPane, Priority.ALWAYS);
        this.setFileContentPane(fileContentPane);

        this.setSpacing(8);
        this.setPadding(new Insets(8, 8, 8, 4));
        this.getChildren().addAll(filesListTitledPane, fileContentWrapperTitledPane);

    }

    public EventHandler<KeyEvent> createNavigationKeyPressedEventHandler() {
        return event -> {
            if (event.getCode() == KeyCode.PAGE_DOWN) {
                if (event.isShiftDown()) {
                    Optional.ofNullable(this.getFileContentPane().getValue()).ifPresent(pane -> pane.changePage(1));
                } else if (event.isControlDown()) {
                    Optional.ofNullable(this.getFileContentPane().getValue()).ifPresent(pane -> pane.scrollDocument(1));
                } else {
                    int indexOfPreviewFile = this.getSelectedFile().getValue() == null ? -1 : this.getFiles().indexOf(this.getSelectedFile().getValue());
                    if (indexOfPreviewFile < 0 && !this.getFiles().isEmpty()) {
                        this.getSelectedFile().setValue(this.getFiles().get(0));
                    } else if ((indexOfPreviewFile + 1) < this.getFiles().size()) {
                        this.getSelectedFile().setValue(this.getFiles().get(indexOfPreviewFile + 1));
                    }
                }
                event.consume();
            } else if (event.getCode() == KeyCode.PAGE_UP) {
                if (event.isShiftDown()) {
                    Optional.ofNullable(this.getFileContentPane().getValue()).ifPresent(pane -> pane.changePage(-1));
                } else if (event.isControlDown()) {
                    Optional.ofNullable(this.getFileContentPane().getValue()).ifPresent(pane -> pane.scrollDocument(-1));
                } else {
                    int indexOfPreviewFile = this.getSelectedFile().getValue() == null ? -1 : this.getFiles().indexOf(this.getSelectedFile().getValue());
                    if (indexOfPreviewFile < 0 && !this.getFiles().isEmpty()) {
                        this.getSelectedFile().setValue(this.getFiles().get(this.getFiles().size() - 1));
                    } else if ((indexOfPreviewFile - 1) >= 0 && !this.getFiles().isEmpty()) {
                        this.getSelectedFile().setValue(this.getFiles().get(indexOfPreviewFile - 1));
                    }
                }
                event.consume();
            }
        };
    }

    private ObservableList<File> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<File> files) {
        this.files = files;
    }

    private ObjectProperty<File> getSelectedFile() {
        return this.selectedFile;
    }
    private void setSelectedFile(ObjectProperty<File> selectedFile) {
        this.selectedFile = selectedFile;
    }

    private ObjectProperty<FileContentPane> getFileContentPane() {
        return this.fileContentPane;
    }
    private void setFileContentPane(ObjectProperty<FileContentPane> fileContentPane) {
        this.fileContentPane = fileContentPane;
    }

}
