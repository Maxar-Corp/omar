
<%@ page import="org.ossim.omar.ogc.WmsLog" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="generatedViews"/>
    <title>OMAR: WMS Log Listing</title>
</head>
    <body>
    <content tag="content">
    <a href="#list-wmsLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}">Home</a></li>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <li><g:link class="clear" action="clear">Clear Log</g:link></li>
                </sec:ifAllGranted>
            </ul>
		</div>
		<div id="list-wmsLog" class="content scaffold-list" role="main">
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
                        <g:sortableColumn property="id" title="${message(code: 'wmsLog.id.label', default: 'Id')}" />
                        <g:sortableColumn property="userName" title="${message(code: 'wmsLog.userName.label', default: 'user')}" />
                        <g:sortableColumn property="startDate" title="${message(code: 'wmsLog.startDate.label', default: 'Start Date')}" />
                        <g:sortableColumn property="internalTime" title="${message(code: 'wmsLog.internalTime.label', default: 'Internal Time (s)')}" />
                        <g:sortableColumn property="renderTime" title="${message(code: 'wmsLog.renderTime.label', default: 'Render Time (s)')}" />
                        <g:sortableColumn property="totalTime" title="${message(code: 'wmsLog.renderTime.label', default: 'Total Time (s)')}" />
                        <g:sortableColumn property="meanGsd" title="${message(code: 'wmsLog.meanGsd.label', default: 'Mean Gsd (m)')}" />
                        <g:sortableColumn property="width" title="${message(code: 'wmsLog.width.label', default: 'Width')}" />
						<g:sortableColumn property="height" title="${message(code: 'wmsLog.height.label', default: 'Height')}" />
						<g:sortableColumn property="layers" title="${message(code: 'wmsLog.layers.label', default: 'Layers')}" />
						<g:sortableColumn property="styles" title="${message(code: 'wmsLog.styles.label', default: 'Styles')}" />
						<g:sortableColumn property="format" title="${message(code: 'wmsLog.format.label', default: 'Format')}" />
                        <g:sortableColumn property="url" title="${message(code: 'wmsLog.url.label', default: 'Url')}" />

                    </tr>
				</thead>
				<tbody>
				<g:each in="${wmsLogInstanceList}" status="i" var="wmsLogInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link action="show" controller="WmsLog" id="${wmsLogInstance.id}">${fieldValue(bean: wmsLogInstance, field: "id")}</g:link></td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "userName")}</td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "startDate")}</td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "internalTime")}</td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "renderTime")}</td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "totalTime")}</td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "meanGsd")}</td>
                        <td>${fieldValue(bean: wmsLogInstance, field: "width")}</td>
						<td>${fieldValue(bean: wmsLogInstance, field: "height")}</td>
						<td>${fieldValue(bean: wmsLogInstance, field: "layers")}</td>
						<td>${fieldValue(bean: wmsLogInstance, field: "styles")}</td>
						<td>${fieldValue(bean: wmsLogInstance, field: "format")}</td>
                        <td><a href="${fieldValue(bean: wmsLogInstance, field: "url")}">url</a></td>
                    </tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${wmsLogInstanceTotal}" />
			</div>
		</div>
        </content>
	</body>

</html>
