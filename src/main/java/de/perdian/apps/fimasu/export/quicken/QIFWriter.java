package de.perdian.apps.fimasu.export.quicken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class QIFWriter {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M.d.yy");
    public static final NumberFormat SHORT_NUMBER_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
    public static final NumberFormat LONG_NUMBER_FORMAT = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));

    private StringBuilder output = null;

    public QIFWriter() {
        StringBuilder output = new StringBuilder();
        output.append("!Type:Invst\n");
        this.setOutput(output);
    }

    public QIFWriter appendLine(char key) {
        return this.appendLine(key, "");
    }

    public QIFWriter appendLine(char key, String line) {
        this.getOutput().append(key);
        if (StringUtils.isNotEmpty(line)) {
            this.getOutput().append(" ").append(line.strip());
        }
        this.getOutput().append("\n");
        return this;
    }

    public String toOutput() {
        return this.getOutput().toString();
    }

    private StringBuilder getOutput() {
        return this.output;
    }
    private void setOutput(StringBuilder output) {
        this.output = output;
    }

}
