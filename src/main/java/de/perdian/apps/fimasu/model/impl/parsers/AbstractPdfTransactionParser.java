package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionParser;

public abstract class AbstractPdfTransactionParser<T extends Transaction> implements TransactionParser {

    private static final Logger log = LoggerFactory.getLogger(AbstractPdfTransactionParser.class);

    @Override
    public List<Transaction> parseTransactionsFromFile(File documentFile) {
        if (this.checkFileName(documentFile.getName())) {
            try {
                log.debug("Analyzing PDF file at: {}", documentFile.getAbsolutePath());
                try (PDDocument pdfDocument = PDDocument.load(documentFile)) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String pdfText = pdfStripper.getText(pdfDocument);
                    T transaction = this.createTransaction(documentFile.getName());
                    this.updateTransaction(transaction, pdfText, documentFile.getName());
                    return List.of(transaction);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot analyze file: " + documentFile.getAbsolutePath(), e);
            }
        }
        return Collections.emptyList();
    }

    protected boolean checkFileName(String fileName) {
        return fileName.endsWith(".pdf");
    }

    protected abstract T createTransaction(String sourceFileName);

    protected void updateTransaction(T transaction, String pdfText, String sourceFileName) throws IOException {
        try (BufferedReader lineReader = new BufferedReader(new StringReader(pdfText))) {
            for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                if (StringUtils.isNotBlank(line)) {
                    this.updateTransactionFromLine(transaction, line.trim());
                }
            }
        }
    }

    protected abstract void updateTransactionFromLine(T transaction, String line);

}
