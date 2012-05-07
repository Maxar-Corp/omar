<%@ page import="org.ossim.omar.security.Requestmap" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="mainG2">
  <g:set var="entityName" value="${message( code: 'requestmap.label', default: 'Requestmap' )}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <filterpane:includes/>
</head>

<body>

<content tag="content">
  <a href="#list-requestmap" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
  </a>

  <div class="nav" role="navigation">
    <ul>
      <li><a class="home" href="${createLink( uri: '/' )}"><g:message code="default.home.label"/></a></li>
      <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
  </div>

  <div id="list-requestmap" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
    </g:if>

    <filterpane:currentCriteria domainBean="org.ossim.omar.security.Requestmap"
                                removeImgDir="images" removeImgFile="bullet_delete.png"
                                fullAssociationPathFieldNames="no"/>

    <table>
      <thead>
      <tr>

        <g:sortableColumn property="url" title="${message( code: 'requestmap.url.label', default: 'Url' )}"
                          params="${filterParams}"/>

        <g:sortableColumn property="configAttribute"
                          title="${message( code: 'requestmap.configAttribute.label', default: 'Config Attribute' )}"
                          params="${filterParams}"/>

      </tr>
      </thead>
      <tbody>
      <g:each in="${requestmapInstanceList}" status="i" var="requestmapInstance">
        <tr class="${( i % 2 ) == 0 ? 'even' : 'odd'}">

          <td><g:link action="show"
                      id="${requestmapInstance.id}">${fieldValue( bean: requestmapInstance, field: "url" )}</g:link></td>

          <td>${fieldValue( bean: requestmapInstance, field: "configAttribute" )}</td>

        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="pagination">
      <g:paginate total="${requestmapInstanceTotal == null ? City.count() : requestmapInstanceTotal}"
                  params="${filterParams}"/>
      <filterpane:filterButton text="Add Filter"/>
    </div>
  </div>
  <filterpane:filterPane domain="org.ossim.omar.security.Requestmap" dialog="true"/>
</content>
</body>
</html>
