package de.perdian.apps.fimasu4.model.persistence.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

import de.perdian.apps.fimasu4.fx.FimasuPreferences;
import de.perdian.apps.fimasu4.model.FimasuModel;
import de.perdian.apps.fimasu4.model.persistence.FimasuModelRepository;
import de.perdian.apps.fimasu4.model.persistence.xml.XmlElementTranslator.ParentTranslator;
import de.perdian.apps.fimasu4.model.types.MonetaryValue;
import de.perdian.apps.fimasu4.model.types.StockIdentfier;
import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import de.perdian.apps.fimasu4.model.types.TransactionType;

public class XmlModelRepository implements FimasuModelRepository {

    private static final Logger log = LoggerFactory.getLogger(XmlModelRepository.class);

    private XmlElementTranslator<FimasuModel> modelTranslator = null;
    private File modelFile = null;

    public XmlModelRepository(FimasuPreferences preferences) {

        ParentTranslator<MonetaryValue> monetaryValueTranslator = new ParentTranslator<>();
        monetaryValueTranslator.registerStringProperty("currency", MonetaryValue::getCurrency);

        ParentTranslator<StockIdentfier> stockIdentifierTranslator = new ParentTranslator<>();
        stockIdentifierTranslator.registerStringProperty("wkn", StockIdentfier::getWkn);
        stockIdentifierTranslator.registerStringProperty("isin", StockIdentfier::getIsin);
        stockIdentifierTranslator.registerStringProperty("title", StockIdentfier::getTitle);

        ParentTranslator<Transaction> transactionTranslator = new ParentTranslator<>();
        transactionTranslator.registerBooleanProperty("persistent", Transaction::getPersistent);
        transactionTranslator.registerEnumProperty("type", Transaction::getType, TransactionType.class);
        transactionTranslator.registerEmbeddedProperty("stockIdentifier", Transaction::getStockIdentifier, stockIdentifierTranslator);
        transactionTranslator.registerEmbeddedProperty("stockPrice", Transaction::getStockPrice, monetaryValueTranslator);
        transactionTranslator.registerEmbeddedProperty("payoutValue", Transaction::getPayoutValue, monetaryValueTranslator);
        transactionTranslator.registerEmbeddedProperty("bookingValue", Transaction::getBookingValue, monetaryValueTranslator);

        ParentTranslator<TransactionGroup> transactionGroupTranslator = new ParentTranslator<>();
        transactionGroupTranslator.registerListProperty("transaction", TransactionGroup::getTransactions, Transaction::new, transactionTranslator, transactionGroup -> transactionGroup.getPersistent().getValue());
        transactionGroupTranslator.registerStringProperty("bankAccountName", TransactionGroup::getBankAccountName);
        transactionGroupTranslator.registerStringProperty("exportFileName", TransactionGroup::getExportFileName);
        transactionGroupTranslator.registerBooleanProperty("persistent", TransactionGroup::getPersistent);
        transactionGroupTranslator.registerBooleanProperty("selected", TransactionGroup::getSelected);
        transactionGroupTranslator.registerStringProperty("title", TransactionGroup::getTitle);

        ParentTranslator<FimasuModel> modelTranslator = new ParentTranslator<>();
        modelTranslator.registerListProperty("transactionGroup", FimasuModel::getTransactionGroups, TransactionGroup::new, transactionGroupTranslator, transaction -> transaction.getPersistent().getValue());
        this.setModelTranslator(modelTranslator);
        this.setModelFile(preferences.resolveFile("model.gz"));

    }

    @Override
    public FimasuModel loadModel() {
        FimasuModel modelFromFile = this.loadModelFromFile(this.getModelFile());
        FimasuModel model = modelFromFile == null ? new FimasuModel() : modelFromFile;
        model.addChangeListener((o, oldValue, newValue) -> this.writeModelToFile(model, this.getModelFile()));
        return model;
    }

    private FimasuModel loadModelFromFile(File sourceFile) {
        if (sourceFile.exists()) {
            try {
                try (InputStream inputStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(sourceFile)))) {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.parse(inputStream);
                    Element documentElement = document.getDocumentElement();
                    FimasuModel model = new FimasuModel();
                    this.getModelTranslator().updateBeanFromElement(model, documentElement);
                    return model;
                }
            } catch (Exception e) {
                log.warn("Cannot load model from file at: {}", sourceFile.getAbsolutePath(), e);
            }
        }
        return null;
    }

    private void writeModelToFile(FimasuModel model, File targetFile) {
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element documentElement = document.createElement("fimasuModel");
            document.appendChild(documentElement);

            this.getModelTranslator().extractBeanToElement(model, documentElement, document);

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

    private XmlElementTranslator<FimasuModel> getModelTranslator() {
        return this.modelTranslator;
    }
    private void setModelTranslator(XmlElementTranslator<FimasuModel> modelTranslator) {
        this.modelTranslator = modelTranslator;
    }

    private File getModelFile() {
        return this.modelFile;
    }
    private void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

}
