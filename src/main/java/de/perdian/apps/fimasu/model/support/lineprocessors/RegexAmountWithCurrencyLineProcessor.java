package de.perdian.apps.fimasu.model.support.lineprocessors;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Supplier;
import java.util.regex.Matcher;

import javafx.beans.value.WritableValue;

public class RegexAmountWithCurrencyLineProcessor extends RegexLineProcessor {

    public enum Mode {

        SET {
            @Override protected Number computeNewAmount(Number oldValue, Number newValue) {
                return newValue;
            }
        },

        ADD {
            @Override protected Number computeNewAmount(Number oldValue, Number newValue) {
                return (oldValue == null ? 0d : oldValue.doubleValue()) + (newValue == null ? 0d : newValue.doubleValue());
            }
        };

        protected abstract Number computeNewAmount(Number oldValue, Number newValue);
    }

    private NumberFormat amountFormat = null;
    private WritableValue<Number> amountProperty = null;
    private WritableValue<String> currencyProperty = null;
    private Mode mode = null;
    private Supplier<Double> signComputer = null;

    public RegexAmountWithCurrencyLineProcessor(String pattern, WritableValue<Number> amountProperty, NumberFormat amountFormat, WritableValue<String> currencyProperty, RegexAmountWithCurrencyLineProcessor.Mode mode) {
        this(pattern, amountProperty, amountFormat, currencyProperty, mode, () -> 1d);
    }

    public RegexAmountWithCurrencyLineProcessor(String pattern, WritableValue<Number> amountProperty, NumberFormat amountFormat, WritableValue<String> currencyProperty, RegexAmountWithCurrencyLineProcessor.Mode mode, Supplier<Double> signComputer) {
        super(pattern);
        this.setAmountProperty(amountProperty);
        this.setAmountFormat(amountFormat);
        this.setCurrencyProperty(currencyProperty);
        this.setMode(mode);
        this.setSignComputer(signComputer);
    }

    @Override
    protected void processLineWithMatcher(String line, Matcher lineMatcher) {
        String amountString = lineMatcher.group("amount");
        String signString = RegexHelper.extractGroupForName(lineMatcher, "sign");
        String currencyString = RegexHelper.extractGroupForName(lineMatcher, "currency");
        try {
            if (this.getAmountProperty() != null) {
                Number amountValue = this.getAmountFormat().parse(amountString);
                Number amountValueSigned = amountValue.doubleValue() * ("-".equalsIgnoreCase(signString) ? -1d : 1d) * this.getSignComputer().get().doubleValue();
                Number newAmount = this.getMode().computeNewAmount(this.getAmountProperty().getValue(), amountValueSigned);
                this.getAmountProperty().setValue(newAmount);
            }
            if (this.getCurrencyProperty() != null && currencyString != null) {
                this.getCurrencyProperty().setValue(currencyString);
            }
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

    private Mode getMode() {
        return this.mode;
    }
    private void setMode(Mode mode) {
        this.mode = mode;
    }

    private Supplier<Double> getSignComputer() {
        return this.signComputer;
    }
    private void setSignComputer(Supplier<Double> signComputer) {
        this.signComputer = signComputer;
    }

}
