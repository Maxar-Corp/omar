<%@ page import="org.ossim.omar.raster.GetTileLog" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="generatedViews"/>
    <title>OMAR:Show Log</title>
</head>
 <body>
 <content tag="content">
    <a href="#show-getTileLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="list">List</g:link></li>
			</ul>
		</div>
		<div id="show-getTileLog" class="content scaffold-show" role="main">
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list getTileLog">
				<g:if test="${getTileLogInstance?.x}">
				<li class="fieldcontain">
					<span id="x-label" class="property-label"><g:message code="getTileLog.x.label" default="X" /></span>
					
						<span class="property-value" aria-labelledby="x-label"><g:fieldValue bean="${getTileLogInstance}" field="x"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.y}">
				<li class="fieldcontain">
					<span id="y-label" class="property-label"><g:message code="getTileLog.y.label" default="Y" /></span>
					
						<span class="property-value" aria-labelledby="y-label"><g:fieldValue bean="${getTileLogInstance}" field="y"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.width}">
				<li class="fieldcontain">
					<span id="width-label" class="property-label"><g:message code="getTileLog.width.label" default="Width" /></span>
					
						<span class="property-value" aria-labelledby="width-label"><g:fieldValue bean="${getTileLogInstance}" field="width"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.height}">
				<li class="fieldcontain">
					<span id="height-label" class="property-label"><g:message code="getTileLog.height.label" default="Height" /></span>
					
						<span class="property-value" aria-labelledby="height-label"><g:fieldValue bean="${getTileLogInstance}" field="height"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.format}">
				<li class="fieldcontain">
					<span id="format-label" class="property-label"><g:message code="getTileLog.format.label" default="Format" /></span>
					
						<span class="property-value" aria-labelledby="format-label"><g:fieldValue bean="${getTileLogInstance}" field="format"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.layers}">
				<li class="fieldcontain">
					<span id="layers-label" class="property-label"><g:message code="getTileLog.layers.label" default="Layers" /></span>
					
						<span class="property-value" aria-labelledby="layers-label"><g:fieldValue bean="${getTileLogInstance}" field="layers"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.scale}">
				<li class="fieldcontain">
					<span id="scale-label" class="property-label"><g:message code="getTileLog.scale.label" default="Scale" /></span>
					
						<span class="property-value" aria-labelledby="scale-label"><g:fieldValue bean="${getTileLogInstance}" field="scale"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.internalTime}">
				<li class="fieldcontain">
					<span id="internalTime-label" class="property-label"><g:message code="getTileLog.internalTime.label" default="Internal Time" /></span>
					
						<span class="property-value" aria-labelledby="internalTime-label"><g:fieldValue bean="${getTileLogInstance}" field="internalTime"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.renderTime}">
				<li class="fieldcontain">
					<span id="renderTime-label" class="property-label"><g:message code="getTileLog.renderTime.label" default="Render Time" /></span>
					
						<span class="property-value" aria-labelledby="renderTime-label"><g:fieldValue bean="${getTileLogInstance}" field="renderTime"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.totalTime}">
				<li class="fieldcontain">
					<span id="totalTime-label" class="property-label"><g:message code="getTileLog.totalTime.label" default="Total Time" /></span>
					
						<span class="property-value" aria-labelledby="totalTime-label"><g:fieldValue bean="${getTileLogInstance}" field="totalTime"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.startDate}">
				<li class="fieldcontain">
					<span id="startDate-label" class="property-label"><g:message code="getTileLog.startDate.label" default="Start Date" /></span>
					
						<span class="property-value" aria-labelledby="startDate-label"><g:formatDate date="${getTileLogInstance?.startDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.endDate}">
				<li class="fieldcontain">
					<span id="endDate-label" class="property-label"><g:message code="getTileLog.endDate.label" default="End Date" /></span>
					
						<span class="property-value" aria-labelledby="endDate-label"><g:formatDate date="${getTileLogInstance?.endDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.userName}">
				<li class="fieldcontain">
					<span id="userName-label" class="property-label"><g:message code="getTileLog.userName.label" default="User Name" /></span>
					
						<span class="property-value" aria-labelledby="userName-label"><g:fieldValue bean="${getTileLogInstance}" field="userName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.ip}">
				<li class="fieldcontain">
					<span id="ip-label" class="property-label"><g:message code="getTileLog.ip.label" default="Ip" /></span>
					
						<span class="property-value" aria-labelledby="ip-label"><g:fieldValue bean="${getTileLogInstance}" field="ip"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${getTileLogInstance?.url}">
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="getTileLog.url.label" default="Url" /></span>
					
						<span class="property-value" aria-labelledby="url-label">
                            <a href="${fieldValue(bean: getTileLogInstance, field: "url")}">${fieldValue(bean: getTileLogInstance, field: "url")}</a>
                        </span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${getTileLogInstance?.id}" />
                    <%--
                    <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    <g:link class="edit" action="edit" id="${getTileLogInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    --%>
				</fieldset>
			</g:form>
		</div>
 </content>
 </body>
</html>
