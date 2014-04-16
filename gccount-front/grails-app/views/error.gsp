<!DOCTYPE html>
<html>
	<head>
		<title>Grails Runtime Exception</title>
		<!-- 
			@author   : prayag upd
			@modified : 22 dec, 2012
		-->
		<meta name="layout" content="bootstrap">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
	</head>
	<body>
		<g:renderException exception="${exception}" />
	</body>
</html>
