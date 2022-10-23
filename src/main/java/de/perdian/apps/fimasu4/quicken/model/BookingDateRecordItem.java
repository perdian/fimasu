package de.perdian.apps.fimasu4.quicken.model;

import java.time.LocalDate;

public class BookingDateRecordItem extends AbstractDateRecordItem {

    public BookingDateRecordItem(LocalDate date) {
        super('D', date);
    }

}
