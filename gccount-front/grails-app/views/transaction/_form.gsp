<%@ page import="eccount.Transaction" %>



<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'account', 'error')} required">
	<label for="account">
		<g:message code="transaction.account.label" default="Account" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="account" name="account.id" from="${eccount.Account.list()}" optionKey="id" required="" value="${transactionInstance?.account?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'amount', 'error')} required">
	<label for="amount">
		<g:message code="transaction.amount.label" default="Amount" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="amount" value="${fieldValue(bean: transactionInstance, field: 'amount')}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'transactionDetails', 'error')} ">
	<label for="transactionDetails">
		<g:message code="transaction.transactionDetails.label" default="Transaction Details" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${transactionInstance?.transactionDetails?}" var="t">
    <li><g:link controller="transactionDetails" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="transactionDetails" action="create" params="['transaction.id': transactionInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'transactionDetails.label', default: 'TransactionDetails')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'created', 'error')} required">
	<label for="created">
		<g:message code="transaction.created.label" default="Created" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="created" precision="day"  value="${transactionInstance?.created}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'approvedBy', 'error')} required">
	<label for="approvedBy">
		<g:message code="transaction.approvedBy.label" default="Approved By" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="approvedBy" name="approvedBy.id" from="${eccount.User.list()}" optionKey="id" required="" value="${transactionInstance?.approvedBy?.id}" class="many-to-one"/>
</div>

