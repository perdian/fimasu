package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import de.perdian.apps.qifgenerator.model.TransactionGroup;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class TransactionGroupKeyPressedEventHandler implements EventHandler<KeyEvent> {

    private TransactionGroup transactionGroup = null;

    TransactionGroupKeyPressedEventHandler(TransactionGroup transactionGroup) {
        this.setTransactionGroup(transactionGroup);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && event.isMetaDown()) {
            new TransactionGroupExportActionEventHandler(this.getTransactionGroup()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        }
    }

    private TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}
