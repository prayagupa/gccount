<%@ page import="eccount.User" %>



<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'active', 'error')} ">
	<label for="active">
		<g:message code="user.active.label" default="Active" />
		
	</label>
	<g:checkBox name="active" value="${userInstance?.active}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} ">
	<label for="name">
		<g:message code="user.username.label" default="Username" />
		
	</label>
	<g:textField name="username" value="${userInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} ">
	<label for="password">
		<g:message code="user.password.label" default="Password" />
		
	</label>
	<g:textField name="password" value="${userInstance?.password}"/>
</div>

