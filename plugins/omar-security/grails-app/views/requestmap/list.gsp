<%@ page import="org.ossim.omar.Requestmap" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Permission List</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
	<span class="menuButton"><g:link class="create" action="create">Create Permission</g:link></span>
  </div>
  <div class="body">
    <h1>OMAR: Permission List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
	</g:if>
    <div class="list">
      <table>
        <thead>
        <tr>
          <g:sortableColumn property="id" title="Id"/>
		  <g:sortableColumn property="url" title="URL"/>
		  <g:sortableColumn property="configAttribute" title="Roles"/>
		</tr>
        </thead>
        <tbody>
        <g:each in="${requestmapList}" status="i" var="requestmap">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${requestmap.id}">${requestmap.id}</g:link></td>
			<td>${requestmap.url?.encodeAsHTML()}</td>
<%
def names = []
org.springframework.util.StringUtils.commaDelimitedListToStringArray(requestmap.configAttribute).each { role ->
	if (role.startsWith('ROLE_')) {
		names << role.substring(5).toLowerCase().encodeAsHTML()
	}
	else {
		names << role.encodeAsHTML()
	}
}
%>
            <td>${names.join(',')}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${Requestmap.count()}"/>
    </div>
  </div>
</content>
</body>
</html>