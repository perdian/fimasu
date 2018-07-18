package de.perdian.apps.qifgenerator.fxnew;

import de.perdian.apps.qifgenerator.fxnew.modules.documents.DocumentsPane;
import de.perdian.apps.qifgenerator.fxnew.modules.transactions.TransactionsPane;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

class QifGeneratorPane extends BorderPane {

    QifGeneratorPane(QifGeneratorPreferences preferences) {

        TransactionsPane transactionsPane = new TransactionsPane(preferences);
        transactionsPane.setPrefWidth(600);
        transactionsPane.setMaxHeight(Double.MAX_VALUE);
        transactionsPane.setPadding(new Insets(0, 2, 0, 0));

        DocumentsPane documentsPane = new DocumentsPane(preferences);
        documentsPane.setMaxWidth(Double.MAX_VALUE);
        documentsPane.setMaxHeight(Double.MAX_VALUE);
        documentsPane.setPadding(new Insets(0, 0, 0, 2));

        this.setLeft(transactionsPane);
        this.setCenter(documentsPane);
        this.setPadding(new Insets(4, 4, 4, 4));

    }

}
