
<%@ page import="org.ossim.omar.ogc.WmsLog" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="generatedViews"/>
    <title>OMAR: WMS Log Show Entry</title>
</head>
<body>
<content tag="content">
<a href="#show-wmsLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="list">List</g:link></li>
			</ul>
		</div>
		<div id="show-wmsLog" class="content scaffold-show" role="main">
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list wmsLog">
			
				<g:if test="${wmsLogInstance?.width}">
				<li class="fieldcontain">
					<span id="width-label" class="property-label"><g:message code="wmsLog.width.label" default="Width" /></span>
					
						<span class="property-value" aria-labelledby="width-label"><g:fieldValue bean="${wmsLogInstance}" field="width"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.height}">
				<li class="fieldcontain">
					<span id="height-label" class="property-label"><g:message code="wmsLog.height.label" default="Height" /></span>
					
						<span class="property-value" aria-labelledby="height-label"><g:fieldValue bean="${wmsLogInstance}" field="height"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.layers}">
				<li class="fieldcontain">
					<span id="layers-label" class="property-label"><g:message code="wmsLog.layers.label" default="Layers" /></span>
					
						<span class="property-value" aria-labelledby="layers-label"><g:fieldValue bean="${wmsLogInstance}" field="layers"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.styles}">
				<li class="fieldcontain">
					<span id="styles-label" class="property-label"><g:message code="wmsLog.styles.label" default="Styles" /></span>
					
						<span class="property-value" aria-labelledby="styles-label"><g:fieldValue bean="${wmsLogInstance}" field="styles"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.format}">
				<li class="fieldcontain">
					<span id="format-label" class="property-label"><g:message code="wmsLog.format.label" default="Format" /></span>
					
						<span class="property-value" aria-labelledby="format-label"><g:fieldValue bean="${wmsLogInstance}" field="format"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.request}">
				<li class="fieldcontain">
					<span id="request-label" class="property-label"><g:message code="wmsLog.request.label" default="Request" /></span>
					
						<span class="property-value" aria-labelledby="request-label"><g:fieldValue bean="${wmsLogInstance}" field="request"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.bbox}">
				<li class="fieldcontain">
					<span id="bbox-label" class="property-label"><g:message code="wmsLog.bbox.label" default="Bbox" /></span>
					
						<span class="property-value" aria-labelledby="bbox-label"><g:fieldValue bean="${wmsLogInstance}" field="bbox"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.internalTime}">
				<li class="fieldcontain">
					<span id="internalTime-label" class="property-label"><g:message code="wmsLog.internalTime.label" default="Internal Time" /></span>
					
						<span class="property-value" aria-labelledby="internalTime-label"><g:fieldValue bean="${wmsLogInstance}" field="internalTime"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.renderTime}">
				<li class="fieldcontain">
					<span id="renderTime-label" class="property-label"><g:message code="wmsLog.renderTime.label" default="Render Time" /></span>
					
						<span class="property-value" aria-labelledby="renderTime-label"><g:fieldValue bean="${wmsLogInstance}" field="renderTime"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.totalTime}">
				<li class="fieldcontain">
					<span id="totalTime-label" class="property-label"><g:message code="wmsLog.totalTime.label" default="Total Time" /></span>
					
						<span class="property-value" aria-labelledby="totalTime-label"><g:fieldValue bean="${wmsLogInstance}" field="totalTime"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.startDate}">
				<li class="fieldcontain">
					<span id="startDate-label" class="property-label"><g:message code="wmsLog.startDate.label" default="Start Date" /></span>
					
						<span class="property-value" aria-labelledby="startDate-label"><g:formatDate date="${wmsLogInstance?.startDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.endDate}">
				<li class="fieldcontain">
					<span id="endDate-label" class="property-label"><g:message code="wmsLog.endDate.label" default="End Date" /></span>
					
						<span class="property-value" aria-labelledby="endDate-label"><g:formatDate date="${wmsLogInstance?.endDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.userName}">
				<li class="fieldcontain">
					<span id="userName-label" class="property-label"><g:message code="wmsLog.userName.label" default="User Name" /></span>
					
						<span class="property-value" aria-labelledby="userName-label"><g:fieldValue bean="${wmsLogInstance}" field="userName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.ip}">
				<li class="fieldcontain">
					<span id="ip-label" class="property-label"><g:message code="wmsLog.ip.label" default="Ip" /></span>
					
						<span class="property-value" aria-labelledby="ip-label"><g:fieldValue bean="${wmsLogInstance}" field="ip"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.url}">
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="wmsLog.url.label" default="Url" /></span>

                    <a href="${fieldValue(bean: wmsLogInstance, field: "url")}">${fieldValue(bean: wmsLogInstance, field: "url")}</a>

				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.meanGsd}">
				<li class="fieldcontain">
					<span id="meanGsd-label" class="property-label"><g:message code="wmsLog.meanGsd.label" default="Mean Gsd" /></span>
					
						<span class="property-value" aria-labelledby="meanGsd-label"><g:fieldValue bean="${wmsLogInstance}" field="meanGsd"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${wmsLogInstance?.geometry}">
				<li class="fieldcontain">
					<span id="geometry-label" class="property-label"><g:message code="wmsLog.geometry.label" default="Geometry" /></span>
					
						<span class="property-value" aria-labelledby="geometry-label"><g:fieldValue bean="${wmsLogInstance}" field="geometry"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${wmsLogInstance?.id}" />
                    <sec:ifAllGranted roles="ROLE_ADMIN">
					    <g:link class="edit" action="edit" id="${wmsLogInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					    <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				    </sec:ifAllGranted>
                </fieldset>
			</g:form>
		</div>
    </content>
	</body>
</html>
