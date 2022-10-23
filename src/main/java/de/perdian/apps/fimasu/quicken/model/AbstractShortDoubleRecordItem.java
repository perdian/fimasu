package de.perdian.apps.fimasu.quicken.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import de.perdian.apps.fimasu.quicken.RecordItem;

public abstract class AbstractShortDoubleRecordItem implements RecordItem {

    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    private char code = '0';
    private double value = 0d;

    public AbstractShortDoubleRecordItem(char code, Number value) {
        this.setCode(code);
        this.setValue(value == null ? 0d : value.doubleValue());
    }

    @Override
    public String toQifString() {
        return this.getCode() + NUMBER_FORMAT.format(this.getValue());
    }

    private char getCode() {
        return this.code;
    }
    private void setCode(char code) {
        this.code = code;
    }

    private double getValue() {
        return this.value;
    }
    private void setValue(double value) {
        this.value = value;
    }

}
