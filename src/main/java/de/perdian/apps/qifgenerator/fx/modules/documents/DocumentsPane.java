package de.perdian.apps.qifgenerator.fx.modules.documents;

import de.perdian.apps.qifgenerator.fx.QifGeneratorHelper;
import de.perdian.apps.qifgenerator.fx.QifGeneratorPreferences;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DocumentsPane extends GridPane {

    private DocumentsController documentsController = null;

    public DocumentsPane(QifGeneratorPreferences preferences) {

        DocumentFilesPane documentFilesPane = new DocumentFilesPane(preferences);
        documentFilesPane.setMinHeight(250);
        documentFilesPane.setMaxHeight(250);
        TitledPane filesPaneWrapper = QifGeneratorHelper.wrapInTitledPane("Files", documentFilesPane);

        DocumentContentPane documentContentPane = new DocumentContentPane(documentFilesPane.selectedFile());
        TitledPane contentPaneWrapper = QifGeneratorHelper.wrapInTitledPane("Content", documentContentPane);
        GridPane.setHgrow(contentPaneWrapper, Priority.ALWAYS);
        GridPane.setVgrow(contentPaneWrapper, Priority.ALWAYS);

        this.add(filesPaneWrapper, 0, 0, 1, 1);
        this.add(contentPaneWrapper, 0, 1, 1, 1);
        this.setHgap(4);
        this.setVgap(4);
        this.setDocumentsController(new DocumentsController() {

            @Override
            public void scrollCurrentDocument(int direction) {
                documentContentPane.scrollDocument(direction);
            }

            @Override
            public void changeDocument(int direction) {
                documentFilesPane.changeDocument(direction);
            }

            @Override
            public void changePage(int direction) {
                documentContentPane.changePage(direction);
            }

        });

    }

    public DocumentsController getDocumentsController() {
        return this.documentsController;
    }
    private void setDocumentsController(DocumentsController documentsController) {
        this.documentsController = documentsController;
    }

}
