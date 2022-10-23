package de.perdian.apps.fimasu4.model.parsers.impl;

import java.io.File;

import de.perdian.apps.fimasu4.model.parsers.support.LineProcessorList;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexAmountWithCurrencyLineProcessor;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexAmountWithCurrencyLineProcessor.Mode;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu4.model.parsers.support.lineprocessors.RegexGroupsLookup;
import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionType;

public class ING_TransactionParser extends AbstractPdfTransactionParser {

    @Override
    public boolean canHandleFile(File documentFile) {
        return (documentFile.getName().indexOf("Direkt_Depot") > -1 && documentFile.getName().indexOf("Abrechnung") > -1 && documentFile.getName().endsWith(".pdf"));
    }

    @Override
    protected Transaction createTransaction(String pdfText, File pdfFile) throws Exception {
        Transaction transaction = new Transaction();
        transaction.getType().setValue(TransactionType.BUY);
        return transaction;
    }

    @Override
    protected LineProcessorList createLineProcessorList(Transaction transaction, File pdfFile) {
        LineProcessorList lineProcessorList = new LineProcessorList();
        lineProcessorList.add(
            new RegexGroupsLineProcessor("ISIN \\(WKN\\)\\s+(?<isin>[A-Z]{2}[0-9]+)\\s+\\((?<wkn>[A-Z0-9]+)\\)")
                .setString(RegexGroupsLookup.byName("wkn"), transaction.getStockIdentifier().getWkn())
                .setString(RegexGroupsLookup.byName("isin"), transaction.getStockIdentifier().getIsin())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Wertpapierbezeichnung (?<title>.*)")
                .setString(RegexGroupsLookup.byName("title"), transaction.getStockIdentifier().getTitle())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Nominale Stück (?<numberOfShares>.*)")
                .setNumber(RegexGroupsLookup.byName("numberOfShares"), transaction.getStockCount(), ING.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Kurs (?<marketCurrency>[A-Z0-9]{3}) (?<marketPrice>.*?)")
                .setNumber(RegexGroupsLookup.byName("marketPrice"), transaction.getStockPrice().getAmount(), ING.AMOUNT_FORMAT)
                .setString(RegexGroupsLookup.byName("marketCurrency"), transaction.getStockPrice().getCurrency())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Ausführungstag \\/ \\-zeit\\s+(?<bookingDate>[0-9]+\\.[0-9]+\\.[0-9]+).*?")
                .setDate(RegexGroupsLookup.byName("bookingDate"), transaction.getBookingDate(), ING.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Ausführungstag\\s+(?<bookingDate>[0-9]+\\.[0-9]+\\.[0-9]+).*?")
                .setDate(RegexGroupsLookup.byName("bookingDate"), transaction.getBookingDate(), ING.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("Rabatt (?<currency>[A-Z0-9]{3}) (?<sign>\\-?)\\s?(?<amount>.*)", transaction.getAdditionalCharges().getAmount(), ING.AMOUNT_FORMAT, transaction.getAdditionalCharges().getCurrency(), Mode.ADD)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("Provision (?<currency>[A-Z0-9]{3}) (?<sign>\\-?)\\s?(?<amount>.*)", transaction.getAdditionalCharges().getAmount(), ING.AMOUNT_FORMAT, transaction.getAdditionalCharges().getCurrency(), Mode.ADD)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Valuta (?<valutaDate>.*?)")
                .setDate(RegexGroupsLookup.byName("valutaDate"), transaction.getValutaDate(), ING.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("Endbetrag zu Ihren Lasten (?<currency>[A-Z0-9]{3}) (?<sign>\\-?)\\s?(?<amount>.*)", null, ING.AMOUNT_FORMAT, transaction.getBookingValue().getCurrency(), Mode.SET)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("umger\\. zum Devisenkurs \\((?<currency>[A-Z]{3}) \\= (?<amount>.*?)\\).*?", transaction.getBookingConversionRate(), ING.AMOUNT_FORMAT, null, Mode.SET)
        );
        return lineProcessorList;
    }

}
