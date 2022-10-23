package de.perdian.apps.fimasu4.model.parsers.impl;

import java.io.File;
import java.text.DecimalFormat;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.apps.fimasu4.model.parsers.support.LineProcessorList;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexAmountWithCurrencyLineProcessor;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexAmountWithCurrencyLineProcessor.Mode;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexGroupsLookup;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexSimpleMatchLineProcessor;
import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionType;

public class FrankfurterSparkasse_TransactionParser extends AbstractPdfTransactionParser {

    @Override
    public boolean canHandleFile(File documentFile) {
        return FrankfurterSparkasse.FILE_NAME_PATTERN.matcher(documentFile.getName()).matches();
    }

    @Override
    protected Transaction createTransaction(String pdfText, File pdfFile) throws Exception {
        Transaction transaction = new Transaction();
        transaction.getType().setValue(TransactionType.BUY);
        return transaction;
    }

    @Override
    protected LineProcessorList createLineProcessorList(Transaction transaction, File pdfFile) {
        LineProcessorList stringExtractor = new LineProcessorList();
        stringExtractor.add(
            new RegexSimpleMatchLineProcessor("^Wertpapier Abrechnung Kauf", line -> transaction.getType().setValue(TransactionType.BUY))
        );
        stringExtractor.add(
            new RegexSimpleMatchLineProcessor("^Wertpapier Abrechnung Verkauf", line -> transaction.getType().setValue(TransactionType.SELL))
        );
        stringExtractor.add(
            new RegexGroupsLineProcessor("(?<isin>[A-Z]{2}[A-Z0-9]{10})\\s+\\((?<wkn>[A-Z0-9]{6})\\)")
                .setString(RegexGroupsLookup.byName("isin"), transaction.getStockIdentifier().getIsin()).setString(RegexGroupsLookup.byName("wkn"), transaction.getStockIdentifier().getWkn())
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Ausführungskurs (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getStockPrice().getAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getStockPrice().getCurrency(), Mode.SET)
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
                .setNumber(RegexGroupsLookup.byIndex(1), transaction.getStockCount(), new DecimalFormat("0.#####"))
                .setString(RegexGroupsLookup.byIndex(2), transaction.getStockIdentifier().getTitle())
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Devisenkurs \\(.*?\\) (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+vom.*?", transaction.getBookingConversionRate(), FrankfurterSparkasse.AMOUNT_FORMAT, null, Mode.SET)
        );
//        stringExtractor.add(
//            new RegexAmountWithCurrencyLineProcessor("Kurswert (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getBookingAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getBookingCurrency(), Mode.SET)
//        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Provision .*? vom Kurswert (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getAdditionalCharges().getAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getAdditionalCharges().getCurrency(), Mode.ADD, () -> 1d)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Kundenbonifikation .*? vom Kurswert (?<amount>[0-9,\\.]+)(?<sign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getAdditionalCharges().getAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getAdditionalCharges().getCurrency(), Mode.ADD, () -> StockChangeType.SELL.equals(transaction.getType().getValue()) ? 1d : -1)
        );
        stringExtractor.add(
            new RegexAmountWithCurrencyLineProcessor("Kundenbonifikation .*? vom Ausgabeaufschlag (?<amount>[0-9,\\.]+)(?<sign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getAdditionalCharges().getAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getAdditionalCharges().getCurrency(), Mode.ADD, () -> StockChangeType.SELL.equals(transaction.getType().getValue()) ? 1d : -1)
        );
//        stringExtractor.add(
//            new RegexAmountWithCurrencyLineProcessor("Ausmachender Betrag (?<amount>[0-9,\\.]+)(?<IGNOREsign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})", transaction.getTotalAmount(), FrankfurterSparkasse.AMOUNT_FORMAT, transaction.getBookingCurrency(), Mode.SET)
//        );
        return stringExtractor;
    }

}
