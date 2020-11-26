package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.apps.fimasu.model.support.LineProcessorList;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexAmountWithCurrencyLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLookup;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexSimpleMatchLineProcessor;

public class FrankfurterSparkasse_TransactionParser extends AbstractPdfTransactionParser<StockChangeTransaction> {

    private static final NumberFormat AMOUNT_FORMAT = new DecimalFormat("#,###0.00", new DecimalFormatSymbols(Locale.GERMANY));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[0-9]+\\-.*?\\-[0-9]+\\-[0-9]+\\.pdf");

    @Override
    public boolean canHandleFile(File documentFile) {
        return FILE_NAME_PATTERN.matcher(documentFile.getName()).matches();
    }

    @Override
    protected StockChangeTransaction createTransaction(String pdfText, File pdfFile) throws Exception {
        return new StockChangeTransaction();
    }

    @Override
    protected LineProcessorList createLineProcessorList(StockChangeTransaction transaction, File pdfFile) {
        LineProcessorList stringExtractor = new LineProcessorList();
        stringExtractor.add(new RegexSimpleMatchLineProcessor("^Wertpapier Abrechnung Kauf", line -> transaction.getType().setValue(StockChangeType.BUY)));
        stringExtractor.add(new RegexSimpleMatchLineProcessor("^Wertpapier Abrechnung Verkauf", line -> transaction.getType().setValue(StockChangeType.SELL)));
        stringExtractor.add(new RegexGroupsLineProcessor("(?<isin>[A-Z]{2}[A-Z0-9]{10})\\s+\\((?<wkn>[A-Z0-9]{6})\\)").addString(RegexGroupsLookup.byName("isin"), transaction.getIsin()).addString(RegexGroupsLookup.byName("wkn"), transaction.getWkn()));
        stringExtractor.add(new RegexAmountWithCurrencyLineProcessor("Ausführungskurs (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getMarketPrice(), AMOUNT_FORMAT, transaction.getMarketCurrency()));
        stringExtractor.add(new RegexGroupsLineProcessor("Schlusstag/-Zeit ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?").addDate(RegexGroupsLookup.byIndex(1), transaction.getBookingDate(), DATE_FORMAT));
        stringExtractor.add(new RegexGroupsLineProcessor("Den Gegenwert buchen wir mit Valuta ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?").addDate(RegexGroupsLookup.byIndex(1), transaction.getValutaDate(), DATE_FORMAT));
        stringExtractor.add(new RegexGroupsLineProcessor("Stück (.*?) (.+)").addNumber(RegexGroupsLookup.byIndex(1), transaction.getNumberOfShares(), new DecimalFormat("0.#####")).addString(RegexGroupsLookup.byIndex(2), transaction.getTitle()));
        stringExtractor.add(new RegexAmountWithCurrencyLineProcessor("Kurswert (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getMarketAmount(), AMOUNT_FORMAT, transaction.getMarketCurrency()));
        stringExtractor.add(new RegexAmountWithCurrencyLineProcessor("Fremde Abwicklungsgebühr für die Umschreibung von Namensaktien (?<amount>[0-9,\\.]+)(?<sign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getChargesAmount(), AMOUNT_FORMAT, transaction.getChargesCurrency(), () -> StockChangeType.SELL.equals(transaction.getType().getValue()) ? 1d : -1));
        stringExtractor.add(new RegexAmountWithCurrencyLineProcessor("Ausmachender Betrag (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getTotalAmount(), AMOUNT_FORMAT, transaction.getBookingCurrency()));
        return stringExtractor;
    }

}
