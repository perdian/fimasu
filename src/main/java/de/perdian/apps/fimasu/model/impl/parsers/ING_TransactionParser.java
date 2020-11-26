package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.apps.fimasu.model.support.LineProcessorList;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexAmountWithCurrencyLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexAmountWithCurrencyLineProcessor.Mode;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLineProcessor;
import de.perdian.apps.fimasu.model.support.lineprocessors.RegexGroupsLookup;

public class ING_TransactionParser extends AbstractPdfTransactionParser<StockChangeTransaction> {

    @Override
    public boolean canHandleFile(File documentFile) {
        return (documentFile.getName().indexOf("Direkt_Depot") > -1 && documentFile.getName().indexOf("Abrechnung") > -1 && documentFile.getName().endsWith(".pdf"));
    }

    @Override
    protected StockChangeTransaction createTransaction(String pdfText, File pdfFile) throws Exception {
        StockChangeTransaction transaction = new StockChangeTransaction();
        transaction.getType().setValue(StockChangeType.BUY);
        return transaction;
    }

    @Override
    protected LineProcessorList createLineProcessorList(StockChangeTransaction transaction, File pdfFile) {
        LineProcessorList lineProcessorList = new LineProcessorList();
        lineProcessorList.add(
            new RegexGroupsLineProcessor("ISIN \\(WKN\\)\\s+(?<isin>[A-Z]{2}[0-9]+)\\s+\\((?<wkn>[A-Z0-9]+)\\)")
                .setString(RegexGroupsLookup.byName("wkn"), transaction.getWkn())
                .setString(RegexGroupsLookup.byName("isin"), transaction.getIsin())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Wertpapierbezeichnung (?<title>.*)")
                .setString(RegexGroupsLookup.byName("title"), transaction.getTitle())
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Nominale Stück (?<numberOfShares>.*)")
                .setNumber(RegexGroupsLookup.byName("numberOfShares"), transaction.getNumberOfShares(), ING.AMOUNT_FORMAT)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Kurs (?<marketCurrency>[A-Z0-9]{3}) (?<marketPrice>.*?)")
                .setNumber(RegexGroupsLookup.byName("marketPrice"), transaction.getMarketPrice(), ING.AMOUNT_FORMAT)
                .setString(RegexGroupsLookup.byName("marketCurrency"), transaction.getMarketCurrency())
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
            new RegexAmountWithCurrencyLineProcessor("Rabatt (?<currency>[A-Z0-9]{3}) (?<sign>\\-?)\\s?(?<amount>.*)", transaction.getChargesAmount(), ING.AMOUNT_FORMAT, transaction.getChargesCurrency(), Mode.ADD)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("Provision (?<currency>[A-Z0-9]{3}) (?<sign>\\-?)\\s?(?<amount>.*)", transaction.getChargesAmount(), ING.AMOUNT_FORMAT, transaction.getChargesCurrency(), Mode.ADD)
        );
        lineProcessorList.add(
            new RegexGroupsLineProcessor("Valuta (?<valutaDate>.*?)")
                .setDate(RegexGroupsLookup.byName("valutaDate"), transaction.getValutaDate(), ING.DATE_FORMAT)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("Endbetrag zu Ihren Lasten (?<currency>[A-Z0-9]{3}) (?<sign>\\-?)\\s?(?<amount>.*)", null, ING.AMOUNT_FORMAT, transaction.getBookingCurrency(), Mode.SET)
        );
        lineProcessorList.add(
            new RegexAmountWithCurrencyLineProcessor("umger\\. zum Devisenkurs \\((?<currency>[A-Z]{3}) \\= (?<amount>.*?)\\).*?", transaction.getMarketExchangeRate(), ING.AMOUNT_FORMAT, null, Mode.SET)
        );
        return lineProcessorList;
    }

}
