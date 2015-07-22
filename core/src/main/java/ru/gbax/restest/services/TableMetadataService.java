package ru.gbax.restest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gbax.restest.dao.TableMetadataDAO;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.entity.model.TableData;
import ru.gbax.restest.exceptions.ServiceErrorException;

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
    public List<String> getTableList() {
        return tableMetadataDAO.getTableList();
    }

    /**
     * Получение метаданных таблицы
     * @return
     */
    public List<TableColumn> getTableMetadata(final String tableName) throws ServiceErrorException {
        return tableMetadataDAO.getTableMetadata(tableName);
    }

    /**
     * Получение данных таблицы
     * @param filter фильтр для таблицы
     * @return данные таблицы
     * @throws ServiceErrorException
     */
    public TableData getTableData(final TableFilter filter) throws ServiceErrorException {
        List<TableRow> tableRows = tableMetadataDAO.getTableRows(filter);
        if (filter.isFilterChanged()) {
            Integer tableRowsCount = tableMetadataDAO.getTableRowsCount(filter);
            Integer pageCount = (tableRowsCount + TableFilter.PAGE_SIZE - 1) / TableFilter.PAGE_SIZE;
            return new TableData(tableRows, pageCount);
        } else {
            return new TableData(tableRows);
        }
    }
}
