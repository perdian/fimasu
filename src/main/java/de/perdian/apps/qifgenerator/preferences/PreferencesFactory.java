package de.perdian.apps.qifgenerator.preferences;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
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

        ObservableMap<String, String> values = FXCollections.observableHashMap();
        return new Preferences(values, preferencesDirectory);

    }

}
