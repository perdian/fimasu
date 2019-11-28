package de.perdian.apps.qifgenerator_OLD.fx.modules;

import de.perdian.apps.qifgenerator_OLD.fx.modules.documents.DocumentsController;
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
        if (event.getCode() == KeyCode.PAGE_UP) {
            this.handleKeyEventPage(event, -1);
        } else if (event.getCode() == KeyCode.PAGE_DOWN) {
            this.handleKeyEventPage(event, 1);
        }
    }

    private void handleKeyEventPage(KeyEvent event, int direction) {
        if (event.isMetaDown()) {
            this.getDocumentsController().changeDocument(direction);
        } else if (event.isShiftDown()) {
            this.getDocumentsController().changePage(direction);
        } else {
            this.getDocumentsController().scrollDocument(direction);
        }
    }

    private DocumentsController getDocumentsController() {
        return this.documentsController;
    }
    private void setDocumentsController(DocumentsController documentsController) {
        this.documentsController = documentsController;
    }

}
