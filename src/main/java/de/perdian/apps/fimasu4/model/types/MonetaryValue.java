package de.perdian.apps.fimasu4.model.types;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class MonetaryValue implements Serializable {

    static final long serialVersionUID = 1L;

    private ObjectProperty<BigDecimal> amount = null;
    private StringProperty currency = null;

    MonetaryValue() {
        this.setAmount(new SimpleObjectProperty<>());
        this.setCurrency(new SimpleStringProperty("EUR"));
    }

    public void addListener(ChangeListener<Object> changeListener) {
        this.getAmount().addListener(changeListener);
        this.getCurrency().addListener(changeListener);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("amount", this.getAmount().getValue());
        toStringBuilder.append("currency", this.getCurrency().getValue());
        return toStringBuilder.toString();
    }

    public ObjectProperty<BigDecimal> getAmount() {
        return this.amount;
    }
    private void setAmount(ObjectProperty<BigDecimal> amount) {
        this.amount = amount;
    }

    public StringProperty getCurrency() {
        return this.currency;
    }
    private void setCurrency(StringProperty currency) {
        this.currency = currency;
    }

}
