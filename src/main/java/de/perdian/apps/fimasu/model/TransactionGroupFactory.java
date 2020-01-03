package de.perdian.apps.fimasu.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransactionGroupFactory {

    private static final Logger log = LoggerFactory.getLogger(TransactionGroupFactory.class);

    public static ObservableList<TransactionGroup> loadTransactionGroups(Path storageFile) {

        ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();

        ChangeListener<TransactionGroup> transactionGroupChangeListener = (o, oldValue, newValue) -> TransactionGroupFactory.writeToStorageFile(transactionGroups, storageFile);
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                change.getRemoved().forEach(removedGroup -> removedGroup.removeChangeListener(transactionGroupChangeListener));
                change.getAddedSubList().forEach(addedGroup -> addedGroup.addChangeListener(transactionGroupChangeListener));
            }
        });

        if (Files.exists(storageFile)) {
            log.info("Loading transaction groups from file: {}", storageFile);
            try (InputStream transactionGroupsStream = new BufferedInputStream(Files.newInputStream(storageFile))) {
                transactionGroups.addAll(TransactionGroupPersistence.loadTransactionGroups(transactionGroupsStream));
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", storageFile, e);
            }
        }

        if (transactionGroups.isEmpty()) {
            TransactionGroup defaultTransactionGroup = new TransactionGroup();
            defaultTransactionGroup.getPersistent().setValue(Boolean.TRUE);
            defaultTransactionGroup.getTitle().setValue("Default");
            transactionGroups.add(defaultTransactionGroup);
        }
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> TransactionGroupFactory.writeToStorageFile(transactionGroups, storageFile));

        return transactionGroups;

    }

    private static void writeToStorageFile(List<TransactionGroup> transactionGroups, Path storageFile) {
        if (!Files.exists(storageFile.getParent())) {
            try {
                log.debug("Creating target directory: {}", storageFile.getParent());
                Files.createDirectories(storageFile.getParent());
            } catch (IOException e) {
                log.warn("Cannot create target directory: {}", storageFile.getParent(), e);
            }
        }
        log.debug("Storing {} transaction groups into file: {}", transactionGroups.size(), storageFile);
        try (OutputStream transactionGroupsStream = new BufferedOutputStream(Files.newOutputStream(storageFile))) {
            TransactionGroupPersistence.writeTransactionGroups(transactionGroups, transactionGroupsStream);
            transactionGroupsStream.flush();
        } catch (Exception e) {
            log.warn("Cannot store transaction groups into file: {}", storageFile, e);
        }
    }

}
