package de.perdian.apps.fimasu.model.impl.transactions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.Property;

class StockChangeHelper {

    static double convert(Number sourceValue, String sourceCurrency, Number exchangeRate, String targetCurrency) {
        if (sourceValue == null || sourceValue.doubleValue() == 0d) {
            return 0d;
        } else if (Objects.equals(sourceCurrency, targetCurrency)) {
            return sourceValue.doubleValue();
        } else if (exchangeRate == null || exchangeRate.doubleValue() == 0d) {
            return 0d;
        } else {
            return sourceValue.doubleValue() / exchangeRate.doubleValue();
        }
    }

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
