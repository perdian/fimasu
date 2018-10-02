package de.perdian.apps.qifgenerator.fx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fx.model.TransactionGroup;

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

    @SuppressWarnings("unchecked")
    static List<TransactionGroup> readTransactionGroups(File transactionGroupsFile) {
        if (transactionGroupsFile.exists()) {
            log.info("Loading transaction groups from file: {}", transactionGroupsFile.getAbsolutePath());
            try (ObjectInputStream transactionGroupsStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(transactionGroupsFile)))) {
                List<TransactionGroup> transactionGroups = (List<TransactionGroup>)transactionGroupsStream.readObject();
                return Optional.ofNullable(transactionGroups).orElseGet(Collections::emptyList);
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", transactionGroupsFile.getAbsolutePath(), e);
            }
        }
        return Collections.emptyList();
    }

    static void storeTransactionGroups(List<TransactionGroup> transactionGroups, File transactionGroupsFile) {

        if (!transactionGroupsFile.getParentFile().exists()) {
            log.debug("Creating target directory: {}", transactionGroupsFile.getParentFile().getAbsolutePath());
            transactionGroupsFile.getParentFile().mkdirs();
        }

        log.debug("Storing {} transaction groups into file: {}", transactionGroups.size(), transactionGroupsFile.getAbsolutePath());
        try (ObjectOutputStream transactionGroupsStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(transactionGroupsFile)))) {
            transactionGroupsStream.writeObject(new ArrayList<>(transactionGroups));
            transactionGroupsStream.flush();
        } catch (Exception e) {
            log.warn("Cannot store transaction groups into file: {}", transactionGroupsFile.getAbsolutePath(), e);
        }

    }

}
