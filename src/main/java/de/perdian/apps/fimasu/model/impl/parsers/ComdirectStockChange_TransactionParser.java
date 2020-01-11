package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionParserHelper;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;

public class ComdirectStockChange_TransactionParser extends AbstractPdfTransactionParser<StockChangeTransaction> {

    private static final Logger log = LoggerFactory.getLogger(ComdirectStockChange_TransactionParser.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMANY);
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00000", new DecimalFormatSymbols(Locale.GERMANY));

    @Override
    protected boolean checkFileName(String fileName) {
        return super.checkFileName(fileName) && fileName.startsWith("Wertpapierabrechnung_");
    }

    @Override
    public List<Transaction> parseTransactionsFromFile(File documentFile) {
        try {
            return super.parseTransactionsFromFile(documentFile);
        } catch (Exception e) {
            log.warn("Cannot analyze Comdirect file at: {}", documentFile.getAbsolutePath(), e);
            return Collections.emptyList();
        }
    }

    @Override
    protected StockChangeTransaction createTransaction(String sourceFileName) {
        StockChangeTransaction transaction = new StockChangeTransaction();
        transaction.getType().setValue(sourceFileName.startsWith("Wertpapierabrechnung_Verkauf") ? StockChangeType.SELL : StockChangeType.BUY);
        return transaction;
    }

    @Override
    protected void updateTransactionFromLine(StockChangeTransaction transaction, String line) {
        TransactionParserHelper.regexSet(line, "Geschäftstag\\s+\\:\\s+(\\d+\\.\\d+\\.\\d+)\\s.*?", transaction.getBookingDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        TransactionParserHelper.regexSet(line, "Die Belastung erfolgt mit Valuta (.*?) auf Konto .*", transaction.getValutaDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        TransactionParserHelper.regexSetNumberOfSharesPlusAmountWithCurrency(line, "St\\.\\s+(?<numberOfShares>.*?)\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getNumberOfShares(), transaction.getMarketPrice(), NUMBER_FORMAT, transaction.getMarketCurrency());
        TransactionParserHelper.regexSet(line, "Stk\\..*?\\d+.*?\\s+(.*?)\\,.*?WKN.*?\\:\\s+.*?\\s+\\/\\s+.*?", transaction.getTitle());
        TransactionParserHelper.regexSet(line, "Stk\\..*?\\d+.*?\\s+.*?\\,.*?WKN.*?\\:\\s+(.*?)\\s+\\/\\s+.*?", transaction.getWkn());
        TransactionParserHelper.regexSet(line, "Stk\\..*?\\d+.*?\\s+.*?\\,.*?WKN.*?\\:\\s+.*?\\s+\\/\\s+(.*?)", transaction.getIsin());
        TransactionParserHelper.regexSet(line, ".*?Umrechnung zum Devisenkurs\\s+(.*?)\\s+[A-Z]{3}\\s+.*?", transaction.getMarketExchangeRate(), string -> NUMBER_FORMAT.parse(string));
        TransactionParserHelper.regexSetAmountWithCurrency(line, ".*?Reduktion Kaufaufschlag.*?(?<currency>[A-Z]{3})\\s+(?<amount>.*?)\\-", transaction.getChargesAmount(), -1d, NUMBER_FORMAT, transaction.getChargesCurrency());
        TransactionParserHelper.regexAddAmountWithCurrency(line, "Abwickl.entgelt Clearstream\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getChargesAmount(), NUMBER_FORMAT, transaction.getChargesCurrency());
        TransactionParserHelper.regexAddAmountWithCurrency(line, "Provision\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getChargesAmount(), NUMBER_FORMAT, transaction.getChargesCurrency());
        TransactionParserHelper.regexAddAmountWithCurrency(line, "Börsenplatzabhäng. Entgelt.\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)", transaction.getChargesAmount(), NUMBER_FORMAT, transaction.getChargesCurrency());
    }

}
