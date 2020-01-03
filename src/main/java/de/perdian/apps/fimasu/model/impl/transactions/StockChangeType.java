package de.perdian.apps.fimasu.model.impl.transactions;

public enum StockChangeType {

    BUY(1, "Buy", "Kauf"),
    SELL(-1, "Sell", "Verkauf");

    private double factor = 0;
    private String title = null;
    private String qifType = null;

    private StockChangeType(double factor, String title, String qifType) {
        this.setFactor(factor);
        this.setTitle(title);
        this.setQifType(qifType);
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

    String getQifType() {
        return this.qifType;
    }
    private void setQifType(String qifType) {
        this.qifType = qifType;
    }

}
