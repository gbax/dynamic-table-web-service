package ru.gbax.restest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gbax.restest.dao.TableMetadataDAO;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableNameModel;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.entity.model.TableData;
import ru.gbax.restest.utils.TableEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GBAX on 21.07.2015.
 */
@Service
public class TableMetadataService {

    @Autowired
    private TableMetadataDAO tableMetadataDAO;

    public List<TableNameModel> getTableList() {
        List<TableNameModel> tableNameModels = new ArrayList<>();
        for (TableEnum tableEnum : TableEnum.values()) {
            tableNameModels.add(new TableNameModel(tableEnum.getName(), tableEnum.getTranslateName()));
        }
        return tableNameModels;
    }

    public List<TableColumn> getTableMetadata(final TableEnum tableEnum) {
        return tableMetadataDAO.getTableMetadata(tableEnum.getTableClass());
    }

    public TableEnum getTableName(final String tableName) {
        return TableEnum.valueOf(tableName);
    }

    public TableData getTableData(final TableFilter filter) {
        TableEnum tableEnum = TableEnum.valueOf(filter.getTableName().toUpperCase());
        List<TableRow> tableRows = tableMetadataDAO.getTableRows(tableEnum, filter);
        Integer tableRowsCount = tableMetadataDAO.getTableRowsCount(tableEnum, filter);
        Integer pageCount = (tableRowsCount + TableFilter.PAGE_SIZE - 1) / TableFilter.PAGE_SIZE;
        return new TableData(tableRows, pageCount);
    }
}
