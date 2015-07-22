package ru.gbax.restest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gbax.restest.entity.model.TableColumn;
import ru.gbax.restest.entity.model.TableFilter;
import ru.gbax.restest.entity.model.TableNameModel;
import ru.gbax.restest.entity.model.TableRow;
import ru.gbax.restest.services.TableMetadataService;
import ru.gbax.restest.utils.TableEnum;

import java.util.List;

@Controller
@Transactional
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private TableMetadataService tableMetadataService;

    @RequestMapping(value = "/defaultFilter", method = RequestMethod.GET)
    @ResponseBody
	public TableFilter getDefaultFilter() {
        return new TableFilter();
    }

    @RequestMapping(value = "/applyFilter", method = RequestMethod.POST)
	@ResponseBody
    public List<TableRow> applyFilter(@RequestBody TableFilter filter) {
        return tableMetadataService.getTableData(filter);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        final List<TableNameModel> tablesList = tableMetadataService.getTableList();
        model.addAttribute("tablesList", tablesList);
        return "index";
    }

    @RequestMapping(value = "/table/{tableName}", method = RequestMethod.GET)
    public String showTable(@PathVariable("tableName") String tableName, Model model) {
        TableEnum tableEnum = tableMetadataService.getTableName(tableName.toUpperCase());
        final List<TableColumn> tableMetadata = tableMetadataService.getTableMetadata(tableEnum);
        final List<TableNameModel> tableList = tableMetadataService.getTableList();
        model.addAttribute("tablesList", tableList);
        model.addAttribute("tableName", tableEnum.getName());
        model.addAttribute("tableTranslatedName", tableEnum.getTranslateName());
        model.addAttribute("structure", tableMetadata);
        return "index";
    }

}
