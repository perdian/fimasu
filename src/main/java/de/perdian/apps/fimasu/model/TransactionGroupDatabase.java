package de.perdian.apps.fimasu.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.perdian.apps.fimasu.model.transactions.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.transactions.impl.transactions.StockChangeTransactionPlugin;
import de.perdian.apps.fimasu.model.transactions.impl.transactions.StockChangeType;
import de.perdian.apps.fimasu.model.transactions.persistence.PersistenceHelperTransactionPlugin;

public class TransactionGroupDatabase {

    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY));

    private Map<String, PersistenceHelperTransactionPlugin> transactionPlugins = null;

    private TransactionGroupDatabase() {
        Map<String, PersistenceHelperTransactionPlugin> transactionPlugins = new HashMap<>();
        transactionPlugins.put(StockChangeTransaction.class.getName(), new StockChangeTransactionPlugin());
        this.setTransactionPlugins(transactionPlugins);
    }

    public List<TransactionGroup> loadTransactionGroups(InputStream inputStream) throws IOException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            NodeList transactionGroupElements = document.getDocumentElement().getElementsByTagName("transactionGroup");
            List<TransactionGroup> transactionGroups = new ArrayList<>(transactionGroupElements.getLength());
            for (int transactionGroupIndex = 0; transactionGroupIndex < transactionGroupElements.getLength(); transactionGroupIndex++) {
                Element transactionGroupElement = (Element)transactionGroupElements.item(transactionGroupIndex);
                transactionGroups.add(this.extractTransactionGroup(transactionGroupElement));
            }
            return transactionGroups;
        } catch (Exception e) {
            throw new IOException("Cannot import transaction groups", e);
        }
    }

    private TransactionGroup extractTransactionGroup(Element transactionGroupElement) {
        Element transactionsElement = (Element)transactionGroupElement.getElementsByTagName("transactions").item(0);
        NodeList transactionElements = transactionsElement.getElementsByTagName("transaction");
        List<Transaction> transactions = new ArrayList<>(transactionElements.getLength());
        for (int transactionIndex = 0; transactionIndex < transactionElements.getLength(); transactionIndex++) {
            Element transactionElement = (Element)transactionElements.item(transactionIndex);
            transactions.add(this.extractTransaction(transactionElement));
        }
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.getTransactions().addAll(transactions);
        transactionGroup

        PersistenceHelper.extractAttribute(transactionGroupElement, "title", transactionGroup.getTitle(), Function.identity());
        PersistenceHelper.extractAttribute(transactionGroupElement, "account", transactionGroup.getAccount(), Function.identity());
        PersistenceHelper.extractAttribute(transactionGroupElement, "targetFilePath", transactionGroup.getTargetFilePath(), Function.identity());
        transactionGroups.add(transactionGroup);
    }

    private static Transaction extractTransaction(Element transactionElement) {
        String transactionClass = transactionElement.getAttribute("class");
        if (StockChangeTransaction.class.getSimpleName().equals(transactionClass)) {
            Transaction transaction = PersistenceHelper.extractTransaction_StockChange(transactionElement);
            transaction.getTitle().setValue(PersistenceHelper.extractAttributeString(transactionElement, "title").orElse(null));
            transaction.getWkn().setValue(PersistenceHelper.extractAttributeString(transactionElement, "wkn").orElse(null));
            transaction.getIsin().setValue(PersistenceHelper.extractAttributeString(transactionElement, "isin").orElse(null));
            return transaction;
        } else {
            throw new IllegalArgumentException("Found illegal transaction class: " + transactionClass);
        }
    }

    private static Transaction extractTransaction_StockChange(Element transactionElement) {
        StockChangeTransaction transaction = new StockChangeTransaction();
        transaction.getType().setValue(PersistenceHelper.extractAttributeEnum(transactionElement, "type", StockChangeType.class).orElse(null));


        PersistenceHelper.extractAttribute(transactionElement, "bookingCurrency", transaction.getBookingCurrency(), Function.identity());
        PersistenceHelper.extractAttribute(transactionElement, "charges", transaction.getCharges(), Double::valueOf);
        PersistenceHelper.extractAttribute(transactionElement, "chargesCurrency", transaction.getChargesCurrency(), Function.identity());
        PersistenceHelper.extractAttribute(transactionElement, "financeTax", transaction.getFinanceTax(), Double::valueOf);
        PersistenceHelper.extractAttribute(transactionElement, "finanxeTaxCurrency", transaction.getFinanceTaxCurrency(), Function.identity());
        PersistenceHelper.extractAttribute(transactionElement, "marketCurrency", transaction.getMarketCurrency(), Function.identity());
        PersistenceHelper.extractAttribute(transactionElement, "solidarityTax", transaction.getSolidarityTax(), Double::valueOf);
        PersistenceHelper.extractAttribute(transactionElement, "solidarityTaxCurrency", transaction.getSolidarityTaxCurrency(), Function.identity());

        return null;
    }

    private static Optional<String> extractAttributeString(Element element, String attributeName) {
        String stringValue = element.getAttribute(attributeName);
        return StringUtils.isEmpty(stringValue) ? Optional.empty() : Optional.of(stringValue);
    }

    private static <E extends Enum<E>> Optional<E> extractAttributeEnum(Element element, String attributeName, Class<E> enumClass) {
        return PersistenceHelper.extractAttributeString(element, attributeName).map(stringValue -> Enum.valueOf(enumClass, stringValue));
    }

    public static void writeTransactionGroups(List<TransactionGroup> transactionGroups, OutputStream outputStream) throws IOException {
        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("transactionGroups");
            document.appendChild(rootElement);
            transactionGroups.forEach(transactionGroup -> PersistenceHelper.appendTransactionGroup(rootElement, transactionGroup, document));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));

        } catch (Exception e) {
            throw new IOException("Cannot export transaction groups", e);
        }
    }

    private static void appendTransactionGroup(Element parentElement, TransactionGroup transactionGroup, Document document) {
        if (transactionGroup.getPersistent().get()) {
            Element transactionGroupElement = document.createElement("transactionGroup");
            PersistenceHelper.appendAttribute(transactionGroupElement, "title", transactionGroup.getTitle().getValue());
            PersistenceHelper.appendAttribute(transactionGroupElement, "account", transactionGroup.getAccount().getValue());
            PersistenceHelper.appendAttribute(transactionGroupElement, "targetFilePath", transactionGroup.getTargetFilePath().getValue());
            Element transactionsElement = document.createElement("transactions");
            for (Transaction transaction : transactionGroup.getTransactions()) {
                PersistenceHelper.appendTransaction(transactionsElement, transaction, document);
            }
            transactionGroupElement.appendChild(transactionsElement);
            parentElement.appendChild(transactionGroupElement);
        }
    }

    private static void appendTransaction(Element parentElement, Transaction transaction, Document document) {
        if (transaction.getPersistent().get()) {
            Element transactionElement = document.createElement("transaction");
            PersistenceHelper.appendAttribute(transactionElement, "class", transaction.getClass().getSimpleName());
            PersistenceHelper.appendAttribute(transactionElement, "isin", transaction.getIsin().getValue());
            PersistenceHelper.appendAttribute(transactionElement, "title", transaction.getTitle().getValue());
            PersistenceHelper.appendAttribute(transactionElement, "wkn", transaction.getWkn().getValue());
            if (transaction instanceof StockChangeTransaction) {
                PersistenceHelper.appendTransaction_StockChange(parentElement, (StockChangeTransaction)transaction);
            } else {
                throw new IllegalArgumentException("Cannot convert transaction class to XML:" + transaction.getClass().getName());
            }
            parentElement.appendChild(transactionElement);
        }
    }

    private static <T> void appendAttribute(Element targetElement, String attributeName, MonetaryValue monetaryValue) {
        if (monetaryValue.getValue() != 0d) {
            targetElement.setAttribute(attributeName, NUMBER_FORMAT.format(monetaryValue.getValue().doubleValue()));
            targetElement.setAttribute(attributeName + "Currency", monetaryValue.getCurrency());
        }
    }

    private static <T> void appendAttribute(Element targetElement, String attributeName, String value) {
        if (StringUtils.isNotEmpty(value)) {
            targetElement.setAttribute(attributeName, value);
        }
    }

    private Map<String, PersistenceHelperTransactionPlugin> getTransactionPlugins() {
        return this.transactionPlugins;
    }
    private void setTransactionPlugins(Map<String, PersistenceHelperTransactionPlugin> transactionPlugins) {
        this.transactionPlugins = transactionPlugins;
    }

}
