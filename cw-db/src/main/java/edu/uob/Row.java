package edu.uob;

import java.util.List;

/**
 * @author force
 */
public class Row {
    private final List<String> valueList;

    public Row(List<String> valueList) {
        this.valueList = valueList;
    }

    public String getValue(int index) {
        return this.valueList.get(index);
    }

    public void setValue(int index, String value) {
        this.valueList.set(index, value);
    }

    public List<String> getValueList() {
        return valueList;
    }
}
