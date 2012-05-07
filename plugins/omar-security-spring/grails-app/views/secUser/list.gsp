<%@ page import="org.ossim.omar.security.SecUser" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message( code: 'secUser.label', default: 'SecUser' )}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <g:javascript library="prototype"/>
</head>

<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink( uri: '/' )}">
      <g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="create" action="create">
      <g:message code="default.new.label" args="[entityName]"/></g:link>
    </span>
  </div>

  <div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <filter:dynamic bean="org.ossim.omar.security.SecUser" success="userList"
                    params="${[plugin: 'omar-security-spring']}"/>
    <g:render template="list"/>

    <g:form name="exportForm" onsubmit="updateExportFilter();">
      <g:hiddenField name="exportFilterBean" value="org.ossim.omar.security.SecUser"/>
      <g:hiddenField name="exportFilterField" value=""/>
      <g:hiddenField name="exportFilterCriteria" value=""/>
      <g:hiddenField name="exportFilterValue" value=""/>
      <div class="buttons">
        <span class="button">
          <g:actionSubmit class="save" action="export"
                          value="${message( code: 'default.button.export.label', default: 'Export' )}"/>
        </span>
      </div>
    </g:form>
    <g:javascript>
      function updateExportFilter()
      {
        $( 'exportFilterField' ).value = $( 'filterField' ).value;
        $( 'exportFilterCriteria' ).value = $( 'filterCriteria' ).value;
        $( 'exportFilterValue' ).value = $( 'filterValue' ).value;
        $( 'exportForm' ).submit();
      }
    </g:javascript>
  </div>
</content>
</body>
</html>
