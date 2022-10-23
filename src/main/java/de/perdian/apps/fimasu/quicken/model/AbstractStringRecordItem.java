package de.perdian.apps.fimasu.quicken.model;

import de.perdian.apps.fimasu.quicken.RecordItem;

public abstract class AbstractStringRecordItem implements RecordItem {

    private char code = '0';
    private String value = null;

    protected AbstractStringRecordItem(char code, String value) {
        this.setCode(code);
        this.setValue(value);
    }

    @Override
    public String toQifString() {
        return this.getCode() + this.getValue();
    }

    private char getCode() {
        return this.code;
    }
    private void setCode(char code) {
        this.code = code;
    }

    private String getValue() {
        return this.value;
    }
    private void setValue(String value) {
        this.value = value;
    }

}
