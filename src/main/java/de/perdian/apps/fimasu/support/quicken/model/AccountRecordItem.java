package de.perdian.apps.fimasu.support.quicken.model;

public class AccountRecordItem extends AbstractStringRecordItem {

    public AccountRecordItem(String value) {
        super('L', "|[" + value + "]");
    }

}
