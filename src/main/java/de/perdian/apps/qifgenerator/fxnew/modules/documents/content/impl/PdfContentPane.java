package de.perdian.apps.qifgenerator.fxnew.modules.documents.content.impl;

import java.io.File;
import java.util.ResourceBundle;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;

public class PdfContentPane extends BorderPane {

    public PdfContentPane(File file) throws Exception {

        this.setCenter(new Label("Loading PDF document..."));

        new Thread(() -> {

            SwingController swingController = new SwingController();
            swingController.setIsEmbeddedComponent(true);
            swingController.openDocument(file.getAbsolutePath());

            PropertiesManager properties = new PropertiesManager(System.getProperties(), ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_STATUSBAR, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FORMS, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_PAGENAV, true);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_UTILITY, false);
            properties.setBoolean(PropertiesManager.PROPERTY_VIEWPREF_FITWINDOW, true);

            SwingViewBuilder swingViewBuilder = new SwingViewBuilder(swingController, properties);
            javax.swing.JComponent viewerPanel = swingViewBuilder.buildViewerPanel();
            viewerPanel.revalidate();

            Platform.runLater(() -> {

                SwingNode fxSwingNode = new SwingNode();
                fxSwingNode.setContent(viewerPanel);
                fxSwingNode.addEventHandler(ScrollEvent.ANY, event -> event.consume());

                ScrollPane fxScrollPane = new ScrollPane(fxSwingNode);
                fxScrollPane.setFitToWidth(true);
                this.setCenter(fxScrollPane);

            });

        }).start();




//        FontPropertiesManager.getInstance().loadOrReadSystemFonts();

//       PropertiesManager properties = PropertiesManager.getInstance();
//       properties.getPreferences().putFloat(PropertiesManager.PROPERTY_DEFAULT_ZOOM_LEVEL, 1.25f);
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_OPEN, false);
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_SAVE, false);
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_PRINT, false);
//       // hide the status bar
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_STATUSBAR, false);
//       // hide a few toolbars, just to show how the prefered size of the viewer changes.
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, false);
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE, false);
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, false);
//       properties.getPreferences().putBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FORMS, false);


    }

}
