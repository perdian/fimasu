package de.perdian.apps.fimasu4.quicken.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.perdian.apps.fimasu4.quicken.RecordItem;

public class AbstractDateRecordItem implements RecordItem {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M.d.yy");

    private char code = 'D';
    private LocalDate date = null;

    protected AbstractDateRecordItem(char code, LocalDate date) {
        this.setCode(code);
        this.setDate(date);
    }

    @Override
    public String toQifString() {
        return this.getCode() + DATE_FORMATTER.format(this.getDate());
    }

    private char getCode() {
        return this.code;
    }
    private void setCode(char code) {
        this.code = code;
    }

    public LocalDate getDate() {
        return this.date;
    }
    private void setDate(LocalDate date) {
        this.date = date;
    }

}
