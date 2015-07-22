package ru.gbax.restest.dao;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.gbax.restest.entity.model.FilterField;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.exceptions.ServiceErrorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

/**
 * Дао для работы с таблицами
 * Created by GBAX on 21.07.2015.
 */
@Repository
public class TableMetadataDAO {

    final static String STRING_TYPE_NAME = "VARCHAR_IGNORECASE";
    final static String LONG_TYPE_NAME = "INTEGER";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Получение метаданных таблицы
     *
     * @param tableName название энтити
     * @return метаданные таблицы
     */
    public List<TableColumn> getTableMetadata(final String tableName) throws ServiceErrorException {
        isTableExist(tableName, getTableList());
        List<TableColumn> tableColumns = new ArrayList<>();
        final String sql = String.format("SELECT COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='%s'", tableName);
        final Query nativeQuery = entityManager.createNativeQuery(sql);
        final List rows = nativeQuery.getResultList();
        for (Object row : rows) {
            Object[] cols = (Object[]) row;
            tableColumns.add(new TableColumn((String)cols[0], (String)cols[1]));
        }
        return tableColumns;
    }

    private void isTableExist(String tableName, List<String> tableList) throws ServiceErrorException {
         for (String name : tableList) {
             if (StringUtils.equalsIgnoreCase(name, tableName)) {
                 return;
             }
         }
        throw new ServiceErrorException(String.format("Таблица %s не существует", tableName));
    }


    /**
     * Получение данных таблицы
     *
     * @param filter
     * @return
     * @throws ServiceErrorException
     */
    public List<TableRow> getTableRows(final TableFilter filter) throws ServiceErrorException {
        if (filter.isFilterChanged()) {
            filter.setCurrentPage(1);
        }
        List rows = getData(filter, false);
        List<TableRow> tableRows = new ArrayList<>();
        for (Object row : rows) {
            Object[] cols = (Object[]) row;
            List<String> tableCols = new ArrayList<>();
            for (Object col : cols) {
                tableCols.add(col.toString());
            }
            tableRows.add(new TableRow(tableCols));
        }
        return tableRows;
    }

    /**
     * Получение количества строк в таблице
     *
     * @param filter
     * @return
     * @throws ServiceErrorException
     */
    public Integer getTableRowsCount(final TableFilter filter) throws ServiceErrorException {
        List rows = getData(filter, true);
        if (rows.size() == 1) {
            final Object o = rows.get(0);
            BigInteger count = (BigInteger) o;
            return count.intValue();
        }
        return 0;
    }

    /**
     * Непосредственно построение запроса и получение из БД
     *
     * @param filter
     * @param isPageCountCalc
     * @return
     * @throws ServiceErrorException
     */
    private List getData(TableFilter filter, final Boolean isPageCountCalc) throws ServiceErrorException {
        final String tableName = filter.getTableName();
        final List<TableColumn> tableMetadata = getTableMetadata(tableName);
        String sql;
        if (!isPageCountCalc) {
            List<String> fieldNames = new ArrayList<>();
            for (TableColumn column : tableMetadata) {
                fieldNames.add(column.getColumn());
            }
            sql = String.format("select %s from %s", Joiner.on(" , ").join(fieldNames), tableName);
        } else {
            sql = String.format("select count(*) from %s", tableName);
        }
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Map<TableColumn, FilterField> columnFilterFieldMap = new LinkedHashMap<>();
        if (filter.getFilters().size() > 0) {
            sqlBuilder.append(" where");
            Integer countFiedls = 0;
            for (FilterField fieldFilter : filter.getFilters()) {
                final TableColumn column = getColumn(tableMetadata, fieldFilter.getField());
                String field = column.getColumn();
                if (StringUtils.equalsIgnoreCase(column.getColumpType(), STRING_TYPE_NAME)) {
                    sqlBuilder.append(String.format(" %s like ?", field));
                } else {
                    sqlBuilder.append(String.format(" %s = ?", field));
                }
                if (countFiedls++ != filter.getFilters().size() - 1) {
                    sqlBuilder.append(" and");
                }
                columnFilterFieldMap.put(column, fieldFilter);
            }
        }
        if (!isPageCountCalc) {
            if (StringUtils.isNotEmpty(filter.getSort())) {
                final TableColumn column = getColumn(tableMetadata, filter.getSort());
                final String sort = column.getColumn();
                String order = filter.getOrder();
                if (StringUtils.isEmpty(order)) {
                    order = "desc";
                }
                sqlBuilder.append(String.format(" order by %s %s", sort, order));
            }
            Integer offset = (filter.getCurrentPage() - 1) * TableFilter.PAGE_SIZE;
            sqlBuilder.append(String.format(" limit %s offset %s", TableFilter.PAGE_SIZE, offset));
        }
        final Query nativeQuery = entityManager.createNativeQuery(sqlBuilder.toString());
        if (filter.getFilters().size() > 0) {
            Integer i = 1;
            for (Map.Entry<TableColumn, FilterField> fieldFilter : columnFilterFieldMap.entrySet()) {
                if (StringUtils.equalsIgnoreCase(fieldFilter.getKey().getColumpType(), STRING_TYPE_NAME)) {
                    nativeQuery.setParameter(i++, String.format("%%%s%%", fieldFilter.getValue().getValue()));
                } else if (StringUtils.equalsIgnoreCase(fieldFilter.getKey().getColumpType(), LONG_TYPE_NAME)) {
                    Long longValue;
                    try {
                        longValue = Long.valueOf(fieldFilter.getValue().getValue());
                    } catch (NumberFormatException e) {
                        throw new ServiceErrorException("Неверно задан параметр", e);
                    }
                    nativeQuery.setParameter(i++, longValue);
                } else {
                    nativeQuery.setParameter(i++, fieldFilter.getValue().getValue());
                }
            }
        }
        return nativeQuery.getResultList();
    }

    /**
     * Получение колонки
     *
     * @param tableMetadata
     * @param field
     * @return
     * @throws ServiceErrorException
     */
    private TableColumn getColumn(List<TableColumn> tableMetadata, String field) throws ServiceErrorException {
        for (TableColumn tableColumn : tableMetadata) {
            if (StringUtils.equalsIgnoreCase(tableColumn.getColumn(), field)) {
                return tableColumn;
            }
        }
        throw new ServiceErrorException(String.format("Поля %s не существует", field));
    }

    public List<String> getTableList() {
        final Query nativeQuery =
                entityManager.createNativeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CLASS='org.h2.table.RegularTable'");
        final List rows = nativeQuery.getResultList();
        List<String> result = new ArrayList<>();
        for (Object row : rows) {
            result.add((String)row);
        }
        return result;
    }
}
