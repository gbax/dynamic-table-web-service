package ru.gbax.restest.entity.model;

/**
 * Поле для фильтрации
 * Created by GBAX on 21.07.2015.
 */
public class FilterField {

    private String field;
    private String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
