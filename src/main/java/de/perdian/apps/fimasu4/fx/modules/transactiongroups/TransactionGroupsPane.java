package de.perdian.apps.fimasu4.fx.modules.transactiongroups;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import de.perdian.apps.fimasu4.model.TransactionGroup;
import de.perdian.apps.fimasu4.model.TransactionGroupModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Wrapper for all available (and possible) transaction groups
 *
 * @author Christian Seifert
 */

public class TransactionGroupsPane extends BorderPane {

    public TransactionGroupsPane(TransactionGroupModel transactionGroupModel) {

        MenuItem addTransactionGroupMenuItem = new MenuItem("Add transaction group");
        addTransactionGroupMenuItem.setGraphic(new FontIcon(MaterialDesignP.PLUS));
        addTransactionGroupMenuItem.setOnAction(event -> {
            TransactionGroup newTransactionGroup = new TransactionGroup();
            transactionGroupModel.getTransactionGroups().add(newTransactionGroup);
            transactionGroupModel.getSelectedTransactionGroup().setValue(newTransactionGroup);
        });
        ContextMenu tabPaneContextMenu = new ContextMenu();
        tabPaneContextMenu.getItems().add(addTransactionGroupMenuItem);

        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(Double.MAX_VALUE);
        tabPane.setContextMenu(tabPaneContextMenu);
        this.setCenter(tabPane);

        transactionGroupModel.getTransactionGroups().forEach(transactionGroup -> this.addTransactionGroupTab(transactionGroup, tabPane));
        transactionGroupModel.getTransactionGroups().addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> this.onTransactionGroupsChange(change, tabPane));
        transactionGroupModel.getSelectedTransactionGroup().addListener((o, oldValue, newValue) -> this.onSelectedTransactionGroupChanged(newValue, tabPane));

        this.setCenter(tabPane);

    }

    private void addTransactionGroupTab(TransactionGroup transactionGroup, TabPane targetPane) {
        Tab newTab = new Tab();
        newTab.setContent(new TransactionGroupPane(transactionGroup));
        newTab.textProperty().bind(transactionGroup.getTitle());
        newTab.closableProperty().bind(Bindings.greaterThan(Bindings.size(targetPane.getTabs()), 1));
        targetPane.getTabs().add(newTab);
    }

    private void onTransactionGroupsChange(Change<? extends TransactionGroup> change, TabPane targetPane) {
        while (change.next()) {
            for (TransactionGroup addedTransactionGroup : change.getAddedSubList()) {
                this.onChangedTransactionGroupAdded(addedTransactionGroup, targetPane);
            }
            for (TransactionGroup removedTransactionGroup : change.getRemoved()) {
                this.onChangedTransactionGroupRemoved(removedTransactionGroup, targetPane);
            }
        }
    }

    private void onChangedTransactionGroupAdded(TransactionGroup addedTransactionGroup, TabPane targetPane) {
        this.addTransactionGroupTab(addedTransactionGroup, targetPane);
    }

    private void onChangedTransactionGroupRemoved(TransactionGroup removedTransactionGroup, TabPane targetPane) {
        Tab tab = this.findSelectedTabForTransactionGroup(removedTransactionGroup, targetPane);
        if (tab != null) {
            targetPane.getTabs().remove(tab);
        }
    }

    private void onSelectedTransactionGroupChanged(TransactionGroup transactionGroup, TabPane tabPane) {
        Tab tab = this.findSelectedTabForTransactionGroup(transactionGroup, tabPane);
        if (tab != null) {
            tabPane.getSelectionModel().select(tab);
        }
    }

    private Tab findSelectedTabForTransactionGroup(TransactionGroup transactionGroup, TabPane tabPane) {
        for (Tab tab : tabPane.getTabs()) {
            TransactionGroup tabTransactionGroup = ((TransactionGroupPane)tab.getContent()).getTransactionGroup();
            if (tabTransactionGroup.equals(transactionGroup)) {
                return tab;
            }
        }
        return null;
    }

}
