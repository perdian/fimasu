package de.perdian.apps.qifgenerator.fx.support.components.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.StringConverter;

public class LocalDateStringConverter extends StringConverter<LocalDate> {

    private static final Logger log = LoggerFactory.getLogger(LocalDateStringConverter.class);

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final List<DateTimeFormatter> dateTimeParsers = Arrays.asList(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyyMMdd"),
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    @Override
    public String toString(LocalDate object) {
        return object == null ? null : this.getDateTimeFormatter().format(object);
    }

    @Override
    public LocalDate fromString(String string) {

        for (DateTimeFormatter parser : this.getDateTimeParsers()) {
            try {
                return LocalDate.parse(string, parser);
            } catch (Exception e) {
                log.trace("Cannot parse date '{}' using formatter '{}'", string, parser);
            }
        }

        log.trace("Cannot parse date '{}' using formatters", string);
        return null;

    }

    private DateTimeFormatter getDateTimeFormatter() {
        return this.dateTimeFormatter;
    }
    private List<DateTimeFormatter> getDateTimeParsers() {
        return this.dateTimeParsers;
    }

}