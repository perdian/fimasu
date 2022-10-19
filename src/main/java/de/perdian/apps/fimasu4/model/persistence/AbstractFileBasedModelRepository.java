package de.perdian.apps.fimasu4.model.persistence;

import java.io.File;

import de.perdian.apps.fimasu4.model.FimasuModel;
import de.perdian.apps.fimasu4.model.FimasuModelRepository;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;

public abstract class AbstractFileBasedModelRepository implements FimasuModelRepository {

    @Override
    public FimasuModel loadModel() {
        File modelFile = new File(System.getProperty("user.home"), ".fimasu/model.gz");
        FimasuModel modelFromFile = this.loadModelFromFile(modelFile);
        FimasuModel model = modelFromFile == null ? new FimasuModel() : modelFromFile;
        if (model.getTransactionGroups().isEmpty()) {
            model.getTransactionGroups().add(new TransactionGroup(null));
        }
        if (model.getSelectedTransactionGroup().getValue() == null) {
            model.getSelectedTransactionGroup().setValue(model.getTransactionGroups().get(0));
        }
        model.addChangeListener((o, oldValue, newValue) -> this.writeModelToFile(model, modelFile));
        return model;
    }

    protected abstract FimasuModel loadModelFromFile(File modelFile);
    protected abstract void writeModelToFile(FimasuModel model, File targetFile);

}
