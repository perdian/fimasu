package de.perdian.apps.fimasu.model.support.lineprocessors;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Supplier;
import java.util.regex.Matcher;

import javafx.beans.value.WritableValue;

public class RegexAmountWithCurrencyLineProcessor extends RegexLineProcessor {

    private NumberFormat amountFormat = null;
    private WritableValue<Number> amountProperty = null;
    private WritableValue<String> currencyProperty = null;
    private Supplier<Double> signComputer = null;

    public RegexAmountWithCurrencyLineProcessor(String pattern, WritableValue<Number> amountProperty, NumberFormat amountFormat, WritableValue<String> currencyProperty) {
        this(pattern, amountProperty, amountFormat, currencyProperty, () -> 1d);
    }

    public RegexAmountWithCurrencyLineProcessor(String pattern, WritableValue<Number> amountProperty, NumberFormat amountFormat, WritableValue<String> currencyProperty, Supplier<Double> signComputer) {
        super(pattern);
        this.setAmountProperty(amountProperty);
        this.setAmountFormat(amountFormat);
        this.setCurrencyProperty(currencyProperty);
        this.setSignComputer(signComputer);
    }

    @Override
    protected void processLineWithMatcher(String line, Matcher lineMatcher) {
        String amountString = lineMatcher.group("amount");
        String signString = RegexHelper.extractGroupForName(lineMatcher, "sign");
        String currencyString = lineMatcher.group("currency");
        try {
            Number amountValue = this.getAmountFormat().parse(amountString);
            Number amountValueSigned = amountValue.doubleValue() * ("-".equalsIgnoreCase(signString) ? -1d : 1d) * this.getSignComputer().get().doubleValue();
            this.getAmountProperty().setValue(amountValueSigned);
            this.getCurrencyProperty().setValue(currencyString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid amount value: " + amountString, e);
        }
    }

    private NumberFormat getAmountFormat() {
        return this.amountFormat;
    }
    private void setAmountFormat(NumberFormat amountFormat) {
        this.amountFormat = amountFormat;
    }

    private WritableValue<Number> getAmountProperty() {
        return this.amountProperty;
    }
    private void setAmountProperty(WritableValue<Number> amountProperty) {
        this.amountProperty = amountProperty;
    }

    private WritableValue<String> getCurrencyProperty() {
        return this.currencyProperty;
    }
    private void setCurrencyProperty(WritableValue<String> currencyProperty) {
        this.currencyProperty = currencyProperty;
    }

    private Supplier<Double> getSignComputer() {
        return this.signComputer;
    }
    private void setSignComputer(Supplier<Double> signComputer) {
        this.signComputer = signComputer;
    }

}
