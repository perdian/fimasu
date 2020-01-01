package de.perdian.apps.fimasu.fx.widgets.transactiongroups;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions.AddTransactionGroupActionEventHandler;
import de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions.BackupTransactionGroupsActionEventHandler;
import de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions.RestoreTransactionGroupsActionEventHandler;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.commons.fx.execution.GuiExecutor;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

class TransactionGroupsContextMenu extends ContextMenu {

    public TransactionGroupsContextMenu(ObservableList<TransactionGroup> transactionGroups, GuiExecutor guiExecutor, Preferences preferences) {

        MenuItem addTransactionGroupItem = new MenuItem("Add transaction group", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addTransactionGroupItem.setOnAction(new AddTransactionGroupActionEventHandler(transactionGroups));
        this.getItems().add(addTransactionGroupItem);

        this.getItems().add(new SeparatorMenuItem());

        StringProperty backupDirectory = preferences.getStringProperty("transactionGroups.backupDirectory", null);
        MenuItem backupTransactionGroupsItem = new MenuItem("Backup transaction groups", new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        backupTransactionGroupsItem.setOnAction(new BackupTransactionGroupsActionEventHandler(transactionGroups, backupDirectory, guiExecutor));
        MenuItem restoreTransactionGroupsItem = new MenuItem("Restore transaction groups", new FontAwesomeIconView(FontAwesomeIcon.UPLOAD));
        restoreTransactionGroupsItem.setOnAction(new RestoreTransactionGroupsActionEventHandler(transactionGroups, backupDirectory, guiExecutor));
        this.getItems().addAll(backupTransactionGroupsItem, restoreTransactionGroupsItem);

    }

}
