package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions.AddTransactionGroupActionEventHandler;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

class TransactionGroupsContextMenu extends ContextMenu {

    public TransactionGroupsContextMenu(ObservableList<TransactionGroup> transactionGroups) {
        MenuItem createTransactionGroupItem = new MenuItem("Add transaction group");
        createTransactionGroupItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        createTransactionGroupItem.setOnAction(new AddTransactionGroupActionEventHandler(transactionGroups));
        this.getItems().add(createTransactionGroupItem);
    }

}
