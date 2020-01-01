package de.perdian.apps.fimasu.model.transactions.persistence;

import org.w3c.dom.Element;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;

class PersistenceHelperTransactions {

    static void appendTransaction_StockChange(Element transactionElement, StockChangeTransaction transaction) {
        PersistenceHelper.appendAttribute(transactionElement, "type", transaction.getType().getValue().name());
        PersistenceHelper.appendAttribute(transactionElement, "marketCurrency", transaction.getMarketValue().getValue().getCurrency());
        PersistenceHelper.appendAttribute(transactionElement, "bookingCurrency", transaction.getBookingCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "bookingValue", transaction.getBookingValue().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "charges", transaction.getCharges().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "financeTax", transaction.getFinanceTax().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "solidarityTax", transaction.getSolidarityTax().getValue());
    }

}
