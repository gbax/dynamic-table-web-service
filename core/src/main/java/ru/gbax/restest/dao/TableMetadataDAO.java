package ru.gbax.restest.dao;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.gbax.restest.entity.model.FilterField;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.exceptions.ServiceErrorException;
import ru.gbax.restest.utils.TableEnum;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;

/**
 * Дао для работы с таблицами
 * Created by GBAX on 21.07.2015.
 */
@Repository
public class TableMetadataDAO {

    final static String STRING_TYPE_NAME = "string";
    final static String LONG_TYPE_NAME = "long";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Получение метаданных таблицы
     * @param tableClass класс энтити
     * @return метаданные таблицы
     */
    public List<TableColumn> getTableMetadata(final Class tableClass) {
        EntityType<?> targetEntityType = null;
        Metamodel metamodel = entityManager.getMetamodel();
        for (final EntityType<?> next : metamodel.getEntities()) {
            if (next.getJavaType().equals(tableClass)) {
                targetEntityType = next;
            }
        }
        final Field[] declaredFields = tableClass.getDeclaredFields();
        List<TableColumn> tableColumns = new ArrayList<>();
        if (targetEntityType != null) {
            for (Attribute<?, ?> attribute : targetEntityType.getAttributes()) {
                final String name = attribute.getName();
                final String type = attribute.getJavaType().getSimpleName();
                String dbName = getDbName(declaredFields, name);
                tableColumns.add(new TableColumn(name, type, dbName));
            }
        }
        return tableColumns;
    }

    /**
     * Получение название столбца в БД
     * @param declaredFields поля энтити
     * @param name название поля
     * @return
     */
    private String getDbName(Field[] declaredFields, String name) {
        for (Field field : declaredFields) {
            if (StringUtils.equalsIgnoreCase(field.getName(), name)) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && StringUtils.isNotEmpty(column.name())) {
                    return column.name();
                }
            }
        }
        return null;
    }

    /**
     * Получение данных таблицы
     * @param tableEnum
     * @param filter
     * @return
     * @throws ServiceErrorException
     */
    public List<TableRow> getTableRows(final TableEnum tableEnum, final TableFilter filter) throws ServiceErrorException {
        if (filter.isFilterChanged()) {
            filter.setCurrentPage(1);
        }
        List rows = getData(tableEnum, filter, false);
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
     * @param tableEnum
     * @param filter
     * @return
     * @throws ServiceErrorException
     */
    public Integer getTableRowsCount(final TableEnum tableEnum, final TableFilter filter) throws ServiceErrorException {
        List rows = getData(tableEnum, filter, true);
        if (rows.size() == 1) {
            final Object o = rows.get(0);
            BigInteger count = (BigInteger) o;
            return count.intValue();
        }
        return 0;
    }

    /**
     * Непосредственно построение таблицы и получение из БД
     * @param tableEnum
     * @param filter
     * @param isPageCountCalc
     * @return
     * @throws ServiceErrorException
     */
    private List getData(TableEnum tableEnum, TableFilter filter, final Boolean isPageCountCalc) throws ServiceErrorException {
        final List<TableColumn> tableMetadata = getTableMetadata(tableEnum.getTableClass());
        String sql;
        if (!isPageCountCalc) {
            List<String> fieldNames = new ArrayList<>();
            for (TableColumn column : tableMetadata) {
                fieldNames.add(StringUtils.isNotEmpty(column.getDbName()) ? column.getDbName() : column.getColumn());
            }
            sql = String.format("select %s from %s", Joiner.on(" , ").join(fieldNames), tableEnum.getName());
        } else {
            sql = String.format("select count(*) from %s", tableEnum.getName());
        }
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Map<TableColumn, FilterField> columnFilterFieldMap = new LinkedHashMap<>();
        if (filter.getFilters().size() > 0) {
            sqlBuilder.append(" where");
            Integer countFiedls = 0;
            for (FilterField fieldFilter : filter.getFilters()) {
                final TableColumn column = getColumn(tableMetadata, fieldFilter.getField());
                String field = StringUtils.isNotEmpty(column.getDbName()) ? column.getDbName() : column.getColumn();
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
                final String sort = StringUtils.isNotEmpty(column.getDbName()) ? column.getDbName() : column.getColumn();
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
}
