package de.perdian.apps.fimasu.model.impl.transactions;

import de.perdian.apps.fimasu.support.quicken.model.TransactionTypeRecordItem;

public enum StockChangeType {

    BUY(1, "Buy", TransactionTypeRecordItem.BUY),
    SELL(-1, "Sell", TransactionTypeRecordItem.SELL);

    private double factor = 0;
    private String title = null;
    private TransactionTypeRecordItem transactionTypeRecordItem = null;

    private StockChangeType(double factor, String title, TransactionTypeRecordItem transactionTypeRecordItem) {
        this.setFactor(factor);
        this.setTitle(title);
        this.setTransactionTypeRecordItem(transactionTypeRecordItem);
    }

    double getFactor() {
        return this.factor;
    }
    private void setFactor(double factor) {
        this.factor = factor;
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    TransactionTypeRecordItem getTransactionTypeRecordItem() {
        return this.transactionTypeRecordItem;
    }
    private void setTransactionTypeRecordItem(TransactionTypeRecordItem transactionTypeRecordItem) {
        this.transactionTypeRecordItem = transactionTypeRecordItem;
    }

}
