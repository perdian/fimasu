package de.perdian.apps.qifgenerator.fx;

import de.perdian.apps.qifgenerator.fx.modules.documents.DocumentsController;
import de.perdian.apps.qifgenerator.fx.modules.documents.DocumentsPane;
import de.perdian.apps.qifgenerator.fx.modules.transactions.TransactionsPane;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

class QifGeneratorPane extends BorderPane {

    QifGeneratorPane(QifGeneratorPreferences preferences) {

        DocumentsPane documentsPane = new DocumentsPane(preferences);
        documentsPane.setMaxWidth(Double.MAX_VALUE);
        documentsPane.setMaxHeight(Double.MAX_VALUE);
        documentsPane.setPadding(new Insets(0, 0, 0, 2));

        ComponentBuilder transactionComponentBuilder = new ComponentBuilder();
        transactionComponentBuilder.addOnKeyPressedEventHandler(new QifGeneratorDocumentsControllerKeyPressedEventHandler(documentsPane.getDocumentsController()));

        TransactionsPane transactionsPane = new TransactionsPane(preferences, transactionComponentBuilder);
        transactionsPane.setPrefWidth(800);
        transactionsPane.setMaxHeight(Double.MAX_VALUE);
        transactionsPane.setPadding(new Insets(0, 2, 0, 0));

        this.setLeft(transactionsPane);
        this.setCenter(documentsPane);
        this.setPadding(new Insets(4, 4, 4, 4));

    }

    static class QifGeneratorDocumentsControllerKeyPressedEventHandler implements EventHandler<KeyEvent> {

        private DocumentsController documentsController = null;

        QifGeneratorDocumentsControllerKeyPressedEventHandler(DocumentsController documentsController) {
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

}
