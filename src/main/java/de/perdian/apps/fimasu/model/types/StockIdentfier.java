package de.perdian.apps.fimasu.model.types;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class StockIdentfier {

    private StringProperty wkn = null;
    private StringProperty isin = null;
    private StringProperty title = null;

    public StockIdentfier() {
        this.setWkn(new SimpleStringProperty());
        this.setIsin(new SimpleStringProperty());
        this.setTitle(new SimpleStringProperty());
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("wkn", this.getWkn().getValue());
        toStringBuilder.append("isin", this.getIsin().getValue());
        toStringBuilder.append("title", this.getTitle().getValue());
        return toStringBuilder.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof StockIdentfier thatIdentifier) {
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getWkn().getValue(), thatIdentifier.getWkn().getValue());
            equalsBuilder.append(this.getIsin().getValue(), thatIdentifier.getIsin().getValue());
            return equalsBuilder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getWkn());
        hashCodeBuilder.append(this.getIsin());
        return hashCodeBuilder.toHashCode();
    }

    void addChangeListener(ChangeListener<Object> changeListener) {
        this.getWkn().addListener(changeListener);
        this.getIsin().addListener(changeListener);
        this.getTitle().addListener(changeListener);
    }

    public StringProperty getWkn() {
        return this.wkn;
    }
    private void setWkn(StringProperty wkn) {
        this.wkn = wkn;
    }

    public StringProperty getIsin() {
        return this.isin;
    }
    private void setIsin(StringProperty isin) {
        this.isin = isin;
    }

    public StringProperty getTitle() {
        return this.title;
    }
    private void setTitle(StringProperty title) {
        this.title = title;
    }

}
