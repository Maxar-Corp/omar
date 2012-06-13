<%@ page import="org.ossim.omar.ChipFormat" %>

<div class="fieldcontain ${hasErrors(bean: chipFormat, field: 'label', 'error')} required">
	<label for="label">
		<g:message code="chipFormat.label.label" default="Label" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="label" required="" value="${chipFormat?.label}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: chipFormat, field: 'width', 'error')} required">
    <label for="width">
        <g:message code="chipFormat.width.label" default="Width" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="width" required="" value="${chipFormat?.width}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: chipFormat, field: 'height', 'error')} required">
    <label for="height">
        <g:message code="chipFormat.height.label" default="Height" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="height" required="" value="${chipFormat?.height}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: chipFormat, field: 'comment', 'error')} ">
	<label for="comment">
		<g:message code="chipFormat.comment.label" default="Comment" />
		
	</label>
	<g:textField name="comment" value="${chipFormat?.comment}"/>
</div>

