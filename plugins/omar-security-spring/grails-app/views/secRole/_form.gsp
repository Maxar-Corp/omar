<%@ page import="org.ossim.omar.security.SecRole" %>



<div class="fieldcontain ${hasErrors(bean: secRoleInstance, field: 'authority', 'error')} required">
	<label for="authority">
		<g:message code="secRole.authority.label" default="Authority" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="authority" required="" value="${secRoleInstance?.authority}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: secRoleInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="secRole.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${secRoleInstance?.description}"/>
</div>

