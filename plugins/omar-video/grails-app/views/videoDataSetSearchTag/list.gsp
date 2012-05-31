<%@ page import="org.ossim.omar.video.VideoDataSetSearchTag" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews" />
  <g:set var="entityName" value="${message(code: 'videoDataSetSearchTag.label', default: 'VideoDataSetSearchTag')}"/>
  <title>OMAR: Video Search Tag List</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR™ Home</a></li>
          <li class="menuButton"><g:link class="create" action="create">Create Video Search Tag</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Video Search Tag List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
      <table>
        <thead>
        <tr>
          <g:sortableColumn property="id" title="${message(code: 'videoDataSetSearchTag.id.label', default: 'Id')}"/>
          <g:sortableColumn property="name" title="${message(code: 'videoDataSetSearchTag.name.label', default: 'Name')}"/>
          <g:sortableColumn property="description" title="${message(code: 'videoDataSetSearchTag.description.label', default: 'Description')}"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${videoDataSetSearchTagInstanceList}" status="i" var="videoDataSetSearchTagInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${videoDataSetSearchTagInstance.id}">${fieldValue(bean: videoDataSetSearchTagInstance, field: "id")}</g:link></td>
            <td>${fieldValue(bean: videoDataSetSearchTagInstance, field: "name")}</td>
            <td>${fieldValue(bean: videoDataSetSearchTagInstance, field: "description")}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${videoDataSetSearchTagInstanceTotal}"/>
    </div>
  </div>
</content>
</body>
</html>