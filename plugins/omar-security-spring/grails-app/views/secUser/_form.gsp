<%@ page import="org.ossim.omar.security.SecUser" %>



<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="secUser?.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${secUserInstance?.username}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'password', 'error')} required">
	<label for="password">
		<g:message code="secUser?.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
    <g:link controller="userPreferences" action="changePassword"
                id="${secUserInstance?.id}">Change Password</g:link>
    <%--
	<g:passwordField name="password" required="" value="${secUserInstance?.password}"/>
    --%>
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'userRealName', 'error')} ">
	<label for="userRealName">
		<g:message code="secUser?.userRealName.label" default="User Real Name" />
		
	</label>
	<g:textField name="userRealName" value="${secUserInstance?.userRealName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'email', 'error')} required">
	<label for="email">
		<g:message code="secUser?.email.label" default="Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="email" name="email" required="" value="${secUserInstance?.email}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'organization', 'error')} ">
	<label for="organization">
		<g:message code="secUser?.organization.label" default="Organization" />
		
	</label>
	<g:textField name="organization" value="${secUserInstance?.organization}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'phoneNumber', 'error')} ">
	<label for="phoneNumber">
		<g:message code="secUser?.phoneNumber?.label" default="Phone Number" />
		
	</label>
	<g:textField name="phoneNumber" value="${secUserInstance?.phoneNumber}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'accountExpired', 'error')} ">
	<label for="accountExpired">
		<g:message code="secUser?.accountExpired.label" default="Account Expired" />
		
	</label>
	<g:checkBox name="accountExpired" value="${secUserInstance?.accountExpired}" />
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'accountLocked', 'error')} ">
	<label for="accountLocked">
		<g:message code="secUser?.accountLocked.label" default="Account Locked" />
		
	</label>
	<g:checkBox name="accountLocked" value="${secUserInstance?.accountLocked}" />
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'enabled', 'error')} ">
	<label for="enabled">
		<g:message code="secUser?.enabled.label" default="Enabled" />
		
	</label>
	<g:checkBox name="enabled" value="${secUserInstance?.enabled}" />
</div>

<div class="fieldcontain ${hasErrors(bean: secUserInstance, field: 'passwordExpired', 'error')} ">
	<label for="passwordExpired">
		<g:message code="secUser?.passwordExpired.label" default="Password Expired" />
		
	</label>
	<g:checkBox name="passwordExpired" value="${secUserInstance?.passwordExpired}" />
</div>


<hr/>

<h1>Roles:</h1>

<g:set var="userRoles" value="${secUserInstance?.authorities}"/>
<g:set var="allRoles" value="${org.ossim.omar.security.SecRole.list().sort { it.authority } }"/>

<table>
  <tbody>
  <g:each var="role" in="${allRoles}">
    <tr class="prop">
      <td valign="top" class="name">
        <label for="${role?.authority}">${role?.authority}</label>
      </td>
      <td valign="top">
        <g:checkBox name="${role?.authority}" value="${role?.authority in userRoles?.authority}"/>
      </td>
    </tr>
  </g:each>
  </tbody>
</table>

