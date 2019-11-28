package de.perdian.apps.qifgenerator.preferences;

import java.io.File;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;

public class Preferences {

    private File storageDirectory = null;
    private ObservableMap<String, String> values = null;

    Preferences(ObservableMap<String, String> values, File storageDirectory) {
        this.setStorageDirectory(storageDirectory);
        this.setValues(values);
    }

    public StringProperty getStringProperty(String propertyName, String defaultValue) {
        return new SimpleStringProperty(defaultValue);
    }

    public IntegerProperty getIntegerProperty(String propertyName, int defaultValue) {
        return new SimpleIntegerProperty(defaultValue);
    }

    public File getStorageDirectory() {
        return this.storageDirectory;
    }
    private void setStorageDirectory(File storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public ObservableMap<String, String> getValues() {
        return this.values;
    }
    private void setValues(ObservableMap<String, String> values) {
        this.values = values;
    }

}
