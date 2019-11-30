package de.perdian.apps.qifgenerator.fx.widgets.previews;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fx.widgets.previews.impl.PdfContentPane;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

class PreviewContentPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(PreviewContentPane.class);

    private final ObjectProperty<PreviewDocumentPane> previewDocumentPaneProperty = new SimpleObjectProperty<>();

    PreviewContentPane(ObjectProperty<File> selectedFileProperty, Preferences preferences) {
        selectedFileProperty.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                this.setCenter(new Label("No content loaded"));
                this.getPreviewDocumentPaneProperty().setValue(null);
            } else {
                log.info("Loading content for file: {}", newValue.getAbsolutePath());
                this.setCenter(new Label("Loading content for file:\n" + newValue.getName()));
                try {
                    this.loadContent(newValue);
                } catch (Exception e) {
                    log.warn("Cannot load content for file: {}", newValue, e);
                    this.setCenter(new Label("Cannot load content for file:\n" + newValue.getName()));
                    this.getPreviewDocumentPaneProperty().setValue(null);
                }
            }
        });
    }

    private void loadContent(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            PdfContentPane pdfContentDetailPane = new PdfContentPane(file);
            this.setCenter(pdfContentDetailPane);
            this.getPreviewDocumentPaneProperty().setValue(pdfContentDetailPane);
        } else {
            throw new UnsupportedOperationException("Unsupported file: " + file.getName());
        }
    }

    ObjectProperty<PreviewDocumentPane> getPreviewDocumentPaneProperty() {
        return this.previewDocumentPaneProperty;
    }

}
