package de.perdian.apps.fimasu4.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransactionGroup {

    private StringProperty title = null;

    public TransactionGroup() {
        this.setTitle(new SimpleStringProperty("New transaction group"));
    }

    public StringProperty getTitle() {
        return this.title;
    }
    private void setTitle(StringProperty title) {
        this.title = title;
    }

}
