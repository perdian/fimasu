package de.perdian.apps.fimasu.model;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MonetaryValue {

    private Double value = null;
    private String currency = "EUR";

    public MonetaryValue(Double value, String currency) {
        this.setValue(value == null ? 0d : value);
        this.setCurrency(StringUtils.defaultIfEmpty(currency, "EUR"));
    }

    public MonetaryValue convert(Number targetExchangeRate, String targetCurrency) {
        if (Objects.equals(this.getCurrency(), targetCurrency)) {
            return this;
        } else if (targetExchangeRate == null || targetExchangeRate.doubleValue() == 0d) {
            return new MonetaryValue(0d, targetCurrency);
        } else {
            return new MonetaryValue(this.getValue().doubleValue() / targetExchangeRate.doubleValue(), targetCurrency);
        }
    }

    public MonetaryValue add(MonetaryValue input) {
        if (!Objects.equals(this.getCurrency(), input.getCurrency())) {
            throw new IllegalArgumentException("Cannot add value with currency '" + input.getCurrency() + "' to value of currency '" + this.getCurrency() + "'");
        } else {
            return this.add(input.getValue());
        }
    }

    public MonetaryValue add(Number input) {
        return new MonetaryValue(this.getValue().doubleValue() + input.doubleValue(), this.getCurrency());
    }

    public MonetaryValue multiply(Number factor) {
        return new MonetaryValue(this.getValue().doubleValue() * factor.doubleValue(), this.getCurrency());
    }

    public static StringProperty asCurrencyProperty(Property<MonetaryValue> inputProperty) {
        StringProperty stringProperty = new SimpleStringProperty(inputProperty.getValue().getCurrency());
        inputProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue.getCurrency(), stringProperty.getValue())) {
                stringProperty.setValue(newValue.getCurrency());
            }
        });
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue, inputProperty.getValue().getCurrency())) {
                inputProperty.setValue(new MonetaryValue(inputProperty.getValue().getValue(), newValue));
            }
        });
        return stringProperty;
    }

    public static DoubleProperty asValueProperty(Property<MonetaryValue> inputProperty) {
        DoubleProperty doubleProperty = new SimpleDoubleProperty();
        if (inputProperty.getValue().getValue() != null) {
            doubleProperty.setValue(inputProperty.getValue().getValue());
        }
        inputProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue.getValue(), doubleProperty.getValue())) {
                doubleProperty.setValue(newValue.getValue());
            }
        });
        doubleProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue, inputProperty.getValue().getValue())) {
                inputProperty.setValue(new MonetaryValue(newValue == null ? null : newValue.doubleValue(), inputProperty.getValue().getCurrency()));
            }
        });
        return doubleProperty;
    }

    public Double getValue() {
        return this.value;
    }
    private void setValue(Double value) {
        this.value = value;
    }

    public String getCurrency() {
        return this.currency;
    }
    private void setCurrency(String currency) {
        this.currency = currency;
    }

}
