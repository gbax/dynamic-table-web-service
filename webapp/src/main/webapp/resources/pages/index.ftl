<#import "/spring.ftl" as spring />
<html>
<head>
    <title>Test REST-service with dynamic table meta</title>
    <link href="<@spring.url '/resources/static/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<@spring.url '/resources/static/css/bootstrap-responsive.min.css'/>" rel="stylesheet">
    <link href="<@spring.url '/resources/static/css/style.css'/>" rel="stylesheet">
    <script src="<@spring.url '/resources/static/js/jquery-2.0.2.min.js'/>"></script>
    <script src="<@spring.url '/resources/static/js/bootstrap.min.js'/>"></script>
    <script src="<@spring.url '/resources/static/js/main.js'/>"></script>
</head>
<body>
<div class="container">
    <h2>Test REST-service with dynamic table meta</h2>

    <div class="dropdown">
        <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="true">
            Выберите таблицу
            <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
        <#list tablesList as tabledata>
            <li><a href="<@spring.url "/table/${tabledata.name}"/>">${tabledata.translateName}</a></li>
        </#list>
        </ul>
    </div>

<#if tableName??>
    <h4>Таблица <b>${tableTranslatedName}</b></h4>
    <div class="row">
        <input class="btn pull-right" type="button" onclick="Table.clearFilter()" value="Сбросить фильтр">
    </div>
    <table class="table">
        <thead>
            <#list structure as col>
            <th>
                ${col.column}
                <a id="${col.column}_asc" onclick="Table.sorting('${col.column}','asc')" href="#" class="btn btn-mini">asc</a>
                <a id="${col.column}_desc" onclick="Table.sorting('${col.column}','desc')" href="#" class="btn btn-mini">desc</a>
                <div style="margin-top: 10px">
                    <input type="text" id="${col.column}_filter" class="input-small filter-input" style="height: 30px;margin-bottom: 0;"/>
                    <input type="button" onclick="Table.filter('${col.column}')" value="Применить" class="btn">
                </div>
            </th>
            </#list>
        </thead>
        <tbody></tbody>
    </table>
    <div class="row">
        <div class="span4"></div>
        <div class="span4">
        <div class="pagination">
            <ul>

            </ul>
        </div>
        <script type="text/javascript">
            $(document).ready(function () {
                Table.load('${tableName}');
            })
        </script>
        </div>
        <div class="span4"></div>
    </div>
</#if>
</div>
</body>
</html>