package de.perdian.apps.fimasu4.quicken.model;

import java.time.LocalDate;

public class ValutaDateRecordItem extends AbstractDateRecordItem {

    public ValutaDateRecordItem(LocalDate date) {
        super('V', date);
    }

}
