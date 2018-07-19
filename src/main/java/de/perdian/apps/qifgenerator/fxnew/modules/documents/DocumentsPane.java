package de.perdian.apps.qifgenerator.fxnew.modules.documents;

import de.perdian.apps.qifgenerator.fxnew.QifGeneratorHelper;
import de.perdian.apps.qifgenerator.fxnew.QifGeneratorPreferences;
import de.perdian.apps.qifgenerator.fxnew.modules.documents.content.ContentPane;
import de.perdian.apps.qifgenerator.fxnew.modules.documents.files.FilesPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DocumentsPane extends GridPane {

    public DocumentsPane(QifGeneratorPreferences preferences) {

        FilesPane filesPane = new FilesPane(preferences);
        filesPane.setMinHeight(250);
        filesPane.setMaxHeight(250);
        TitledPane filesPaneWrapper = QifGeneratorHelper.wrapInTitledPane("Files", filesPane);

        ContentPane contentPane = new ContentPane(filesPane.selectedFile());
        TitledPane contentPaneWrapper = QifGeneratorHelper.wrapInTitledPane("Content", contentPane);
        GridPane.setHgrow(contentPaneWrapper, Priority.ALWAYS);
        GridPane.setVgrow(contentPaneWrapper, Priority.ALWAYS);

        this.add(filesPaneWrapper, 0, 0, 1, 1);
        this.add(contentPaneWrapper, 0, 1, 1, 1);
        this.setHgap(4);
        this.setVgap(4);

    }

}
