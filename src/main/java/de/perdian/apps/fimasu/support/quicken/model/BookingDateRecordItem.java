package de.perdian.apps.fimasu.support.quicken.model;

import java.time.LocalDate;

public class BookingDateRecordItem extends AbstractDateRecordItem {

    public BookingDateRecordItem(LocalDate date) {
        super('D', date);
    }

}
