package de.perdian.apps.fimasu4.model;

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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu4.model.persistence.Values;
import de.perdian.apps.fimasu4.model.persistence.impl.XmlBackedValuesStore;

public class TransactionGroupRepository {

    private static final Logger log = LoggerFactory.getLogger(TransactionGroupRepository.class);

    public static TransactionGroupModel loadTransactionGroupModel() {

        File repositoryFile = TransactionGroupRepository.resolveRepositoryFile();
        if (repositoryFile.exists()) {
            try (InputStream inputStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(repositoryFile)))) {
                return TransactionGroupRepository.loadTransactionGroupModel(inputStream);
            } catch (Exception e) {
                log.error("Cannot load transaction groups from repository file at: {}", repositoryFile.getAbsolutePath(), e);
            }
        }

        // We couldn't load the model from an existing file, so we'll create one from scratch
        TransactionGroupModel newModel = new TransactionGroupModel();
        newModel.addChangeListener((o, oldValue, newValue) -> TransactionGroupRepository.writeTransactionGroupModel(newModel));
        newModel.getTransactionGroups().add(new TransactionGroup());
        return newModel;

    }

    private static TransactionGroupModel loadTransactionGroupModel(InputStream inputStream) throws Exception {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);

        XmlBackedValuesStore documentValuesStore = new XmlBackedValuesStore(document.getDocumentElement());
        TransactionGroupModel newModel = new TransactionGroupModel();
        newModel.readValues(documentValuesStore.createValues());
        return newModel;

    }

    public static void writeTransactionGroupModel(TransactionGroupModel model) {
        File repositoryFile = TransactionGroupRepository.resolveRepositoryFile();
        try {
            if (!repositoryFile.getParentFile().exists()) {
                log.trace("Creating transaction groups repository directory at: {}", repositoryFile.getParentFile().getAbsolutePath());
                repositoryFile.getParentFile().mkdirs();
            }
            log.info("Writing transaction groups into repository file at: {}", repositoryFile.getAbsolutePath());
            try (OutputStream outputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(repositoryFile)))) {
                TransactionGroupRepository.writeTransactionGroupModel(model, outputStream);
            }
        } catch (Exception e) {
            log.error("Cannot write transaction groups into repository file at: {}", repositoryFile.getAbsolutePath(), e);
        }
    }

    private static void writeTransactionGroupModel(TransactionGroupModel model, OutputStream outputStream) throws Exception {

        Values values = model.writeValues();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element documentElement = document.createElement("transactionGroupModel");
        XmlBackedValuesStore documentValuesStore = new XmlBackedValuesStore(documentElement);
        documentValuesStore.storeValues(values);

        document.appendChild(documentElement);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(outputStream));

    }

    private static File resolveRepositoryFile() {
        return new File(System.getProperty("user.home"), ".fimasu/transactionGroupModel.gz");
    }

}
