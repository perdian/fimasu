package de.perdian.apps.qifgenerator_OLD.fx.modules.documents;

import de.perdian.apps.qifgenerator_OLD.fx.QifGeneratorHelper;
import de.perdian.apps.qifgenerator_OLD.fx.QifGeneratorPreferences;
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

        DocumentContentPaneWrapper documentContentPaneWrapper = new DocumentContentPaneWrapper(documentFilesPane.getSelectedFileProperty());
        TitledPane contentPaneWrapper = QifGeneratorHelper.wrapInTitledPane("Content", documentContentPaneWrapper);
        GridPane.setHgrow(contentPaneWrapper, Priority.ALWAYS);
        GridPane.setVgrow(contentPaneWrapper, Priority.ALWAYS);

        this.add(filesPaneWrapper, 0, 0, 1, 1);
        this.add(contentPaneWrapper, 0, 1, 1, 1);
        this.setHgap(4);
        this.setVgap(4);

        DocumentsController documentsController = new DocumentsController();
        documentsController.setAvailableFiles(documentFilesPane.getFiles());
        documentsController.setSelectedFile(documentFilesPane.getSelectedFileProperty());
        documentsController.setDocumentContentPane(documentContentPaneWrapper.getDocumentContentPaneProperty());
        this.setDocumentsController(documentsController);

    }

    public DocumentsController getDocumentsController() {
        return this.documentsController;
    }
    private void setDocumentsController(DocumentsController documentsController) {
        this.documentsController = documentsController;
    }

}
