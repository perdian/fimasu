package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.apps.fimasu.model.support.LineProcessorList;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLookup;

public class Comdirect_TransactionParser extends AbstractPdfTransactionParser<StockChangeTransaction> {

    @Override
    public boolean canHandleFile(File documentFile) {
        return documentFile.getName().startsWith("Wertpapierabrechnung_") && documentFile.getName().endsWith(".pdf");
    }

    @Override
    public StockChangeTransaction createTransaction(String pdfText, File pdfFile) {
        StockChangeTransaction transaction = new StockChangeTransaction();
        transaction.getType().setValue(pdfFile.getName().startsWith("Wertpapierabrechnung_Verkauf") ? StockChangeType.SELL : StockChangeType.BUY);
        return transaction;
    }

    @Override
    protected LineProcessorList createLineProcessorList(StockChangeTransaction transaction, File pdfFile) {
        LineProcessorList lineProcessorList = new LineProcessorList();
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Geschäftstag\\s+\\:\\s+(?<bookingDate>\\d+\\.\\d+\\.\\d+)\\s.*?")
                .setDate(RegexGroupsLookup.byName("bookingDate"), transaction.getBookingDate(), Comdirect.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Die Belastung erfolgt mit Valuta (?<valutaDate>.*?) auf Konto .*")
                .setDate(RegexGroupsLookup.byName("valutaDate"), transaction.getValutaDate(), Comdirect.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("St\\.\\s+(?<numberOfShares>.*?)\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .setNumber(RegexGroupsLookup.byName("numberOfShares"), transaction.getNumberOfShares(), Comdirect.AMOUNT_FORMAT)
                .setNumber(RegexGroupsLookup.byName("amount"), transaction.getMarketPrice(), Comdirect.AMOUNT_FORMAT)
                .setString(RegexGroupsLookup.byName("currency"), transaction.getMarketCurrency())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Geschäftstag\\s+\\:\\s+(?<bookingDate>\\d+\\.\\d+\\.\\d+)\\s.*?")
                .setDate(RegexGroupsLookup.byName("bookingDate"), transaction.getBookingDate(), Comdirect.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Stk\\..*?\\d+.*?\\s+(?<title>.*?)\\,.*?WKN.*?\\:\\s+(?<wkn>.*?)\\s+\\/\\s+(?<isin>.*?)")
                .setString(RegexGroupsLookup.byName("title"), transaction.getTitle())
                .setString(RegexGroupsLookup.byName("wkn"), transaction.getWkn())
                .setString(RegexGroupsLookup.byName("isin"), transaction.getIsin())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor(".*?Umrechnung zum Devisenkurs\\s+(?<marketExchangeRate>.*?)\\s+[A-Z]{3}\\s+.*?")
                .setNumber(RegexGroupsLookup.byName("marketExchangeRate"), transaction.getMarketExchangeRate(), Comdirect.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor(".*?Reduktion Kaufaufschlag.*?(?<currency>[A-Z]{3})\\s+(?<amount>.*?)\\-")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getChargesAmount(), Comdirect.AMOUNT_FORMAT, -1)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Abwickl.entgelt Clearstream\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getChargesAmount(), Comdirect.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Provision\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getChargesAmount(), Comdirect.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Börsenplatzabhäng. Entgelt.\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getChargesAmount(), Comdirect.AMOUNT_FORMAT)
        );
        return lineProcessorList;
    }

}
