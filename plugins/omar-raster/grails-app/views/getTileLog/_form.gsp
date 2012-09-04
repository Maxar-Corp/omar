<%@ page import="org.ossim.omar.raster.GetTileLog" %>



<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'x', 'error')} ">
	<label for="x">
		<g:message code="getTileLog.x.label" default="X" />
		
	</label>
	<g:field type="number" name="x" step="any" value="${getTileLogInstance.x}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'y', 'error')} ">
	<label for="y">
		<g:message code="getTileLog.y.label" default="Y" />
		
	</label>
	<g:field type="number" name="y" step="any" value="${getTileLogInstance.y}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'width', 'error')} ">
	<label for="width">
		<g:message code="getTileLog.width.label" default="Width" />
		
	</label>
	<g:field type="number" name="width" value="${getTileLogInstance.width}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'height', 'error')} ">
	<label for="height">
		<g:message code="getTileLog.height.label" default="Height" />
		
	</label>
	<g:field type="number" name="height" value="${getTileLogInstance.height}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'format', 'error')} ">
	<label for="format">
		<g:message code="getTileLog.format.label" default="Format" />
		
	</label>
	<g:textField name="format" value="${getTileLogInstance?.format}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'layers', 'error')} ">
	<label for="layers">
		<g:message code="getTileLog.layers.label" default="Layers" />
		
	</label>
	<g:textField name="layers" value="${getTileLogInstance?.layers}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'scale', 'error')} ">
	<label for="scale">
		<g:message code="getTileLog.scale.label" default="Scale" />
		
	</label>
	<g:field type="number" name="scale" step="any" value="${getTileLogInstance.scale}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'internalTime', 'error')} ">
	<label for="internalTime">
		<g:message code="getTileLog.internalTime.label" default="Internal Time" />
		
	</label>
	<g:field type="number" name="internalTime" step="any" value="${getTileLogInstance.internalTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'renderTime', 'error')} ">
	<label for="renderTime">
		<g:message code="getTileLog.renderTime.label" default="Render Time" />
		
	</label>
	<g:field type="number" name="renderTime" step="any" value="${getTileLogInstance.renderTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'totalTime', 'error')} ">
	<label for="totalTime">
		<g:message code="getTileLog.totalTime.label" default="Total Time" />
		
	</label>
	<g:field type="number" name="totalTime" step="any" value="${getTileLogInstance.totalTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="getTileLog.startDate.label" default="Start Date" />
		
	</label>
	<g:datePicker name="startDate" precision="day"  value="${getTileLogInstance?.startDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="getTileLog.endDate.label" default="End Date" />
		
	</label>
	<g:datePicker name="endDate" precision="day"  value="${getTileLogInstance?.endDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'userName', 'error')} ">
	<label for="userName">
		<g:message code="getTileLog.userName.label" default="User Name" />
		
	</label>
	<g:textField name="userName" value="${getTileLogInstance?.userName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'ip', 'error')} ">
	<label for="ip">
		<g:message code="getTileLog.ip.label" default="Ip" />
		
	</label>
	<g:textField name="ip" value="${getTileLogInstance?.ip}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: getTileLogInstance, field: 'url', 'error')} ">
	<label for="url">
		<g:message code="getTileLog.url.label" default="Url" />
		
	</label>
	<g:textField name="url" value="${getTileLogInstance?.url}"/>
</div>

