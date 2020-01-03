package de.perdian.apps.fimasu.model.impl.transactions;

public enum StockChangeType {

    BUY(1, "Buy"),
    SELL(-1, "Sell");

    private double factor = 0;
    private String title = null;

    private StockChangeType(double factor, String title) {
        this.setFactor(factor);
        this.setTitle(title);
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

}
