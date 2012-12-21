
<%@ page import="eccount.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
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
				
					<g:if test="${transactionInstance?.account}">
						<dt><g:message code="transaction.account.label" default="Account" /></dt>
						
							<dd><g:link controller="account" action="show" id="${transactionInstance?.account?.id}">${transactionInstance?.account?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${transactionInstance?.amount}">
						<dt><g:message code="transaction.amount.label" default="Amount" /></dt>
						
							<dd><g:fieldValue bean="${transactionInstance}" field="amount"/></dd>
						
					</g:if>
				
					<g:if test="${transactionInstance?.transactionDetails}">
						<dt><g:message code="transaction.transactionDetails.label" default="Transaction Details" /></dt>
						
							<g:each in="${transactionInstance.transactionDetails}" var="t">
							<dd><g:link controller="transactionDetails" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${transactionInstance?.created}">
						<dt><g:message code="transaction.created.label" default="Created" /></dt>
						
							<dd><g:formatDate date="${transactionInstance?.created}" /></dd>
						
					</g:if>
				
					<g:if test="${transactionInstance?.approvedBy}">
						<dt><g:message code="transaction.approvedBy.label" default="Approved By" /></dt>
						
							<dd><g:link controller="user" action="show" id="${transactionInstance?.approvedBy?.id}">${transactionInstance?.approvedBy?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${transactionInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${transactionInstance?.id}">
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
