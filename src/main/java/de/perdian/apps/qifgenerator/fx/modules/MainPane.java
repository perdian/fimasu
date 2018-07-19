package de.perdian.apps.qifgenerator.fx.modules;

import de.perdian.apps.qifgenerator.fx.QifGeneratorPreferences;
import de.perdian.apps.qifgenerator.fx.modules.documents.DocumentsPane;
import de.perdian.apps.qifgenerator.fx.modules.transactions.TransactionsPane;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class MainPane extends BorderPane {

    public MainPane(QifGeneratorPreferences preferences) {

        DocumentsPane documentsPane = new DocumentsPane(preferences);
        documentsPane.setMaxWidth(Double.MAX_VALUE);
        documentsPane.setMaxHeight(Double.MAX_VALUE);
        documentsPane.setPadding(new Insets(0, 0, 0, 2));

        ComponentBuilder transactionComponentBuilder = new ComponentBuilder();
        transactionComponentBuilder.addOnKeyPressedEventHandler(new MainPaneKeyPressedEventHandler(documentsPane.getDocumentsController()));

        TransactionsPane transactionsPane = new TransactionsPane(preferences, transactionComponentBuilder);
        transactionsPane.setPrefWidth(800);
        transactionsPane.setMaxHeight(Double.MAX_VALUE);
        transactionsPane.setPadding(new Insets(0, 2, 0, 0));

        this.setLeft(transactionsPane);
        this.setCenter(documentsPane);
        this.setPadding(new Insets(4, 4, 4, 4));

    }

}
