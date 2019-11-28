package de.perdian.apps.qifgenerator.fx;

import de.perdian.apps.qifgenerator.fx.widgets.previews.PreviewsPane;
import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.TransactionGroupsPane;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class QifGeneratorPane extends GridPane {

    QifGeneratorPane(ObservableList<TransactionGroup> transactionGroups, Preferences preferences) {

        TransactionGroupsPane transactionGroupsPane = new TransactionGroupsPane(transactionGroups, preferences);
        transactionGroupsPane.setMinSize(800, 200);
        GridPane.setVgrow(transactionGroupsPane, Priority.ALWAYS);

        PreviewsPane previewsPane = new PreviewsPane();
        GridPane.setHgrow(previewsPane, Priority.ALWAYS);
        GridPane.setVgrow(previewsPane, Priority.ALWAYS);

        this.add(transactionGroupsPane, 0, 0, 1, 1);
        this.add(new Separator(Orientation.VERTICAL), 1, 0, 1, 1);
        this.add(previewsPane, 2, 0, 1, 1);

    }

}
