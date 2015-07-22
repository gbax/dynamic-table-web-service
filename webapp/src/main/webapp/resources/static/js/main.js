var Utils = (function () {

    var filter;
    var table;
    var tableBody;
    var pageCount;

    function load(tableName, tablePageCount) {
        pageCount = tablePageCount;
        table = $('.table');
        tableBody = table.children('tbody');
        $.ajax({
            type: "GET",
            url: "/restest/defaultFilter",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (defaultFilter) {
                filter = defaultFilter;
                filter.tableName = tableName;
                applyFilter();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Error: " + textStatus + " errorThrown: " + errorThrown);
            }
        });

    }

    function applyFilter() {
        $.ajax({
            type: "POST",
            url: "/restest/applyFilter",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify(filter),
            success: function (data) {
                tableBody.empty();
                fillTable(data);
            },
            error: function (textStatus, errorThrown) {
                alert(textStatus)
            }
        });
    }

    function fillTable(data) {
        data.forEach(function (row) {
            var tRow = '<tr>';
            row.values.forEach(function (col) {
                tRow += '<td>';
                tRow += col;
                tRow += '</td>'
            });
            tRow += '</tr>';
            tableBody.append(tRow);
        });
    }

    function sorting(column, order) {
        if (!!filter.sort && !!filter.order) {
            var prevSortBtn = $("#" + filter.sort + "_" + filter.order);
            prevSortBtn.css('color','#08c');
            prevSortBtn.css('text-decoration','none');
        }
        filter.sort = column;
        filter.order = order;
        var curSortBtn = $("#" + column + "_" + order);
        curSortBtn.css('color','#800035');
        curSortBtn.css('text-decoration','underline');
        applyFilter();
    }

    function filterTable(column) {
        var value = $("#"+column+"_filter").val();
        var foundedIndex;
        var findedFilter = $.grep(filter.filters, function(n, i) {
            foundedIndex = i;
            return n.field === column;
        });
        if (findedFilter.length == 1) {
            if (!value) {
                filter.filters.splice(foundedIndex, 1);
            } else {
                findedFilter[0].value = value;
            }
        } else {
            filter.filters.push({field: column, value: value});
        }
        applyFilter();
    }

    return {
        load: load,
        sorting: sorting,
        filter: filterTable
    };
})();
