package de.perdian.apps.fimasu.model.transactions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.beans.property.Property;

public class TransactionGroupSerializer {

    private static final Logger log = LoggerFactory.getLogger(TransactionGroupSerializer.class);

    public static void serializeTransactionGroups(Collection<TransactionGroup> transactionGroups, OutputStream targetStream) throws IOException {
        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("transactionGroups");
            document.appendChild(rootElement);
            for (TransactionGroup transactionGroup : transactionGroups) {
                Element transactionGroupElement = document.createElement("transactionGroup");
                TransactionGroupSerializer.appendAttribute(transactionGroupElement, "title", transactionGroup.getTitle(), Function.identity());
                TransactionGroupSerializer.appendAttribute(transactionGroupElement, "account", transactionGroup.getAccount(), Function.identity());
                TransactionGroupSerializer.appendAttribute(transactionGroupElement, "targetFilePath", transactionGroup.getTargetFilePath(), Function.identity());
                Element transactionsElement = document.createElement("transactions");
                for (Transaction transaction : transactionGroup.getTransactions()) {
                    Element transactionElement = document.createElement("transaction");
                    TransactionGroupSerializer.appendAttribute(transactionElement, "bookingCurrency", transaction.getBookingCurrency(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "charges", transaction.getCharges(), Number::toString);
                    TransactionGroupSerializer.appendAttribute(transactionElement, "chargesCurrency", transaction.getChargesCurrency(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "financeTax", transaction.getFinanceTax(), Number::toString);
                    TransactionGroupSerializer.appendAttribute(transactionElement, "finanxeTaxCurrency", transaction.getFinanceTaxCurrency(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "isin", transaction.getIsin(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "marketCurrency", transaction.getMarketCurrency(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "solidarityTax", transaction.getSolidarityTax(), Number::toString);
                    TransactionGroupSerializer.appendAttribute(transactionElement, "solidarityTaxCurrency", transaction.getSolidarityTaxCurrency(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "title", transaction.getTitle(), Function.identity());
                    TransactionGroupSerializer.appendAttribute(transactionElement, "type", transaction.getType(), Enum::name);
                    TransactionGroupSerializer.appendAttribute(transactionElement, "wkn", transaction.getWkn(), Function.identity());
                    transactionsElement.appendChild(transactionElement);
                }
                transactionGroupElement.appendChild(transactionsElement);
                rootElement.appendChild(transactionGroupElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(targetStream));

        } catch (Exception e) {
            throw new IOException("Cannot export transaction groups", e);
        }
    }

    private static <T> void appendAttribute(Element targetElement, String attributeName, Property<T> property, Function<T, String> stringConverterFunction) {
        try {
            T propertyValue = property.getValue();
            String stringValue = propertyValue == null ? null : stringConverterFunction.apply(propertyValue);
            if (StringUtils.isNotEmpty(stringValue)) {
                targetElement.setAttribute(attributeName, stringValue);
            }
        } catch (Exception e) {
            log.debug("Cannot convert value for attribute '{}' using converter: {}", attributeName, stringConverterFunction);
        }
    }

    public static List<TransactionGroup> deserializeTransactionGroups(InputStream sourceStream) throws IOException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(sourceStream);
            NodeList transactionGroupElements = document.getDocumentElement().getElementsByTagName("transactionGroup");
            List<TransactionGroup> transactionGroups = new ArrayList<>(transactionGroupElements.getLength());
            for (int transactionGroupIndex = 0; transactionGroupIndex < transactionGroupElements.getLength(); transactionGroupIndex++) {
                Element transactionGroupElement = (Element)transactionGroupElements.item(transactionGroupIndex);
                Element transactionsElement = (Element)transactionGroupElement.getElementsByTagName("transactions").item(0);
                NodeList transactionElements = transactionsElement.getElementsByTagName("transaction");
                List<Transaction> transactions = new ArrayList<>(transactionElements.getLength());
                for (int transactionIndex = 0; transactionIndex < transactionElements.getLength(); transactionIndex++) {
                    Element transactionElement = (Element)transactionElements.item(transactionIndex);
                    Transaction transaction = new Transaction();
                    TransactionGroupSerializer.extractAttribute(transactionElement, "bookingCurrency", transaction.getBookingCurrency(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "charges", transaction.getCharges(), Double::valueOf);
                    TransactionGroupSerializer.extractAttribute(transactionElement, "chargesCurrency", transaction.getChargesCurrency(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "financeTax", transaction.getFinanceTax(), Double::valueOf);
                    TransactionGroupSerializer.extractAttribute(transactionElement, "finanxeTaxCurrency", transaction.getFinanceTaxCurrency(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "isin", transaction.getIsin(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "marketCurrency", transaction.getMarketCurrency(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "solidarityTax", transaction.getSolidarityTax(), Double::valueOf);
                    TransactionGroupSerializer.extractAttribute(transactionElement, "solidarityTaxCurrency", transaction.getSolidarityTaxCurrency(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "title", transaction.getTitle(), Function.identity());
                    TransactionGroupSerializer.extractAttribute(transactionElement, "type", transaction.getType(), TransactionType::valueOf);
                    TransactionGroupSerializer.extractAttribute(transactionElement, "wkn", transaction.getWkn(), Function.identity());
                    transactions.add(transaction);
                }
                TransactionGroup transactionGroup = new TransactionGroup();
                transactionGroup.getTransactions().addAll(transactions);
                TransactionGroupSerializer.extractAttribute(transactionGroupElement, "title", transactionGroup.getTitle(), Function.identity());
                TransactionGroupSerializer.extractAttribute(transactionGroupElement, "account", transactionGroup.getAccount(), Function.identity());
                TransactionGroupSerializer.extractAttribute(transactionGroupElement, "targetFilePath", transactionGroup.getTargetFilePath(), Function.identity());
                transactionGroups.add(transactionGroup);
            }
            return transactionGroups;
        } catch (Exception e) {
            throw new IOException("Cannot import transaction groups", e);
        }
    }

    private static <T> void extractAttribute(Element sourceElement, String attributeName, Property<T> property, Function<String, T> stringConverterFunction) {
        String stringValue = sourceElement.getAttribute(attributeName);
        if (!StringUtils.isEmpty(stringValue)) {
            property.setValue(stringConverterFunction.apply(stringValue));
        }
    }

}
