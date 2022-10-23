package de.perdian.apps.fimasu.model.parsers.impl;

import java.io.File;

import de.perdian.apps.fimasu.model.parsers.support.LineProcessorList;
import de.perdian.apps.fimasu.model.parsers.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu.model.parsers.support.lineprocessors.RegexGroupsLookup;
import de.perdian.apps.fimasu.model.types.Transaction;
import de.perdian.apps.fimasu.model.types.TransactionType;

public class Comdirect_TransactionParser extends AbstractPdfTransactionParser {

    @Override
    public boolean canHandleFile(File documentFile) {
        return documentFile.getName().startsWith("Wertpapierabrechnung_") && documentFile.getName().endsWith(".pdf");
    }

    @Override
    public Transaction createTransaction(String pdfText, File pdfFile) {
        Transaction transaction = new Transaction();
        transaction.getType().setValue(pdfFile.getName().startsWith("Wertpapierabrechnung_Verkauf") ? TransactionType.SELL : TransactionType.BUY);
        return transaction;
    }

    @Override
    protected LineProcessorList createLineProcessorList(Transaction transaction, File pdfFile) {
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
                .setNumber(RegexGroupsLookup.byName("numberOfShares"), transaction.getStockCount(), Comdirect.AMOUNT_FORMAT)
                .setNumber(RegexGroupsLookup.byName("amount"), transaction.getStockPrice().getAmount(), Comdirect.AMOUNT_FORMAT)
                .setString(RegexGroupsLookup.byName("currency"), transaction.getStockPrice().getCurrency())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Geschäftstag\\s+\\:\\s+(?<bookingDate>\\d+\\.\\d+\\.\\d+)\\s.*?")
                .setDate(RegexGroupsLookup.byName("bookingDate"), transaction.getBookingDate(), Comdirect.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Stk\\..*?\\d+.*?\\s+(?<title>.*?)\\,.*?WKN.*?\\:\\s+(?<wkn>.*?)\\s+\\/\\s+(?<isin>.*?)")
                .setString(RegexGroupsLookup.byName("title"), transaction.getStockIdentifier().getTitle())
                .setString(RegexGroupsLookup.byName("wkn"), transaction.getStockIdentifier().getWkn())
                .setString(RegexGroupsLookup.byName("isin"), transaction.getStockIdentifier().getIsin())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor(".*?Umrechnung zum Devisenkurs\\s+(?<marketExchangeRate>.*?)\\s+[A-Z]{3}\\s+.*?")
                .setNumber(RegexGroupsLookup.byName("marketExchangeRate"), transaction.getBookingConversionRate(), Comdirect.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor(".*?Reduktion Kaufaufschlag.*?(?<currency>[A-Z]{3})\\s+(?<amount>.*?)\\-")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getAdditionalCharges().getAmount(), Comdirect.AMOUNT_FORMAT, -1)
                .setString(RegexGroupsLookup.byName("currency"), transaction.getAdditionalCharges().getCurrency())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Abwickl.entgelt Clearstream\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getAdditionalCharges().getAmount(), Comdirect.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Provision\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getAdditionalCharges().getAmount(), Comdirect.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Börsenplatzabhäng. Entgelt.\\s+\\:\\s+(?<currency>[A-Z]{3})\\s+(?<amount>.*?)")
                .addNumber(RegexGroupsLookup.byName("amount"), transaction.getAdditionalCharges().getAmount(), Comdirect.AMOUNT_FORMAT)
        );
        return lineProcessorList;
    }

}
