package de.perdian.apps.qifgenerator.fxnew;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class QifGeneratorPreferences {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorPreferences.class);

    private File preferencesDirectory = null;
    private File valuesFile = null;
    private File transactionGroupsFile = null;
    private Map<String, StringProperty> valueProperties = null;
    private ObservableList<TransactionGroup> transactionGroups = null;

    private QifGeneratorPreferences() {
    }

    public static QifGeneratorPreferences create() {

        QifGeneratorPreferences preferences = new QifGeneratorPreferences();
        File preferencesDirectory = new File(System.getProperty("user.home"), ".qifgenerator");
        log.info("Using preferences directory: {}", preferencesDirectory);
        preferences.setPreferencesDirectory(preferencesDirectory);

        Map<String, StringProperty> valueProperties = new LinkedHashMap<>();
        File valuesFile = new File(preferencesDirectory, "values.object");
        if (valuesFile.exists()) {
            log.info("Loading preference values from file: {}", valuesFile.getAbsolutePath());
            try {
                QifGeneratorPreferences.readValues(valuesFile).forEach((key, value) -> {
                    StringProperty property = new SimpleStringProperty();
                    property.setValue(value);
                    property.addListener(preferences::handleValueUpdated);
                    valueProperties.put(key, property);
                });
            } catch (Exception e) {
                log.warn("Cannot load preference values from file: " + valuesFile.getAbsolutePath(), e);
            }
        }
        preferences.setValuesFile(valuesFile);
        preferences.setValueProperties(valueProperties);

        ChangeListener<TransactionGroup> transactionGroupChangeListener = preferences::handleValueUpdated;
        ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();
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
        File transactionGroupsFile = new File(preferencesDirectory, "transactionGroups.object");
        if (transactionGroupsFile.exists()) {
            log.info("Loading transaction groups from file: {}", transactionGroupsFile.getAbsolutePath());
            try {
                transactionGroups.addAll(QifGeneratorPreferences.readTransactionGroups(transactionGroupsFile));
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", transactionGroupsFile.getAbsolutePath(), e);
            }
        }
        if (transactionGroups.isEmpty()) {
            transactionGroups.add(new TransactionGroup("Default"));
        }
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> preferences.storeTransactionGroups());
        preferences.setTransactionGroupsFile(transactionGroupsFile);
        preferences.setTransactionGroups(transactionGroups);

        return preferences;

    }

    public StringProperty getValueProperty(String key) {
        StringProperty property = this.getValueProperties().get(key);
        if (property == null) {
            property = new SimpleStringProperty();
            property.addListener(this::handleValueUpdated);
            this.getValueProperties().put(key, property);
        }
        return property;
    }

    private static List<TransactionGroup> readTransactionGroups(File transactionGroupsFile) {
        throw new UnsupportedOperationException();
    }

    private void storeTransactionGroups() {

        if (!this.getTransactionGroupsFile().getParentFile().exists()) {
            log.debug("Creating target directory: {}", this.getTransactionGroupsFile().getParentFile().getAbsolutePath());
            this.getTransactionGroupsFile().getParentFile().mkdirs();
        }

        log.debug("Storing {} transaction groups into file: {}", this.getTransactionGroups().size(), this.getTransactionGroupsFile().getAbsolutePath());
        throw new UnsupportedOperationException();

    }

    private static Map<String, String> readValues(File valuesFile) throws Exception {
        try (InputStream valuesStream = new BufferedInputStream(new FileInputStream(valuesFile))) {
            Map<String, String> resultMap = new HashMap<>();
            Properties properties = new Properties();
            properties.loadFromXML(valuesStream);
            for (Map.Entry<Object, Object> propertyEntry : properties.entrySet()) {
                resultMap.put((String)propertyEntry.getKey(), (String)propertyEntry.getValue());
            }
            return resultMap;
        }
    }

    private void storeValues() {
        try {

            if (!this.getValuesFile().getParentFile().exists()) {
                log.debug("Creating target directory: {}", this.getValuesFile().getParentFile().getAbsolutePath());
                this.getValuesFile().getParentFile().mkdirs();
            }

            log.debug("Storing {} preferences values into file: {}", this.getValueProperties().size(), this.getValuesFile().getAbsolutePath());
            try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(this.getValuesFile()))) {
                Properties properties = new Properties();
                this.getValueProperties().entrySet().stream().filter(entry -> StringUtils.isNotEmpty(entry.getValue().getValue())).forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue().getValue()));
                properties.storeToXML(fileStream, null, "UTF-8");
                fileStream.flush();
            }

        } catch (Exception e) {
            log.warn("Cannot write preferences value into file: " + this.getValuesFile().getAbsolutePath(), e);
        }
    }

    private void handleValueUpdated(Object source, Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            this.storeValues();
        }
    }

    public File getPreferencesDirectory() {
        return this.preferencesDirectory;
    }
    private void setPreferencesDirectory(File preferencesDirectory) {
        this.preferencesDirectory = preferencesDirectory;
    }

    private File getValuesFile() {
        return this.valuesFile;
    }
    private void setValuesFile(File valuesFile) {
        this.valuesFile = valuesFile;
    }

    private Map<String, StringProperty> getValueProperties() {
        return this.valueProperties;
    }
    private void setValueProperties(Map<String, StringProperty> valueProperties) {
        this.valueProperties = valueProperties;
    }

    private File getTransactionGroupsFile() {
        return this.transactionGroupsFile;
    }
    private void setTransactionGroupsFile(File transactionGroupsFile) {
        this.transactionGroupsFile = transactionGroupsFile;
    }

    public ObservableList<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }
    private void setTransactionGroups(ObservableList<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

}
