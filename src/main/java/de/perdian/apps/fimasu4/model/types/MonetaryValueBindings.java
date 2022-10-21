package de.perdian.apps.fimasu4.model.types;

import java.math.BigDecimal;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;

public class MonetaryValueBindings {

    public static MonetaryValue multiply(MonetaryValue sourceValue, ObjectProperty<BigDecimal> numberOfStocks) {
        MonetaryValue targetValue = new MonetaryValue();
        targetValue.getCurrency().bind(sourceValue.getCurrency());
        ChangeListener<Object> targetValueRecomputeListener = (o, oldValue, newValue) -> {
            BigDecimal sourceValueAmount = sourceValue.getAmount().getValue();
            BigDecimal numberOfStocksAmount = numberOfStocks.getValue();
            if (sourceValueAmount == null || numberOfStocksAmount == null) {
                targetValue.getAmount().setValue(null);
            } else {
                targetValue.getAmount().setValue(sourceValueAmount.multiply(numberOfStocksAmount));
            }
        };
        sourceValue.getAmount().addListener(targetValueRecomputeListener);
        numberOfStocks.addListener(targetValueRecomputeListener);
        return targetValue;
    }

}
