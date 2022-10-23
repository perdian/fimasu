package de.perdian.apps.fimasu4.model.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import de.perdian.apps.fimasu4.model.types.Transaction;

public interface TransactionParser {

    static List<Transaction> parseTransactions(File file) {
        List<Transaction> transactions = new ArrayList<>();
        ServiceLoader<TransactionParser> transactionParserLoader = ServiceLoader.load(TransactionParser.class);
        transactionParserLoader
            .stream()
            .map(transactionParserProvider -> transactionParserProvider.get())
            .filter(transactionParser -> transactionParser.canHandleFile(file))
            .forEach(transactionParser -> transactions.addAll(transactionParser.parseTransactionsFromFile(file)));
        return transactions;
    }

    boolean canHandleFile(File documentFile);
    List<Transaction> parseTransactionsFromFile(File documentFile);

}
