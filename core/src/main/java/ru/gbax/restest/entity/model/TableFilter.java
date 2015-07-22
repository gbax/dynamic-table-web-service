package ru.gbax.restest.entity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GBAX on 21.07.2015.
 */
public class TableFilter {

    public static final Integer PAGE_SIZE = 5;

    private String tableName;
    private String sort;
    private String order;
    private List<FilterField> filters = new ArrayList<>();
    private Integer currentPage = 0;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<FilterField> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterField> filters) {
        this.filters = filters;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
