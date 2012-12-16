<%@ page import="eccount.RfidCard" %>



<div class="fieldcontain ${hasErrors(bean: rfidCardInstance, field: 'identifier', 'error')} required">
	<label for="identifier">
		<g:message code="rfidCard.identifier.label" default="Identifier" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="identifier" required="" value="${rfidCardInstance?.identifier}"/>
</div>

