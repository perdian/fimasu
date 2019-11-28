package de.perdian.apps.qifgenerator.fx;

import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.TransactionGroupsPane;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class QifGeneratorPane extends GridPane {

    QifGeneratorPane(ObservableList<TransactionGroup> transactionGroups, Preferences preferences) {

        TransactionGroupsPane transactionGroupsPane = new TransactionGroupsPane(transactionGroups, preferences);
        GridPane.setHgrow(transactionGroupsPane, Priority.ALWAYS);

        this.add(transactionGroupsPane, 0, 0, 1, 1);

    }

}
