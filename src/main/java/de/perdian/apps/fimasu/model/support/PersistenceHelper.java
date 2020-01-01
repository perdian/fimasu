package de.perdian.apps.fimasu.model.support;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu.model.MonetaryValue;

public class PersistenceHelper {

    private static final NumberFormat XML_NUMBER_FORMAT = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY));

    public static <T> void appendAttribute(Element targetElement, String attributeName, String value) {
        if (StringUtils.isNotEmpty(value)) {
            targetElement.setAttribute(attributeName, value);
        }
    }

    public static <T> void appendAttribute(Element targetElement, String attributeName, MonetaryValue monetaryValue) {
        if (monetaryValue.getValue() != 0d) {
            PersistenceHelper.appendAttribute(targetElement, attributeName, XML_NUMBER_FORMAT.format(monetaryValue.getValue().doubleValue()));
            PersistenceHelper.appendAttribute(targetElement, attributeName + "Currency", monetaryValue.getCurrency());
        }
    }

    public static <T> void appendAttribute(Element targetElement, String attributeName, LocalDate date) {
        if (date != null) {
            PersistenceHelper.appendAttribute(targetElement, attributeName, date.toString());
        }
    }

    public static <T> void appendAttribute(Element targetElement, String attributeName, Number number) {
        if (number != null) {
            PersistenceHelper.appendAttribute(targetElement, attributeName, XML_NUMBER_FORMAT.format(number.doubleValue()));
        }
    }

    public static <T> Optional<T> extractAttribute(Element element, String attributeName, Function<String, T> stringConverterFunction) {
        String stringValue = element.getAttribute(attributeName);
        if (StringUtils.isEmpty(stringValue)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(stringConverterFunction.apply(stringValue));
        }
    }

    public static Optional<String> extractAttributeString(Element element, String attributeName) {
        return PersistenceHelper.extractAttribute(element, attributeName, Function.identity());
    }

    public static <E extends Enum<E>> Optional<E> extractAttributeEnum(Element element, String attributeName, Class<E> enumClass) {
        return PersistenceHelper.extractAttributeString(element, attributeName).map(stringValue -> Enum.valueOf(enumClass, stringValue));
    }

    public static Optional<LocalDate> extractAttributeDate(Element transactionElement, String attributeName) {
        return PersistenceHelper.extractAttribute(transactionElement, attributeName, LocalDate::parse);
    }

    public static Optional<Double> extractAttributeDouble(Element transactionElement, String attributeName) {
        return PersistenceHelper.extractAttribute(transactionElement, attributeName, stringValue -> {
            try {
                return XML_NUMBER_FORMAT.parse(stringValue).doubleValue();
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid numeric value: " + stringValue, e);
            }
        });
    }

    public static Optional<MonetaryValue> extractAttributeMonetaryValue(Element transactionElement, String attributeName) {
        double value = PersistenceHelper.extractAttributeDouble(transactionElement, attributeName).orElse(0d);
        String currency = PersistenceHelper.extractAttributeString(transactionElement, attributeName + "Currency").orElse("EUR");
        if (value == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new MonetaryValue(value, currency));
        }
    }

}
