<%@ page import="org.ossim.omar.core.Report" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">
    <omar:logout/>
  <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
          <li class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>

      </ul>
  </div>
  <div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
      <table>
        <thead>
        <tr>

          <g:sortableColumn property="id" title="${message(code: 'report.id.label', default: 'Id')}"/>

          <g:sortableColumn property="name" title="${message(code: 'report.name.label', default: 'Name')}"/>

          <g:sortableColumn property="dateCreated" title="${message(code: 'report.dateCreated.label', default: 'Date Created')}"/>

          <g:sortableColumn property="dateCreated" title="${message(code: 'report.lastUpdated.label', default: 'Last Updated')}"/>

          <g:sortableColumn property="status" title="${message(code: 'report.status.label', default: 'Status')}"/>

          <g:sortableColumn property="report" title="${message(code: 'report.report.label', default: 'Report')}"/>

          <g:sortableColumn property="comment" title="${message(code: 'report.comment.label', default: 'Comment')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${reportInstanceList}" status="i" var="reportInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${reportInstance.id}">${fieldValue(bean: reportInstance, field: "id")}</g:link></td>

            <td>${fieldValue(bean: reportInstance, field: "name")}</td>

            <td><g:formatDate date="${reportInstance.dateCreated}"/></td>

            <td><g:formatDate date="${reportInstance.lastUpdated}"/></td>

            <td>${fieldValue(bean: reportInstance, field: "status")}</td>

            <td>${fieldValue(bean: reportInstance, field: "report")}</td>

            <td>${fieldValue(bean: reportInstance, field: "comment")}</td>

          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${reportInstanceTotal}"/>
    </div>
  </div>
</content>
</body>
</html>
