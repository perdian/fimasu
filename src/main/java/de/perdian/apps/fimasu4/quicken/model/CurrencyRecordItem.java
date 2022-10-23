package de.perdian.apps.fimasu4.quicken.model;

import org.apache.commons.lang3.StringUtils;

public class CurrencyRecordItem extends AbstractStringRecordItem {

    public CurrencyRecordItem(String currencyCode) {
        super('F', StringUtils.defaultIfEmpty(currencyCode, "EUR"));
    }

}
