package de.perdian.apps.fimasu4.quicken;

import java.util.List;

public class RecordList {

    private List<Record> records = null;
    private RecordListType type = RecordListType.INVESTMENT;

    public RecordList(List<Record> records) {
        this.setRecords(records);
    }

    public String toQifString() {
        StringBuilder result = new StringBuilder();
        result.append("!Type:").append(this.getType().getQifValue()).append("\n");
        for (int recordIndex=0; recordIndex < this.getRecords().size(); recordIndex++) {
            result.append(this.getRecords().get(recordIndex).toQifString());
            result.append("^\n");
        }
        return result.toString();
    }

    public List<Record> getRecords() {
        return this.records;
    }
    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public RecordListType getType() {
        return this.type;
    }
    public void setType(RecordListType type) {
        this.type = type;
    }

}
