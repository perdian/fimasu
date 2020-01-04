package de.perdian.apps.fimasu.fx.widgets.transactiongroups;

import java.io.File;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions.ExportAsQifActionEventHandler;
import de.perdian.apps.fimasu.fx.widgets.transactions.actions.ImportFromFilesActionEventHandler;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.model.impl.transactions.PayoutTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.execution.GuiExecutor;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class TransactionGroupToolBar extends ToolBar {

    public TransactionGroupToolBar(TransactionGroup transactionGroup, ObservableList<File> files, GuiExecutor guiExecutor, ComponentBuilder componentBuilder, Preferences preferences) {

        Button addBuySellTransactionButton = new Button("Buy/Sell", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addBuySellTransactionButton.setOnAction(event -> {
            Transaction newTransaction = new StockChangeTransaction();
            newTransaction.getPersistent().setValue(Boolean.TRUE);
            transactionGroup.getTransactions().add(newTransaction);
        });
        Button addPayoutTransactionButton = new Button("Payout", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addPayoutTransactionButton.setOnAction(event -> transactionGroup.getTransactions().add(new PayoutTransaction()));
        HBox addTransactionButtonBox = new HBox(0, addBuySellTransactionButton);
        this.getItems().addAll(addTransactionButtonBox, addPayoutTransactionButton);

        HBox separatorBox = new HBox();
        HBox.setHgrow(separatorBox, Priority.ALWAYS);
        this.getItems().add(separatorBox);

        Button importFromFilesButton = new Button("Import from files", new FontAwesomeIconView(FontAwesomeIcon.UPLOAD));
        importFromFilesButton.setOnAction(new ImportFromFilesActionEventHandler(transactionGroup.getTransactions(), files, guiExecutor));
        Button exportAsQifButton = new Button("Export as QIF", new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        exportAsQifButton.setOnAction(new ExportAsQifActionEventHandler(transactionGroup, guiExecutor));
        this.getItems().addAll(importFromFilesButton, exportAsQifButton);

    }

}