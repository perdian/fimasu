package de.perdian.apps.fimasu.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TransactionGroupPersistence {

    public static List<TransactionGroup> loadTransactionGroups(InputStream inputStream) throws IOException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            NodeList transactionGroupElements = document.getDocumentElement().getElementsByTagName("transactionGroup");
            List<TransactionGroup> transactionGroups = new ArrayList<>(transactionGroupElements.getLength());
            for (int transactionGroupIndex = 0; transactionGroupIndex < transactionGroupElements.getLength(); transactionGroupIndex++) {
                Element transactionGroupElement = (Element)transactionGroupElements.item(transactionGroupIndex);
                TransactionGroup transactionGroup = new TransactionGroup();
                transactionGroup.loadFromXML(transactionGroupElement);
                transactionGroups.add(transactionGroup);
            }
            return transactionGroups;
        } catch (Exception e) {
            throw new IOException("Cannot import transaction groups", e);
        }
    }

    public static void writeTransactionGroups(List<TransactionGroup> transactionGroups, OutputStream outputStream) throws IOException {
        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("transactionGroups");
            document.appendChild(rootElement);
            for (TransactionGroup transactionGroup : transactionGroups) {
                if (transactionGroup.getPersistent().get()) {
                    Element transactionGroupElement = document.createElement(transactionGroup.getClass().getSimpleName());
                    transactionGroup.appendToXML(transactionGroupElement, document);
                    rootElement.appendChild(transactionGroupElement);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));

        } catch (Exception e) {
            throw new IOException("Cannot export transaction groups", e);
        }
    }

}
