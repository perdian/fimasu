package de.perdian.apps.qifgenerator.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
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
    private final DoubleProperty charges = new SimpleDoubleProperty();
    private final DoubleProperty financeTax = new SimpleDoubleProperty();
    private final DoubleProperty solidarityTax = new SimpleDoubleProperty();
    private final DoubleProperty totalValue = new SimpleDoubleProperty();
    private final List<ChangeListener<Transaction>> changeListeners = new CopyOnWriteArrayList<>();

    public Transaction() {

        this.initializeProperty(this.getWkn());
        this.initializeProperty(this.getIsin());
        this.initializeProperty(this.getTitle());
        this.initializeProperty(this.getType(), (oldValue, newValue) -> this.recomputeTotalValue(this.getBookingValue().getValue(), this.getCharges().getValue(), this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));

        this.initializeProperty(this.getBookingDate(), (oldValue, newValue) -> this.recomputeValutaDate(newValue));
        this.initializeProperty(this.getValutaDate());

        this.initializeProperty(this.getMarketPrice(), (oldValue, newValue) -> this.recomputeMarketValue(this.getNumberOfShares().getValue(), newValue));
        this.initializeProperty(this.getMarketCurrency(), (oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), this.getBookingCurrencyRate().getValue(), newValue, this.getBookingCurrency().getValue()));
        this.initializeProperty(this.getNumberOfShares(), (oldValue, newValue) -> this.recomputeMarketValue(newValue, this.getMarketPrice().getValue()));
        this.initializeProperty(this.getMarketValue(), (oldValue, newValue) -> this.recomputeBookingValue(newValue, this.getBookingCurrencyRate().getValue(), this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue()));

        this.initializeProperty(this.getBookingValue(), (oldValue, newValue) -> this.recomputeTotalValue(newValue, this.getCharges().getValue(), this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));
        this.initializeProperty(this.getBookingCurrency(), (oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), this.getBookingCurrencyRate().getValue(), this.getMarketCurrency().getValue(), newValue));
        this.initializeProperty(this.getBookingCurrencyRate(), (oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), newValue, this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue()));

        this.initializeProperty(this.getCharges(), (oldValue, newValue) -> this.recomputeTotalValue(this.getBookingValue().getValue(), newValue, this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));
        this.initializeProperty(this.getFinanceTax(), (oldValue, newValue) -> this.recomputeTotalValue(this.getBookingValue().getValue(), this.getCharges().getValue(), newValue, this.getSolidarityTax().getValue()));
        this.initializeProperty(this.getSolidarityTax(), (oldValue, newValue) -> this.recomputeTotalValue(this.getBookingValue().getValue(), this.getCharges().getValue(), this.getFinanceTax().getValue(), newValue));
        this.initializeProperty(this.getTotalValue());

    }

    private <T> void initializeProperty(Property<T> property) {
        property.addListener((o, oldValue, newValue) -> this.fireChange());
    }

    private <T> void initializeProperty(Property<T> property, BiConsumer<T, T> changeConsumer) {
        this.initializeProperty(property);
        property.addListener((o, oldValue, newValue) -> changeConsumer.accept(oldValue, newValue));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.getWkn().getValue());
        out.writeObject(this.getIsin().getValue());
        out.writeObject(this.getTitle().getValue());
        out.writeObject(this.getType().getValue());
        out.writeObject(this.getMarketCurrency().getValue());
        out.writeObject(this.getBookingCurrency().getValue());
        out.writeObject(this.getCharges().getValue());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.getWkn().setValue((String)in.readObject());
        this.getIsin().setValue((String)in.readObject());
        this.getTitle().setValue((String)in.readObject());
        this.getType().setValue((TransactionType)in.readObject());
        this.getMarketCurrency().setValue((String)in.readObject());
        this.getBookingCurrency().setValue((String)in.readObject());
        this.getCharges().setValue((Double)in.readObject());
    }

    private void recomputeMarketValue(Number numberOfShares, Number marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.getMarketValue().setValue(null);
        } else {
            double marketValue = numberOfShares.doubleValue() * marketPrice.doubleValue();
            this.getMarketValue().setValue(marketValue);
        }
    }

    private void recomputeBookingValue(Number marketValue, Number bookingConversionValue, String marketCurrency, String bookingCurrency) {
        if (marketValue == null || marketValue.doubleValue() == 0d) {
            this.getBookingValue().setValue(null);
        } else {
            if (Objects.equals(marketCurrency, bookingCurrency)) {
                this.getBookingValue().setValue(marketValue);
            } else {
                double conversionFactor = bookingConversionValue == null || bookingConversionValue.doubleValue() == 0d ? 1d : bookingConversionValue.doubleValue();
                double bookingValue = marketValue.doubleValue() / conversionFactor;
                this.getBookingValue().setValue(bookingValue);
            }
        }
    }

    private void recomputeTotalValue(Number bookingValue, Number charges, Number financeTax, Number solidarityTax) {
        if (bookingValue == null || bookingValue.doubleValue() == 0d) {
            this.getTotalValue().setValue(null);
        } else {

            double factor = this.getType().getValue().equals(TransactionType.BUY) ? 1d : -1d;

            double totalValue = bookingValue.doubleValue();
            totalValue += factor * (charges == null ? 0d : charges.doubleValue());
            totalValue += factor * (financeTax == null ? 0d : financeTax.doubleValue());
            totalValue += factor * (solidarityTax == null ? 0d : solidarityTax.doubleValue());
            this.getTotalValue().setValue(totalValue);

        }
    }

    private void recomputeValutaDate(LocalDate bookingDate) {
        if (bookingDate == null) {
            this.getValutaDate().setValue(null);
        } else if (this.getValutaDate().getValue() == null) {
            LocalDate nextValutaDate = bookingDate.plusDays(2);
            if (bookingDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                nextValutaDate = nextValutaDate.plusDays(2);
            } else if (bookingDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                nextValutaDate = nextValutaDate.plusDays(1);
            }
            this.getValutaDate().setValue(nextValutaDate);
        }
    }

    public StringProperty getWkn() {
        return this.wkn;
    }

    public StringProperty getIsin() {
        return this.isin;
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public ObjectProperty<TransactionType> getType() {
        return this.type;
    }

    public ObjectProperty<LocalDate> getBookingDate() {
        return this.bookingDate;
    }

    public ObjectProperty<LocalDate> getValutaDate() {
        return this.valutaDate;
    }

    public DoubleProperty getMarketPrice() {
        return this.marketPrice;
    }

    public StringProperty getMarketCurrency() {
        return this.marketCurrency;
    }

    public DoubleProperty getNumberOfShares() {
        return this.numberOfShares;
    }

    public DoubleProperty getMarketValue() {
        return this.marketValue;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }

    public DoubleProperty getBookingCurrencyRate() {
        return this.bookingCurrencyRate;
    }

    public DoubleProperty getBookingValue() {
        return this.bookingValue;
    }

    public DoubleProperty getCharges() {
        return this.charges;
    }

    public DoubleProperty getFinanceTax() {
        return this.financeTax;
    }

    public DoubleProperty getSolidarityTax() {
        return this.solidarityTax;
    }

    public DoubleProperty getTotalValue() {
        return this.totalValue;
    }

    public List<ChangeListener<Transaction>> getChangeListeners() {
        return this.changeListeners;
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
