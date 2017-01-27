package de.perdian.apps.qifgenerator.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class Transaction {

    static final long serialVersionUID = 1L;

    private final StringProperty wkn = new SimpleStringProperty();
    private final StringProperty isin = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final DoubleProperty value = new SimpleDoubleProperty();

    private final Property<TransactionType> type = new SimpleObjectProperty<>(TransactionType.BUY);
    private final Property<LocalDate> bookingDate = new SimpleObjectProperty<>();
    private final Property<LocalDate> valutaDate = new SimpleObjectProperty<>();
    private final DoubleProperty marketPrice = new SimpleDoubleProperty();
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final DoubleProperty marketValue = new SimpleDoubleProperty();
    private final DoubleProperty charges = new SimpleDoubleProperty();
    private final DoubleProperty financeTax = new SimpleDoubleProperty();
    private final DoubleProperty solidarityTax = new SimpleDoubleProperty();
    private final DoubleProperty totalValue = new SimpleDoubleProperty();
    private final List<ChangeListener<Transaction>> changeListeners = new ArrayList<>();

    public Transaction() {
        ChangeListener<Object> shareChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.chargesProperty().addListener(shareChangeListener);
        this.isinProperty().addListener(shareChangeListener);
        this.titleProperty().addListener(shareChangeListener);
        this.valueProperty().addListener(shareChangeListener);
        this.wknProperty().addListener(shareChangeListener);
        this.numberOfSharesProperty().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(newValue, this.marketPriceProperty().getValue()));
        this.marketPriceProperty().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(this.numberOfSharesProperty().getValue(), newValue));
        this.marketValueProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(newValue, this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
        this.chargesProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.marketValueProperty().getValue(), newValue, this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
        this.financeTaxProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.marketValueProperty().getValue(), this.chargesProperty().getValue(), newValue, this.solidarityTaxProperty().getValue()));
        this.solidarityTaxProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.marketValueProperty().getValue(), this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), newValue));
        this.typeProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.marketValueProperty().getValue(), this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
    }

    private void recomputeMarketValue(Number numberOfShares, Number marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.marketValueProperty().setValue(null);
        } else {
            this.marketValueProperty().setValue(numberOfShares.doubleValue() * marketPrice.doubleValue());
        }
    }

    private void recomputeTotalValue(Number marketValue, Number charges, Number financeTax, Number solidarityTax) {
        if (marketValue == null || marketValue.doubleValue() == 0d) {
            this.totalValueProperty().setValue(null);
        } else {
            double factor = this.typeProperty().getValue().equals(TransactionType.BUY) ? 1d : -1d;
            double totalValue = marketValue.doubleValue();
            totalValue += factor * (charges == null ? 0d : charges.doubleValue());
            totalValue += factor * (financeTax == null ? 0d : financeTax.doubleValue());
            totalValue += factor * (solidarityTax == null ? 0d : solidarityTax.doubleValue());
            this.totalValueProperty().setValue(totalValue);
        }
    }

    public StringProperty wknProperty() {
        return this.wkn;
    }

    public StringProperty isinProperty() {
        return this.isin;
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public DoubleProperty valueProperty() {
        return this.value;
    }

    public Property<TransactionType> typeProperty() {
        return this.type;
    }

    public Property<LocalDate> bookingDateProperty() {
        return this.bookingDate;
    }

    public Property<LocalDate> valutaDateProperty() {
        return this.valutaDate;
    }

    public DoubleProperty marketPriceProperty() {
        return this.marketPrice;
    }

    public DoubleProperty marketValueProperty() {
        return this.marketValue;
    }

    public DoubleProperty numberOfSharesProperty() {
        return this.numberOfShares;
    }

    public DoubleProperty totalValueProperty() {
        return this.totalValue;
    }

    public DoubleProperty chargesProperty() {
        return this.charges;
    }

    public DoubleProperty financeTaxProperty() {
        return this.financeTax;
    }

    public DoubleProperty solidarityTaxProperty() {
        return this.solidarityTax;
    }

    void fireChange() {
        for (ChangeListener<Transaction> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }
    void addChangeListener(ChangeListener<Transaction> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<Transaction> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
