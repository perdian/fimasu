package de.perdian.apps.qifgenerator.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransactionGroupFactory {

    private static final Logger log = LoggerFactory.getLogger(TransactionGroupFactory.class);

    @SuppressWarnings("unchecked")
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
            try (ObjectInputStream transactionGroupsStream = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(storageFile)))) {
                List<TransactionGroup> storageTransactionGroups = (List<TransactionGroup>)transactionGroupsStream.readObject();
                if (storageTransactionGroups != null) {
                    for (TransactionGroup transactionGroup : storageTransactionGroups) {
                        transactionGroups.add(transactionGroup);
                    }
                }
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", storageFile, e);
            }
        }

        if (transactionGroups.isEmpty()) {
            TransactionGroup defaultTransactionGroup = new TransactionGroup();
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
        try (ObjectOutputStream transactionGroupsStream = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(storageFile)))) {
            transactionGroupsStream.writeObject(new ArrayList<>(transactionGroups));
            transactionGroupsStream.flush();
        } catch (Exception e) {
            log.warn("Cannot store transaction groups into file: {}", storageFile, e);
        }
    }

}
