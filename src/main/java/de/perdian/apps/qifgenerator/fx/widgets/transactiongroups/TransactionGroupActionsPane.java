package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

class TransactionGroupActionsPane extends GridPane {

    TransactionGroupActionsPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder, Preferences preferences) {

        this.add(componentBuilder.createLabel("Transaction group title"), 0, 0, 1, 1);
        this.add(componentBuilder.createTextField(transactionGroup.getTitle()).width(200d).focusTraversable(false).get(), 0, 1, 1, 1);
        this.add(componentBuilder.createLabel("Account name"), 1, 0, 1, 1);
        this.add(componentBuilder.createTextField(transactionGroup.getAccount()).focusTraversable(false).get(), 1, 1, 1, 1);
        this.setVgap(2);
        this.setHgap(4);
        this.setPadding(new Insets(8, 8, 8, 8));

    }

}
