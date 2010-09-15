<%@ page import="org.ossim.omar.AuthUser" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>OMAR: User List</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">Create User</g:link></span>
  </div>
  <div class="body">
    <h1>User List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
      <table>
        <thead>
        <tr>
          <g:sortableColumn property="id" title="Id"/>
          <g:sortableColumn property="username" title="Login Name"/>
          <g:sortableColumn property="email" title="E-Mail"/>
          <g:sortableColumn property="userRealName" title="Full Name"/>
          <g:sortableColumn property="enabled" title="Enabled"/>
          <g:sortableColumn property="description" title="Description"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${personList}" status="i" var="person">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${person.id}">${fieldValue(bean: person, field: 'id')}</g:link></td>
            <td>${person.username?.encodeAsHTML()}</td>
            <td>${person.email?.encodeAsHTML()}</td>
            <td>${person.userRealName?.encodeAsHTML()}</td>
            <td>${person.enabled?.encodeAsHTML()}</td>
            <td>${person.description?.encodeAsHTML()}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${AuthUser.count()}"/>
    </div>
  </div>
</content>
</body>
</html>