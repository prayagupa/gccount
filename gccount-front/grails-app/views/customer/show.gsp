
<%@ page import="eccount.Customer" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
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
				
					<g:if test="${customerInstance?.firstName}">
						<dt><g:message code="customer.firstName.label" default="First Name" /></dt>
						
							<dd><g:fieldValue bean="${customerInstance}" field="firstName"/></dd>
						
					</g:if>
				
					<g:if test="${customerInstance?.middleName}">
						<dt><g:message code="customer.middleName.label" default="Middle Name" /></dt>
						
							<dd><g:fieldValue bean="${customerInstance}" field="middleName"/></dd>
						
					</g:if>
				
					<g:if test="${customerInstance?.lastName}">
						<dt><g:message code="customer.lastName.label" default="Last Name" /></dt>
						
							<dd><g:fieldValue bean="${customerInstance}" field="lastName"/></dd>
						
					</g:if>
				
					<g:if test="${customerInstance?.active}">
						<dt><g:message code="customer.active.label" default="Active" /></dt>
						
							<dd><g:formatBoolean boolean="${customerInstance?.active}" /></dd>
						
					</g:if>
				
					<g:if test="${customerInstance?.created}">
						<dt><g:message code="customer.created.label" default="Created" /></dt>
						
							<dd><g:formatDate date="${customerInstance?.created}" /></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${customerInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${customerInstance?.id}">
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
