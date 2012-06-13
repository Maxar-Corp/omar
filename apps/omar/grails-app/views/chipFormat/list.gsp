<%@ page import="org.ossim.omar.ChipFormat" %>
<html>
	<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="generatedViews">
		<g:set var="entityName" value="Chip Format" />
		<title>Chip Format List</title>
	</head>
	<body>
    <content tag="content">
        <omar:logout/>
		<div class="nav">
			<ul>
				<li class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li class="menuButton"><g:link class="create" action="create">Enter New Chip Format</g:link></li>
			</ul>
		</div>

		<div class="body">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
            <div class="list">
			<table>
				<thead>
					<tr>
                        <g:sortableColumn property="id" title="${message(code: 'chipFormat.id.label', default: 'Id')}"/>
                        <g:sortableColumn property="label" title="${message(code: 'chipFormat.label.label', default: 'Label')}" />
                        <g:sortableColumn property="width" title="${message(code: 'chipFormat.width.label', default: 'Width')}" />
                        <g:sortableColumn property="height" title="${message(code: 'chipFormat.height.label', default: 'Height')}" />
                        <g:sortableColumn property="comment" title="${message(code: 'chipFormat.comment.label', default: 'Comment')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${chipFormatInstanceList}" status="i" var="chipFormat">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link action="show" id="${chipFormat.id}">${fieldValue(bean: chipFormat, field: "id")}</g:link></td>
                        <td>${fieldValue(bean: chipFormat, field: "label")}</td>
                        <td>${fieldValue(bean: chipFormat, field: "width")}</td>
                        <td>${fieldValue(bean: chipFormat, field: "height")}</td>
                        <td>${fieldValue(bean: chipFormat, field: "comment")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
            </div>
            <div class="pagination">
				<g:paginate total="${chipFormatInstanceTotal}" />
			</div>
		</div>
    </content>
	</body>
</html>
