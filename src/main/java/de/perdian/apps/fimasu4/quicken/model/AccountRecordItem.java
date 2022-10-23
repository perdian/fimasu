package de.perdian.apps.fimasu4.quicken.model;

public class AccountRecordItem extends AbstractStringRecordItem {

    public AccountRecordItem(String value) {
        super('L', "|[" + value + "]");
    }

}
