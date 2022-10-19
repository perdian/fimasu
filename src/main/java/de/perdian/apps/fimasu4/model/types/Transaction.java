package de.perdian.apps.fimasu4.model.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.fimasu4.model.persistence.Values;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public class Transaction implements Serializable {

    static final long serialVersionUID = 1L;

    private ObjectProperty<TransactionType> type = null;
    private BooleanProperty persistent = null;
    private List<ChangeListener<Object>> changeListeners = null;

    public Transaction() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        this.setChangeListeners(changeListeners);

        ObjectProperty<TransactionType> type = new SimpleObjectProperty<>();
        this.setType(type);

        BooleanProperty persistent = new SimpleBooleanProperty();
        this.setPersistent(persistent);

    }

    public void readValues(Values sourcrValues) {
    }

    public Values writeValues() {
        Values values = new Values();
        values.setAttribute("class", this.getClass().getName());
        return values;
    }

    public ObjectProperty<TransactionType> getType() {
        return this.type;
    }
    private void setType(ObjectProperty<TransactionType> type) {
        this.type = type;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }
    private void setPersistent(BooleanProperty persistent) {
        this.persistent = persistent;
    }

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    private List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<Object>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}
