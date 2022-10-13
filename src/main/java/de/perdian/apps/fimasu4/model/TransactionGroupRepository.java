package de.perdian.apps.fimasu4.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionGroupRepository {

    private static final Logger log = LoggerFactory.getLogger(TransactionGroupRepository.class);

    public static TransactionGroupModel loadTransactionGroupModel() {

        File repositoryFile = TransactionGroupRepository.resolveRepositoryFile();
        if (repositoryFile.exists()) {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(repositoryFile))) {
                return TransactionGroupRepository.loadTransactionGroupModel(inputStream);
            } catch (Exception e) {
                log.error("Cannot load transaction groups from repository file at: {}", repositoryFile.getAbsolutePath(), e);
            }
        }

        // We couldn't load the model from an existing file, so we'll create one from scratch
        TransactionGroupModel newModel = new TransactionGroupModel();
        newModel.addChangeListener((o, oldValue, newValue) -> TransactionGroupRepository.writeTransactionGroupModel(newModel));
        return newModel;

    }

    private static TransactionGroupModel loadTransactionGroupModel(InputStream inputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static void writeTransactionGroupModel(TransactionGroupModel model) {
        File repositoryFile = TransactionGroupRepository.resolveRepositoryFile();
        try {
            if (!repositoryFile.getParentFile().exists()) {
                log.trace("Creating transaction groups repository directory at: {}", repositoryFile.getParentFile().getAbsolutePath());
                repositoryFile.getParentFile().mkdirs();
            }
            log.info("Writing transaction groups into repository file at: {}", repositoryFile.getAbsolutePath());
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(repositoryFile))) {
                TransactionGroupRepository.writeTransactionGroupModel(model, outputStream);
            }
        } catch (Exception e) {
            log.error("Cannot write transaction groups into repository file at: {}", repositoryFile.getAbsolutePath(), e);
        }
    }

    private static void writeTransactionGroupModel(TransactionGroupModel model, OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    private static File resolveRepositoryFile() {
        return new File(System.getProperty("user.home"), ".fimasu/transactionGroups");
    }

}
