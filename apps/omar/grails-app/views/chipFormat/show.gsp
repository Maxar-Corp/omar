
<%@ page import="org.ossim.omar.ChipFormat" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="generatedViews">
		<g:set var="entityName" value="Chip Format" />
		<title>Show Chip Format</title>
	</head>
	<body>
    <content tag="content">
        <omar:logout/>
        <div class="nav">
            <ul>
				<li class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div class="body">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list chipFormat">
			
				<g:if test="${chipFormat?.label}">
				<li class="fieldcontain">
					<span id="label-label" class="property-label"><g:message code="chipFormat.label.label" default="Label" /></span>
					
						<span class="property-value" aria-labelledby="label-label"><g:fieldValue bean="${chipFormat}" field="label"/></span>
					
				</li>
				</g:if>

                <g:if test="${chipFormat?.width}">
                    <li class="fieldcontain">
                        <span id="width-label" class="property-label"><g:message code="chipFormat.width.label" default="Width" /></span>

                        <span class="property-value" aria-labelledby="width-label"><g:fieldValue bean="${chipFormat}" field="width"/></span>

                    </li>
                </g:if>

                <g:if test="${chipFormat?.height}">
                    <li class="fieldcontain">
                        <span id="height-label" class="property-label"><g:message code="chipFormat.height.label" default="Height" /></span>

                        <span class="property-value" aria-labelledby="height-label"><g:fieldValue bean="${chipFormat}" field="height"/></span>

                    </li>
                </g:if>

                <g:if test="${chipFormat?.comment}">
				<li class="fieldcontain">
					<span id="comment-label" class="property-label"><g:message code="chipFormat.comment.label" default="Comment" /></span>
					
						<span class="property-value" aria-labelledby="comment-label"><g:fieldValue bean="${chipFormat}" field="comment"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${chipFormat?.id}" />
					<g:link class="edit" action="edit" id="${chipFormat?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
    </content>
	</body>
</html>
