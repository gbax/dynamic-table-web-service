package ru.gbax.restest.utils;

import ru.gbax.restest.entity.User;

/**
 * Created by GBAX on 21.07.2015.
 */
public enum TableEnum {

    USER("user", "Пользователи", User.class);

    TableEnum(String name, String translateName, Class aClass) {
        this.name = name;
        this.translateName = translateName;
        this.tableClass = aClass;
    }

    private String name;
    private String translateName;
    private Class tableClass;

    public String getName() {
        return name;
    }

    public Class getTableClass() {
        return tableClass;
    }

    public String getTranslateName() {
        return translateName;
    }
}
