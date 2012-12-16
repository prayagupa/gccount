
<%@ page import="eccount.Stall" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'stall.label', default: 'Stall')}" />
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
				
					<g:if test="${stallInstance?.name}">
						<dt><g:message code="stall.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${stallInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${stallInstance?.items}">
						<dt><g:message code="stall.items.label" default="Items" /></dt>
						
							<g:each in="${stallInstance.items}" var="i">
							<dd><g:link controller="item" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${stallInstance?.user}">
						<dt><g:message code="stall.user.label" default="User" /></dt>
						
							<dd><g:link controller="user" action="show" id="${stallInstance?.user?.id}">${stallInstance?.user?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${stallInstance?.created}">
						<dt><g:message code="stall.created.label" default="Created" /></dt>
						
							<dd><g:formatDate date="${stallInstance?.created}" /></dd>
						
					</g:if>
				
					<g:if test="${stallInstance?.active}">
						<dt><g:message code="stall.active.label" default="Active" /></dt>
						
							<dd><g:formatBoolean boolean="${stallInstance?.active}" /></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${stallInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${stallInstance?.id}">
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
