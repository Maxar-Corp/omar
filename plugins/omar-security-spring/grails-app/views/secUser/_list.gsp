<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/9/11
  Time: 7:49 PM
  To change this template use File | Settings | File Templates.
--%>

<div id="userList">
  <div class="list">
    <table>
      <thead>
      <tr>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="id"
                               title="${message(code: 'secUser.id.label', default: 'Id')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="username"
                               title="${message(code: 'secUser.username.label', default: 'Username')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="userRealName"
                               title="${message(code: 'secUser.userRealName.label', default: 'Real Name')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="organization"
                               title="${message(code: 'secUser.organization.label', default: 'Organization')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="phoneNumber"
                               title="${message(code: 'secUser.phoneNumber.label', default: 'Phone Number')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="email"
                               title="${message(code: 'secUser.email.label', default: 'Email')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="enabled"
                               title="${message(code: 'secUser.enabled.label', default: 'Enabled')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="accountExpired"
                               title="${message(code: 'secUser.accountExpired.label', default: 'Account Expired')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="accountLocked"
                               title="${message(code: 'secUser.accountLocked.label', default: 'Account Locked')}"
                               params="${[plugin: 'omar-security-spring']}"/>

        <filter:sortableColumn bean="org.ossim.omar.security.SecUser" update="userList" property="passwordExpired"
                               title="${message(code: 'secUser.passwordExpired.label', default: 'Password Expired')}"
                               params="${[plugin: 'omar-security-spring']}"/>

      </tr>
      </thead>
      <tbody>
      <g:each in="${secUserInstanceList}" status="i" var="secUserInstance">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

          <td><g:link controller="secUser" action="show"
                      id="${secUserInstance.id}">${fieldValue(bean: secUserInstance, field: "id")}</g:link></td>

          <td>${fieldValue(bean: secUserInstance, field: "username")}</td>

          <td>${fieldValue(bean: secUserInstance, field: "userRealName")}</td>

          <td>${fieldValue(bean: secUserInstance, field: "organization")}</td>

          <td>${fieldValue(bean: secUserInstance, field: "phoneNumber")}</td>

          <td>${fieldValue(bean: secUserInstance, field: "email")}</td>

          <td><g:formatBoolean boolean="${secUserInstance.enabled}"/></td>

          <td><g:formatBoolean boolean="${secUserInstance.accountExpired}"/></td>

          <td><g:formatBoolean boolean="${secUserInstance.accountLocked}"/></td>

          <td><g:formatBoolean boolean="${secUserInstance.passwordExpired}"/></td>

        </tr>
      </g:each>
      </tbody>
    </table>
  </div>

  <div class="paginateButtons">
    <filter:paginate total="${secUserInstanceTotal}" bean="org.ossim.omar.security.SecUser" update="userList"
                     params="${[plugin: 'omar-security-spring']}"/>

  </div>

</div>