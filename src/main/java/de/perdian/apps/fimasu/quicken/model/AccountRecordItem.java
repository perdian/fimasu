package de.perdian.apps.fimasu.quicken.model;

public class AccountRecordItem extends AbstractStringRecordItem {

    public AccountRecordItem(String value) {
        super('L', "|[" + value + "]");
    }

}
