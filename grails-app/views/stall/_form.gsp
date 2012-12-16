<%@ page import="eccount.Stall" %>



<div class="fieldcontain ${hasErrors(bean: stallInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="stall.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${stallInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: stallInstance, field: 'items', 'error')} ">
	<label for="items">
		<g:message code="stall.items.label" default="Items" />
		
	</label>
	<g:select name="items" from="${eccount.Item.list()}" multiple="multiple" optionKey="id" size="5" value="${stallInstance?.items*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: stallInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="stall.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${eccount.User.list()}" optionKey="id" required="" value="${stallInstance?.user?.id}" class="many-to-one"/>
</div>

