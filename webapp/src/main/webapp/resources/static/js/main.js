var Table = (function () {

    var filter;
    var table;
    var tableBody;
    var pageCount;
    var pagination;

    function load(tableName) {
        table = $('.table');
        tableBody = table.children('tbody');
        pagination = $('.pagination > ul');
        $.ajax({
            type: "GET",
            url: "/restest/defaultFilter",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (defaultFilter) {
                filter = defaultFilter;
                filter.tableName = tableName;
                refresh();
                drawPaginator();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Error: " + textStatus + " errorThrown: " + errorThrown);
            }
        });

    }

    function refresh() {
        $.ajax({
            type: "POST",
            url: "/restest/applyFilter",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify(filter),
            success: function (tableData) {
                var data = tableData.data;
                fillTable(data);
                pageCount = tableData.pageCount;
                if (filter.filterChanged) {
                    filter.currentPage = 1;
                    filter.filterChanged = false;
                }
                drawPaginator();
            },
            error: function (textStatus, errorThrown) {
                alert(textStatus)
            }
        });
    }

    function fillTable(data) {
        tableBody.empty();
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
        refresh();
    }

    function filterTable(column) {
        var value = $("#"+column+"_filter").val();
        var findedFilter = $.grep(filter.filters, function(n) {

            return n.field === column;
        });
        if (findedFilter.length == 1) {
            if (!value) {
                filter.filters.splice(filter.filters.indexOf(findedFilter[0]), 1);
            } else {
                findedFilter[0].value = value;
            }
            filter.filterChanged = true;
        } else {
            if (!!value) {
                filter.filters.push({field: column, value: value});
                filter.filterChanged = true;
            }
        }
        refresh();
    }

    function drawPaginator() {
        var COUNT_PAGES = 5;
        var COUNT_SIDE = 2;
        pagination.empty();
        var curP = filter.currentPage;
        if (pageCount > 1) {
            var downR = curP - COUNT_SIDE;
            var upR = curP + COUNT_SIDE;
            if (downR < 1) {
                downR = 1;
                if (pageCount < COUNT_PAGES) {
                    upR = pageCount;
                } else {
                    upR = COUNT_PAGES;
                }
            } else {
                if (upR > pageCount) {
                    upR = pageCount;
                    if (pageCount <= COUNT_PAGES) {
                        downR = 1;
                    } else {
                        downR = (upR - COUNT_PAGES) + 1;
                    }
                }
            }
            var pages;
            if (curP == 1) {
                pages = '<li class="disabled"><a href="#">Пред</a></li>';
            } else {
                pages = '<li><a href="#" onclick="Table.setPrevPage()">Пред</a></li>';
            }
            for (var i = downR; i <= upR; i++) {
                if (i == curP) {
                    pages += '<li class="active"><a href="#" onclick="Table.changePage('+i+')">'+i+'</a></li>';
                } else {
                    pages += '<li><a href="#" onclick="Table.changePage('+i+')">'+i+'</a></li>';
                }
            }
            if (curP == pageCount) {
                pages += '<li class="disabled"><a href="#">След</a></li>';
            } else {
                pages += '<li><a href="#" onclick="Table.setNextPage()">След</a></li>';
            }
        }
        pagination.append(pages);
    }

    function changePage(page) {
        filter.currentPage = page;
        refresh();
    }

    function setNextPage() {
        filter.currentPage++;
        refresh();
    }

    function setPrevPage() {
        filter.currentPage--;
        refresh();
    }

    function clearFilter() {
        filter.filters = [];
        filter.filterChanged = true;
        filter.sort = undefined;
        filter.order = undefined;
        $('.filter-input').val("");
        refresh();
    }

    return {
        load: load,
        sorting: sorting,
        filter: filterTable,
        changePage: changePage,
        setNextPage: setNextPage,
        setPrevPage: setPrevPage,
        clearFilter: clearFilter
    };
})();
