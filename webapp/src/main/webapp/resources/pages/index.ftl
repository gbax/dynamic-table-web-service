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
        <table class="table">
            <thead>
                <#list structure as col>
                    <th>
                        ${col.column}
                        <a id="${col.column}_asc" onclick="Utils.sorting('${col.column}','asc')" href="#">asc</a>
                        <a id="${col.column}_desc" onclick="Utils.sorting('${col.column}','desc')" href="#">desc</a>
                        <input type="text" id="${col.column}_filter"/>
                        <input type="button" onclick="Utils.filter('${col.column}')" value="Применить">
                    </th>
                </#list>
            </thead>
            <tbody>

            </tbody>
        </table>
        <script type="text/javascript">
            $(document).ready(function () {
                Utils.load('${tableName}', ${pageCount});
            })
        </script>
    </#if>
</div>
</body>
</html>