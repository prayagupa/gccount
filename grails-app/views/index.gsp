<!DOCTYPE html>
<html>
	<head>
		<!-- 
		-- @author   : Prayag Upd
		-- @modified : 10 Dec, 2012
		-- @desc     : add bootstrap to content
		-->
		<meta name="layout" content="bootstrap"/>
		<title>:: Prab Food Court ::</title>
		<style type="text/css" media="screen">
			#status {
				background-color: #333;
				border: .2em solid #fff;
				margin: 2em 2em 1em;
				padding: 1em;
				width: 12em;
				float: left;
				-moz-box-shadow: 0px 0px 1.25em #ccc;
				-webkit-box-shadow: 0px 0px 1.25em #ccc;
				box-shadow: 0px 0px 1.25em #ccc;
				-moz-border-radius: 0.6em;
				-webkit-border-radius: 0.6em;
				border-radius: 0.6em;
			}

			.ie6 #status {
				display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
			}

			#status ul {
				font-size: 0.9em;
				list-style-type: none;
				margin-bottom: 0.6em;
				padding: 0;
			}
            
			#status li {
				line-height: 1.3;
			}

			#status h1 {
				text-transform: uppercase;
				font-size: 1.1em;
				margin: 0 0 0.3em;
			}

			#page-body {
				margin: 2em 1em 1.25em 18em;
			}

			h2 {
				margin-top: 1em;
				margin-bottom: 0.3em;
				font-size: 1em;
			}

			p {
				line-height: 1.5;
				margin: 0.25em 0;
			}

			#controller-list ul {
				list-style-position: inside;
			}

			#controller-list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.25em 0;
			}

			@media screen and (max-width: 480px) {
				#status {
					display: none;
				}

				#page-body {
					margin: 0 1em 1em;
				}

				#page-body h1 {
					margin-top: 0;
				}
			}
		</style>
	</head>
	<body>
<div class="row-fluid">
			<aside id="application-status" class="span3">
				<div class="well sidebar-nav">
						<ul class="nav nav-list">
							<li><g:link controller="category">Category</g:link></li>
							<li><g:link controller="item">    Item</g:link></li>
							<li><g:link controller="stall">   Stall</g:link></li>
							<li><g:link controller="user">    Staff</g:link></li>
							<li><g:link controller="customer">    Customer</g:link></li>
							<li><g:link controller="transactions">    Transactions</g:link></li>
						</ul>
				</div>
			</aside>

			<section id="main" class="span9">

				<div class="hero-unit">
					<h1>Cashless Payment System</h1>

					<p>Prab Food Court</p>
				</p>
				</div>

				<div class="row-fluid">

					<div class="span4">
						<h2>Navigate</h2>
						<ul class="nav nav-list">
							<li><g:link controller="category">Category</g:link></li>
							<li><g:link controller="item">    Item</g:link></li>
							<li><g:link controller="stall">   Stall</g:link></li>
							<li><g:link controller="user">    Staff</g:link></li>
							<li><g:link controller="customer">    Customer</g:link></li>
							<li><g:link controller="transactions">    Transactions</g:link></li>
						</ul>
					</div>

					<div class="span4">
						<h2>About Us</h2>
						<p>A recently established food court located in one of the most popular shopping mall in 							   Kathmandu, Star Mall.</p>
						<p>The food court is expected to have at least 8 Food stalls with the maximum catering capacity of 							100 customers at once.</p>
						<p>Each Stall and the Cash Counter will have a RFID Card Reader, a dot-matrix Bill Printer and a POS application.</p>
					</div>

					<div class="span4">
						<h2>Location</h2>
						<p>Starr Mall, Kathmandu</p>
					</div>

				</div>

			</section>
		</div>
	</body>
</html>
