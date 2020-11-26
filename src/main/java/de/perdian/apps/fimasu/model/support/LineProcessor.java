package de.perdian.apps.fimasu.model.support;

@FunctionalInterface
public interface LineProcessor {

    boolean processLine(String line);

}
