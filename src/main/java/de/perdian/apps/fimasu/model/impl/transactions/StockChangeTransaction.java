package de.perdian.apps.fimasu.model.impl.transactions;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu.model.MonetaryValue;
import de.perdian.apps.fimasu.model.Transaction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StockChangeTransaction extends Transaction {

    private final ObjectProperty<StockChangeType> type = new SimpleObjectProperty<>(StockChangeType.BUY);
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final ObjectProperty<MonetaryValue> marketPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<MonetaryValue> marketValue = new SimpleObjectProperty<>();
    private final StringProperty bookingCurrency = new SimpleStringProperty();
    private final DoubleProperty bookingExchangeRate = new SimpleDoubleProperty();
    private final ObjectProperty<MonetaryValue> charges = new SimpleObjectProperty<>();
    private final ObjectProperty<MonetaryValue> financeTax = new SimpleObjectProperty<>();
    private final ObjectProperty<MonetaryValue> solidarityTax = new SimpleObjectProperty<>();
    private final ObjectProperty<MonetaryValue> totalValue = new SimpleObjectProperty<>();

    public StockChangeTransaction() {

        this.getType().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketValue().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingExchangeRate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getCharges().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTax().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTax().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTotalValue().addListener((o, oldValue, newValue) -> this.fireChange());

        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(newValue, this.getMarketPrice().getValue()));
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.recomputeMarketValue(this.getNumberOfShares().getValue(), newValue));

        this.getMarketValue().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(newValue, this.getBookingCurrency().getValue(), this.getBookingExchangeRate().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), newValue, this.getBookingExchangeRate().getValue()));
        this.getBookingExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getMarketValue().getValue(), this.getBookingCurrency().getValue(), newValue));

        this.getType().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(newValue, this.getBookingValue().getValue(), this.getBookingExchangeRate().getValue(), this.getCharges().getValue(), this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));
        this.getBookingValue().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), newValue, this.getBookingExchangeRate().getValue(), this.getCharges().getValue(), this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));
        this.getBookingExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), newValue, this.getCharges().getValue(), this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));
        this.getCharges().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingExchangeRate().getValue(), newValue, this.getFinanceTax().getValue(), this.getSolidarityTax().getValue()));
        this.getFinanceTax().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingExchangeRate().getValue(), this.getCharges().getValue(), newValue, this.getSolidarityTax().getValue()));
        this.getSolidarityTax().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.getType().getValue(), this.getBookingValue().getValue(), this.getBookingExchangeRate().getValue(), this.getCharges().getValue(), this.getFinanceTax().getValue(), newValue));

        this.getBookingValue().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(newValue, this.getMarketPrice().getValue())));
        this.getMarketValue().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(this.getBookingValue().getValue(), newValue)));

    }

    private void recomputeMarketValue(Number numberOfShares, MonetaryValue marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.getValue().doubleValue() == 0d) {
            this.getMarketValue().setValue(null);
        } else {
            this.getMarketValue().setValue(new MonetaryValue(numberOfShares.doubleValue() * marketPrice.getValue().doubleValue(), marketPrice.getCurrency()));
        }
    }

    private void recomputeBookingValue(MonetaryValue marketValue, String bookingCurrency, Number bookingExchangeRateValue) {
        if (marketValue == null || marketValue.getValue().doubleValue() == 0d) {
            this.getBookingValue().setValue(null);
        } else {
            this.getBookingValue().setValue(marketValue.convert(bookingExchangeRateValue, bookingCurrency));
        }
    }

    private void recomputeTotalValue(StockChangeType type, MonetaryValue bookingValue, Number bookingExchangeRate, MonetaryValue charges, MonetaryValue financeTax, MonetaryValue solidarityTax) {
        if (bookingValue == null || bookingValue.getValue().doubleValue() == 0d) {
            this.getTotalValue().setValue(null);
        } else {
            double factor = StockChangeType.BUY.equals(type) ? 1d : -1d;
            MonetaryValue totalValue = bookingValue;
            totalValue = charges == null ? totalValue : totalValue.add(charges.convert(bookingExchangeRate, bookingValue.getCurrency())).multiply(factor);
            totalValue = financeTax == null ? totalValue : totalValue.add(financeTax.convert(bookingExchangeRate, bookingValue.getCurrency())).multiply(factor);
            totalValue = solidarityTax == null ? totalValue : totalValue.add(solidarityTax.convert(bookingExchangeRate, bookingValue.getCurrency())).multiply(factor);
            this.getTotalValue().setValue(totalValue);
        }
    }

    private void recomputeCurrencies(List<MonetaryValue> inputValues) {
        List<String> consolidatedCurrencies = inputValues.stream().map(MonetaryValue::getCurrency).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        List.of(this.getCharges(), this.getFinanceTax(), this.getSolidarityTax()).stream().forEach(property -> {
            if (StringUtils.isEmpty(property.getValue().getCurrency()) || !consolidatedCurrencies.contains(property.getValue().getCurrency())) {
                property.setValue(new MonetaryValue(property.getValue().getValue(), consolidatedCurrencies.isEmpty() ? null : consolidatedCurrencies.get(0)));
            }
        });
    }

    public ObjectProperty<StockChangeType> getType() {
        return this.type;
    }

    public DoubleProperty getNumberOfShares() {
        return this.numberOfShares;
    }

    public ObjectProperty<MonetaryValue> getMarketPrice() {
        return this.marketPrice;
    }

    public ObjectProperty<MonetaryValue> getMarketValue() {
        return this.marketValue;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }

    public DoubleProperty getBookingExchangeRate() {
        return this.bookingExchangeRate;
    }

    public ObjectProperty<MonetaryValue> getCharges() {
        return this.charges;
    }

    public ObjectProperty<MonetaryValue> getFinanceTax() {
        return this.financeTax;
    }

    public ObjectProperty<MonetaryValue> getSolidarityTax() {
        return this.solidarityTax;
    }

    public ObjectProperty<MonetaryValue> getTotalValue() {
        return this.totalValue;
    }

}
