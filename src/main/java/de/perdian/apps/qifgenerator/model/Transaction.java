package de.perdian.apps.qifgenerator.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final DoubleProperty marketPrice = new SimpleDoubleProperty();
    private final StringProperty marketCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty marketValue = new SimpleDoubleProperty();
    private final StringProperty bookingCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty bookingCurrencyExchangeRate = new SimpleDoubleProperty();
    private final DoubleProperty bookingValue = new SimpleDoubleProperty();
    private final DoubleProperty charges = new SimpleDoubleProperty();
    private final StringProperty chargesCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty financeTax = new SimpleDoubleProperty();
    private final StringProperty financeTaxCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty solidarityTax = new SimpleDoubleProperty();
    private final StringProperty solidarityTaxCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty totalValue = new SimpleDoubleProperty();
    private final List<ChangeListener<Transaction>> changeListeners = new CopyOnWriteArrayList<>();

    public Transaction() {

        this.getWkn().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getIsin().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTitle().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getType().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getValutaDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketValue().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingCurrencyExchangeRate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingValue().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getCharges().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getChargesCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTax().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTaxCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTax().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTaxCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTotalValue().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTotalValue().addListener((o, oldValue, newValue) -> this.fireChange());

        this.getBookingDate().addListener((o, oldValue, newValue) -> this.recomputeValutaDate(newValue));

        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(newValue, this.getMarketPrice().getValue(), this.getMarketCurrency().getValue()));
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(this.getNumberOfShares().getValue(), newValue, this.getMarketCurrency().getValue()));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(this.getNumberOfShares().getValue(), this.getMarketPrice().getValue(), newValue));

        this.getMarketValue().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(newValue, this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), this.getMarketCurrency().getValue(), newValue, this.getBookingCurrencyExchangeRate().getValue()));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), newValue, this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue()));
        this.getBookingCurrencyExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue(), newValue));

        this.getType().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(newValue, this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));

        this.getBookingValue().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), newValue, this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), newValue, this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getBookingCurrencyExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), newValue, this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getCharges().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), newValue, this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getChargesCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), newValue, this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getFinanceTax().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), newValue, this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getFinanceTaxCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), newValue, this.getSolidarityTax().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getSolidarityTax().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), newValue, this.getSolidarityTaxCurrency().getValue()));
        this.getSolidarityTaxCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingCurrency().getValue(), this.getBookingCurrencyExchangeRate().getValue(), this.getCharges().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTax().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTax().getValue(), newValue));

        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(newValue, this.getMarketCurrency().getValue())));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(this.getBookingCurrency().getValue(), newValue)));

    }

    public void copyValuesInto(Transaction targetTransaction) {
        Optional.ofNullable(this.getBookingCurrency().getValue()).ifPresent(targetTransaction.getBookingCurrency()::setValue);
        Optional.ofNullable(this.getBookingCurrencyExchangeRate().getValue()).ifPresent(targetTransaction.getBookingCurrencyExchangeRate()::setValue);
        Optional.ofNullable(this.getBookingDate().getValue()).ifPresent(targetTransaction.getBookingDate()::setValue);
        Optional.ofNullable(this.getCharges().getValue()).ifPresent(targetTransaction.getCharges()::setValue);
        Optional.ofNullable(this.getChargesCurrency().getValue()).ifPresent(targetTransaction.getChargesCurrency()::setValue);
        Optional.ofNullable(this.getFinanceTax().getValue()).ifPresent(targetTransaction.getFinanceTax()::setValue);
        Optional.ofNullable(this.getFinanceTaxCurrency().getValue()).ifPresent(targetTransaction.getFinanceTaxCurrency()::setValue);
        Optional.ofNullable(this.getMarketCurrency().getValue()).ifPresent(targetTransaction.getMarketCurrency()::setValue);
        Optional.ofNullable(this.getMarketPrice().getValue()).ifPresent(targetTransaction.getMarketPrice()::setValue);
        Optional.ofNullable(this.getNumberOfShares().getValue()).ifPresent(targetTransaction.getNumberOfShares()::setValue);
        Optional.ofNullable(this.getSolidarityTax().getValue()).ifPresent(targetTransaction.getSolidarityTax()::setValue);
        Optional.ofNullable(this.getSolidarityTaxCurrency().getValue()).ifPresent(targetTransaction.getSolidarityTaxCurrency()::setValue);
        Optional.ofNullable(this.getType().getValue()).ifPresent(targetTransaction.getType()::setValue);
        Optional.ofNullable(this.getValutaDate().getValue()).ifPresent(targetTransaction.getValutaDate()::setValue);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.getType().getValue());
        out.writeObject(this.getWkn().getValue());
        out.writeObject(this.getIsin().getValue());
        out.writeObject(this.getTitle().getValue());
        out.writeObject(this.getMarketCurrency().getValue());
        out.writeObject(this.getBookingCurrency().getValue());
        out.writeObject(this.getCharges().getValue());
        out.writeObject(this.getChargesCurrency().getValue());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.getType().setValue((TransactionType)in.readObject());
        this.getWkn().setValue((String)in.readObject());
        this.getIsin().setValue((String)in.readObject());
        this.getTitle().setValue((String)in.readObject());
        this.getMarketCurrency().setValue((String)in.readObject());
        this.getBookingCurrency().setValue((String)in.readObject());
        this.getCharges().setValue((Double)in.readObject());
        this.getChargesCurrency().setValue(StringUtils.defaultIfEmpty((String)in.readObject(), this.getBookingCurrency().getValue()));
        this.getFinanceTaxCurrency().setValue(this.getBookingCurrency().getValue());
        this.getSolidarityTaxCurrency().setValue(this.getBookingCurrency().getValue());
    }

    private void recomputeMarketValue(Number numberOfShares, Number marketPrice, String marketCurrency) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.getMarketValue().setValue(null);
        } else {
            this.getMarketValue().setValue(numberOfShares.doubleValue() * marketPrice.doubleValue());
        }
    }

    private void recomputeBookingValue(Number marketValue, String marketCurrency, String bookingCurrency, Number bookingCurrencyExchangeRateValue) {
        if (marketValue == null || marketValue.doubleValue() == 0d) {
            this.getBookingValue().setValue(null);
        } else {
            this.getBookingValue().setValue(this.computeAmountInBookingCurrency(marketValue, marketCurrency, bookingCurrency, bookingCurrencyExchangeRateValue));
        }
    }

    private void recomputeTotalValue(TransactionType transactionType, Number bookingValue, String bookingCurrency, Number bookingCurrencyExchangeRate, Number charges, String chargesCurrency, Number financeTax, String financeTaxCurrency, Number solidarityTax, String solidarityTaxCurrency) {
        if (bookingValue == null || bookingValue.doubleValue() == 0d) {
            this.getTotalValue().setValue(null);
        } else {
            double factor = TransactionType.BUY.equals(transactionType) ? 1d : -1d;
            double totalValue = bookingValue.doubleValue();
            totalValue += factor * (charges == null ? 0d : this.computeAmountInBookingCurrency(charges, chargesCurrency, bookingCurrency, bookingCurrencyExchangeRate));
            totalValue += factor * (financeTax == null ? 0d : this.computeAmountInBookingCurrency(financeTax, financeTaxCurrency, bookingCurrency, bookingCurrencyExchangeRate));
            totalValue += factor * (solidarityTax == null ? 0d : this.computeAmountInBookingCurrency(solidarityTax, solidarityTaxCurrency, bookingCurrency, bookingCurrencyExchangeRate));
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

    private void recomputeCurrencies(List<String> availableCurrencies) {
        List<String> consolidatedCurrencies = availableCurrencies.stream().filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        List<Property<String>> currencyProperties = List.of(this.getChargesCurrency(), this.getFinanceTaxCurrency(), this.getSolidarityTaxCurrency());
        currencyProperties.stream().forEach(property -> {
            if (StringUtils.isEmpty(property.getValue()) || !consolidatedCurrencies.contains(property.getValue())) {
                property.setValue(consolidatedCurrencies.isEmpty() ? null : consolidatedCurrencies.get(0));
            }
        });
    }

    private double computeAmountInBookingCurrency(Number inputValue, String inputCurrency, String bookingCurrency, Number bookingCurrencyExchangeRate) {
        if (inputValue == null || inputValue.doubleValue() == 0d) {
            return 0d;
        } else if (Objects.equals(inputCurrency, bookingCurrency)) {
            return inputValue.doubleValue();
        } else if (bookingCurrencyExchangeRate == null || bookingCurrencyExchangeRate.doubleValue() == 0d) {
            return 0;
        } else {
            return inputValue.doubleValue() / bookingCurrencyExchangeRate.doubleValue();
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

    public DoubleProperty getNumberOfShares() {
        return this.numberOfShares;
    }

    public DoubleProperty getMarketPrice() {
        return this.marketPrice;
    }

    public StringProperty getMarketCurrency() {
        return this.marketCurrency;
    }

    public DoubleProperty getMarketValue() {
        return this.marketValue;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }

    public DoubleProperty getBookingCurrencyExchangeRate() {
        return this.bookingCurrencyExchangeRate;
    }

    public DoubleProperty getBookingValue() {
        return this.bookingValue;
    }

    public DoubleProperty getCharges() {
        return this.charges;
    }

    public StringProperty getChargesCurrency() {
        return this.chargesCurrency;
    }

    public DoubleProperty getFinanceTax() {
        return this.financeTax;
    }

    public StringProperty getFinanceTaxCurrency() {
        return this.financeTaxCurrency;
    }

    public DoubleProperty getSolidarityTax() {
        return this.solidarityTax;
    }

    public StringProperty getSolidarityTaxCurrency() {
        return this.solidarityTaxCurrency;
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
