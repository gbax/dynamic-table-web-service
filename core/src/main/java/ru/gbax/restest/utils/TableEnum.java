package ru.gbax.restest.utils;

import org.apache.commons.lang3.StringUtils;
import ru.gbax.restest.entity.Customer;
import ru.gbax.restest.entity.Order;
import ru.gbax.restest.entity.User;

/**
 * Список таблиц со вспомогательными данными
 * Created by GBAX on 21.07.2015.
 */
public enum TableEnum {

    USER("user", "Пользователи", User.class),
    CUSTOMER("customer", "Заказчики", Customer.class),
    ORDER("orders", "Заказы", Order.class);

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

    public static TableEnum getByName(String name) {
        for(TableEnum tableEnum : values()) {
            if (StringUtils.equalsIgnoreCase(tableEnum.getName(),name)){
                return tableEnum;
            }
        }
        return null;
    }
}
