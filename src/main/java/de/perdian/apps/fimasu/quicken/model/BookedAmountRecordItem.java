package de.perdian.apps.fimasu.quicken.model;

public class BookedAmountRecordItem extends AbstractShortDoubleRecordItem {

    public BookedAmountRecordItem(Number value) {
        super('$', value);
    }

}
