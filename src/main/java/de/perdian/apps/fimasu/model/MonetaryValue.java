package de.perdian.apps.fimasu.model;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

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
