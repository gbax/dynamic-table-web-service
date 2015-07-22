package ru.gbax.restest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gbax.restest.entity.model.*;
import ru.gbax.restest.exceptions.ServiceErrorException;
import ru.gbax.restest.services.TableMetadataService;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Главный контроллер
 */
@Controller
@Transactional
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private TableMetadataService tableMetadataService;

    /**
     * Обработка ошибки на сервере
     *
     * @param e      исключение ServiceErrorException
     * @param writer поток для вывода сообщения
     * @throws IOException
     */
    @ExceptionHandler(ServiceErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleException(final ServiceErrorException e, Writer writer) throws IOException {
        logger.error(e.getMessage(), e);
        writer.write(String.format("{\"error\":\"%s\"}", e.getMessage()));
    }

    /**
     * @return Возвращает пустой фильтр
     */
    @RequestMapping(value = "/defaultFilter", method = RequestMethod.GET)
    @ResponseBody
    public TableFilter getDefaultFilter() {
        return new TableFilter();
    }

    /**
     * Обработка фильтра и возврат результата
     *
     * @param filter фильтр
     * @return результат
     * @throws ServiceErrorException
     */
    @RequestMapping(value = "/applyFilter", method = RequestMethod.POST)
    @ResponseBody
    public TableData applyFilter(@RequestBody TableFilter filter) throws ServiceErrorException {
        return tableMetadataService.getTableData(filter);
    }

    /**
     * Отображение начальной страницы
     *
     * @param model модель для данных
     * @return начальная страница
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        final List<String> tablesList = tableMetadataService.getTableList();
        model.addAttribute("tablesList", tablesList);
        return "index";
    }

    /**
     * Отображение страницы с таблицей
     *
     * @param tableName название таблицы
     * @param model модель для данных
     * @return страница с таблицей
     * @throws ServiceErrorException
     */
    @RequestMapping(value = "/table/{tableName}", method = RequestMethod.GET)
    public String showTable(@PathVariable("tableName") String tableName, Model model) throws ServiceErrorException {
        final List<TableColumn> tableMetadata = tableMetadataService.getTableMetadata(tableName);
        final List<String> tableList = tableMetadataService.getTableList();
        model.addAttribute("tablesList", tableList);
        model.addAttribute("tableName", tableName);
        model.addAttribute("structure", tableMetadata);
        return "index";
    }

}
