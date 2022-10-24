package de.perdian.apps.fimasu.model.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;

class TransactionHelper {

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

}
