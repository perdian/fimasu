package de.perdian.apps.fimasu.fx.support.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.StringConverter;

public class LocalDateStringConverter extends StringConverter<LocalDate> {

    private static final Logger log = LoggerFactory.getLogger(LocalDateStringConverter.class);

    private DateTimeFormatter formatter = null;
    private List<DateTimeFormatter> parsers = null;

    public LocalDateStringConverter() {
        this(DateTimeFormatter.ofPattern("yyyyMMdd"), List.of(DateTimeFormatter.ofPattern("yyyyMMdd"), DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("dd.MM.yyyy"), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    public LocalDateStringConverter(DateTimeFormatter formatter, List<DateTimeFormatter> parsers) {
        this.setFormatter(formatter);
        this.setParsers(parsers);
    }

    @Override
    public String toString(LocalDate object) {
        return object == null ? null : this.getFormatter().format(object);
    }

    @Override
    public LocalDate fromString(String string) {

        for (DateTimeFormatter parser : this.getParsers()) {
            try {
                return LocalDate.parse(string, parser);
            } catch (Exception e) {
                log.trace("Cannot parse date '{}' using formatter '{}'", string, parser);
            }
        }

        log.trace("Cannot parse date '{}' using formatters", string);
        return null;

    }

    private DateTimeFormatter getFormatter() {
        return this.formatter;
    }
    private void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    private List<DateTimeFormatter> getParsers() {
        return this.parsers;
    }
    private void setParsers(List<DateTimeFormatter> parsers) {
        this.parsers = parsers;
    }

}
