package de.perdian.apps.qifgenerator.fx.widgets.previews;

import java.io.File;

import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PreviewsPane extends VBox {

    private ObservableList<File> previewFiles = null;
    private ObjectProperty<File> selectedPreviewFile = null;

    public PreviewsPane(Preferences preferences) {

        PreviewFilesPane filesPane = new PreviewFilesPane(preferences);
        filesPane.setPadding(new Insets(8, 8, 8, 8));
        this.setPreviewFiles(filesPane.getFiles());
        this.setSelectedPreviewFile(filesPane.getSelectedFileProperty());
        TitledPane filesTitledPane = new TitledPane("Files", filesPane);
        filesTitledPane.setCollapsible(false);

        PreviewContentPane contentPane = new PreviewContentPane();
        TitledPane contentTitledPane = new TitledPane("Content", contentPane);
        contentTitledPane.setCollapsible(false);
        contentTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(contentTitledPane, Priority.ALWAYS);

        this.setSpacing(8);
        this.setPadding(new Insets(8, 8, 8, 4));
        this.getChildren().addAll(filesTitledPane, contentTitledPane);

    }

    public EventHandler<KeyEvent> createNavigationKeyPressedEventHandler() {
        return event -> {
            if (event.getCode() == KeyCode.PAGE_DOWN && !event.isMetaDown() && !event.isShiftDown()) {
                int indexOfPreviewFile = this.getSelectedPreviewFile().getValue() == null ? -1 : this.getPreviewFiles().indexOf(this.getSelectedPreviewFile().getValue());
                if (indexOfPreviewFile < 0 && !this.getPreviewFiles().isEmpty()) {
                    this.getSelectedPreviewFile().setValue(this.getPreviewFiles().get(0));
                } else if ((indexOfPreviewFile + 1) < this.getPreviewFiles().size()) {
                    this.getSelectedPreviewFile().setValue(this.getPreviewFiles().get(indexOfPreviewFile + 1));
                }
            } else if (event.getCode() == KeyCode.PAGE_UP && !event.isMetaDown() && !event.isShiftDown()) {
                int indexOfPreviewFile = this.getSelectedPreviewFile().getValue() == null ? -1 : this.getPreviewFiles().indexOf(this.getSelectedPreviewFile().getValue());
                if (indexOfPreviewFile < 0 && !this.getPreviewFiles().isEmpty()) {
                    this.getSelectedPreviewFile().setValue(this.getPreviewFiles().get(this.getPreviewFiles().size() - 1));
                } else if ((indexOfPreviewFile - 1) >= 0 && !this.getPreviewFiles().isEmpty()) {
                    this.getSelectedPreviewFile().setValue(this.getPreviewFiles().get(indexOfPreviewFile - 1));
                }
            }
        };
    }

    private ObservableList<File> getPreviewFiles() {
        return this.previewFiles;
    }
    private void setPreviewFiles(ObservableList<File> previewFiles) {
        this.previewFiles = previewFiles;
    }

    private ObjectProperty<File> getSelectedPreviewFile() {
        return this.selectedPreviewFile;
    }
    private void setSelectedPreviewFile(ObjectProperty<File> selectedPreviewFile) {
        this.selectedPreviewFile = selectedPreviewFile;
    }

}
