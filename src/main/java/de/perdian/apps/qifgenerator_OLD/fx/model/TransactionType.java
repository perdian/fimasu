package de.perdian.apps.qifgenerator_OLD.fx.model;

public enum TransactionType {

    BUY("Buy"),
    SELL("Sell");

    private String value = null;

    private TransactionType(String value) {
        this.setValue(value);
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    private String getValue() {
        return this.value;
    }
    private void setValue(String value) {
        this.value = value;
    }

}
