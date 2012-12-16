<%@ page import="eccount.Account" %>



<div class="fieldcontain ${hasErrors(bean: accountInstance, field: 'customer', 'error')} required">
	<label for="customer">
		<g:message code="account.customer.label" default="Customer" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="customer" name="customer.id" from="${eccount.Customer.list()}" optionKey="id" required="" value="${accountInstance?.customer?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: accountInstance, field: 'rfid', 'error')} required">
	<label for="rfid">
		<g:message code="account.rfid.label" default="Rfid" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="rfid" name="rfid.id" from="${eccount.RfidCard.list()}" optionKey="id" required="" value="${accountInstance?.rfid?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: accountInstance, field: 'balance', 'error')} required">
	<label for="balance">
		<g:message code="account.balance.label" default="Balance" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="balance" value="${fieldValue(bean: accountInstance, field: 'balance')}" required=""/>
</div>

