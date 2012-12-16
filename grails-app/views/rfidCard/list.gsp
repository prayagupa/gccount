
<%@ page import="eccount.RfidCard" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'rfidCard.label', default: 'RfidCard')}" />
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
						
							<g:sortableColumn property="identifier" title="${message(code: 'rfidCard.identifier.label', default: 'Identifier')}" />
						
							<g:sortableColumn property="created" title="${message(code: 'rfidCard.created.label', default: 'Created')}" />
						
							<g:sortableColumn property="active" title="${message(code: 'rfidCard.active.label', default: 'Active')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${rfidCardInstanceList}" var="rfidCardInstance">
						<tr>
						
							<td>${fieldValue(bean: rfidCardInstance, field: "identifier")}</td>
						
							<td><g:formatDate date="${rfidCardInstance.created}" /></td>
						
							<td><g:formatBoolean boolean="${rfidCardInstance.active}" /></td>
						
							<td class="link">
								<g:link action="show" id="${rfidCardInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${rfidCardInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
