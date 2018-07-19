package de.perdian.apps.qifgenerator.fxnew;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class QifGeneratorPreferences {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorPreferences.class);

    private Map<String, StringProperty> valueProperties = null;
    private ObservableList<TransactionGroup> transactionGroups = null;

    public QifGeneratorPreferences() {

        File preferencesDirectory = new File(System.getProperty("user.home"), ".qifgenerator");
        log.info("Using preferences directory: {}", preferencesDirectory);

        ObservableMap<String, StringProperty> valueProperties = FXCollections.observableHashMap();
        File valuesFile = new File(preferencesDirectory, "values.object");
        ChangeListener<String> valueChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                QifGeneratorPreferencesHelper.storeValues(valueProperties.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getValue())), valuesFile);
            }
        };
        valueProperties.addListener((MapChangeListener.Change<? extends String, ? extends StringProperty> change) -> {
            if (change.wasAdded()) {
                change.getValueAdded().addListener(valueChangeListener);
            }
        });
        for (Map.Entry<String, String> valueEntry : QifGeneratorPreferencesHelper.readValues(valuesFile).entrySet()) {
            valueProperties.put(valueEntry.getKey(), new SimpleStringProperty(valueEntry.getValue()));
        }
        this.setValueProperties(valueProperties);

        ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();
        File transactionGroupsFile = new File(preferencesDirectory, "transactionGroups.object");
        ChangeListener<TransactionGroup> transactionGroupChangeListener = (o, oldValue, newValue) -> QifGeneratorPreferencesHelper.storeTransactionGroups(transactionGroups, transactionGroupsFile);
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                for (TransactionGroup removedGroup : change.getRemoved()) {
                    removedGroup.removeChangeListener(transactionGroupChangeListener);
                }
                for (TransactionGroup newGroup : change.getAddedSubList()) {
                    newGroup.addChangeListener(transactionGroupChangeListener);
                }
            }
        });
        transactionGroups.addAll(QifGeneratorPreferencesHelper.readTransactionGroups(transactionGroupsFile));
        if (transactionGroups.isEmpty()) {
            transactionGroups.add(new TransactionGroup("Default"));
        }
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> QifGeneratorPreferencesHelper.storeTransactionGroups(transactionGroups, transactionGroupsFile));
        this.setTransactionGroups(transactionGroups);

    }

    public StringProperty getValueProperty(String key) {
        return this.getValueProperties().compute(key, (k, v) -> v == null ? new SimpleStringProperty() : v);
    }

    private Map<String, StringProperty> getValueProperties() {
        return this.valueProperties;
    }
    private void setValueProperties(Map<String, StringProperty> valueProperties) {
        this.valueProperties = valueProperties;
    }

    public ObservableList<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }
    private void setTransactionGroups(ObservableList<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

}
