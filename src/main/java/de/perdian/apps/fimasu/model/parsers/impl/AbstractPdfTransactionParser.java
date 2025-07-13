package de.perdian.apps.fimasu.model.parsers.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.parsers.TransactionParser;
import de.perdian.apps.fimasu.model.parsers.support.LineProcessorList;
import de.perdian.apps.fimasu.model.types.Transaction;

public abstract class AbstractPdfTransactionParser implements TransactionParser {

    private static final Logger log = LoggerFactory.getLogger(AbstractPdfTransactionParser.class);

    @Override
    public boolean canHandleFile(File documentFile) {
        return documentFile.getName().toLowerCase().endsWith(".pdf");
    }

    @Override
    public List<Transaction> parseTransactionsFromFile(File pdfFile) {
        try {
            log.debug("Analyzing PDF file at: {}", pdfFile.getAbsolutePath());
            try (PDDocument pdfDocument = Loader.loadPDF(pdfFile)) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String pdfText = pdfStripper.getText(pdfDocument);
                Transaction transaction = this.createTransaction(pdfText, pdfFile);
                this.createLineProcessorList(transaction, pdfFile).process(pdfText);
                return transaction == null ? Collections.emptyList() : List.of(transaction);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot analyze file: " + pdfFile.getAbsolutePath(), e);
        }
    }

    protected abstract LineProcessorList createLineProcessorList(Transaction transaction, File pdfFile);
    protected abstract Transaction createTransaction(String pdfText, File pdfFile) throws Exception;

}
