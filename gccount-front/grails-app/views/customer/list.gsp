
<%@ page import="eccount.Customer" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
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
						
							<g:sortableColumn property="firstName" title="${message(code: 'customer.firstName.label', default: 'First Name')}" />
						
							<g:sortableColumn property="middleName" title="${message(code: 'customer.middleName.label', default: 'Middle Name')}" />
						
							<g:sortableColumn property="lastName" title="${message(code: 'customer.lastName.label', default: 'Last Name')}" />
						
							<g:sortableColumn property="active" title="${message(code: 'customer.active.label', default: 'Active')}" />
						
							<g:sortableColumn property="created" title="${message(code: 'customer.created.label', default: 'Created')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${customerInstanceList}" var="customerInstance">
						<tr>
						
							<td>${fieldValue(bean: customerInstance, field: "firstName")}</td>
						
							<td>${fieldValue(bean: customerInstance, field: "middleName")}</td>
						
							<td>${fieldValue(bean: customerInstance, field: "lastName")}</td>
						
							<td><g:formatBoolean boolean="${customerInstance.active}" /></td>
						
							<td><g:formatDate date="${customerInstance.created}" /></td>
						
							<td class="link">
								<g:link action="show" id="${customerInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${customerInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
