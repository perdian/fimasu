package de.perdian.apps.fimasu4.quicken;

public enum RecordListType {

    INVESTMENT("Invst");

    private String qifValue = null;

    private RecordListType(String qifValue) {
        this.setQifValue(qifValue);
    }

    String getQifValue() {
        return this.qifValue;
    }
    private void setQifValue(String qifValue) {
        this.qifValue = qifValue;
    }

}
