package de.perdian.apps.fimasu.quicken.model;

import java.time.LocalDate;

public class ValutaDateRecordItem extends AbstractDateRecordItem {

    public ValutaDateRecordItem(LocalDate date) {
        super('V', date);
    }

}
