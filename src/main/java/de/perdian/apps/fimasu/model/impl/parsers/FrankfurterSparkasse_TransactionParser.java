package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;
import java.text.DecimalFormat;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.apps.fimasu.model.support.LineProcessorList;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexAmountWithCurrencyLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexAmountWithCurrencyLineProcessor.Mode;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLookup;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexSimpleMatchLineProcessor;

public class FrankfurterSparkasse_TransactionParser extends AbstractPdfTransactionParser<StockChangeTransaction> {

    @Override
    public boolean canHandleFile(File documentFile) {
        return FrankfurterSparkasse.FILE_NAME_PATTERN.matcher(documentFile.getName()).matches();
    }

    @Override
    protected StockChangeTransaction createTransaction(String pdfText, File pdfFile) throws Exception {
        return new StockChangeTransaction();
    }

    @Override
    protected LineProcessorList createLineProcessorList(StockChangeTransaction transaction, File pdfFile) {
        LineProcessorList stringExtractor = new LineProcessorList();
        stringExtractor.add(
            new RegexSimpleMatchLineProcessor("^Wertpapier Abrechnung Kauf", line -> transaction.getType().setValue(StockChangeType.BUY))
        );
        stringExtractor.add(
            new RegexSimpleMatchLineProcessor("^Wertpapier Abrechnung Verkauf", line -> transaction.getType().setValue(StockChangeType.SELL))
        );
        stringExtractor.add(
            new RegexGroupsLineProcessor("(?<isin>[A-Z]{2}[A-Z0-9]{10})\\s+\\((?<wkn>[A-Z0-9]{6})\\)")
                .setString(RegexGroupsLookup.byName("isin"), transaction.getIsin()).setString(RegexGroupsLookup.byName("wkn"), transaction.getWkn())
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Ausführungskurs (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getMarketPrice(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getMarketCurrency(), Mode.SET)
        );
        stringExtractor.add(
            new RegexGroupsLineProcessor("Schlusstag/-Zeit ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?")
                .setDate(RegexGroupsLookup.byIndex(1), transaction.getBookingDate(), FrankfurterSparkasse.DATE_FORMAT)
        );
        stringExtractor.add(
            new RegexGroupsLineProcessor("Schlusstag ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4})")
                .setDate(RegexGroupsLookup.byIndex(1), transaction.getBookingDate(), FrankfurterSparkasse.DATE_FORMAT)
        );
        stringExtractor.add(
            new RegexGroupsLineProcessor("Den Gegenwert buchen wir mit Valuta ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?")
                .setDate(RegexGroupsLookup.byIndex(1), transaction.getValutaDate(), FrankfurterSparkasse.DATE_FORMAT)
        );
        stringExtractor.add(
            new RegexGroupsLineProcessor("Stück (.*?) (.+)")
                .setNumber(RegexGroupsLookup.byIndex(1), transaction.getNumberOfShares(), new DecimalFormat("0.#####"))
                .setString(RegexGroupsLookup.byIndex(2), transaction.getTitle())
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Devisenkurs \\(.*?\\) (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+vom.*?", transaction.getMarketExchangeRate(), FrankfurterSparkasse.AMOUNT_FORMAT, null, Mode.SET)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Kurswert (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getBookingAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getBookingCurrency(), Mode.SET)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Provision .*? vom Kurswert (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getChargesAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getChargesCurrency(), Mode.ADD, () -> 1d)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Kundenbonifikation .*? vom Kurswert (?<amount>[0-9,\\.]+)(?<sign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getChargesAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getChargesCurrency(), Mode.ADD, () -> StockChangeType.SELL.equals(transaction.getType().getValue()) ? 1d : -1)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Kundenbonifikation .*? vom Ausgabeaufschlag (?<amount>[0-9,\\.]+)(?<sign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getChargesAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getChargesCurrency(), Mode.ADD, () -> StockChangeType.SELL.equals(transaction.getType().getValue()) ? 1d : -1)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Ausmachender Betrag (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getTotalAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getBookingCurrency(), Mode.SET)
        );
        return stringExtractor;
    }

}
