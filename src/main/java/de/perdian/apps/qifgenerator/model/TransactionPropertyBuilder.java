package de.perdian.apps.qifgenerator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

class TransactionPropertyBuilder {

    private Transaction transaction = null;

    TransactionPropertyBuilder(Transaction transaction) {
        this.setTransaction(transaction);
    }

    TransactionPropertyBuilderItem<DoubleProperty, Number> createDoubleProperty() {
        return new TransactionPropertyBuilderItem<>(SimpleDoubleProperty::new);
    }

    TransactionPropertyBuilderItem<StringProperty, String> createStringProperty() {
        return new TransactionPropertyBuilderItem<>(SimpleStringProperty::new);
    }

    class TransactionPropertyBuilderItem<P extends Property<T>, T> {

        private Supplier<P> propertySupplier = null;
        private List<ChangeListener<T>> changeListeners = null;

        TransactionPropertyBuilderItem(Supplier<P> propertySupplier) {
            this.setPropertySupplier(propertySupplier);
            this.setChangeListeners(new ArrayList<>(List.of((o, oldValue, newValue) -> TransactionPropertyBuilder.this.getTransaction().fireChange())));
        }

        public TransactionPropertyBuilderItem<P, T> changeListener(BiConsumer<T, T> valuesConsumer) {
            this.getChangeListeners().add(0, (o, oldValue, newValue) -> valuesConsumer.accept(oldValue, newValue));
            return this;
        }

        public P get() {
            P property = this.getPropertySupplier().get();
            this.getChangeListeners().forEach(changeListener -> property.addListener(changeListener));
            return property;
        }

        private Supplier<P> getPropertySupplier() {
            return this.propertySupplier;
        }
        private void setPropertySupplier(Supplier<P> propertySupplier) {
            this.propertySupplier = propertySupplier;
        }

        private List<ChangeListener<T>> getChangeListeners() {
            return this.changeListeners;
        }
        private void setChangeListeners(List<ChangeListener<T>> changeListeners) {
            this.changeListeners = changeListeners;
        }

    }

    private Transaction getTransaction() {
        return this.transaction;
    }
    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

}