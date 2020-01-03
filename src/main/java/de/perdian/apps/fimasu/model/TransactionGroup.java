package de.perdian.apps.fimasu.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.persistence.PersistenceHelper;
import de.perdian.apps.fimasu.support.quicken.QIFWriter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransactionGroup {

    static final long serialVersionUID = 1L;

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty account = new SimpleStringProperty();
    private final StringProperty targetFilePath = new SimpleStringProperty();
    private final BooleanProperty persistent = new SimpleBooleanProperty(false);
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final List<ChangeListener<TransactionGroup>> changeListeners = new CopyOnWriteArrayList<>();

    public TransactionGroup() {
        this.getTitle().addListener((x, oldValue, newValue) -> this.fireChange());
        this.getAccount().addListener((x, oldValue, newValue) -> this.fireChange());
        this.getTargetFilePath().addListener((x, oldValue, newValue) -> this.fireChange());
        this.getPersistent().addListener((x, oldValue, newValue) -> this.fireChange());
        this.getTransactions().addListener((ListChangeListener<Transaction>)event -> {
            while (event.next()) {
                for (Transaction addedTransaction : event.getAddedSubList()) {
                    addedTransaction.addChangeListener((o, oldValue, newValue) -> this.fireChange());
                    addedTransaction.getBookingDate().addListener((o, oldValue, newValue) -> this.onChangeTransactionDate(addedTransaction, newValue));
                }
            }
            this.fireChange();
        });
    }

    private void fireChange() {
        for (ChangeListener<TransactionGroup> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }

    private void onChangeTransactionDate(Transaction transaction, LocalDate newValue) {
        int indexOfTransaction = this.getTransactions().indexOf(transaction);
        if (indexOfTransaction == 0) {
            for (int i=1; i < this.getTransactions().size(); i++) {
                Transaction needleTransaction = this.getTransactions().get(i);
                if (needleTransaction.getBookingDate().getValue() == null && needleTransaction.getValutaDate().getValue() == null) {
                    needleTransaction.getBookingDate().setValue(transaction.getBookingDate().getValue());
                    needleTransaction.getValutaDate().setValue(transaction.getValutaDate().getValue());
                } else {
                    break;
                }
            }
        }
    }

    protected void loadFromXML(Element transactionGroupElement) {

        this.getPersistent().setValue(Boolean.TRUE);
        this.getTitle().setValue(PersistenceHelper.extractAttributeString(transactionGroupElement, "title").orElse(null));
        this.getAccount().setValue(PersistenceHelper.extractAttributeString(transactionGroupElement, "account").orElse(null));
        this.getTargetFilePath().setValue(PersistenceHelper.extractAttributeString(transactionGroupElement, "targetFilePath").orElse(null));

        Element transactionsElement = (Element)transactionGroupElement.getElementsByTagName("transactions").item(0);
        NodeList transactionElements = transactionsElement.getChildNodes();
        List<Transaction> transactions = new ArrayList<>(transactionElements.getLength());
        for (int transactionIndex = 0; transactionIndex < transactionElements.getLength(); transactionIndex++) {
            if (transactionElements.item(transactionIndex) instanceof Element) {
                Element transactionElement = (Element)transactionElements.item(transactionIndex);
                String transactionClass = this.getClass().getPackage().getName() + ".impl.transactions." + StringUtils.defaultIfEmpty(transactionElement.getNodeName(), StockChangeTransaction.class.getSimpleName());
                try {
                    Transaction transaction = (Transaction)this.getClass().getClassLoader().loadClass(transactionClass).getDeclaredConstructor().newInstance();
                    transaction.loadFromXML(transactionElement);
                    transactions.add(transaction);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot load transaction from XML for class: " + transactionClass, e);
                }
            }
        }
        this.getTransactions().addAll(transactions);

    }

    public void appendToXML(Element transactionGroupElement, Document document) {

        PersistenceHelper.appendAttribute(transactionGroupElement, "title", this.getTitle().getValue());
        PersistenceHelper.appendAttribute(transactionGroupElement, "account", this.getAccount().getValue());
        PersistenceHelper.appendAttribute(transactionGroupElement, "targetFilePath", this.getTargetFilePath().getValue());

        Element transactionsElement = document.createElement("transactions");
        for (Transaction transaction : this.getTransactions()) {
            if (transaction.getPersistent().get()) {
                Element transactionElement = document.createElement(transaction.getClass().getSimpleName());
                transaction.appendToXML(transactionElement, document);
                transactionsElement.appendChild(transactionElement);
            }
        }
        transactionGroupElement.appendChild(transactionsElement);

    }

    public void appendToQIF(QIFWriter qifWriter) {
        for (Transaction transaction : this.getTransactions()) {
            transaction.appendToQIF(qifWriter, this);
        }
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public StringProperty getAccount() {
        return this.account;
    }

    public StringProperty getTargetFilePath() {
        return this.targetFilePath;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }

    public ObservableList<Transaction> getTransactions() {
        return this.transactions;
    }

    public boolean addChangeListener(ChangeListener<TransactionGroup> changeListener) {
        return this.getChangeListeners().add(changeListener);
    }
    public boolean removeChangeListener(ChangeListener<TransactionGroup> changeListener) {
        return this.getChangeListeners().remove(changeListener);
    }
    private List<ChangeListener<TransactionGroup>> getChangeListeners() {
        return this.changeListeners;
    }


}
