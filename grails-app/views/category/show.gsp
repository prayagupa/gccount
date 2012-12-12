
<%@ page import="eccount.Category" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'category.label', default: 'Category')}" />
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
				
					<g:if test="${categoryInstance?.created}">
						<dt><g:message code="category.created.label" default="Created" /></dt>
						
							<dd><g:formatDate date="${categoryInstance?.created}" /></dd>
						
					</g:if>
				
					<g:if test="${categoryInstance?.active}">
						<dt><g:message code="category.active.label" default="Active" /></dt>
						
							<dd><g:formatBoolean boolean="${categoryInstance?.active}" /></dd>
						
					</g:if>
				
					<g:if test="${categoryInstance?.items}">
						<dt><g:message code="category.items.label" default="Items" /></dt>
						
							<g:each in="${categoryInstance.items}" var="i">
							<dd><g:link controller="item" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${categoryInstance?.name}">
						<dt><g:message code="category.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${categoryInstance}" field="name"/></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${categoryInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${categoryInstance?.id}">
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
