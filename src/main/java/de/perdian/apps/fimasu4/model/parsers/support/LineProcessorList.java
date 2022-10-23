package de.perdian.apps.fimasu4.model.parsers.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class LineProcessorList {

    private List<LineProcessor> lineProcessors = null;

    public LineProcessorList() {
        this.setLineProcessors(new ArrayList<>());
    }

    public LineProcessorList add(LineProcessor lineProcessor) {
        this.getLineProcessors().add(lineProcessor);
        return this;
    }

    public void process(String input) throws IOException {
        try (BufferedReader lineReader = new BufferedReader(new StringReader(input))) {
            for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                if (StringUtils.isNotBlank(line)) {
                    for (LineProcessor lineProcessor : this.getLineProcessors()) {
                        lineProcessor.processLine(line.strip());
                    }
                }
            }
        }
    }

    private List<LineProcessor> getLineProcessors() {
        return this.lineProcessors;
    }
    private void setLineProcessors(List<LineProcessor> lineProcessors) {
        this.lineProcessors = lineProcessors;
    }

}
