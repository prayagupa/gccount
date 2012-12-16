
<%@ page import="eccount.Account" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'account.label', default: 'Account')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li>
							<g:link class="list" action="list">
								<i class="icon-list"></i>
								<g:message code="default.list.label" args="[entityName]" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>
			
			<div class="span9">

				<div class="page-header">
					<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>

				<dl>
				
					<g:if test="${accountInstance?.customer}">
						<dt><g:message code="account.customer.label" default="Customer" /></dt>
						
							<dd><g:link controller="customer" action="show" id="${accountInstance?.customer?.id}">${accountInstance?.customer?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${accountInstance?.rfid}">
						<dt><g:message code="account.rfid.label" default="Rfid" /></dt>
						
							<dd><g:link controller="rfidCard" action="show" id="${accountInstance?.rfid?.id}">${accountInstance?.rfid?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${accountInstance?.balance}">
						<dt><g:message code="account.balance.label" default="Balance" /></dt>
						
							<dd><g:fieldValue bean="${accountInstance}" field="balance"/></dd>
						
					</g:if>
				
					<g:if test="${accountInstance?.created}">
						<dt><g:message code="account.created.label" default="Created" /></dt>
						
							<dd><g:formatDate date="${accountInstance?.created}" /></dd>
						
					</g:if>
				
					<g:if test="${accountInstance?.active}">
						<dt><g:message code="account.active.label" default="Active" /></dt>
						
							<dd><g:formatBoolean boolean="${accountInstance?.active}" /></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${accountInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${accountInstance?.id}">
							<i class="icon-pencil"></i>
							<g:message code="default.button.edit.label" default="Edit" />
						</g:link>
						<button class="btn btn-danger" type="submit" name="_action_delete">
							<i class="icon-trash icon-white"></i>
							<g:message code="default.button.delete.label" default="Delete" />
						</button>
					</div>
				</g:form>

			</div>

		</div>
	</body>
</html>
