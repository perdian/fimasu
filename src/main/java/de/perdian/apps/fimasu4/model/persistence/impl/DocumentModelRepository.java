package de.perdian.apps.fimasu4.model.persistence.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.perdian.apps.fimasu4.model.FimasuModel;
import de.perdian.apps.fimasu4.model.FimasuModelRepository;
import de.perdian.apps.fimasu4.model.persistence.AbstractFileBasedModelRepository;
import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import de.perdian.apps.fimasu4.model.types.TransactionType;

public class DocumentModelRepository extends AbstractFileBasedModelRepository {

    private static final Logger log = LoggerFactory.getLogger(FimasuModelRepository.class);

    @Override
    protected FimasuModel loadModelFromFile(File sourceFile) {
        if (sourceFile.exists()) {
            try {
                try (InputStream inputStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(sourceFile)))) {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    return this.loadModelFromElement(documentBuilder.parse(inputStream).getDocumentElement());
                }
            } catch (Exception e) {
                log.warn("Cannot load model from file at: {}", sourceFile.getAbsolutePath(), e);
            }
        }
        return null;
    }

    private FimasuModel loadModelFromElement(Element modelElement) {
        FimasuModel model = new FimasuModel();

        NodeList transactionGroupElements = modelElement.getElementsByTagName("transactionGroup");
        for (int i=0; i < transactionGroupElements.getLength(); i++) {
            Element transactionGroupElement = (Element)transactionGroupElements.item(i);
            TransactionGroup transactionGroup = this.loadTransactionGroupFromElement(transactionGroupElement);
            model.getTransactionGroups().add(transactionGroup);
            if (Objects.equals(transactionGroup.getId(), modelElement.getAttribute("selectedTransactionGroupId"))) {
                model.getSelectedTransactionGroup().setValue(transactionGroup);
            }
        }

        return model;
    }

    private TransactionGroup loadTransactionGroupFromElement(Element transactionGroupElement) {
        TransactionGroup transactionGroup = new TransactionGroup(transactionGroupElement.getAttribute("id"));
        transactionGroup.getTitle().setValue(transactionGroupElement.getAttribute("title"));
        transactionGroup.getExportFileName().setValue(transactionGroupElement.getAttribute("exportFileName"));
        transactionGroup.getBankAccountName().setValue(transactionGroupElement.getAttribute("bankAccountName"));
        transactionGroup.getPersistent().setValue(true);
        transactionGroup.getTransactions().setAll(this.loadTransactionsFromNodeList(transactionGroupElement.getElementsByTagName("transaction")));
        return transactionGroup;
    }

    private List<Transaction> loadTransactionsFromNodeList(NodeList transactionElements) {
        if (transactionElements == null || transactionElements.getLength() <= 0) {
            return Collections.emptyList();
        } else {
            List<Transaction> transactions = new ArrayList<>(transactionElements.getLength());
            for (int i=0; i < transactionElements.getLength(); i++) {
                transactions.add(this.loadTransactionFromElement((Element)transactionElements.item(i)));
            }
            return transactions;
        }
    }

    private Transaction loadTransactionFromElement(Element transactionElement) {
        Transaction transaction = new Transaction();
        transaction.getPersistent().setValue(true);
        transaction.getType().setValue(DocumentModelHelper.resolveEnumValue(TransactionType.class, transactionElement.getAttribute("type"), TransactionType.BUY));
        return transaction;
    }

    @Override
    protected void writeModelToFile(FimasuModel model, File targetFile) {
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element documentElement = document.createElement("fimasuModel");
            document.appendChild(documentElement);

            this.appendModelToElement(model, documentElement, document);

            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            try (OutputStream targetStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)))) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(new DOMSource(document), new StreamResult(targetStream));
            }

        } catch (Exception e) {
            log.warn("Cannot write model into file at: {}", targetFile.getAbsolutePath(), e);
        }
    }

    private void appendModelToElement(FimasuModel model, Element element, Document document) {
        TransactionGroup selectedTransactionGroup = model.getSelectedTransactionGroup().getValue();
        if (selectedTransactionGroup != null) {
            element.setAttribute("selectedTransactionGroupId", model.getSelectedTransactionGroup().getValue().getId());
        }
        for (TransactionGroup transactionGroup : model.getTransactionGroups()) {
            if (transactionGroup.getPersistent().getValue()) {
                Element transactionGroupElement = document.createElement("transactionGroup");
                this.appendTransactionGroupToElement(transactionGroup, transactionGroupElement, document);
                element.appendChild(transactionGroupElement);
            }
        }
    }

    private void appendTransactionGroupToElement(TransactionGroup transactionGroup, Element transactionGroupElement, Document document) {
        transactionGroupElement.setAttribute("id", transactionGroup.getId());
        transactionGroupElement.setAttribute("title", transactionGroup.getTitle().getValue());
        transactionGroupElement.setAttribute("exportFileName", transactionGroup.getExportFileName().getValue());
        transactionGroupElement.setAttribute("bankAccountName", transactionGroup.getBankAccountName().getValue());
        for (Transaction transaction : transactionGroup.getTransactions()) {
            if (transaction.getPersistent().getValue()) {
                Element transactionElement = document.createElement("transaction");
                this.appendTransactionToElement(transaction, transactionElement, document);
                transactionGroupElement.appendChild(transactionElement);
            }
        }
    }

    private void appendTransactionToElement(Transaction transaction, Element transactionElement, Document document) {
        transactionElement.setAttribute("type", transaction.getType().getValue() == null ? "" : transaction.getType().getValue().name());
    }

}
