<%@ page import="org.ossim.omar.core.WmsLayers" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'wmsLayers.label', default: 'WmsLayers')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">

  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">Home</g:link></li>
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

          <g:sortableColumn property="id" title="${message(code: 'wmsLayers.id.label', default: 'Id')}"/>

          <g:sortableColumn property="name" title="${message(code: 'wmsLayers.name.label', default: 'Name')}"/>

          <g:sortableColumn property="url" title="${message(code: 'wmsLayers.url.label', default: 'Url')}"/>

          <g:sortableColumn property="params" title="${message(code: 'wmsLayers.params.label', default: 'Params')}"/>

          <g:sortableColumn property="options" title="${message(code: 'wmsLayers.options.label', default: 'Options')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${wmsLayersInstanceList}" status="i" var="wmsLayersInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${wmsLayersInstance.id}">${fieldValue(bean: wmsLayersInstance, field: "id")}</g:link></td>

            <td>${fieldValue(bean: wmsLayersInstance, field: "name")}</td>

            <td>${fieldValue(bean: wmsLayersInstance, field: "url")}</td>

            <td>${fieldValue(bean: wmsLayersInstance, field: "params")}</td>

            <td>${fieldValue(bean: wmsLayersInstance, field: "options")}</td>

          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${wmsLayersInstanceTotal}"/>
    </div>
  </div>
</content>
</body>
</html>
