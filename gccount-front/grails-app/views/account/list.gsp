
<%@ page import="eccount.Account" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'account.label', default: 'Account')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li class="active">
							<g:link class="list" action="list">
								<i class="icon-list icon-white"></i>
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
					<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<th class="header"><g:message code="account.customer.label" default="Customer" /></th>
						
							<th class="header"><g:message code="account.rfid.label" default="Rfid" /></th>
						
							<g:sortableColumn property="balance" title="${message(code: 'account.balance.label', default: 'Balance')}" />
						
							<g:sortableColumn property="created" title="${message(code: 'account.created.label', default: 'Created')}" />
						
							<g:sortableColumn property="active" title="${message(code: 'account.active.label', default: 'Active')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${accountInstanceList}" var="accountInstance">
						<tr>
						
							<td>${fieldValue(bean: accountInstance, field: "customer")}</td>
						
							<td>${fieldValue(bean: accountInstance, field: "rfid")}</td>
						
							<td>${fieldValue(bean: accountInstance, field: "balance")}</td>
						
							<td><g:formatDate date="${accountInstance.created}" /></td>
						
							<td><g:formatBoolean boolean="${accountInstance.active}" /></td>
						
							<td class="link">
								<g:link action="show" id="${accountInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${accountInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
