package de.perdian.apps.qifgenerator_OLD.fx.modules.documents;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public class DocumentsController {

    private ObservableList<File> availableFiles = null;
    private ObjectProperty<File> selectedFile = null;
    private ObjectProperty<DocumentContentPane> documentContentPane = null;

    public void changeDocument(int direction) {
        int maxIndex = this.getAvailableFiles().size();
        if (maxIndex > 0) {
            int currentIndex = this.getAvailableFiles().indexOf(this.getSelectedFile().getValue());
            if (currentIndex < 0) {
                this.getSelectedFile().setValue(this.getAvailableFiles().get(0));
            } else {
                int newIndex = currentIndex + direction;
                if (newIndex >= 0 && newIndex < maxIndex) {
                    this.getSelectedFile().setValue(this.getAvailableFiles().get(newIndex));
                }
            }
        }
    }

    public void changePage(int direction) {
        if (this.getDocumentContentPane().getValue() == null || !this.getDocumentContentPane().getValue().changePage(direction)) {
            this.changeDocument(direction);
        }
    }

    public void scrollDocument(int direction) {
        if (this.getDocumentContentPane().getValue() == null || !this.getDocumentContentPane().getValue().scrollDocument(direction)) {
            this.changePage(direction);
        }
    }

    ObservableList<File> getAvailableFiles() {
        return this.availableFiles;
    }
    void setAvailableFiles(ObservableList<File> availableFiles) {
        this.availableFiles = availableFiles;
    }

    ObjectProperty<File> getSelectedFile() {
        return this.selectedFile;
    }
    void setSelectedFile(ObjectProperty<File> selectedFile) {
        this.selectedFile = selectedFile;
    }

    ObjectProperty<DocumentContentPane> getDocumentContentPane() {
        return this.documentContentPane;
    }
    void setDocumentContentPane(ObjectProperty<DocumentContentPane> documentContentPane) {
        this.documentContentPane = documentContentPane;
    }

}
