package de.perdian.apps.fimasu.quicken.model;

import de.perdian.apps.fimasu.quicken.RecordItem;

public enum TransactionTypeRecordItem implements RecordItem {

    BUY("Kauf"),
    SELL("Verkauf"),
    PAYOUT("Payout");

    private String value = null;

    private TransactionTypeRecordItem(String value) {
        this.setValue(value);
    }

    @Override
    public String toQifString() {
        return "N" + this.getValue();
    }

    private String getValue() {
        return this.value;
    }
    private void setValue(String value) {
        this.value = value;
    }

}
