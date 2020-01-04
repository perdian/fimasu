package de.perdian.apps.fimasu.model;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.Property;

public class TransactionParserHelper {

    public static void regexSet(String input, String regexExpression, Property<String> targetProperty) {
        TransactionParserHelper.regexSet(input, regexExpression, targetProperty, in -> in);
    }

    public static <T> void regexSet(String input, String regexExpression, Property<T> targetProperty, StringConverter<T> stringConverterFunction) {
        Matcher regexMatcher = Pattern.compile(regexExpression).matcher(input);
        if (regexMatcher.matches()) {
            try {
                targetProperty.setValue(stringConverterFunction.convert(regexMatcher.group(1)));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid value found in: " + input, e);
            }
        }
    }

    public static void regexSetAmountWithCurrency(String input, String regexExpression, Property<Number> amountProperty, NumberFormat amountFormat, Property<String> currencyProperty) {
        try {
            Matcher regexMatcher = Pattern.compile(regexExpression).matcher(input);
            if (regexMatcher.matches()) {
                amountProperty.setValue(amountFormat.parse(regexMatcher.group("amount")));
                currencyProperty.setValue(regexMatcher.group("currency"));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid numeric value found in: " + input, e);
        }
    }

    public static void regexAddAmountWithCurrency(String input, String regexExpression, Property<Number> amountProperty, NumberFormat amountFormat, Property<String> currencyProperty) {
        try {
            Matcher regexMatcher = Pattern.compile(regexExpression).matcher(input);
            if (regexMatcher.matches()) {
                amountProperty.setValue((amountProperty.getValue() == null ? 0 : amountProperty.getValue().doubleValue()) + amountFormat.parse(regexMatcher.group("amount")).doubleValue());
                currencyProperty.setValue(regexMatcher.group("currency"));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid numeric value found in: " + input, e);
        }
    }

    public static void regexSetNumberOfSharesPlusAmountWithCurrency(String input, String regexExpression, Property<Number> numberOfSharesProperty, Property<Number> amountProperty, NumberFormat amountFormat, Property<String> currencyProperty) {
        try {
            Matcher regexMatcher = Pattern.compile(regexExpression).matcher(input);
            if (regexMatcher.matches()) {
                numberOfSharesProperty.setValue(amountFormat.parse(regexMatcher.group("numberOfShares")));
                amountProperty.setValue(amountFormat.parse(regexMatcher.group("amount")));
                currencyProperty.setValue(regexMatcher.group("currency"));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid numeric value found in: " + input, e);
        }
    }

    public static interface StringConverter<T> {

        T convert(String input) throws Exception;

    }

}
