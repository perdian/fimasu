package de.perdian.apps.qifgenerator.fx.modules.documents;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fx.modules.documents.impl.PdfContentDetailPane;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

class DocumentContentPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(DocumentContentPane.class);

    private DocumentContentDetailPane documentContentDetailPane = null;

    DocumentContentPane(ObjectProperty<File> selectedFileProperty) {
        selectedFileProperty.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                this.setCenter(new Label("No content loaded"));
                this.setContentDetailPane(null);
            } else {
                log.info("Loading content for file: {}", newValue.getAbsolutePath());
                this.setCenter(new Label("Loading content for file:\n" + newValue.getName()));
                try {
                    this.loadContent(newValue);
                } catch (Exception e) {
                    log.warn("Cannot load content for file: {}", newValue, e);
                    this.setCenter(new Label("Cannot load content for file:\n" + newValue.getName()));
                    this.setContentDetailPane(null);
                }
            }
        });
    }

    private void loadContent(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            PdfContentDetailPane pdfContentDetailPane = new PdfContentDetailPane(file);
            this.setCenter(pdfContentDetailPane);
            this.setContentDetailPane(pdfContentDetailPane);
        } else {
            throw new UnsupportedOperationException("Unsupported file: " + file.getName());
        }
    }

    void scrollDocument(int direction) {
        DocumentContentDetailPane documentContentDetailPane = this.getContentDetailPane();
        if (documentContentDetailPane != null) {
            documentContentDetailPane.scrollDocument(direction);
        }
    }

    void changePage(int direction) {
        DocumentContentDetailPane documentContentDetailPane = this.getContentDetailPane();
        if (documentContentDetailPane != null) {
            documentContentDetailPane.changePage(direction);
        }
    }

    private DocumentContentDetailPane getContentDetailPane() {
        return this.documentContentDetailPane;
    }
    private void setContentDetailPane(DocumentContentDetailPane documentContentDetailPane) {
        this.documentContentDetailPane = documentContentDetailPane;
    }

}
