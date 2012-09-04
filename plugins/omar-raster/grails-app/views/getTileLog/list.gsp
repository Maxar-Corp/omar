<%@ page import="org.ossim.omar.raster.GetTileLog" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="generatedViews"/>
    <title>OMAR: Get Tile Log Listing</title>
</head>

<%--
<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'getTileLog.label', default: 'GetTileLog')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>

    </head>
--%>
 <body>
 <content tag="content">
   <a href="#list-getTileLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav">
            <ul>
                <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <li class="menuButton"><g:link class="create" action="clear">Clear Log</g:link></li>
                </sec:ifAllGranted>
            </ul>
        </div>
		<div id="list-getTileLog" class="content scaffold-list" role="main">
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
                        <g:sortableColumn property="id" title="${message(code: 'getTileLog.id.label', default: 'Id')}" />
                        <g:sortableColumn property="userName" title="${message(code: 'getTileLog.userName.label', default: 'user')}" />
                        <g:sortableColumn property="startDate" title="${message(code: 'getTileLog.startDate.label', default: 'Start Date')}" />
                        <g:sortableColumn property="internalTime" title="${message(code: 'getTileLog.internalTime.label', default: 'Internal Time (s)')}" />
                        <g:sortableColumn property="renderTime" title="${message(code: 'getTileLog.renderTime.label', default: 'Render Time (s)')}" />
                        <g:sortableColumn property="totalTime" title="${message(code: 'getTileLog.renderTime.label', default: 'Total Time (s)')}" />
                        <g:sortableColumn property="x" title="${message(code: 'getTileLog.x.label', default: 'x')}" />
                        <g:sortableColumn property="y" title="${message(code: 'getTileLog.y.label', default: 'y')}" />
                        <g:sortableColumn property="width" title="${message(code: 'getTileLog.width.label', default: 'Width')}" />
                        <g:sortableColumn property="height" title="${message(code: 'getTileLog.height.label', default: 'Height')}" />
						<g:sortableColumn property="format" title="${message(code: 'getTileLog.format.label', default: 'Format')}" />
                        <g:sortableColumn property="layers" title="${message(code: 'getTileLog.layers.label', default: 'Layers')}" />
                        <g:sortableColumn property="scale" title="${message(code: 'getTileLog.scale.label', default: 'Scale')}" />
                        <g:sortableColumn property="url" title="${message(code: 'getTileLog.url.label', default: 'Url')}" />
                    </tr>
				</thead>
				<tbody>
				<g:each in="${getTileLogInstanceList}" status="i" var="getTileLogInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                        <td><g:link action="show" id="${getTileLogInstance.id}">${fieldValue(bean: getTileLogInstance, field: "id")}</g:link></td>
                        <%--<td>${fieldValue(bean: getTileLogInstance, field: "id")}</td>  --%>
                        <td>${fieldValue(bean: getTileLogInstance, field: "userName")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "startDate")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "internalTime")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "renderTime")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "totalTime")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "x")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "y")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "width")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "height")}</td>
						<td>${fieldValue(bean: getTileLogInstance, field: "format")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "layers")}</td>
                        <td>${fieldValue(bean: getTileLogInstance, field: "scale")}</td>
                        <td><a href="${fieldValue(bean: getTileLogInstance, field: "url")}">Chip Url</a></td>

                    </tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${getTileLogInstanceTotal}" />
			</div>
		</div>
        </content>
 <g:javascript>
     function init(){

     }
 </g:javascript>
 </body>
</html>
