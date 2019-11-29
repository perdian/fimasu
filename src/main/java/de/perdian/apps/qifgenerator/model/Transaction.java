package de.perdian.apps.qifgenerator.model;

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

    private StringProperty wkn = new SimpleStringProperty();
    private StringProperty isin = new SimpleStringProperty();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<TransactionType> type = new SimpleObjectProperty<>(TransactionType.BUY);
    private ObjectProperty<LocalDate> bookingDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> valutaDate = new SimpleObjectProperty<>();
    private DoubleProperty marketPrice = new SimpleDoubleProperty();
    private StringProperty marketCurrency = new SimpleStringProperty("EUR");
    private DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private DoubleProperty marketValue = new SimpleDoubleProperty();
    private StringProperty bookingCurrency = new SimpleStringProperty("EUR");
    private DoubleProperty bookingCurrencyRate = new SimpleDoubleProperty();
    private DoubleProperty bookingValue = new SimpleDoubleProperty();
    private BooleanProperty bookingCurrencyDifferent = new SimpleBooleanProperty(false);
    private DoubleProperty charges = new SimpleDoubleProperty();
    private DoubleProperty financeTax = new SimpleDoubleProperty();
    private DoubleProperty solidarityTax = new SimpleDoubleProperty();
    private DoubleProperty totalValue = new SimpleDoubleProperty();
    private List<ChangeListener<Transaction>> changeListeners = new ArrayList<>();

    public Transaction() {

        TransactionPropertyBuilder propertyBuilder = new TransactionPropertyBuilder(this);

        this.setCharges(propertyBuilder.createDoubleProperty().changeListener((oldValue, newValue) -> this.recomputeTotalValue(this.getBookingValue().getValue(), newValue, this.getFinanceTax().getValue(), this.getSolidarityTax().getValue())).get());
        this.setIsin(propertyBuilder.createStringProperty().get());
        this.setMarketCurrency(propertyBuilder.createStringProperty().changeListener((oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), this.getBookingCurrencyRate().getValue(), newValue, this.getBookingCurrency().getValue())).get());

//        this.titleProperty().addListener(shareChangeListener);
//        this.wknProperty().addListener(shareChangeListener);
//        this.bookingCurrencyProperty().addListener(shareChangeListener);
//        this.bookingCurrencyRateProperty().addListener(shareChangeListener);
//        this.marketCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeCurrency(newValue, this.bookingCurrencyProperty().getValue()));
//        this.bookingCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeCurrency(this.marketCurrencyProperty().getValue(), newValue));
//        this.numberOfSharesProperty().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(newValue, this.marketPriceProperty().getValue()));
//        this.marketPriceProperty().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(this.numberOfSharesProperty().getValue(), newValue));
//        this.marketValueProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(newValue, this.bookingCurrencyRateProperty().getValue(), this.marketCurrencyProperty().getValue(), this.bookingCurrencyProperty().getValue()));
//        this.bookingCurrencyRateProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.marketValueProperty().getValue(), newValue, this.marketCurrencyProperty().getValue(), this.bookingCurrencyProperty().getValue()));
//        this.bookingCurrencyProperty().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.marketValueProperty().getValue(), this.bookingCurrencyRateProperty().getValue(), this.marketCurrencyProperty().getValue(), newValue));
//        this.bookingValueProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(newValue, this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
//        this.chargesProperty().addListener((o, oldValue, newValue) -> );
//        this.financeTaxProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), this.chargesProperty().getValue(), newValue, this.solidarityTaxProperty().getValue()));
//        this.solidarityTaxProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), newValue));
//        this.typeProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.bookingValueProperty().getValue(), this.chargesProperty().getValue(), this.financeTaxProperty().getValue(), this.solidarityTaxProperty().getValue()));
//        this.bookingDateProperty().addListener((o, oldValue, newValue) -> this.recomputeValutaDate(newValue));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.getWkn().getValue());
        out.writeObject(this.getIsin().getValue());
        out.writeObject(this.getTitle().getValue());
        out.writeObject(this.getType().getValue());
        out.writeObject(this.getMarketCurrency().getValue());
        out.writeObject(this.getBookingCurrency().getValue());
        out.writeObject(this.getBookingCurrencyDifferent().getValue());
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
        this.getBookingCurrencyDifferent().setValue((Boolean)in.readObject());
        this.getCharges().setValue((Double)in.readObject());
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

    public StringProperty getWkn() {
        return this.wkn;
    }
    private void setWkn(StringProperty wkn) {
        this.wkn = wkn;
    }

    public StringProperty getIsin() {
        return this.isin;
    }
    private void setIsin(StringProperty isin) {
        this.isin = isin;
    }

    public StringProperty getTitle() {
        return this.title;
    }
    private void setTitle(StringProperty title) {
        this.title = title;
    }

    public ObjectProperty<TransactionType> getType() {
        return this.type;
    }
    private void setType(ObjectProperty<TransactionType> type) {
        this.type = type;
    }

    public ObjectProperty<LocalDate> getBookingDate() {
        return this.bookingDate;
    }
    private void setBookingDate(ObjectProperty<LocalDate> bookingDate) {
        this.bookingDate = bookingDate;
    }

    public ObjectProperty<LocalDate> getValutaDate() {
        return this.valutaDate;
    }
    private void setValutaDate(ObjectProperty<LocalDate> valutaDate) {
        this.valutaDate = valutaDate;
    }

    public DoubleProperty getMarketPrice() {
        return this.marketPrice;
    }
    private void setMarketPrice(DoubleProperty marketPrice) {
        this.marketPrice = marketPrice;
    }

    public StringProperty getMarketCurrency() {
        return this.marketCurrency;
    }
    private void setMarketCurrency(StringProperty marketCurrency) {
        this.marketCurrency = marketCurrency;
    }

    public DoubleProperty getNumberOfShares() {
        return this.numberOfShares;
    }
    private void setNumberOfShares(DoubleProperty numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public DoubleProperty getMarketValue() {
        return this.marketValue;
    }
    private void setMarketValue(DoubleProperty marketValue) {
        this.marketValue = marketValue;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }
    private void setBookingCurrency(StringProperty bookingCurrency) {
        this.bookingCurrency = bookingCurrency;
    }

    public DoubleProperty getBookingCurrencyRate() {
        return this.bookingCurrencyRate;
    }
    private void setBookingCurrencyRate(DoubleProperty bookingCurrencyRate) {
        this.bookingCurrencyRate = bookingCurrencyRate;
    }

    public DoubleProperty getBookingValue() {
        return this.bookingValue;
    }
    private void setBookingValue(DoubleProperty bookingValue) {
        this.bookingValue = bookingValue;
    }

    public BooleanProperty getBookingCurrencyDifferent() {
        return this.bookingCurrencyDifferent;
    }
    private void setBookingCurrencyDifferent(BooleanProperty bookingCurrencyDifferent) {
        this.bookingCurrencyDifferent = bookingCurrencyDifferent;
    }

    public DoubleProperty getCharges() {
        return this.charges;
    }
    private void setCharges(DoubleProperty charges) {
        this.charges = charges;
    }

    public DoubleProperty getFinanceTax() {
        return this.financeTax;
    }
    private void setFinanceTax(DoubleProperty financeTax) {
        this.financeTax = financeTax;
    }

    public DoubleProperty getSolidarityTax() {
        return this.solidarityTax;
    }
    private void setSolidarityTax(DoubleProperty solidarityTax) {
        this.solidarityTax = solidarityTax;
    }

    public DoubleProperty getTotalValue() {
        return this.totalValue;
    }
    private void setTotalValue(DoubleProperty totalValue) {
        this.totalValue = totalValue;
    }

    public List<ChangeListener<Transaction>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<Transaction>> changeListeners) {
        this.changeListeners = changeListeners;
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
