package de.perdian.apps.fimasu.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.Property;

public class TransactionHelper {

    public static final NumberFormat XML_NUMBER_FORMAT = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY));

    public static <T, P> void copyValue(T sourceObject, T targetObject, Function<T, Property<P>> propertyFunction, Predicate<P> valueCheckFunction) {
        P sourceValue = propertyFunction.apply(sourceObject).getValue();
        if (sourceValue != null) {
            Property<P> targetProperty = propertyFunction.apply(targetObject);
            if (valueCheckFunction.test(targetProperty.getValue())) {
                targetProperty.setValue(sourceValue);
            }
        }
    }

}
