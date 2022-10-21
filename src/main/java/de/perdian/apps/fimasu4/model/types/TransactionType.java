package de.perdian.apps.fimasu4.model.types;

public enum TransactionType {

    BUY("Buy", 1),
    SELL("Sell", -1),
    PAYOUT("Payout", -1);

    private String title = null;
    private double chargesFactor = 0;

    private TransactionType(String title, double chargesFactor) {
        this.setTitle(title);
        this.setChargesFactor(chargesFactor);
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

    public double getChargesFactor() {
        return this.chargesFactor;
    }
    private void setChargesFactor(double chargesFactor) {
        this.chargesFactor = chargesFactor;
    }

}
