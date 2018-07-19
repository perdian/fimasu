package de.perdian.apps.qifgenerator.fx.modules.documents.content.impl;

import java.io.File;
import java.util.ResourceBundle;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;

public class PdfContentPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(PdfContentPane.class);

    public PdfContentPane(File file) throws Exception {

        this.setCenter(new Label("Loading PDF document..."));

        new Thread(() -> {

            SwingController swingController = new SwingController();
            swingController.setIsEmbeddedComponent(true);

            PropertiesManager properties = new PropertiesManager(System.getProperties(), ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_STATUSBAR, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FORMS, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_PAGENAV, true);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_UTILITY, false);
            properties.setFloat(PropertiesManager.PROPERTY_DEFAULT_ZOOM_LEVEL, 1.2f);

            SwingViewBuilder swingViewBuilder = new SwingViewBuilder(swingController, properties);
            javax.swing.JComponent viewerPanel = swingViewBuilder.buildViewerPanel();
            viewerPanel.revalidate();

            log.debug("Rendering PDF document: {}", file.getAbsolutePath());
            swingController.openDocument(file.getAbsolutePath());

            Platform.runLater(() -> {

                SwingNode fxSwingNode = new SwingNode();
                fxSwingNode.setContent(viewerPanel);
                fxSwingNode.addEventHandler(ScrollEvent.ANY, event -> event.consume());

                ScrollPane fxScrollPane = new ScrollPane(fxSwingNode);
                fxScrollPane.setFitToWidth(true);
                fxScrollPane.addEventFilter(ScrollEvent.ANY, event -> {
                    if (event.isShiftDown() || event.isMetaDown() || event.isShortcutDown() || event.isControlDown()) {
                        // Do nothing and let the event bubble up
                    } else {
                        fxScrollPane.setVvalue(fxScrollPane.getVvalue() + (event.getDeltaY() * -0.01d));
                        event.consume();
                    }
                });
                this.setCenter(fxScrollPane);

            });

        }).start();

    }

}
