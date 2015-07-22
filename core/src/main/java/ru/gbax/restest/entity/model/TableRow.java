package ru.gbax.restest.entity.model;

import java.util.List;

/**
 * Строка с данными таблицы
 * Created by GBAX on 21.07.2015.
 */
public class TableRow {

    private List<String> values;

    public TableRow(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String>  values) {
        this.values = values;
    }

}
