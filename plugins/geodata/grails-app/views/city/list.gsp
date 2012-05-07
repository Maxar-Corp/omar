<%@ page import="geodata.City" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message( code: 'city.label', default: 'City' )}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <filterpane:includes/>
</head>

<body>

<a href="#list-city" class="skip" tabindex="-1">
  <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

<div class="nav" role="navigation">
  <ul>
    <li><a class="home" href="${createLink( uri: '/' )}"><g:message code="default.home.label"/></a></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="list-city" class="content scaffold-list" role="main">
  <h1><g:message code="default.list.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>

  <filterpane:currentCriteria domainBean="geodata.City"
                              removeImgDir="images" removeImgFile="bullet_delete.png"
                              fullAssociationPathFieldNames="no"/>

  <table>
    <thead>
    <tr>

      <g:sortableColumn property="name" title="${message( code: 'city.name.label', default: 'Name' )}"
                        params="${filterParams}"/>

      <g:sortableColumn property="country" title="${message( code: 'city.country.label', default: 'Country' )}"
                        params="${filterParams}"/>

      <g:sortableColumn property="population"
                        title="${message( code: 'city.population.label', default: 'Population' )}"
                        params="${filterParams}"/>

      <g:sortableColumn property="capital" title="${message( code: 'city.capital.label', default: 'Capital' )}"
                        params="${filterParams}"/>

      <g:sortableColumn property="longitude" title="${message( code: 'city.longitude.label', default: 'Longitude' )}"
                        params="${filterParams}"/>

      <g:sortableColumn property="latitude" title="${message( code: 'city.latitude.label', default: 'Latitude' )}"
                        params="${filterParams}"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${cityInstanceList}" status="i" var="cityInstance">
      <tr class="${( i % 2 ) == 0 ? 'even' : 'odd'}">

        <td><g:link action="show"
                    id="${cityInstance.id}">${fieldValue( bean: cityInstance, field: "name" )}</g:link></td>

        <td>${fieldValue( bean: cityInstance, field: "country" )}</td>

        <td>${fieldValue( bean: cityInstance, field: "population" )}</td>

        <td><g:formatBoolean boolean="${cityInstance.capital}"/></td>

        <td>${fieldValue( bean: cityInstance, field: "longitude" )}</td>

        <td>${fieldValue( bean: cityInstance, field: "latitude" )}</td>

      </tr>
    </g:each>
    </tbody>
  </table>

  <div class="pagination">
    <g:paginate total="${cityInstanceTotal == null ? City.count() : cityInstanceTotal}"
                params="${filterParams}"/>
    <filterpane:filterButton text="Add Filter"/>
  </div>
</div>
<filterpane:filterPane domain="geodata.City" dialog="true"/>
</body>
</html>
