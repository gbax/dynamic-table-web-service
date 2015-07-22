package ru.gbax.restest.entity.model;

/**
 * Created by GBAX on 21.07.2015.
 */
public class TableNameModel {

    public TableNameModel(String name, String translateName) {
        this.name = name;
        this.translateName = translateName;
    }

    private String name;
    private String translateName;

    public String getTranslateName() {
        return translateName;
    }

    public void setTranslateName(String translateName) {
        this.translateName = translateName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
