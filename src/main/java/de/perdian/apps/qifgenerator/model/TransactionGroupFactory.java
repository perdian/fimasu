package de.perdian.apps.qifgenerator.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public static ObservableList<TransactionGroup> loadTransactionGroups(File storageFile) {

        ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();

        ChangeListener<TransactionGroup> transactionGroupChangeListener = (o, oldValue, newValue) -> TransactionGroupFactory.writeToStorageFile(transactionGroups, storageFile);
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                change.getRemoved().forEach(removedGroup -> removedGroup.removeChangeListener(transactionGroupChangeListener));
                change.getAddedSubList().forEach(addedGroup -> addedGroup.addChangeListener(transactionGroupChangeListener));
            }
        });

        if (storageFile.exists()) {
            log.info("Loading transaction groups from file: {}", storageFile.getAbsolutePath());
            try (ObjectInputStream transactionGroupsStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(storageFile)))) {
                List<TransactionGroup> storageTransactionGroups = (List<TransactionGroup>)transactionGroupsStream.readObject();
                if (storageTransactionGroups != null) {
                    for (TransactionGroup transactionGroup : storageTransactionGroups) {
                        transactionGroups.add(transactionGroup);
                    }
                }
            } catch (Exception e) {
                log.warn("Cannot load transaction groups from file: {}", storageFile.getAbsolutePath(), e);
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

    private static void writeToStorageFile(List<TransactionGroup> transactionGroups, File storageFile) {
        if (!storageFile.getParentFile().exists()) {
            log.debug("Creating target directory: {}", storageFile.getParentFile().getAbsolutePath());
            storageFile.getParentFile().mkdirs();
        }
        log.debug("Storing {} transaction groups into file: {}", transactionGroups.size(), storageFile.getAbsolutePath());
        try (ObjectOutputStream transactionGroupsStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(storageFile)))) {
            transactionGroupsStream.writeObject(new ArrayList<>(transactionGroups));
            transactionGroupsStream.flush();
        } catch (Exception e) {
            log.warn("Cannot store transaction groups into file: {}", storageFile.getAbsolutePath(), e);
        }
    }

}
