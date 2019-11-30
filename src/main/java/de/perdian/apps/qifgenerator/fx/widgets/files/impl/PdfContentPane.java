package de.perdian.apps.qifgenerator.fx.widgets.files.impl;

import java.io.File;
import java.util.ResourceBundle;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fx.widgets.files.FileContentPane;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;

public class PdfContentPane extends BorderPane implements FileContentPane {

    private static final Logger log = LoggerFactory.getLogger(PdfContentPane.class);

    private SwingController swingController = null;
    private ScrollPane scrollPane = null;

    public PdfContentPane(File file) throws Exception {

        this.setCenter(new Label("Loading PDF document..."));

        new Thread(() -> {

            SwingController swingController = new SwingController();
            swingController.setIsEmbeddedComponent(true);
            this.setSwingController(swingController);

            PropertiesManager properties = new PropertiesManager(System.getProperties(), ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_STATUSBAR, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FORMS, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_PAGENAV, true);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, false);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_UTILITY, false);
            properties.setFloat(PropertiesManager.PROPERTY_DEFAULT_ZOOM_LEVEL, 1.25f);

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
                fxScrollPane.setStyle("-fx-background-color: transparent");
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
                this.setScrollPane(fxScrollPane);

            });

        }).start();

    }

    @Override
    public boolean scrollDocument(int direction) {
        double vValueOld = this.getScrollPane().getVvalue();
        if (vValueOld == 0 && direction < 0) {
            return false;
        } else {
            double vValueNew = vValueOld + (Math.signum(direction) * 0.2);
            double vMax = this.getScrollPane().getVmax();
            if (vValueNew < 0 || vValueNew > vMax) {
                return false;
            } else {
                this.getScrollPane().setVvalue(vValueNew);
                return true;
            }
        }
    }

    @Override
    public boolean changePage(int direction) {
        int newPageIndex = (int)(this.getSwingController().getCurrentPageNumber() + Math.signum(direction));
        if (newPageIndex >= 0 && newPageIndex < this.getSwingController().getDocument().getNumberOfPages()) {
            this.getSwingController().showPage(newPageIndex);
            this.getScrollPane().setVvalue(0);
            return true;
        } else {
            return false;
        }
    }

    private SwingController getSwingController() {
        return this.swingController;
    }
    private void setSwingController(SwingController swingController) {
        this.swingController = swingController;
    }

    private ScrollPane getScrollPane() {
        return this.scrollPane;
    }
    private void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

}
