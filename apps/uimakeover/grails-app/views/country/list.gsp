<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main2"/>
  <g:set var="entityName" value="${message(code: 'country.label', default: 'Country')}"/>
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

        <g:sortableColumn property="id" title="${message(code: 'country.id.label', default: 'Id')}"/>

        <g:sortableColumn property="name" title="${message(code: 'country.name.label', default: 'Name')}"/>

      </tr>
      </thead>
      <tbody>
      <g:each in="${countryInstanceList}" status="i" var="countryInstance">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

          <td><g:link action="show" id="${countryInstance.id}">${fieldValue(bean: countryInstance, field: "id")}</g:link></td>

          <td>${fieldValue(bean: countryInstance, field: "name")}</td>

        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div class="paginateButtons">
    <g:paginate total="${countryInstanceTotal}"/>
  </div>
    --%>

  <gui:dataTable
          id="countryTable"
          draggableColumns="true"
          columnDefs="[
          [id:'Id', sortable:true, resizeable: true, formatter: '@formatIdLink'],
          [name:'Name', sortable:true, resizeable: true]
       ]"
          sortedBy='id'
          controller="country" action="dataAsJSON"
          paginate="true"
          rowExpansion="false"
          rowsPerPage="10"
          totalRecordsKey="countryInstanceTotal"/>

</div>
</body>
</html>
