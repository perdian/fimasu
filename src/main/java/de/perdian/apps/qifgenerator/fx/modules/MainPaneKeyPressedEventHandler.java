package de.perdian.apps.qifgenerator.fx.modules;

import de.perdian.apps.qifgenerator.fx.modules.documents.DocumentsController;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class MainPaneKeyPressedEventHandler implements EventHandler<KeyEvent> {

    private DocumentsController documentsController = null;

    MainPaneKeyPressedEventHandler(DocumentsController documentsController) {
        this.setDocumentsController(documentsController);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.PAGE_UP && event.isShiftDown()) {
            this.getDocumentsController().changePage(-1);
        } else if (event.getCode() == KeyCode.PAGE_UP && event.isMetaDown()) {
            this.getDocumentsController().changeDocument(-1);
        } else if (event.getCode() == KeyCode.PAGE_UP) {
            this.getDocumentsController().scrollCurrentDocument(-1);
        } else if (event.getCode() == KeyCode.PAGE_DOWN && event.isShiftDown()) {
            this.getDocumentsController().changePage(1);
        } else if (event.getCode() == KeyCode.PAGE_DOWN && event.isMetaDown()) {
            this.getDocumentsController().changeDocument(1);
        } else if (event.getCode() == KeyCode.PAGE_DOWN) {
            this.getDocumentsController().scrollCurrentDocument(1);
        }
    }

    private DocumentsController getDocumentsController() {
        return this.documentsController;
    }
    private void setDocumentsController(DocumentsController documentsController) {
        this.documentsController = documentsController;
    }

}