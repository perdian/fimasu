package de.perdian.apps.qifgenerator.fx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fx.model.Transaction;
import de.perdian.apps.qifgenerator.fx.model.TransactionGroup;
import de.perdian.apps.qifgenerator.fx.model.TransactionType;

class QifGeneratorPreferencesHelper {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorPreferencesHelper.class);

    static Map<String, String> readValues(File valuesFile) {
        Map<String, String> resultMap = new HashMap<>();
        if (valuesFile.exists()) {
            log.info("Reading preferences values from file: {}", valuesFile.getAbsolutePath());
            try (InputStream valuesStream = new BufferedInputStream(new FileInputStream(valuesFile))) {
                Properties properties = new Properties();
                properties.loadFromXML(valuesStream);
                for (Map.Entry<Object, Object> propertyEntry : properties.entrySet()) {
                    resultMap.put((String)propertyEntry.getKey(), (String)propertyEntry.getValue());
                }
            } catch (Exception e) {
                log.warn("Cannot read preferences values from file: {}", valuesFile.getAbsolutePath(), e);
            }
        }
        return resultMap;
    }

    static void storeValues(Map<String, String> values, File valuesFile) {
        try {

            if (!valuesFile.getParentFile().exists()) {
                log.debug("Creating target directory: {}", valuesFile.getParentFile().getAbsolutePath());
                valuesFile.getParentFile().mkdirs();
            }

            log.debug("Storing {} preferences values into file: {}", values.size(), valuesFile.getAbsolutePath());
            try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(valuesFile))) {
                Properties properties = new Properties();
                values.entrySet().stream().filter(entry -> StringUtils.isNotEmpty(entry.getValue())).forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));
                properties.storeToXML(fileStream, null, "UTF-8");
                fileStream.flush();
            }

        } catch (Exception e) {
            log.warn("Cannot write preferences values into file: " + valuesFile.getAbsolutePath(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static List<TransactionGroup> readTransactionGroups(File transactionGroupsFile) {
        List<TransactionGroup> transactionGroups = new ArrayList<>();
        if (transactionGroupsFile.exists()) {
            log.info("Loading transaction groups from file: {}", transactionGroupsFile.getAbsolutePath());
            try (ObjectInputStream transactionGroupsStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(transactionGroupsFile)))) {
                List<TransactionGroupBean> transactionGroupBeans = (List<TransactionGroupBean>)transactionGroupsStream.readObject();
                transactionGroupBeans.stream().map(TransactionGroupBean::toTransactionGroup).forEach(transactionGroups::add);
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", transactionGroupsFile.getAbsolutePath(), e);
            }
        }
        return transactionGroups;
    }

    static void storeTransactionGroups(List<TransactionGroup> transactionGroups, File transactionGroupsFile) {

        if (!transactionGroupsFile.getParentFile().exists()) {
            log.debug("Creating target directory: {}", transactionGroupsFile.getParentFile().getAbsolutePath());
            transactionGroupsFile.getParentFile().mkdirs();
        }

        log.debug("Storing {} transaction groups into file: {}", transactionGroups.size(), transactionGroupsFile.getAbsolutePath());
        List<TransactionGroupBean> transactionGroupBeans = transactionGroups.stream().map(TransactionGroupBean::new).collect(Collectors.toList());
        try (ObjectOutputStream transactionGroupsStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(transactionGroupsFile)))) {
            transactionGroupsStream.writeObject(transactionGroupBeans);
            transactionGroupsStream.flush();
        } catch (Exception e) {
            log.warn("Cannot store transaction groups into file: {}", transactionGroupsFile.getAbsolutePath(), e);
        }

    }

    static class TransactionGroupBean implements Serializable {

        static final long serialVersionUID = 1L;

        private String title = null;
        private String account = null;
        private File targetFile = null;
        private List<TransactionBean> transactions = null;

        TransactionGroupBean(TransactionGroup input) {
            this.setAccount(input.accountProperty().getValue());
            this.setTargetFile(input.targetFileProperty().getValue());
            this.setTitle(input.titleProperty().getValue());
            this.setTransactions(input.transactionsProperty().stream().map(TransactionBean::new).collect(Collectors.toList()));
        }

        TransactionGroup toTransactionGroup() {
            TransactionGroup output = new TransactionGroup(this.getTitle());
            output.accountProperty().setValue(this.getAccount());
            output.targetFileProperty().setValue(this.getTargetFile());
            output.transactionsProperty().setAll(this.getTransactions().stream().map(TransactionBean::toTransaction).collect(Collectors.toList()));
            return output;
        }

        String getTitle() {
            return this.title;
        }
        void setTitle(String title) {
            this.title = title;
        }

        String getAccount() {
            return this.account;
        }
        void setAccount(String account) {
            this.account = account;
        }

        File getTargetFile() {
            return this.targetFile;
        }
        void setTargetFile(File targetFile) {
            this.targetFile = targetFile;
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

        private TransactionType type = null;
        private String wkn = null;
        private String isin = null;
        private String title = null;
        private String marketCurrency = null;
        private String bookingCurrency = null;

        TransactionBean(Transaction input) {
            this.setType(input.typeProperty().getValue());
            this.setWkn(input.wknProperty().getValue());
            this.setIsin(input.isinProperty().getValue());
            this.setTitle(input.titleProperty().getValue());
            this.setMarketCurrency(input.marketCurrencyProperty().getValue());
            this.setBookingCurrency(input.bookingCurrencyProperty().getValue());
        }

        Transaction toTransaction() {
            Transaction output = new Transaction();
            output.typeProperty().setValue(this.getType());
            output.marketCurrencyProperty().setValue(StringUtils.defaultIfEmpty(this.getMarketCurrency(), "EUR"));
            output.bookingCurrencyProperty().setValue(StringUtils.defaultIfEmpty(this.getBookingCurrency(), "EUR"));
            output.wknProperty().setValue(this.getWkn());
            output.isinProperty().setValue(this.getIsin());
            output.titleProperty().setValue(this.getTitle());
            return output;
        }

        TransactionType getType() {
            return this.type;
        }
        void setType(TransactionType type) {
            this.type = type;
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

        String getMarketCurrency() {
            return this.marketCurrency;
        }
        void setMarketCurrency(String marketCurrency) {
            this.marketCurrency = marketCurrency;
        }

        String getBookingCurrency() {
            return this.bookingCurrency;
        }
        void setBookingCurrency(String bookingCurrency) {
            this.bookingCurrency = bookingCurrency;
        }

    }

}
