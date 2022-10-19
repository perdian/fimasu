package de.perdian.apps.fimasu4.model;

import de.perdian.apps.fimasu4.model.persistence.impl.DocumentModelRepository;

public interface FimasuModelRepository {

    public static FimasuModelRepository getRepository() {
        return new DocumentModelRepository();
    }

    FimasuModel loadModel();

}
