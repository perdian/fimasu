package de.perdian.apps.fimasu4.quicken.model;

public class BookedAmountRecordItem extends AbstractShortDoubleRecordItem {

    public BookedAmountRecordItem(Number value) {
        super('$', value);
    }

}
