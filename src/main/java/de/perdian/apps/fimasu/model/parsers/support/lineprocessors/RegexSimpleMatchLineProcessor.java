package de.perdian.apps.fimasu.model.parsers.support.lineprocessors;

import java.util.function.Consumer;
import java.util.regex.Matcher;

public class RegexSimpleMatchLineProcessor extends RegexLineProcessor {

    private Consumer<String> matchedLineConsumer = null;

    public RegexSimpleMatchLineProcessor(String pattern, Consumer<String> matchedLineConsumer) {
        super(pattern);
        this.setMatchedLineConsumer(matchedLineConsumer);
    }

    @Override
    protected void processLineWithMatcher(String line, Matcher lineMatcher) {
        this.getMatchedLineConsumer().accept(line);
    }

    private Consumer<String> getMatchedLineConsumer() {
        return this.matchedLineConsumer;
    }
    private void setMatchedLineConsumer(Consumer<String> matchedLineConsumer) {
        this.matchedLineConsumer = matchedLineConsumer;
    }

}
