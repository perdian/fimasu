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

    public ContentPane(ObjectProperty<File> selectedFileProperty) {
        selectedFileProperty.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                this.setCenter(new Label("No content loaded"));
            } else {
                log.info("Loading content for file: {}", newValue.getAbsolutePath());
                this.setCenter(new Label("Loading content for file:\n" + newValue.getName()));
                try {
                    this.loadContent(newValue);
                } catch (Exception e) {
                    log.warn("Cannot load content for file: {}", newValue, e);
                    this.setCenter(new Label("Cannot load content for file:\n" + newValue.getName()));
                }
            }
        });

    }

    private void loadContent(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            this.setCenter(new PdfContentPane(file));
        } else {
            throw new UnsupportedOperationException("Unsupported file: " + file.getName());
        }
    }

}
