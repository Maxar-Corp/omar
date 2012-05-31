<%@ page import="org.ossim.omar.security.SecUser" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="mainG2">
  <g:set var="entityName" value="${message( code: 'secUser.label', default: 'SecUser' )}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <filterpane:includes/>
</head>

<body>
<content tag="content">
  <a href="#list-secUser" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
  </a>

  <div class="nav" role="navigation">
    <ul>
      <li><a class="home" href="${createLink( uri: '/' )}"><g:message code="default.home.label"/></a></li>
      <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
  </div>


  <div id="list-secUser" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
    </g:if>

    <filterpane:currentCriteria domainBean="org.ossim.omar.security.SecUser"
                                removeImgDir="images" removeImgFile="bullet_delete.png"
                                fullAssociationPathFieldNames="no"/>

    <table>
      <thead>
      <tr>

          <g:sortableColumn property="id"
                            title="${message( code: 'secUser.id.label', default: 'Id' )}"
                            params="${filterParams}"/>

          <g:sortableColumn property="username"
                            title="${message( code: 'secUser.username.label', default: 'Username' )}"
                            params="${filterParams}"/>
          <!--
                 <g:sortableColumn property="password"
                                   title="${message( code: 'secUser.password.label', default: 'Password' )}"
                                   params="${filterParams}"/>
          -->
        <g:sortableColumn property="userRealName"
                          title="${message( code: 'secUser.userRealName.label', default: 'User Real Name' )}"
                          params="${filterParams}"/>

        <g:sortableColumn property="email" title="${message( code: 'secUser.email.label', default: 'Email' )}"
                          params="${filterParams}"/>

        <g:sortableColumn property="organization"
                          title="${message( code: 'secUser.organization.label', default: 'Organization' )}"
                          params="${filterParams}"/>

        <g:sortableColumn property="phoneNumber"
                          title="${message( code: 'secUser.phoneNumber.label', default: 'Phone Number' )}"
                          params="${filterParams}"/>

      </tr>
      </thead>
      <tbody>
      <g:each in="${secUserInstanceList}" status="i" var="secUserInstance">
        <tr class="${( i % 2 ) == 0 ? 'even' : 'odd'}">
            <td>${fieldValue( bean: secUserInstance, field: "id" )}</td>
          <td><g:link action="show"
                      id="${secUserInstance.id}">${fieldValue( bean: secUserInstance, field: "username" )}</g:link></td>

<!--          <td>${fieldValue( bean: secUserInstance, field: "password" )}</td>    -->

          <td>${fieldValue( bean: secUserInstance, field: "userRealName" )}</td>

          <td>${fieldValue( bean: secUserInstance, field: "email" )}</td>

          <td>${fieldValue( bean: secUserInstance, field: "organization" )}</td>

          <td>${fieldValue( bean: secUserInstance, field: "phoneNumber" )}</td>

        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="pagination">
      <g:paginate total="${secUserInstanceTotal == null ? SecUser.count() : secUserInstanceTotal}"
                  params="${filterParams}"/>
      <filterpane:filterButton text="Add Filter"/>
      <g:link action="export" params="${filterParams}">Export</g:link>
    </div>
  </div>
  <filterpane:filterPane domain="org.ossim.omar.security.SecUser" dialog="true"/>
</content>
</body>
</html>
