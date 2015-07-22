package ru.gbax.restest.entity.model;

/**
 * Данные по колонке таблицы
 * Created by GBAX on 21.07.2015.
 */
public class TableColumn {

    private String column;
    private String columpType;

    public TableColumn(String column, String columpType) {
        this.column = column;
        this.columpType = columpType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumpType() {
        return columpType;
    }

    public void setColumpType(String columpType) {
        this.columpType = columpType;
    }
}
