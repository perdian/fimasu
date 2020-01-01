package de.perdian.apps.fimasu.fx.widgets.files;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.fx.widgets.files.impl.PdfContentPane;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

class FileContentWrapperPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(FileContentWrapperPane.class);

    private ObjectProperty<FileContentPane> fileContentPane = null;

    FileContentWrapperPane(ObjectProperty<File> selectedFile, ObjectProperty<FileContentPane> fileContentPane, Preferences preferences) {
        this.setFileContentPane(fileContentPane);
        selectedFile.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                this.setCenter(new Label("No content loaded"));
                this.getFileContentPane().setValue(null);
            } else {
                log.info("Loading content for file: {}", newValue.getAbsolutePath());
                this.setCenter(new Label("Loading content for file:\n" + newValue.getName()));
                try {
                    this.loadContent(newValue);
                } catch (Exception e) {
                    log.warn("Cannot load content for file: {}", newValue, e);
                    this.setCenter(new Label("Cannot load content for file:\n" + newValue.getName()));
                    this.getFileContentPane().setValue(null);
                }
            }
        });
    }

    private void loadContent(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            PdfContentPane pdfContentDetailPane = new PdfContentPane(file);
            this.setCenter(pdfContentDetailPane);
            this.getFileContentPane().setValue(pdfContentDetailPane);
        } else {
            throw new UnsupportedOperationException("Unsupported file: " + file.getName());
        }
    }

    private ObjectProperty<FileContentPane> getFileContentPane() {
        return this.fileContentPane;
    }
    private void setFileContentPane(ObjectProperty<FileContentPane> fileContentPane) {
        this.fileContentPane = fileContentPane;
    }


}
