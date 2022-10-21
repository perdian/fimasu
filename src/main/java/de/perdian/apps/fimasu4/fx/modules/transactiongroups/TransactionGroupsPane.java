package de.perdian.apps.fimasu4.fx.modules.transactiongroups;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import de.perdian.apps.fimasu4.model.FimasuModel;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.Node;
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

    public TransactionGroupsPane(FimasuModel fimasuModel) {

        MenuItem addTransactionGroupMenuItem = new MenuItem("Add transaction group");
        addTransactionGroupMenuItem.setGraphic(new FontIcon(MaterialDesignP.PLUS));
        addTransactionGroupMenuItem.setOnAction(event -> {
            TransactionGroup newTransactionGroup = new TransactionGroup();
            fimasuModel.getTransactionGroups().add(newTransactionGroup);
            fimasuModel.getSelectedTransactionGroup().setValue(newTransactionGroup);
        });
        ContextMenu tabPaneContextMenu = new ContextMenu();
        tabPaneContextMenu.getItems().add(addTransactionGroupMenuItem);

        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(Double.MAX_VALUE);
        tabPane.setContextMenu(tabPaneContextMenu);
        this.setCenter(tabPane);

        fimasuModel.getTransactionGroups().forEach(transactionGroup -> this.addTransactionGroupTab(transactionGroup, fimasuModel.getSelectedTransactionGroup().getValue(), tabPane));
        fimasuModel.getTransactionGroups().addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> this.onTransactionGroupsChange(change, tabPane));
        fimasuModel.getSelectedTransactionGroup().addListener((o, oldValue, newValue) -> this.onSelectedTransactionGroupChanged(newValue, tabPane));

        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> change) -> {
            while (change.next()) {
                for (Tab removedTab : change.getRemoved()) {
                    if (removedTab.getContent() instanceof TransactionGroupPane) {
                        fimasuModel.getTransactionGroups().remove(((TransactionGroupPane)removedTab.getContent()).getTransactionGroup());
                    }
                }
            }
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            Node selectedContentNode = newValue == null ? null : newValue.getContent();
            if (selectedContentNode instanceof TransactionGroupPane) {
                fimasuModel.getSelectedTransactionGroup().setValue(((TransactionGroupPane)selectedContentNode).getTransactionGroup());
            }
        });

    }

    private void addTransactionGroupTab(TransactionGroup transactionGroup, TransactionGroup selectedTransactionGroup, TabPane targetPane) {
        TransactionGroupPane newGroupPane = new TransactionGroupPane(transactionGroup);
        newGroupPane.setPadding(new Insets(10, 10, 10, 10));
        Tab newTab = new Tab();
        newTab.setContent(newGroupPane);
        newTab.textProperty().bind(transactionGroup.getTitle());
        newTab.closableProperty().bind(Bindings.greaterThan(Bindings.size(targetPane.getTabs()), 1));
        targetPane.getTabs().add(newTab);
        if (transactionGroup.equals(selectedTransactionGroup)) {
            targetPane.getSelectionModel().select(newTab);
        }
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
        this.addTransactionGroupTab(addedTransactionGroup, null, targetPane);
    }

    private void onChangedTransactionGroupRemoved(TransactionGroup removedTransactionGroup, TabPane targetPane) {
        Tab tab = this.findTabForTransactionGroup(removedTransactionGroup, targetPane);
        if (tab != null) {
            targetPane.getTabs().remove(tab);
        }
    }

    private void onSelectedTransactionGroupChanged(TransactionGroup transactionGroup, TabPane tabPane) {
        Tab tab = this.findTabForTransactionGroup(transactionGroup, tabPane);
        if (tab != null) {
            tabPane.getSelectionModel().select(tab);
        }
    }

    private Tab findTabForTransactionGroup(TransactionGroup transactionGroup, TabPane tabPane) {
        for (Tab tab : tabPane.getTabs()) {
            TransactionGroup tabTransactionGroup = ((TransactionGroupPane)tab.getContent()).getTransactionGroup();
            if (tabTransactionGroup.equals(transactionGroup)) {
                return tab;
            }
        }
        return null;
    }

}
