
<%@ page import="eccount.Item" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'item.label', default: 'Item')}" />
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
						
							<g:sortableColumn property="created" title="${message(code: 'item.created.label', default: 'Created')}" />
						
							<g:sortableColumn property="active" title="${message(code: 'item.active.label', default: 'Active')}" />
						
							<th class="header"><g:message code="item.category.label" default="Category" /></th>
						
							<g:sortableColumn property="name" title="${message(code: 'item.name.label', default: 'Name')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${itemInstanceList}" var="itemInstance">
						<tr>
						
							<td><g:formatDate date="${itemInstance.created}" /></td>
						
							<td><g:formatBoolean boolean="${itemInstance.active}" /></td>
						
							<td>${fieldValue(bean: itemInstance, field: "category")}</td>
						
							<td>${fieldValue(bean: itemInstance, field: "name")}</td>
						
							<td class="link">
								<g:link action="show" id="${itemInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${itemInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
