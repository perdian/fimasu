package de.perdian.apps.fimasu.model.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableList;

class TransactionHelper {

    static final String DEFAULT_CURRENCY = "EUR";

    static ChangeListener<Object> createChangeListenerDelegate(List<ChangeListener<Object>> changeListeners) {
        return (o, oldValue, newValue) -> {
            for (ChangeListener<Object> changeListenerDelegee : changeListeners) {
                changeListenerDelegee.changed(o, oldValue, newValue);
            }
        };
    }

    static void recomputeValutaDate(ObjectProperty<LocalDate> valutaDateProperty, ObservableObjectValue<LocalDate> bookingDateProperty) {
        LocalDate newBookingDate = bookingDateProperty.getValue();
        if (newBookingDate != null && valutaDateProperty.getValue() == null) {
            LocalDate computedValutaDate = newBookingDate.plusDays(2);
            while (DayOfWeek.SATURDAY.equals(computedValutaDate.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(computedValutaDate.getDayOfWeek())) {
                computedValutaDate = computedValutaDate.plusDays(1);
            }
            valutaDateProperty.setValue(computedValutaDate);
        }
    }

    static void recomputeStockValue(ObjectProperty<BigDecimal> stockValue, ObservableObjectValue<BigDecimal> stockPrice, ObservableObjectValue<BigDecimal> stockCount) {
        BigDecimal stockPriceDecimal = stockPrice.getValue();
        BigDecimal stockCountDecimal = stockCount.getValue();
        if (stockPriceDecimal == null || stockCountDecimal == null) {
            stockValue.setValue(null);
        } else {
            stockValue.setValue(stockPriceDecimal.multiply(stockCountDecimal).setScale(5, RoundingMode.HALF_UP));
        }
    }

    static void recomputeBookingInput(ObjectProperty<ObservableObjectValue<BigDecimal>> bookingInputValueProperty, ObjectProperty<ObservableStringValue> bookingInputCurrencyProperty, ObjectProperty<TransactionType> transactionType, ObjectProperty<BigDecimal> stockCount, ObjectProperty<BigDecimal> stockPricePerUnit, ObservableObjectValue<BigDecimal> stockValue, ObservableStringValue stockCurrency, ObjectProperty<BigDecimal> payoutValue, ObservableStringValue payoutCurrency, ObjectProperty<BigDecimal> bookingValue, StringProperty bookingCurrency, ObjectProperty<BigDecimal> bookingConversionRate) {
        if (TransactionType.PAYOUT.equals(transactionType.getValue())) {
            bookingInputValueProperty.setValue(payoutValue);
            bookingInputCurrencyProperty.setValue(payoutCurrency);
            stockCount.setValue(null);
            stockPricePerUnit.setValue(null);
            if (StringUtils.isEmpty(bookingCurrency.getValue())) {
                bookingCurrency.setValue(payoutCurrency.getValue());
            }
        } else if (TransactionType.BUY.equals(transactionType.getValue()) || TransactionType.SELL.equals(transactionType.getValue())) {
            bookingInputValueProperty.setValue(stockValue);
            bookingInputCurrencyProperty.setValue(stockCurrency);
            payoutValue.setValue(null);
            if (StringUtils.isEmpty(bookingCurrency.getValue())) {
                bookingCurrency.setValue(stockCurrency.getValue());
            }
        } else {
            bookingInputValueProperty.setValue(null);
            bookingInputCurrencyProperty.setValue(null);
        }
        TransactionHelper.recomputeBookingValue(bookingValue, bookingCurrency, bookingInputValueProperty.getValue(), bookingInputCurrencyProperty.getValue(), bookingConversionRate);
    }

    static void recomputeBookingValue(ObjectProperty<BigDecimal> bookingValue, StringProperty bookingCurrency, ObservableObjectValue<BigDecimal> inputValue, ObservableStringValue inputCurrency, ObservableObjectValue<BigDecimal> bookingConversionRate) {
        BigDecimal inputValueDecimal = inputValue == null ? null : inputValue.getValue();
        String inputCurrencyString = inputCurrency == null ? null : inputCurrency.getValue();
        if (inputValueDecimal == null) {
            bookingValue.setValue(null);
        } else {
            if (StringUtils.isEmpty(bookingCurrency.getValue())) {
                bookingCurrency.setValue(inputCurrencyString);
            }
            String bookingCurrencyString = bookingCurrency.getValue();
            if (Objects.equals(inputCurrencyString, bookingCurrencyString)) {
                bookingValue.setValue(inputValueDecimal);
            } else {
                BigDecimal bookingConversionRateDecimal = bookingConversionRate.getValue();
                if (bookingConversionRateDecimal == null) {
                    bookingValue.setValue(null);
                } else {
                    bookingValue.setValue(inputValueDecimal.divide(bookingConversionRateDecimal, RoundingMode.HALF_UP).setScale(5, RoundingMode.HALF_UP));
                }
            }
        }
    }

    static void recomputeTotalValue(ObjectProperty<BigDecimal> totalValue, StringProperty totalCurrency, ObservableObjectValue<BigDecimal> bookingValue, ObservableStringValue bookingCurrency, ObservableObjectValue<BigDecimal> bookingConversionRate, ObservableObjectValue<BigDecimal> chargesValue, ObservableStringValue chargesCurrency, ObservableObjectValue<BigDecimal> financeTaxValue, ObservableStringValue financeTaxCurrency, ObservableObjectValue<BigDecimal> solidarityTaxValue, ObservableStringValue solidarityTaxCurrency, ObservableObjectValue<TransactionType> transactionType) {
        totalCurrency.setValue(bookingCurrency.getValue());
        if (bookingValue.getValue() == null) {
            totalValue.setValue(bookingValue.getValue());
        } else {
            BigDecimal totalValueDecimal = TransactionHelper.computeAdditionalValue(bookingValue.getValue(), bookingCurrency.getValue(), null, bookingCurrency.getValue(), bookingConversionRate.getValue());
            totalValueDecimal = totalValueDecimal.add(TransactionHelper.computeAdditionalValue(chargesValue.getValue(), chargesCurrency.getValue(), transactionType.getValue(), bookingCurrency.getValue(), bookingConversionRate.getValue()));
            totalValueDecimal = totalValueDecimal.add(TransactionHelper.computeAdditionalValue(financeTaxValue.getValue(), financeTaxCurrency.getValue(), transactionType.getValue(), bookingCurrency.getValue(), bookingConversionRate.getValue()));
            totalValueDecimal = totalValueDecimal.add(TransactionHelper.computeAdditionalValue(solidarityTaxValue.getValue(), solidarityTaxCurrency.getValue(), transactionType.getValue(), bookingCurrency.getValue(), bookingConversionRate.getValue()));
            totalValue.setValue(totalValueDecimal);
        }
    }

    private static BigDecimal computeAdditionalValue(BigDecimal additionalValue, String additionalCurrency, TransactionType transactionType, String bookingCurrency, BigDecimal bookingConversionRate) {
        if (additionalValue == null) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal additionalChangeFactor = transactionType == null ? BigDecimal.ONE : BigDecimal.valueOf(transactionType.getChargesFactor());
            if (Objects.equals(additionalCurrency, bookingCurrency) || StringUtils.isEmpty(additionalCurrency)) {
                return additionalValue.multiply(additionalChangeFactor).setScale(5, RoundingMode.HALF_UP);
            } else if (bookingConversionRate == null) {
                return BigDecimal.ZERO;
            } else {
                return additionalValue
                    .multiply(additionalChangeFactor)
                    .divide(bookingConversionRate, RoundingMode.HALF_UP)
                    .setScale(5, RoundingMode.HALF_UP);
            }
        }
    }

    static void recomputeAvailableCurrencies(ObservableList<String> availableCurrencies, ObservableStringValue bookingCurrency, ObservableStringValue bookingInputCurrency) {
        Set<String> newCurrenciesSet = new LinkedHashSet<>();
        if (bookingCurrency != null && StringUtils.isNotEmpty(bookingCurrency.getValue())) {
            newCurrenciesSet.add(bookingCurrency.getValue());
        }
        if (bookingInputCurrency != null && StringUtils.isNotEmpty(bookingInputCurrency.getValue())) {
            newCurrenciesSet.add(bookingInputCurrency.getValue());
        }
        if (newCurrenciesSet.isEmpty()) {
            newCurrenciesSet.add(TransactionHelper.DEFAULT_CURRENCY);
        }
        if (newCurrenciesSet.size() != availableCurrencies.size() || !availableCurrencies.containsAll(newCurrenciesSet)) {
            availableCurrencies.setAll(newCurrenciesSet);
        }
    }

}
