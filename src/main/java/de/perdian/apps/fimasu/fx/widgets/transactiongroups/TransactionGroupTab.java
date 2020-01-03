package de.perdian.apps.fimasu.fx.widgets.transactiongroups;

import java.io.File;

import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.execution.GuiExecutor;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

class TransactionGroupTab extends Tab {

    private TransactionGroup transactionGroup = null;

    TransactionGroupTab(ObservableList<TransactionGroup> transactionGroups, TransactionGroup transactionGroup, ObservableList<File> files, GuiExecutor guiExecutor, ComponentBuilder componentBuilder, Preferences preferences) {
        componentBuilder.addListener(component -> component.setOnKeyPressed(new TransactionGroupKeyPressedEventHandler(() -> transactionGroup, files, guiExecutor)));
        this.textProperty().bind(transactionGroup.getTitle());
        this.setOnCloseRequest(event -> {
            if (transactionGroups.size() > 1) {
                Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Delete transaction group");
                confirmationAlert.setHeaderText("Delete transaction group");
                confirmationAlert.setContentText("Really delete the transaction group?");
                if (confirmationAlert.showAndWait().get().equals(ButtonType.OK)) {
                    transactionGroups.remove(transactionGroup);
                } else {
                    event.consume();
                }
            } else {
                event.consume();
            }
        });
        this.setTransactionGroup(transactionGroup);
        this.setContent(new TransactionGroupPane(transactionGroup, files, guiExecutor, componentBuilder, preferences));
    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}