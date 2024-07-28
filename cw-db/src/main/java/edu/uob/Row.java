package edu.uob;

import java.util.List;


public class Row {
    private final List<String> valueList;

    public Row(List<String> valueList) {
        this.valueList = valueList;
    }

    public String getRowValue(int index) {
        return this.valueList.get(index);
    }

    public void setRowValue(int index, String value) {
        this.valueList.set(index, value);
    }

    public List<String> getValueListFromRow() {
        return valueList;
    }
}
