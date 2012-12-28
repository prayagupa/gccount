<%@ page import="eccount.User" %>
<!doctype html>
<html>
	<head>
	
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span9">
				<g:form action="doLogin" method="post">
					<div class="dialog">
						<p>Enter your login details below:</p>
						<table class="userForm">
							<tr class='prop'>
								<td valign='top' style='text-align:left;' width='20%'>
								<label for='username'>Username:</label>
								</td>
								<td valign='top' style='text-align:left;' width='80%'>
								<input id="username" type='text' name='username' value='${user?.username}' />
								</td>
							</tr>
							<tr class='prop'>
								<td valign='top' style='text-align:left;' width='20%'>
								<label for='password'>Password:</label>
								</td>
								<td valign='top' style='text-align:left;' width='80%'>
								<input id="password" type='password' name='password'
								value='${user?.password}' />
								</td>
							</tr>
						</table>
					</div>
					<div class="buttons">
						<span class="formButton">
							<input type="submit" value="Login"></input>
						</span>
					</div>
					</g:form>
			</div>

		</div>
	</body>
</html>
