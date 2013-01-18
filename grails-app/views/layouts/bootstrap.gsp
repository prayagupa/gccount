<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<!doctype html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title><g:layoutTitle default="${meta(name: 'app.name')}"/></title>
		<meta name="description" content="">
		<meta name="author" content="">

		<meta name="viewport" content="initial-scale = 1.0">

		<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
		<!--[if lt IE 9]>
			<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

		<r:require modules="scaffolding"/>

		<!-- Le fav and touch icons -->
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="72x72" href="${resource(dir: 'images', file: 'apple-touch-icon-72x72.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-114x114.png')}">

		<g:layoutHead/>
		<r:layoutResources/>
	</head>

	<body>

		<nav class="navbar navbar-fixed-top">
			<div class="navbar-inner">
				<div class="container-fluid">
					
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</a>
				        <!--
                                           @author : Prayag
                                           @modified : 12-12-2012
                                        -->	
					<a class="brand" href="${createLink(uri: '/')}">Prab Food Court</a>

					<div class="nav-collapse">
						<ul class="nav">							
							<li<%= request.forwardURI == "${createLink(uri: '/')}" ? ' class="active"' : '' %>>
                                                           <a href="${createLink(uri: '/user')}">Home</a>
                                                         </li>
                                                        <li <%= "category" == controllerName ? ' class="active"' : '' %>>
								<g:link controller="category">Category</g:link>
							</li>
                                                        <li <%= "item" == controllerName ? ' class="active"' : '' %>>
								<g:link controller="item">Food Item</g:link>
							</li>
                                                        <li <%= "stall" == controllerName ? ' class="active"' :'' %>>
								<g:link controller="stall">Stall</g:link>
							</li>
                                                        <li <%= "user" == controllerName ? ' class="active"' :'' %>>
								<g:link controller="user">Staff</g:link>
							</li>
							<li <%= "rfidCard" == controllerName ? ' class="active"' :'' %>>
								<g:link controller="rfidCard">RFID Card</g:link>
							</li>
                                                        <li <%= "customer" == controllerName ? ' class="active"' :'' %>>
								<g:link controller="customer">Customer</g:link>
							</li>
							<li <%= "account" == controllerName ? ' class="active"' :'' %>>
								<g:link controller="account">Customer Account</g:link>
							</li>
                                                        <li <%= "transaction" == controllerName ? ' class="active"' :'' %>>
								<g:link controller="transaction">Transactions</g:link>
							</li>
<!--
							<g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
								<li<%= c.logicalPropertyName == controllerName ? ' class="active"' : '' %>>
                                                                     <g:link controller="${c.logicalPropertyName}">${c.logicalPropertyName.capitalize()}</g:link>
                                                                </li>
							</g:each>
-->
							<li>
							   <a href="${createLink(uri: '/')}">Logout</a>
							 </li>
						</ul>
					</div>
				</div>
			</div>
		</nav>

		<div class="container-fluid">
			<g:layoutBody/>

			<hr>

			<footer>
				<p>&copy; Company 2012</p>
			</footer>
		</div>

		<r:layoutResources/>

	</body>
</html>
