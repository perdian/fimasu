package de.perdian.personal.stockimporter.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockModelFactory {

    private static final Logger log = LoggerFactory.getLogger(StockModelFactory.class);

    public static StockModel createStockModel() {
        StockModel stockModel = new StockModel();
        StockModelBean stockModelBean = StockModelFactory.loadStockModelBean();
        if (stockModelBean != null && stockModelBean.getTransactionGroups() != null) {
            stockModel.transactionGroupsProperty().addAll(stockModelBean.getTransactionGroups().stream().map(TransactionGroupBean::toTransactionGroup).collect(Collectors.toList()));
        }
        stockModel.addChangeListener((x, oldValue, newValue) -> StockModelFactory.saveStockModel(stockModel));
        return stockModel;
    }

    private static StockModelBean loadStockModelBean() {
        File stockModelFile = StockModelFactory.resolveStockModelFile();
        if (stockModelFile.exists() && stockModelFile.length() > 0) {
            try {
                log.debug("Loading model from file at: {}", stockModelFile.getAbsolutePath());
                try (ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(stockModelFile)))) {
                    return (StockModelBean)objectStream.readObject();
                }
            } catch (Exception e) {
                log.warn("Cannot load model from file at: {}", stockModelFile.getAbsolutePath(), e);
            }
        }
        return null;
    }

    private static void saveStockModel(StockModel stockModel) {

        File targetFile = StockModelFactory.resolveStockModelFile();
        File targetDirectory = targetFile.getParentFile();
        if (!targetDirectory.exists()) {
            log.debug("Creating storage directory at: {}", targetDirectory.getAbsolutePath());
            targetDirectory.mkdirs();
        }

        log.debug("Writing model into file at: {}", targetFile.getAbsolutePath());
        try (ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)))) {
            objectStream.writeObject(new StockModelBean(stockModel));
            objectStream.flush();
            log.debug("Completed writing model into file at: {}", targetFile.getAbsolutePath());
        } catch (Exception e) {
            log.warn("Cannot write model into file at: {}", targetFile.getAbsolutePath(), e);
        }

    }

    private static File resolveStockModelFile() {

        File userHomeDirectory = new File(System.getProperty("user.home"));
        File stockimporterDirectory = new File(userHomeDirectory, ".stockimporter/");
        File stockModelFile = new File(stockimporterDirectory, "model.object") ;

        return stockModelFile;

    }

    static class StockModelBean implements Serializable {

        static final long serialVersionUID = 1L;

        private List<TransactionGroupBean> transactionGroups = null;

        StockModelBean(StockModel stockModel) {
            this.setTransactionGroups(stockModel.transactionGroupsProperty().stream().map(TransactionGroupBean::new).collect(Collectors.toList()));
        }

        List<TransactionGroupBean> getTransactionGroups() {
            return this.transactionGroups;
        }
        void setTransactionGroups(List<TransactionGroupBean> transactionGroups) {
            this.transactionGroups = transactionGroups;
        }

    }

    static class TransactionGroupBean implements Serializable {

        static final long serialVersionUID = 1L;

        private String title = null;
        private List<TransactionBean> transactions = null;

        TransactionGroupBean(TransactionGroup transactionGroup) {
            this.setTitle(transactionGroup.titleProperty().getValue());
            this.setTransactions(transactionGroup.transactionsProperty().stream().map(TransactionBean::new).collect(Collectors.toList()));
        }

        TransactionGroup toTransactionGroup() {
            TransactionGroup transactionGroup = new TransactionGroup();
            transactionGroup.titleProperty().setValue(this.getTitle());
            transactionGroup.transactionsProperty().addAll(this.getTransactions().stream().map(TransactionBean::toTransaction).collect(Collectors.toList()));
            return transactionGroup;
        }

        String getTitle() {
            return this.title;
        }
        void setTitle(String title) {
            this.title = title;
        }

        List<TransactionBean> getTransactions() {
            return this.transactions;
        }
        void setTransactions(List<TransactionBean> transactions) {
            this.transactions = transactions;
        }

    }

    static class TransactionBean implements Serializable {

        static final long serialVersionUID = 1L;

        private String wkn = null;
        private String isin = null;
        private String title = null;
        private Double value = null;
        private Double charges = null;

        TransactionBean(Transaction transaction) {
            this.setIsin(transaction.isinProperty().getValue());
            this.setTitle(transaction.titleProperty().getValue());
            this.setValue(transaction.valueProperty().getValue());
            this.setWkn(transaction.wknProperty().getValue());
            this.setCharges(transaction.chargesProperty().getValue());
        }

        Transaction toTransaction() {
            Transaction transaction = new Transaction();
            transaction.isinProperty().setValue(this.getIsin());
            transaction.titleProperty().setValue(this.getTitle());
            transaction.valueProperty().setValue(this.getValue());
            transaction.wknProperty().setValue(this.getWkn());
            transaction.chargesProperty().setValue(this.getCharges());
            return transaction;
        }

        String getWkn() {
            return this.wkn;
        }
        void setWkn(String wkn) {
            this.wkn = wkn;
        }

        String getIsin() {
            return this.isin;
        }
        void setIsin(String isin) {
            this.isin = isin;
        }

        String getTitle() {
            return this.title;
        }
        void setTitle(String title) {
            this.title = title;
        }

        Double getValue() {
            return this.value;
        }
        void setValue(Double value) {
            this.value = value;
        }

        Double getCharges() {
            return this.charges;
        }
        void setCharges(Double charges) {
            this.charges = charges;
        }

    }

}
