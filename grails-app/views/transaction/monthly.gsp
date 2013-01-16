
<%@ page import="eccount.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
		<g:set var="reportType" value="Monthly"/>
		<g:if test="${fromDate}">
			<g:set var="fromDate" value="${fromDate}"/>
		</g:if>
		<g:else>
			<g:set var="fromDate" value="${new Date()}"/>
		</g:else>
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
						<li>
							<g:link class="list" action="daily"><i class="icon-list icon-white"></i><g:message code="default.sales" args="["Daily"]"/></g:link>
						</li>
						 <li>
                                                        <g:link class="list" action="monthly"><i class="icon-list icon-white"></i><g:message code="default.sales" args="["monthly"]"/></g:link>
                                                </li>
						 <li>
                                                        <g:link class="list" action="anyRange"><i class="icon-list icon-white"></i><g:message code="default.sales" args="["Overall"]"/></g:link>
                                                </li>


					</ul>
				</div>
			</div>

			<div class="span9">
				
				<div class="page-header">
					<h1><g:message code="default.sales" args="[reportType]" /></h1>
				</div>
                                
				 <div>
                                   <g:form class="form-horizontal" action="monthly" method="post">
                                        <table class="dailyTrxnForm">
                                                <tr>
                                                        <td><label for="fromDate">Select Month</label></td>
                                                        <td>
                                                            <g:datePicker name="fromDate" 
                                                                          value="${fromDate}"
                                                                          precision="month"
                                                                          />
                                                        </td>
                                                </tr>
                                                <tr><td>&nbsp;</td><td><input type="submit" class="btn btn-primary" value="Query"/></td></tr>
                                        </table>
                                   </g:form>
				</div>
				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<th class="header"><g:message code="transaction.account.label" default="Account" /></th>
						
							<g:sortableColumn property="amount" title="${message(code: 'transaction.amount.label', default: 'Amount')}" />
						
							<g:sortableColumn property="created" title="${message(code: 'transaction.created.label', default: 'Created')}" />
						
							<th class="header"><g:message code="transaction.approvedBy.label" default="Approved By" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${transactionInstanceList}" var="transactionInstance">
						<tr>
						
							<td>${fieldValue(bean: transactionInstance, field: "account")}</td>
						
							<td>${fieldValue(bean: transactionInstance, field: "amount")}</td>
						
							<td><g:formatDate date="${transactionInstance.created}" /></td>
						
							<td>${fieldValue(bean: transactionInstance, field: "approvedBy")}</td>
						
							<td class="link">
								<g:link action="show" id="${transactionInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${transactionInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
