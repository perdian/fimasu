package de.perdian.apps.qifgenerator.fx.modules.documents.content;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fx.modules.documents.content.impl.PdfContentPane;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ContentPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(ContentPane.class);

    private ContentDetailPane contentDetailPane = null;

    public ContentPane(ObjectProperty<File> selectedFileProperty) {
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
            PdfContentPane pdfContentPane = new PdfContentPane(file);
            this.setCenter(pdfContentPane);
            this.setContentDetailPane(pdfContentPane);
        } else {
            throw new UnsupportedOperationException("Unsupported file: " + file.getName());
        }
    }

    public void scrollDocument(int direction) {
        ContentDetailPane contentDetailPane = this.getContentDetailPane();
        if (contentDetailPane != null) {
            contentDetailPane.scrollDocument(direction);
        }
    }

    public void changePage(int direction) {
        ContentDetailPane contentDetailPane = this.getContentDetailPane();
        if (contentDetailPane != null) {
            contentDetailPane.changePage(direction);
        }
    }

    private ContentDetailPane getContentDetailPane() {
        return this.contentDetailPane;
    }
    private void setContentDetailPane(ContentDetailPane contentDetailPane) {
        this.contentDetailPane = contentDetailPane;
    }

}
