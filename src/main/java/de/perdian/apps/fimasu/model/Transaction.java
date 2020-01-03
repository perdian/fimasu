package de.perdian.apps.fimasu.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu.persistence.PersistenceHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public abstract class Transaction {

    static final long serialVersionUID = 1L;

    private final StringProperty wkn = new SimpleStringProperty();
    private final StringProperty isin = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> bookingDate = new SimpleObjectProperty<>();
    private final DoubleProperty bookingAmount = new SimpleDoubleProperty();
    private final StringProperty bookingCurrency = new SimpleStringProperty("EUR");
    private final ObjectProperty<LocalDate> valutaDate = new SimpleObjectProperty<>();
    private final BooleanProperty persistent = new SimpleBooleanProperty(false);
    private final List<ChangeListener<Transaction>> changeListeners = new CopyOnWriteArrayList<>();

    public Transaction() {

        this.getWkn().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getIsin().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTitle().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getValutaDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getPersistent().addListener((o, oldValue, newValue) -> this.fireChange());

        this.getBookingDate().addListener((o, oldValue, newValue) -> this.recomputeValutaDate(newValue));

    }

    private void recomputeValutaDate(LocalDate bookingDate) {
        if (bookingDate == null) {
            this.getValutaDate().setValue(null);
        } else if (this.getValutaDate().getValue() == null) {
            LocalDate nextValutaDate = bookingDate.plusDays(2);
            if (bookingDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                nextValutaDate = nextValutaDate.plusDays(2);
            } else if (bookingDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                nextValutaDate = nextValutaDate.plusDays(1);
            }
            this.getValutaDate().setValue(nextValutaDate);
        }
    }

    protected void loadFromXML(Element transactionElement) {
        this.getTitle().setValue(PersistenceHelper.extractAttributeString(transactionElement, "title").orElse(null));
        this.getWkn().setValue(PersistenceHelper.extractAttributeString(transactionElement, "wkn").orElse(null));
        this.getIsin().setValue(PersistenceHelper.extractAttributeString(transactionElement, "isin").orElse(null));
    }

    protected void appendToXML(Element transactionElement, Document document) {
        PersistenceHelper.appendAttribute(transactionElement, "title", this.getTitle().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "wkn", this.getWkn().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "isin", this.getIsin().getValue());
    }

    public void copyValuesInto(Transaction targetTransaction) {
        Optional.ofNullable(this.getBookingDate().getValue()).ifPresent(targetTransaction.getBookingDate()::setValue);
        Optional.ofNullable(this.getValutaDate().getValue()).ifPresent(targetTransaction.getValutaDate()::setValue);
        if (StringUtils.isEmpty(targetTransaction.getTitle().getValue())) {
            Optional.ofNullable(this.getTitle().getValue()).ifPresent(targetTransaction.getTitle()::setValue);
        }
    }

    public StringProperty getWkn() {
        return this.wkn;
    }

    public StringProperty getIsin() {
        return this.isin;
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public ObjectProperty<LocalDate> getBookingDate() {
        return this.bookingDate;
    }

    public DoubleProperty getBookingAmount() {
        return this.bookingAmount;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }

    public ObjectProperty<LocalDate> getValutaDate() {
        return this.valutaDate;
    }

    public List<ChangeListener<Transaction>> getChangeListeners() {
        return this.changeListeners;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }

    protected void fireChange() {
        for (ChangeListener<Transaction> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }
    public void addChangeListener(ChangeListener<Transaction> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    public void removeChangeListener(ChangeListener<Transaction> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
