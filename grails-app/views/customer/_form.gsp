<%@ page import="eccount.Customer" %>



<div class="fieldcontain ${hasErrors(bean: customerInstance, field: 'firstName', 'error')} required">
	<label for="firstName">
		<g:message code="customer.firstName.label" default="First Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="firstName" required="" value="${customerInstance?.firstName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerInstance, field: 'middleName', 'error')} ">
	<label for="middleName">
		<g:message code="customer.middleName.label" default="Middle Name" />
		
	</label>
	<g:textField name="middleName" value="${customerInstance?.middleName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerInstance, field: 'lastName', 'error')} required">
	<label for="lastName">
		<g:message code="customer.lastName.label" default="Last Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="lastName" required="" value="${customerInstance?.lastName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerInstance, field: 'created', 'error')} required">
	<label for="created">
		<g:message code="customer.created.label" default="Created" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="created" precision="day"  value="${customerInstance?.created}"  />
</div>

