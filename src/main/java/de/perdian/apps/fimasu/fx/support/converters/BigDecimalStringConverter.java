package de.perdian.apps.fimasu.fx.support.converters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javafx.util.StringConverter;

public class BigDecimalStringConverter extends StringConverter<BigDecimal> {

    private int precisionDigits = 2;
    private NumberFormat numberFormat = null;

    public BigDecimalStringConverter(int precisionDigits) {
        StringBuilder decimalFormatPattern = new StringBuilder("#,##0.");
        for (int i=0; i < precisionDigits; i++) {
            decimalFormatPattern.append("0");
        }
        this.setNumberFormat(new DecimalFormat(decimalFormatPattern.toString(), new DecimalFormatSymbols(Locale.GERMANY)));
        this.setPrecisionDigits(precisionDigits);
    }

    @Override
    public String toString(BigDecimal object) {
        return object == null ? "" : this.getNumberFormat().format(object);
    }

    @Override
    public BigDecimal fromString(String string) {
        try {
            return new BigDecimal(this.getNumberFormat().parse(string).doubleValue()).setScale(this.getPrecisionDigits(), RoundingMode.HALF_UP);
        } catch (ParseException e) {
            return null;
        }
    }

    private int getPrecisionDigits() {
        return this.precisionDigits;
    }
    private void setPrecisionDigits(int precisionDigits) {
        this.precisionDigits = precisionDigits;
    }

    private NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    private void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

}
