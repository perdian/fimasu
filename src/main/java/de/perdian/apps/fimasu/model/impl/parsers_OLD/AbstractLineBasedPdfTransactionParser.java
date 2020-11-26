package de.perdian.apps.fimasu.model.impl.parsers_OLD;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu.model.Transaction;

public abstract class AbstractLineBasedPdfTransactionParser<T extends Transaction> extends AbstractPdfTransactionParser<T> {

    @Override
    protected T createTransaction(String pdfText, File pdfFile) throws Exception {
        T transaction = this.createTransactionInstance(pdfText, pdfFile);
        try (BufferedReader lineReader = new BufferedReader(new StringReader(pdfText))) {
            for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                if (StringUtils.isNotBlank(line)) {
                    this.updateTransactionFromLine(transaction, line.trim(), pdfFile);
                }
            }
        }
        return transaction;
    }

    protected abstract T createTransactionInstance(String pdfText, File pdfFile) throws Exception;
    protected abstract void updateTransactionFromLine(T transaction, String pdfLine, File pdfFile) throws Exception;

}
