<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/9/11
  Time: 7:49 PM
  To change this template use File | Settings | File Templates.
--%>

<div id="requestmapList">
  <div class="list">
    <table>
      <thead>
      <tr>

        <filter:sortableColumn bean="org.ossim.omar.security.Requestmap" update="requestmapList" property="id"
                               title="${message(code: 'requestmap.id.label', default: 'Id')}"
                               params="${[plugin: 'omar-security-spring'] }"/>

        <filter:sortableColumn bean="org.ossim.omar.security.Requestmap" update="requestmapList" property="url"
                               title="${message(code: 'requestmap.url.label', default: 'URL')}"
                               params="${[plugin: 'omar-security-spring'] }"/>

        <filter:sortableColumn bean="org.ossim.omar.security.Requestmap" update="requestmapList" property="configAttribute"
                               title="${message(code: 'requestmap.configAttribute.label', default: 'Config Attribute')}"
                               params="${[plugin: 'omar-security-spring']}"/>

      </tr>
      </thead>
      <tbody>
      <g:each in="${requestmapInstanceList}" status="i" var="requestmapInstance">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

          <td><g:link controller="requestmap" action="show"
                      id="${requestmapInstance.id}">${fieldValue(bean: requestmapInstance, field: "id")}</g:link></td>

          <td>${fieldValue(bean: requestmapInstance, field: "url")}</td>

          <td>${fieldValue(bean: requestmapInstance, field: "configAttribute")}</td>

        </tr>
      </g:each>
      </tbody>
    </table>
  </div>

  <div class="paginateButtons">
    <filter:paginate total="${requestmapInstanceTotal}" bean="org.ossim.omar.security.Requestmap" update="requestmapList"
                     params="${[plugin: 'omar-security-spring']}"/>
  </div>

</div>