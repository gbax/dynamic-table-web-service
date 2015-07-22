package ru.gbax.restest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gbax.restest.dao.TableMetadataDAO;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableNameModel;
import ru.gbax.restest.entity.model.TableRow;
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

    public List<TableRow> getTableData(final TableFilter filter) {
        final TableEnum tableEnum = TableEnum.valueOf(filter.getTableName().toUpperCase());
        return tableMetadataDAO.getTableData(tableEnum, filter, true);
    }
}
