package de.perdian.apps.fimasu.fx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FimasuPreferences {

    private static final Logger log = LoggerFactory.getLogger(FimasuPreferences.class);

    private File directory = null;
    private Map<String, String> values = null;
    private Map<String, StringProperty> properties = null;

    FimasuPreferences(File directory) {
        this.setDirectory(directory);
        this.setValues(new HashMap<>());
        this.setProperties(new HashMap<>());
        this.readPreferencesFromFile();
    }

    public synchronized StringProperty getStringProperty(String key) {
        return this.getStringProperty(key, null);
    }

    public synchronized StringProperty getStringProperty(String key, String defaultValue) {
        StringProperty stringProperty = this.getProperties().get(key);
        if (stringProperty == null) {
            stringProperty = new SimpleStringProperty(this.getStringValue(key).orElse(defaultValue));
            stringProperty.addListener((o, oldValue, newValue) -> this.setStringValue(key, newValue));
            this.getProperties().put(key, stringProperty);
        }
        return stringProperty;
    }

    public synchronized Optional<String> getStringValue(String key) {
        String storedValue = this.getValues().get(key);
        return StringUtils.isEmpty(storedValue) ? Optional.empty() : Optional.of(storedValue);
    }

    public synchronized boolean setStringValue(String key, String newValue) {
        String oldValue = this.getValues().get(key);
        if (Objects.equals(oldValue, newValue)) {
            return false;
        } else {
            this.getValues().put(key, newValue);
            StringProperty stringProperty = this.getProperties().get(key);
            if (stringProperty != null) {
                stringProperty.setValue(newValue);
            }
            this.writePreferencesToFile();
            return true;
        }
    }

    private void readPreferencesFromFile() {
        File preferencesFile = this.resolveFile("preferences.gz");
        if (preferencesFile.exists()) {
            try {
                Properties storageProperties = new Properties();
                try (InputStream storageStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(preferencesFile)))) {
                    storageProperties.loadFromXML(storageStream);
                }
                for (Map.Entry<Object, Object> storagePropertyEntry : storageProperties.entrySet()) {
                    this.getValues().put((String)storagePropertyEntry.getKey(), (String)storagePropertyEntry.getValue());
                }
            } catch (Exception e) {
                log.warn("Cannot read preferences from file: {}", preferencesFile.getAbsolutePath(), e);
            }
        }
    }

    private void writePreferencesToFile() {
        File preferencesFile = this.resolveFile("preferences.gz");
        try {
            if (!preferencesFile.getParentFile().exists()) {
                preferencesFile.getParentFile().mkdirs();
            }
            Properties storageProperties = new Properties();
            this.getProperties().forEach((key, value) -> storageProperties.setProperty(key, value.getValue()));
            try (OutputStream storageStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(preferencesFile)))) {
                storageProperties.storeToXML(storageStream, null, "UTF-8");
            }
        } catch (Exception e) {
            log.warn("Cannot write preferences into file: {}", preferencesFile.getAbsolutePath(), e);
        }
    }

    public File resolveFile(String fileName) {
        return new File(this.getDirectory(), fileName);
    }

    private File getDirectory() {
        return this.directory;
    }
    private void setDirectory(File directory) {
        this.directory = directory;
    }

    private Map<String, String> getValues() {
        return this.values;
    }
    private void setValues(Map<String, String> values) {
        this.values = values;
    }

    private Map<String, StringProperty> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<String, StringProperty> properties) {
        this.properties = properties;
    }

}
