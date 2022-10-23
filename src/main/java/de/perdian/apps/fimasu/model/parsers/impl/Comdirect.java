package de.perdian.apps.fimasu.model.parsers.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class Comdirect {

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMANY);
    static final NumberFormat AMOUNT_FORMAT = new DecimalFormat("#,##0.00000", new DecimalFormatSymbols(Locale.GERMANY));

}
