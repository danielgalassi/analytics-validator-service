<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Analytics Validator Service - ERROR</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/error.css" type="text/css" />
</head>
<body>

	<div class="wrapper">
		<form class="errorForm" action="start.jsp">
			<div class="formtitle">ERROR</div>

			<div class="input nobottomborder">
				<div class="inputtext">Sorry mate! Something went horribly wrong.</div>
				<div class="inputtext">Drop me an email to danielgalassi@gmail.com and I'll try to fix it.</div>
				<div class="inputtext">What about testing some OBIEE repository using the validator service?</div>
			</div>

			<div class="buttons">
				<input class="orangebutton" type="submit"
					value="Take me to the Start Page" />
			</div>
		</form>
	</div>

</body>
</html>
