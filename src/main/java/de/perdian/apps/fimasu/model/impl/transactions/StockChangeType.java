package de.perdian.apps.fimasu.model.impl.transactions;

public enum StockChangeType {

    BUY(1),
    SELL(-1);

    private double factor = 0;

    private StockChangeType(double factor) {
        this.setFactor(factor);
    }

    double getFactor() {
        return this.factor;
    }
    private void setFactor(double factor) {
        this.factor = factor;
    }

}
