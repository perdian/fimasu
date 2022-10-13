package de.perdian.apps.fimasu4.model.persistence;

public interface ValuesStore {

    Values createValues();
    void storeValues(Values values);

}
