package ru.gbax.restest.entity.model;

import java.util.List;

/**
 * Created by GBAX on 22.07.2015.
 */
public class TableData {

    private List<TableRow> data;
    private Integer pageCount;

    public TableData(List<TableRow> data, Integer pageCount) {
        this.data = data;
        this.pageCount = pageCount;
    }

    public List<TableRow> getData() {
        return data;
    }

    public void setData(List<TableRow> data) {
        this.data = data;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
}
