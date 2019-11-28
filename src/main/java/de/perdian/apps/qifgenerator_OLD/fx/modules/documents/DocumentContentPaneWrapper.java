package de.perdian.apps.qifgenerator_OLD.fx.modules.documents;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator_OLD.fx.modules.documents.impl.PdfContentPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

class DocumentContentPaneWrapper extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(DocumentContentPaneWrapper.class);

    private ObjectProperty<DocumentContentPane> documentContentPaneProperty = new SimpleObjectProperty<>();

    DocumentContentPaneWrapper(ObjectProperty<File> selectedFileProperty) {
        selectedFileProperty.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                this.setCenter(new Label("No content loaded"));
                this.getDocumentContentPaneProperty().setValue(null);
            } else {
                log.info("Loading content for file: {}", newValue.getAbsolutePath());
                this.setCenter(new Label("Loading content for file:\n" + newValue.getName()));
                try {
                    this.loadContent(newValue);
                } catch (Exception e) {
                    log.warn("Cannot load content for file: {}", newValue, e);
                    this.setCenter(new Label("Cannot load content for file:\n" + newValue.getName()));
                    this.getDocumentContentPaneProperty().setValue(null);
                }
            }
        });
    }

    private void loadContent(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            PdfContentPane pdfContentDetailPane = new PdfContentPane(file);
            this.setCenter(pdfContentDetailPane);
            this.getDocumentContentPaneProperty().setValue(pdfContentDetailPane);
        } else {
            throw new UnsupportedOperationException("Unsupported file: " + file.getName());
        }
    }

    ObjectProperty<DocumentContentPane> getDocumentContentPaneProperty() {
        return this.documentContentPaneProperty;
    }
    void setDocumentContentPaneProperty(ObjectProperty<DocumentContentPane> documentContentPaneProperty) {
        this.documentContentPaneProperty = documentContentPaneProperty;
    }

}
