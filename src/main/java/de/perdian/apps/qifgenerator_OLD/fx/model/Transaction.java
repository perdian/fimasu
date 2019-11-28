package de.perdian.apps.qifgenerator_OLD.fx.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class Transaction implements Externalizable {

    static final long serialVersionUID = 1L;

    private final StringProperty wkn = new SimpleStringProperty();
    private final StringProperty isin = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<TransactionType> type = new SimpleObjectProperty<>(TransactionType.BUY);
    private final ObjectProperty<LocalDate> bookingDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> valutaDate = new SimpleObjectProperty<>();
    private final DoubleProperty marketPrice = new SimpleDoubleProperty();
    private final StringProperty marketCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final DoubleProperty marketValue = new SimpleDoubleProperty();
    private final StringProperty bookingCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty bookingCurrencyRate = new SimpleDoubleProperty();
    private final DoubleProperty bookingValue = new SimpleDoubleProperty();
    private final BooleanProperty bookingCurrencyDifferent = new SimpleBooleanProperty(false);
    private final DoubleProperty charges = new SimpleDoubleProperty();
    private final DoubleProperty financeTax = new SimpleDoubleProperty();
    private final DoubleProperty solidarityTax = new SimpleDoubleProperty();
    private final DoubleProperty totalValue = new SimpleDoubleProperty();
    private final List<ChangeListener<Transaction>> changeListeners = new ArrayList<>();

    public Transaction() {
        ChangeListener<Object> shareChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.chargesProperty().addListener(shareChangeListener);
        this.marketCurrencyProperty().addListener(shareChangeListener);
        this.isinProperty().addListener(shareChangeListener);
        this.titleProperty().addListener(shareChangeListener);
        this.wknProperty().addListener(shareChangeListener);
        this.bookingCurrencyProperty().addListener(shareChangeListener);
        this.bookingCurrencyRateProperty().addListener(shareChangeListener);
        this.marketCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeCurrency(newValue, this.bookingCurrencyProperty().getValue()));
        this.bookingCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeCurrency(this.marketCurrencyProperty().getValue(), newValue));
        this.numberOfSharesProperty().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(newValue, this.marketPriceProperty().getValue()));
        this.marketPriceProperty().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(this.numberOfSharesProperty().getValue(), newValue));
        this.marketValueProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(newValue, this.bookingCurrencyRateProperty().getValue(), this.marketCurrencyProperty().getValue(), this.bookingCurrencyProperty().getValue()));
        this.bookingCurrencyRateProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.marketValueProperty().getValue(), newValue, this.marketCurrencyProperty().getValue(), this.bookingCurrencyProperty().getValue()));
        this.marketCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.marketValueProperty().getValue(), this.bookingCurrencyRateProperty().getValue(), newValue, this.bookingCurrencyProperty().getValue()));
        this.bookingCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.marketValueProperty().getValue(), this.bookingCurrencyRateProperty().getValue(), this.marketCurrencyProperty().getValue(), newValue));
        this.bookingValueProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(newValue, this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
        this.chargesProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), newValue, this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
        this.financeTaxProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), this.chargesProperty().getValue(), newValue, this.solidarityTaxProperty().getValue()));
        this.solidarityTaxProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), newValue));
        this.typeProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
        this.bookingDateProperty().addListener((o, oldValue, newValue) -> this.recomputeValutaDate(newValue));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.wknProperty().getValue());
        out.writeObject(this.isinProperty().getValue());
        out.writeObject(this.titleProperty().getValue());
        out.writeObject(this.typeProperty().getValue());
        out.writeObject(this.marketCurrencyProperty().getValue());
        out.writeObject(this.bookingCurrencyProperty().getValue());
        out.writeObject(this.bookingCurrencyDifferentProperty().getValue());
        out.writeObject(this.chargesProperty().getValue());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.wknProperty().setValue((String)in.readObject());
        this.isinProperty().setValue((String)in.readObject());
        this.titleProperty().setValue((String)in.readObject());
        this.typeProperty().setValue((TransactionType)in.readObject());
        this.marketCurrencyProperty().setValue((String)in.readObject());
        this.bookingCurrencyProperty().setValue((String)in.readObject());
        this.bookingCurrencyDifferentProperty().setValue((Boolean)in.readObject());
        this.chargesProperty().setValue((Double)in.readObject());
    }

    private void recomputeMarketValue(Number numberOfShares, Number marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.marketValueProperty().setValue(null);
        } else {
            double marketValue = numberOfShares.doubleValue() * marketPrice.doubleValue();
            this.marketValueProperty().setValue(marketValue);
        }
    }

    private void recomputeBookingValue(Number marketValue, Number bookingConversionValue, String marketCurrency, String bookingCurrency) {
        if (marketValue == null || marketValue.doubleValue() == 0d) {
            this.bookingValueProperty().setValue(null);
        } else {
            if (Objects.equals(marketCurrency, bookingCurrency)) {
                this.bookingValueProperty().setValue(marketValue);
            } else {
                double conversionFactor = bookingConversionValue == null || bookingConversionValue.doubleValue() == 0d ? 1d : bookingConversionValue.doubleValue();
                double bookingValue = marketValue.doubleValue() / conversionFactor;
                this.bookingValueProperty().setValue(bookingValue);
            }
        }
    }

    private void recomputeTotalValue(Number bookingValue, Number charges, Number financeTax, Number solidarityTax) {
        if (bookingValue == null || bookingValue.doubleValue() == 0d) {
            this.totalValueProperty().setValue(null);
        } else {

            double factor = this.typeProperty().getValue().equals(TransactionType.BUY) ? 1d : -1d;

            double totalValue = bookingValue.doubleValue();
            totalValue += factor * (charges == null ? 0d : charges.doubleValue());
            totalValue += factor * (financeTax == null ? 0d : financeTax.doubleValue());
            totalValue += factor * (solidarityTax == null ? 0d : solidarityTax.doubleValue());
            this.totalValueProperty().setValue(totalValue);

        }
    }

    private void recomputeCurrency(String currencyValue, String bookingCurrencyValue) {
        this.bookingCurrencyDifferentProperty().setValue(!Objects.equals(currencyValue, bookingCurrencyValue));
    }

    private void recomputeValutaDate(LocalDate bookingDate) {
        if (bookingDate == null) {
            this.valutaDateProperty().setValue(null);
        } else if (this.valutaDateProperty().getValue() == null) {
            LocalDate nextValutaDate = bookingDate.plusDays(2);
            if (bookingDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                nextValutaDate = nextValutaDate.plusDays(2);
            } else if (bookingDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                nextValutaDate = nextValutaDate.plusDays(1);
            }
            this.valutaDateProperty().setValue(nextValutaDate);
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

    public ObjectProperty<TransactionType> typeProperty() {
        return this.type;
    }

    public ObjectProperty<LocalDate> bookingDateProperty() {
        return this.bookingDate;
    }

    public ObjectProperty<LocalDate> valutaDateProperty() {
        return this.valutaDate;
    }

    public DoubleProperty marketPriceProperty() {
        return this.marketPrice;
    }

    public StringProperty marketCurrencyProperty() {
        return this.marketCurrency;
    }

    public DoubleProperty marketValueProperty() {
        return this.marketValue;
    }

    public StringProperty bookingCurrencyProperty() {
        return this.bookingCurrency;
    }

    public DoubleProperty bookingCurrencyRateProperty() {
        return this.bookingCurrencyRate;
    }

    public BooleanProperty bookingCurrencyDifferentProperty() {
        return this.bookingCurrencyDifferent;
    }

    public DoubleProperty bookingValueProperty() {
        return this.bookingValue;
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
    public void addChangeListener(ChangeListener<Transaction> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    public void removeChangeListener(ChangeListener<Transaction> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
