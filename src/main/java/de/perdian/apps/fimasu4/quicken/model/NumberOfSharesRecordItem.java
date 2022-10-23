package de.perdian.apps.fimasu4.quicken.model;

public class NumberOfSharesRecordItem extends AbstractLongDoubleRecordItem {

    public NumberOfSharesRecordItem(Number numberOfShares) {
        super('Q', numberOfShares);
    }

}
