package de.perdian.apps.fimasu4.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MonetaryValue implements Serializable {

    static final long serialVersionUID = 1L;

    private DoubleProperty amount = null;
    private ReadOnlyDoubleProperty amountInEuro = null;
    private StringProperty currency = null;
    private DoubleProperty euroExchangeRate = null;

    MonetaryValue() {

        DoubleProperty amountProperty = new SimpleDoubleProperty();
        StringProperty currencyProperty = new SimpleStringProperty("EUR");
        DoubleProperty euroExchangeRateProperty = new SimpleDoubleProperty();

        DoubleProperty amountInEuroProperty = new SimpleDoubleProperty();
        amountInEuroProperty.bind(amountProperty.multiply(euroExchangeRateProperty));

        this.setAmount(amountProperty);
        this.setCurrency(currencyProperty);
        this.setEuroExchangeRate(euroExchangeRateProperty);
        this.setAmountInEuro(amountInEuroProperty);

    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("amount", this.getAmount().getValue());
        toStringBuilder.append("currency", this.getCurrency().getValue());
        return toStringBuilder.toString();
    }

    public DoubleProperty getAmount() {
        return this.amount;
    }
    private void setAmount(DoubleProperty amount) {
        this.amount = amount;
    }

    public ReadOnlyDoubleProperty getAmountInEuro() {
        return this.amountInEuro;
    }
    private void setAmountInEuro(ReadOnlyDoubleProperty amountInEuro) {
        this.amountInEuro = amountInEuro;
    }

    public StringProperty getCurrency() {
        return this.currency;
    }
    private void setCurrency(StringProperty currency) {
        this.currency = currency;
    }

    public DoubleProperty getEuroExchangeRate() {
        return this.euroExchangeRate;
    }
    private void setEuroExchangeRate(DoubleProperty euroExchangeRate) {
        this.euroExchangeRate = euroExchangeRate;
    }

}
