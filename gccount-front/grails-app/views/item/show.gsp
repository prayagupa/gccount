
<%@ page import="eccount.Item" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'item.label', default: 'Item')}" />
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
				
					<g:if test="${itemInstance?.name}">
						<dt><g:message code="item.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${itemInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${itemInstance?.price}">
						<dt><g:message code="item.price.label" default="Price" /></dt>
						
							<dd><g:fieldValue bean="${itemInstance}" field="price"/></dd>
						
					</g:if>
				
					<g:if test="${itemInstance?.category}">
						<dt><g:message code="item.category.label" default="Category" /></dt>
						
							<dd><g:link controller="category" action="show" id="${itemInstance?.category?.id}">${itemInstance?.category?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${itemInstance?.created}">
						<dt><g:message code="item.created.label" default="Created" /></dt>
						
							<dd><g:formatDate date="${itemInstance?.created}" /></dd>
						
					</g:if>
				
					<g:if test="${itemInstance?.active}">
						<dt><g:message code="item.active.label" default="Active" /></dt>
						
							<dd><g:formatBoolean boolean="${itemInstance?.active}" /></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${itemInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${itemInstance?.id}">
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
