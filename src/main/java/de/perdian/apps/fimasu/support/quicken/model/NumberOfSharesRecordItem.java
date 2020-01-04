package de.perdian.apps.fimasu.support.quicken.model;

public class NumberOfSharesRecordItem extends AbstractLongDoubleRecordItem {

    public NumberOfSharesRecordItem(Number numberOfShares) {
        super('Q', numberOfShares);
    }

}
