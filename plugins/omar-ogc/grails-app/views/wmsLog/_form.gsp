<%@ page import="org.ossim.omar.ogc.WmsLog" %>



<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'width', 'error')} ">
	<label for="width">
		<g:message code="wmsLog.width.label" default="Width" />
		
	</label>
	<g:field type="number" name="width" value="${wmsLogInstance.width}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'height', 'error')} ">
	<label for="height">
		<g:message code="wmsLog.height.label" default="Height" />
		
	</label>
	<g:field type="number" name="height" value="${wmsLogInstance.height}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'layers', 'error')} ">
	<label for="layers">
		<g:message code="wmsLog.layers.label" default="Layers" />
		
	</label>
	<g:textField name="layers" value="${wmsLogInstance?.layers}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'styles', 'error')} ">
	<label for="styles">
		<g:message code="wmsLog.styles.label" default="Styles" />
		
	</label>
	<g:textField name="styles" value="${wmsLogInstance?.styles}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'format', 'error')} ">
	<label for="format">
		<g:message code="wmsLog.format.label" default="Format" />
		
	</label>
	<g:textField name="format" value="${wmsLogInstance?.format}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'request', 'error')} ">
	<label for="request">
		<g:message code="wmsLog.request.label" default="Request" />
		
	</label>
	<g:textField name="request" value="${wmsLogInstance?.request}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'bbox', 'error')} ">
	<label for="bbox">
		<g:message code="wmsLog.bbox.label" default="Bbox" />
		
	</label>
	<g:textField name="bbox" value="${wmsLogInstance?.bbox}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'internalTime', 'error')} ">
	<label for="internalTime">
		<g:message code="wmsLog.internalTime.label" default="Internal Time" />
		
	</label>
	<g:field type="number" name="internalTime" step="any" value="${wmsLogInstance.internalTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'renderTime', 'error')} ">
	<label for="renderTime">
		<g:message code="wmsLog.renderTime.label" default="Render Time" />
		
	</label>
	<g:field type="number" name="renderTime" step="any" value="${wmsLogInstance.renderTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'totalTime', 'error')} ">
	<label for="totalTime">
		<g:message code="wmsLog.totalTime.label" default="Total Time" />
		
	</label>
	<g:field type="number" name="totalTime" step="any" value="${wmsLogInstance.totalTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="wmsLog.startDate.label" default="Start Date" />
		
	</label>
	<g:datePicker name="startDate" precision="day"  value="${wmsLogInstance?.startDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="wmsLog.endDate.label" default="End Date" />
		
	</label>
	<g:datePicker name="endDate" precision="day"  value="${wmsLogInstance?.endDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'userName', 'error')} ">
	<label for="userName">
		<g:message code="wmsLog.userName.label" default="User Name" />
		
	</label>
	<g:textField name="userName" value="${wmsLogInstance?.userName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'ip', 'error')} ">
	<label for="ip">
		<g:message code="wmsLog.ip.label" default="Ip" />
		
	</label>
	<g:textField name="ip" value="${wmsLogInstance?.ip}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'url', 'error')} ">
	<label for="url">
		<g:message code="wmsLog.url.label" default="Url" />
		
	</label>
	<g:textField name="url" value="${wmsLogInstance?.url}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'meanGsd', 'error')} ">
	<label for="meanGsd">
		<g:message code="wmsLog.meanGsd.label" default="Mean Gsd" />
		
	</label>
	<g:field type="number" name="meanGsd" step="any" value="${wmsLogInstance.meanGsd}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: wmsLogInstance, field: 'geometry', 'error')} ">
	<label for="geometry">
		<g:message code="wmsLog.geometry.label" default="Geometry" />
		
	</label>
	
</div>

