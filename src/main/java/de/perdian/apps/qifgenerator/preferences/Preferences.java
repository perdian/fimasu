package de.perdian.apps.qifgenerator.preferences;

import java.io.File;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class Preferences {

    private File storageDirectory = null;
    private ObservableMap<String, String> values = null;

    Preferences(ObservableMap<String, String> values, File storageDirectory) {
        this.setStorageDirectory(storageDirectory);
        this.setValues(values);
    }

    public StringProperty getStringProperty(String propertyName, String defaultValue) {
        StringProperty stringProperty = new SimpleStringProperty(this.getValues().getOrDefault(propertyName, defaultValue));
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && !Objects.equals(newValue, this.getValues().get(propertyName))) {
                if (StringUtils.isEmpty(newValue)) {
                    this.getValues().remove(propertyName);
                } else {
                    this.getValues().put(propertyName, newValue);
                }
            }
        });
        this.getValues().addListener((MapChangeListener.Change<? extends String, ? extends String> change) -> {
            if (Objects.equals(propertyName, change.getKey()) && !Objects.equals(change.getValueAdded(), stringProperty.getValue())) {
                stringProperty.setValue(change.getValueAdded());
            }
        });
        return stringProperty;
    }

    public IntegerProperty getIntegerProperty(String propertyName, int defaultValue) {
        StringProperty stringProperty = this.getStringProperty(propertyName, String.valueOf(defaultValue));
        IntegerProperty integerProperty = new SimpleIntegerProperty();
        try {
            integerProperty.setValue(Integer.valueOf(stringProperty.getValue()));
        } catch (Exception e) {
            // Ignore here
        }
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (StringUtils.isEmpty(newValue)) {
                integerProperty.setValue(null);
            } else {
                try {
                    Integer integerValue = Integer.valueOf(newValue);
                    if (!Objects.equals(integerValue, integerProperty.getValue())) {
                        integerProperty.setValue(integerValue);
                    }
                } catch (Exception e) {
                    // Ignore any error here
                }
            }
        });
        integerProperty.addListener((o, oldValue, newValue) -> {
            String newStringValue = newValue == null ? null : String.valueOf(newValue);
            if (!Objects.equals(newStringValue, stringProperty.getValue())) {
                stringProperty.setValue(newStringValue);
            }
        });
        return integerProperty;
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
