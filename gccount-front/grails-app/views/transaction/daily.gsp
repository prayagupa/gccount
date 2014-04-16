
<%@ page import="eccount.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
		<g:if test="${salesDate}">
			<g:set var="salesDate" value="${salesDate}"/>
		</g:if>
		<g:else>
			<g:set var="salesDate" value="${new Date()}"/>
		</g:else>
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
				 <h3>Daily Sales</h3>
		                 <g:if test="${transactionInstanceList}">
                                        <h3> : ${transactionInstanceList.count}</h3>
                		 </g:if>
				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				<div>
                                   <g:form class="form-horizontal" action="daily" method="post">
					<table class="dailyTrxnForm">
						<tr>
							<td><label for="fromDate">Sales Date</label></td>
							<td>
							    <g:datePicker name="fromDate" 
									  value="${salesDate}"
									  precision="day"
									  />
							</td>
						</tr>
						<tr><td>&nbsp;</td><td><input type="submit" class="btn btn-primary" value="Query"/></td></tr>
					</table>
 				   </g:form>
                                </div>
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
				
			</div>

		</div>
	</body>
</html>
