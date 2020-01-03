package de.perdian.apps.fimasu.fx.widgets.transactions.actions;

import de.perdian.apps.fimasu.model.Transaction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MoveTransactionActionEventHandler implements EventHandler<ActionEvent> {

    private Transaction transaction = null;
    private ObservableList<Transaction> allTransactions = null;
    private int direction = 0;

    public MoveTransactionActionEventHandler(Transaction transaction, ObservableList<Transaction> allTransactions, int direction) {
        this.setTransaction(transaction);
        this.setAllTransactions(allTransactions);
        this.setDirection(direction);
    }

    @Override
    public void handle(ActionEvent event) {
        int currentIndex = this.getAllTransactions().indexOf(this.getTransaction());
        this.getAllTransactions().remove(this.getTransaction());
        if (this.getDirection() < 0) {
            this.getAllTransactions().add(Math.max(0, currentIndex + this.getDirection()), this.getTransaction());
        } else if (this.getDirection() >= 0) {
            this.getAllTransactions().add(Math.min(this.getAllTransactions().size(), currentIndex + this.getDirection()), this.getTransaction());
        }
    }

    private Transaction getTransaction() {
        return this.transaction;
    }
    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    private ObservableList<Transaction> getAllTransactions() {
        return this.allTransactions;
    }
    private void setAllTransactions(ObservableList<Transaction> allTransactions) {
        this.allTransactions = allTransactions;
    }

    private int getDirection() {
        return this.direction;
    }
    private void setDirection(int direction) {
        this.direction = direction;
    }

}
