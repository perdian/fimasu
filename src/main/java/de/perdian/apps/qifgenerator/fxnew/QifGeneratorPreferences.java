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
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QifGeneratorPreferences {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorPreferences.class);

    private File preferencesDirectory = null;
    private File valuesFile = null;
    private Map<String, String> values = null;
    private Map<String, StringProperty> valueProperties = null;

    private QifGeneratorPreferences(File preferencesDirectory, File valuesFile) {
        this.setPreferencesDirectory(preferencesDirectory);

        Map<String, String> values = new LinkedHashMap<>();
        if (valuesFile.exists()) {
            log.info("Loading preference values from file: {}", valuesFile.getAbsolutePath());
            try (InputStream valuesStream = new BufferedInputStream(new FileInputStream(valuesFile))) {
                Properties properties = new Properties();
                properties.loadFromXML(valuesStream);
                properties.forEach((key, value) -> values.put((String)key, (String)value));
            } catch (Exception e) {
                log.warn("Cannot load preference values from file: " + valuesFile.getAbsolutePath(), e);
            }
        }
        this.setValuesFile(valuesFile);
        this.setValues(values);
        this.setValueProperties(new HashMap<>());

    }

    public static QifGeneratorPreferences create() {

        File preferencesDirectory = new File(System.getProperty("user.home"), ".qifgenerator");
        log.info("Using preferences directory: {}", preferencesDirectory);

        return new QifGeneratorPreferences(preferencesDirectory, new File(preferencesDirectory, "values.object"));

    }

    public StringProperty getValueProperty(String key) {
        StringProperty property = this.getValueProperties().get(key);
        if (property == null) {
            property = new SimpleStringProperty(this.getValues().get(key));
            property.addListener((o, oldValue, newValue) -> {
                if (!Objects.equals(oldValue, newValue)) {
                    this.getValues().put(key, newValue);
                    this.storeValues();
                }
            });
            this.getValueProperties().put(key, property);
        }
        return property;
    }

    private void storeValues() {
        try {

            if (!this.getValuesFile().getParentFile().exists()) {
                log.debug("Creating target directory: {}", this.getValuesFile().getParentFile().getAbsolutePath());
                this.getValuesFile().getParentFile().mkdirs();
            }

            log.debug("Storing preferences values into file: {}", this.getValuesFile().getAbsolutePath());
            try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(this.getValuesFile()))) {
                Properties properties = new Properties();
                this.getValues().entrySet().stream().filter(entry -> StringUtils.isNotEmpty(entry.getValue())).forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));
                properties.storeToXML(fileStream, null, "UTF-8");
                fileStream.flush();
            }

        } catch (Exception e) {
            log.warn("Cannot write preferences value into file: " + this.getValuesFile().getAbsolutePath(), e);
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

    private Map<String, String> getValues() {
        return this.values;
    }
    private void setValues(Map<String, String> values) {
        this.values = values;
    }

    private Map<String, StringProperty> getValueProperties() {
        return this.valueProperties;
    }
    private void setValueProperties(Map<String, StringProperty> valueProperties) {
        this.valueProperties = valueProperties;
    }

}
