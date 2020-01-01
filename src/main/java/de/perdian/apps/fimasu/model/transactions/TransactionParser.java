package de.perdian.apps.fimasu.model.transactions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public interface TransactionParser {

    public static List<Transaction> parseTransactions(File file) {
        List<Transaction> transactions = new ArrayList<>();
        ServiceLoader<TransactionParser> transactionParserLoader = ServiceLoader.load(TransactionParser.class);
        transactionParserLoader.forEach(transactionParser -> transactions.addAll(transactionParser.parseTransactionsFromFile(file)));
        return transactions;
    }

    List<Transaction> parseTransactionsFromFile(File documentFile);

}
