package de.perdian.apps.fimasu4.model.types;

public enum TransactionType {

    BUY("Buy"),
    SELL("Sell"),
    PAYOUT("Payout");

    private String title = null;

    private TransactionType(String title) {
        this.setTitle(title);
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

}
