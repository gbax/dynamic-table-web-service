package ru.gbax.restest.entity.model;

/**
 * Данные по колонке таблицы
 * Created by GBAX on 21.07.2015.
 */
public class TableColumn {

    private String column;
    private String columpType;
    private String dbName;

    public TableColumn(String column, String columpType, String dbName) {
        this.column = column;
        this.columpType = columpType;
        this.dbName = dbName;
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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
