package de.perdian.apps.qifgenerator.preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class PreferencesFactory {

    private static final Logger log = LoggerFactory.getLogger(PreferencesFactory.class);

    public static Preferences createPreferences() {

        File preferencesDirectory = new File(System.getProperty("user.home"), ".qifgenerator");
        log.info("Loading preferences from directory: {}", preferencesDirectory.getAbsolutePath());
        if (!preferencesDirectory.exists()) {
            log.debug("Creating preferences directory at: {}", preferencesDirectory.getAbsolutePath());
            preferencesDirectory.mkdirs();
        }

        File preferencesFile = new File(preferencesDirectory, "values");
        ObservableMap<String, String> values = FXCollections.observableHashMap();
        values.putAll(PreferencesFactory.loadValues(preferencesFile));
        values.addListener((MapChangeListener.Change<? extends String, ? extends String> change) -> PreferencesFactory.storeValues(values, preferencesFile));
        return new Preferences(values, preferencesDirectory);

    }

    private static void storeValues(Map<String, String> values, File preferencesFile) {
        try {
            log.debug("Writing preference values into file: " + preferencesFile.getAbsolutePath());
            try (OutputStream preferencesFileStream = new BufferedOutputStream(new FileOutputStream(preferencesFile))) {
                Properties properties = new Properties();
                values.forEach((key, value) -> properties.setProperty(key, value));
                properties.storeToXML(preferencesFileStream, null);
            }
        } catch (Exception e) {
            log.warn("Cannot write preference values into file: " + preferencesFile.getAbsolutePath(), e);
        }
    }

    private static Map<String, String> loadValues(File preferencesFile) {
        Map<String, String> values = new LinkedHashMap<>();
        try {
            if (preferencesFile.exists()) {
                log.debug("Loading preference values from file: " + preferencesFile.getAbsolutePath());
                Properties properties = new Properties();
                try (InputStream preferencesFileStream = new BufferedInputStream(new FileInputStream(preferencesFile))) {
                    properties.loadFromXML(preferencesFileStream);
                }
                properties.forEach((key, value) -> values.put((String)key, (String)value));
            }
        } catch (Exception e) {
            log.warn("Cannot load preference values from file: " + preferencesFile.getAbsolutePath(), e);
        }
        return values;
    }

}
