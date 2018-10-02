package de.perdian.apps.qifgenerator.fx.support.components.converters;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.StringConverter;

public class DoubleStringConverter extends StringConverter<Number> {

    private static final Logger log = LoggerFactory.getLogger(DoubleStringConverter.class);
    private NumberFormat numberFormat = null;

    public DoubleStringConverter(String format) {
        this.setNumberFormat(new DecimalFormat(format, new DecimalFormatSymbols(Locale.GERMANY)));
    }

    @Override
    public String toString(Number object) {
        return object == null || object.doubleValue() == 0d ? "" : this.getNumberFormat().format(object);
    }

    @Override
    public Double fromString(String string) {
        if ("-".equalsIgnoreCase(string) || "+".equalsIgnoreCase(string)) {
            return Double.valueOf(0);
        } else if (!StringUtils.isEmpty(string)) {
            try {
                return this.getNumberFormat().parse(string).doubleValue();
            } catch (ParseException e) {
                log.trace("Invalid string value to convert into Double: {}", string, e);
            }
        }
        return null;
    }

    private NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    private void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

}
