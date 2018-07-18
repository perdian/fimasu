package de.perdian.apps.qifgenerator.fxnew;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;

class QifGeneratorPreferencesHelper {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorPreferencesHelper.class);

    static Map<String, String> readValues(File valuesFile) {
        Map<String, String> resultMap = new HashMap<>();
        if (valuesFile.exists()) {
            log.info("Reading preferences values from file: {}", valuesFile.getAbsolutePath());
            try (InputStream valuesStream = new BufferedInputStream(new FileInputStream(valuesFile))) {
                Properties properties = new Properties();
                properties.loadFromXML(valuesStream);
                for (Map.Entry<Object, Object> propertyEntry : properties.entrySet()) {
                    resultMap.put((String)propertyEntry.getKey(), (String)propertyEntry.getValue());
                }
            } catch (Exception e) {
                log.warn("Cannot read preferences values from file: {}", valuesFile.getAbsolutePath(), e);
            }
        }
        return resultMap;
    }

    static void storeValues(Map<String, String> values, File valuesFile) {
        try {

            if (!valuesFile.getParentFile().exists()) {
                log.debug("Creating target directory: {}", valuesFile.getParentFile().getAbsolutePath());
                valuesFile.getParentFile().mkdirs();
            }

            log.debug("Storing {} preferences values into file: {}", values.size(), valuesFile.getAbsolutePath());
            try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(valuesFile))) {
                Properties properties = new Properties();
                values.entrySet().stream().filter(entry -> StringUtils.isNotEmpty(entry.getValue())).forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));
                properties.storeToXML(fileStream, null, "UTF-8");
                fileStream.flush();
            }

        } catch (Exception e) {
            log.warn("Cannot write preferences values into file: " + valuesFile.getAbsolutePath(), e);
        }
    }

    static List<TransactionGroup> readTransactionGroups(File transactionGroupsFile) {
        List<TransactionGroup> transactionGroups = new ArrayList<>();
        if (transactionGroupsFile.exists()) {
            log.info("Loading transaction groups from file: {}", transactionGroupsFile.getAbsolutePath());
            try {
                throw new UnsupportedOperationException();
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", transactionGroupsFile.getAbsolutePath(), e);
            }
        }
        return transactionGroups;
    }

    static void storeTransactionGroups(List<TransactionGroup> transactionGroups, File transactionGroupsFile) {

        if (!transactionGroupsFile.getParentFile().exists()) {
            log.debug("Creating target directory: {}", transactionGroupsFile.getParentFile().getAbsolutePath());
            transactionGroupsFile.getParentFile().mkdirs();
        }

        log.debug("Storing {} transaction groups into file: {}", transactionGroups.size(), transactionGroupsFile.getAbsolutePath());
        throw new UnsupportedOperationException();

    }

}
