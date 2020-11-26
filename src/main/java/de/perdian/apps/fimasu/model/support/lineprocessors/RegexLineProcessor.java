package de.perdian.apps.fimasu.model.support.lineprocessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.perdian.apps.fimasu.model.support.LineProcessor;

abstract class RegexLineProcessor implements LineProcessor {

    private Pattern pattern = null;

    RegexLineProcessor(String pattern) {
        this.setPattern(Pattern.compile(pattern));
    }

    @Override
    public boolean processLine(String line) {
        Matcher lineMatcher = this.getPattern().matcher(line);
        if (lineMatcher.matches()) {
            this.processLineWithMatcher(line, lineMatcher);
            return true;
        } else {
            return false;
        }
    }

    protected abstract void processLineWithMatcher(String line, Matcher lineMatcher);

    private Pattern getPattern() {
        return this.pattern;
    }
    private void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

}
