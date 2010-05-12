<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main2"/>
  <g:set var="entityName" value="${message(code: 'city.label', default: 'City')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <gui:resources components="['dataTable','datePicker']"/>

  <g:javascript>
    var formatIdLink = function (elCell, oRecord, oColumn, oData){
    var link = '${g.createLink(action: "show")}/' + oData;
    var anchor = "<a href='" + link + "'>" + oData + "</a>";

    YAHOO.widget.DataTable.formatLink(elCell, oRecord, oColumn, anchor);
  }
  </g:javascript>
</head>
<body>
<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
  <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span>
</div>
<div class="body">
  <h1><g:message code="default.list.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
<%--
<div class="list">
  <table>
    <thead>
    <tr>

      <g:sortableColumn property="id" title="${message(code: 'city.id.label', default: 'Id')}"/>

      <g:sortableColumn property="name" title="${message(code: 'city.name.label', default: 'Name')}"/>

      <g:sortableColumn property="population" title="${message(code: 'city.population.label', default: 'Population')}"/>

      <g:sortableColumn property="capital" title="${message(code: 'city.capital.label', default: 'Capital')}"/>

      <g:sortableColumn property="latitude" title="${message(code: 'city.latitude.label', default: 'Latitude')}"/>

      <g:sortableColumn property="longitude" title="${message(code: 'city.longitude.label', default: 'Longitude')}"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${cityInstanceList}" status="i" var="cityInstance">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

        <td><g:link action="show" id="${cityInstance.id}">${fieldValue(bean: cityInstance, field: "id")}</g:link></td>

        <td>${fieldValue(bean: cityInstance, field: "name")}</td>

        <td>${fieldValue(bean: cityInstance, field: "population")}</td>

        <td><g:formatBoolean boolean="${cityInstance.capital}"/></td>

        <td>${fieldValue(bean: cityInstance, field: "latitude")}</td>

        <td>${fieldValue(bean: cityInstance, field: "longitude")}</td>

      </tr>
    </g:each>
    </tbody>
  </table>
</div>
<div class="paginateButtons">
  <g:paginate total="${cityInstanceTotal}"/>
</div>
--%>
  <gui:dataTable
          id="cityTable"
          draggableColumns="true"
          columnDefs="[
          [id:'Id', sortable:true, resizeable: true, formatter: '@formatIdLink'],
          [name:'Name', sortable:true, resizeable: true],
          [population:'Population', sortable:true, resizeable: true],
          [capital:'Capital', sortable:true, resizeable: true],
          [latitude:'Latitude', sortable:true, resizeable: true],
          [longitude:'Longitude', sortable:true, resizeable: true]

       ]"
          sortedBy='id'
          controller="city" action="dataAsJSON"
          paginate="true"
          rowExpansion="false"
          rowsPerPage="10"
          totalRecordsKey="cityInstanceTotal"/>

</div>
</body>
</html>
