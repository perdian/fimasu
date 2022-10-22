package de.perdian.apps.fimasu4.fx.modules.transactions;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import de.perdian.apps.fimasu4.fx.modules.transactions.actions.AddTransactionEventHandler;
import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import de.perdian.apps.fimasu4.model.types.TransactionType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class TransactionsToolBar extends ToolBar {

    TransactionsToolBar(ObjectProperty<TransactionGroup> selectedTransactionGroup, ObservableList<File> importFiles) {

        ComponentFactory componentFactory = new ComponentFactory();

        Button addBuySellTransactionButton = componentFactory.createButton("Buy/Sell", MaterialDesignP.PLUS, new AddTransactionEventHandler(selectedTransactionGroup, TransactionType.BUY));
        addBuySellTransactionButton.disableProperty().bind(selectedTransactionGroup.isNull());
        Button addPayoutTransactionButton = componentFactory.createButton("Payout", MaterialDesignP.PLUS, new AddTransactionEventHandler(selectedTransactionGroup, TransactionType.PAYOUT));
        addPayoutTransactionButton.disableProperty().bind(selectedTransactionGroup.isNull());
        this.getItems().addAll(addBuySellTransactionButton, addPayoutTransactionButton);

        HBox separatorBox = new HBox();
        HBox.setHgrow(separatorBox, Priority.ALWAYS);
        this.getItems().add(separatorBox);

        Button importFromFilesButton = componentFactory.createButton("Import from files", MaterialDesignA.APPLICATION_IMPORT, event -> this.doImportFromFiles(selectedTransactionGroup.getValue(), importFiles));
        importFromFilesButton.disableProperty().bind(Bindings.isEmpty(importFiles));

        Button exportAsQifButton = componentFactory.createButton("Export as QIF", MaterialDesignA.APPLICATION_EXPORT, event -> this.doExportAsQif(selectedTransactionGroup.getValue()));
        TransactionGroup initialTransactionGroup = selectedTransactionGroup.getValue();
        BooleanProperty targetFileAvaiableProperty = new SimpleBooleanProperty(initialTransactionGroup == null ? false : StringUtils.isNotEmpty(initialTransactionGroup.getExportFileName().getValue()));
        BooleanProperty transactionsAvailableProperty = new SimpleBooleanProperty(initialTransactionGroup == null ? false : !initialTransactionGroup.getTransactions().isEmpty());
        BooleanBinding exportButtonDisabledBinding = targetFileAvaiableProperty.and(transactionsAvailableProperty).not();
        exportAsQifButton.disableProperty().bind(exportButtonDisabledBinding);

        this.getItems().addAll(importFromFilesButton, exportAsQifButton);

        ChangeListener<String> targetFileAvailableChangeListener = (o, oldValue, newValue) -> targetFileAvaiableProperty.setValue(StringUtils.isNotEmpty(newValue));
        ListChangeListener<Object> transactionsAvailableChangeListener = change -> transactionsAvailableProperty.setValue(!change.getList().isEmpty());
        if (selectedTransactionGroup.getValue() != null) {
            selectedTransactionGroup.addListener((o, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.getExportFileName().removeListener(targetFileAvailableChangeListener);
                    oldValue.getTransactions().removeListener(transactionsAvailableChangeListener);
                }
                if (newValue == null) {
                    targetFileAvaiableProperty.setValue(false);
                    transactionsAvailableProperty.setValue(false);
                } else {
                    targetFileAvaiableProperty.setValue(StringUtils.isNotEmpty(newValue.getExportFileName().getValue()));
                    transactionsAvailableProperty.setValue(!newValue.getTransactions().isEmpty());
                    newValue.getExportFileName().addListener(targetFileAvailableChangeListener);
                    newValue.getTransactions().addListener(transactionsAvailableChangeListener);
                }
            });
        }

    }

    private void doExportAsQif(TransactionGroup transactionGroup) {
        throw new UnsupportedOperationException();
    }

    private void doImportFromFiles(TransactionGroup transactionGroup, ObservableList<File> importFiles) {
        throw new UnsupportedOperationException();
    }

}
