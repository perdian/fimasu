package de.perdian.apps.fimasu.quicken.model;

public class NumberOfSharesRecordItem extends AbstractLongDoubleRecordItem {

    public NumberOfSharesRecordItem(Number numberOfShares) {
        super('Q', numberOfShares);
    }

}
