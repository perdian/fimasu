package de.perdian.apps.fimasu.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final ObjectProperty<MonetaryValue> bookingValue = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> valutaDate = new SimpleObjectProperty<>();
    private final BooleanProperty persistent = new SimpleBooleanProperty(false);
    private final List<ChangeListener<Transaction>> changeListeners = new CopyOnWriteArrayList<>();

    public Transaction() {

        this.getWkn().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getIsin().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTitle().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getValutaDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingValue().addListener((o, oldValue, newValue) -> this.fireChange());

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

    public ObjectProperty<MonetaryValue> getBookingValue() {
        return this.bookingValue;
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
