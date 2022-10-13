package de.perdian.apps.fimasu4.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;

public abstract class Transaction implements Serializable {

    static final long serialVersionUID = 1L;

    private List<ChangeListener<Object>> changeListeners = null;

    public Transaction() {
        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        this.setChangeListeners(changeListeners);
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
