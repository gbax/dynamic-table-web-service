package ru.gbax.restest.dao;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.gbax.restest.entity.model.FilterField;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.utils.TableEnum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.*;

/**
 * Created by GBAX on 21.07.2015.
 */
@Repository
public class TableMetadataDAO {

    final static String STRING_TYPE_NAME = "string";

    @PersistenceContext
    private EntityManager entityManager;


    public List<TableColumn> getTableMetadata(final Class tableClass) {
        EntityType<?> targetEntityType = null;
        Metamodel metamodel = entityManager.getMetamodel();
        for (final EntityType<?> next : metamodel.getEntities()) {
            if (next.getJavaType().equals(tableClass)) {
                targetEntityType = next;
            }
        }
        List<TableColumn> tableColumns = new ArrayList<>();
        if (targetEntityType != null) {
            for (Attribute<?, ?> attribute : targetEntityType.getAttributes()) {
                final String name = attribute.getName();
                final String type = attribute.getJavaType().getSimpleName();
                tableColumns.add(new TableColumn(name, type));
            }
        }
        return tableColumns;
    }

    public List<TableRow> getTableData(final TableEnum tableEnum, final TableFilter filter, final Boolean needPaging) {
        final List<TableColumn> tableMetadata = getTableMetadata(tableEnum.getTableClass());
        List<String> fieldNames = new ArrayList<>();
        for (TableColumn column : tableMetadata) {
            fieldNames.add(column.getColumn());
        }
        String sql = String.format("select %s from %s", Joiner.on(" , ").join(fieldNames), tableEnum.getName());
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Map<TableColumn, FilterField> columnFilterFieldMap = new LinkedHashMap<>();
        if (filter.getFilters().size() > 0) {
            sqlBuilder.append(" where");
            Integer countFiedls = 0;
            for (FilterField fieldFilter : filter.getFilters()) {
                final String field = fieldFilter.getField();
                final TableColumn column = getColumn(tableMetadata, field);
                if (column != null) {
                    if (StringUtils.equalsIgnoreCase(column.getColumpType(), STRING_TYPE_NAME)) {
                        sqlBuilder.append(String.format(" %s like ?", field));
                    } else {
                        sqlBuilder.append(String.format(" %s = ?", field));
                    }
                    if (countFiedls++ != filter.getFilters().size()-1) {
                        sqlBuilder.append(" and");
                    }
                    columnFilterFieldMap.put(column, fieldFilter);
                } else {
                    throw new IllegalArgumentException("Поле не существует");
                }
            }
        }
        final String sort = filter.getSort();
        if (StringUtils.isNotEmpty(sort) && getColumn(tableMetadata, sort) != null) {
            String order = filter.getOrder();
            if (StringUtils.isEmpty(order)) {
                order = "desc";
            }
            sqlBuilder.append(String.format(" order by %s %s", sort, order));
        }
        final Query nativeQuery = entityManager.createNativeQuery(sqlBuilder.toString());
        if (filter.getFilters().size() > 0) {
            Integer i = 1;
            for (Map.Entry<TableColumn, FilterField> fieldFilter : columnFilterFieldMap.entrySet()) {
                if (StringUtils.equalsIgnoreCase(fieldFilter.getKey().getColumpType(), STRING_TYPE_NAME)) {
                    nativeQuery.setParameter(i++, String.format("%%%s%%", fieldFilter.getValue().getValue()));
                } else {
                    nativeQuery.setParameter(i++, fieldFilter.getValue());
                }
            }
        }
        List rows = nativeQuery.getResultList();
        List<TableRow> tableRows = new ArrayList<>();
        for (Object row : rows) {
            Object[] cols = (Object[]) row;
            List<String> tableCols = new ArrayList<>();
            for (Integer i = 0; i < cols.length; i++) {
                 tableCols.add(cols[i].toString());
            }
            tableRows.add(new TableRow(tableCols));
        }
        return tableRows;
    }

    private TableColumn getColumn(List<TableColumn> tableMetadata, String field) {
        for (TableColumn tableColumn : tableMetadata){
            if (StringUtils.equalsIgnoreCase(tableColumn.getColumn(), field)) {
                return tableColumn;
            }
        }
        return null;
    }
}
