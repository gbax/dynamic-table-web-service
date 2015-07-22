package ru.gbax.restest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gbax.restest.dao.TableMetadataDAO;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableNameModel;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.entity.model.TableData;
import ru.gbax.restest.exceptions.ServiceErrorException;
import ru.gbax.restest.utils.TableEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для вывода данных таблиц
 * Created by GBAX on 21.07.2015.
 */
@Service
public class TableMetadataService {

    @Autowired
    private TableMetadataDAO tableMetadataDAO;

    /**
     * Получение списка таблиц
     * @return
     */
    public List<TableNameModel> getTableList() {
        List<TableNameModel> tableNameModels = new ArrayList<>();
        for (TableEnum tableEnum : TableEnum.values()) {
            tableNameModels.add(new TableNameModel(tableEnum.getName(), tableEnum.getTranslateName()));
        }
        return tableNameModels;
    }

    /**
     * Получение метаданных таблицы
     * @param tableEnum
     * @return
     */
    public List<TableColumn> getTableMetadata(final TableEnum tableEnum) {
        return tableMetadataDAO.getTableMetadata(tableEnum.getTableClass());
    }

    /**
     * Получение вспомогательные данных
     * @param tableName
     * @return
     * @throws ServiceErrorException
     */
    public TableEnum getTableName(final String tableName) throws ServiceErrorException {
        final TableEnum tableEnum = TableEnum.getByName(tableName);
        if (tableEnum == null) {
            throw new ServiceErrorException("Неверно указано название таблицы");
        } else {
            return tableEnum;
        }

    }

    /**
     * Получение данных таблицы
     * @param filter фильтр для таблицы
     * @return данные таблицы
     * @throws ServiceErrorException
     */
    public TableData getTableData(final TableFilter filter) throws ServiceErrorException {
        TableEnum tableEnum = getTableName(filter.getTableName().toUpperCase());
        List<TableRow> tableRows = tableMetadataDAO.getTableRows(tableEnum, filter);
        if (filter.isFilterChanged()) {
            Integer tableRowsCount = tableMetadataDAO.getTableRowsCount(tableEnum, filter);
            Integer pageCount = (tableRowsCount + TableFilter.PAGE_SIZE - 1) / TableFilter.PAGE_SIZE;
            return new TableData(tableRows, pageCount);
        } else {
            return new TableData(tableRows);
        }
    }
}
