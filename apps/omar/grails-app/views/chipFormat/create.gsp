<%@ page import="org.ossim.omar.ChipFormat" %>
<!doctype html>
<html>
	<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="generatedViews">
        <title>Create Chip Format</title>
    </head>

	<body>
    <content tag="content">
        <div class="nav">
			<ul>
                <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
                <li class="menuButton"><g:link class="list" action="list">Chip Format List</g:link></li>
            </ul>
		</div>

		<div class="body">
			<h1>Create Chip Format</h1>
			<g:if test="${flash.message}">
			<div class="message">${flash.message}</div>
			</g:if>

			<g:hasErrors bean="${chipFormat}">
                <div class="errors">
                    <g:renderErrors bean="${chipFormat}" as="list"/>
                </div>
            </g:hasErrors>

			<g:form action="save" >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>

    </content>
    </body>
</html>
