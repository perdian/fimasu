package de.perdian.apps.fimasu.quicken.model;

public class IsinRecordItem extends AbstractStringRecordItem {

    public IsinRecordItem(String isin) {
        super('@', isin);
    }

}
