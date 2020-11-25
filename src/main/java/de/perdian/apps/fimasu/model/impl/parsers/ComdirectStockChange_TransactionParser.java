package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import de.perdian.apps.fimasu.model.TransactionParserHelper;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;

public class ComdirectStockChange_TransactionParser extends AbstractLineBasedPdfTransactionParser<StockChangeTransaction> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMANY);
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00000", new DecimalFormatSymbols(Locale.GERMANY));

    @Override
    public boolean canHandleFile(File documentFile) {
        return documentFile.getName().startsWith("Wertpapierabrechnung_") && documentFile.getName().endsWith(".pdf");
    }

    @Override
    protected StockChangeTransaction createTransactionInstance(String pdfText, File pdfFile) {
        StockChangeTransaction transaction = new StockChangeTransaction();
        transaction.getType().setValue(pdfFile.getName().startsWith("Wertpapierabrechnung_Verkauf") ? StockChangeType.SELL : StockChangeType.BUY);
        return transaction;
    }

    @Override
    protected void updateTransactionFromLine(StockChangeTransaction transaction, String pdfLine, File pdfFile) {
        TransactionParserHelper.regexSet(pdfLine, "Geschäftstag\\s+\\:\\s+(\\d+\\.\\d+\\.\\d+)\\s.*?", transaction.getBookingDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        TransactionParserHelper.regexSet(pdfLine, "Die Belastung erfolgt mit Valuta (.*?) auf Konto .*", transaction.getValutaDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        TransactionParserHelper.regexSetNumberOfSharesPlusAmountWithCurrency(pdfLine, "St\\.\\s+(?<numberOfShares>.*?)\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getNumberOfShares(), transaction.getMarketPrice(), NUMBER_FORMAT, transaction.getMarketCurrency());
        TransactionParserHelper.regexSet(pdfLine, "Stk\\..*?\\d+.*?\\s+(.*?)\\,.*?WKN.*?\\:\\s+.*?\\s+\\/\\s+.*?", transaction.getTitle());
        TransactionParserHelper.regexSet(pdfLine, "Stk\\..*?\\d+.*?\\s+.*?\\,.*?WKN.*?\\:\\s+(.*?)\\s+\\/\\s+.*?", transaction.getWkn());
        TransactionParserHelper.regexSet(pdfLine, "Stk\\..*?\\d+.*?\\s+.*?\\,.*?WKN.*?\\:\\s+.*?\\s+\\/\\s+(.*?)", transaction.getIsin());
        TransactionParserHelper.regexSet(pdfLine, ".*?Umrechnung zum Devisenkurs\\s+(.*?)\\s+[A-Z]{3}\\s+.*?", transaction.getMarketExchangeRate(), string -> NUMBER_FORMAT.parse(string));
        TransactionParserHelper.regexSetAmountWithCurrency(pdfLine, ".*?Reduktion Kaufaufschlag.*?(?<currency>[A-Z]{3})\\s+(?<amount>.*?)\\-", transaction.getChargesAmount(), -1d, NUMBER_FORMAT, transaction.getChargesCurrency());
        TransactionParserHelper.regexAddAmountWithCurrency(pdfLine, "Abwickl.entgelt Clearstream\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getChargesAmount(), NUMBER_FORMAT, transaction.getChargesCurrency());
        TransactionParserHelper.regexAddAmountWithCurrency(pdfLine, "Provision\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getChargesAmount(), NUMBER_FORMAT, transaction.getChargesCurrency());
        TransactionParserHelper.regexAddAmountWithCurrency(pdfLine, "Börsenplatzabhäng. Entgelt.\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getChargesAmount(), NUMBER_FORMAT, transaction.getChargesCurrency());
    }

}
