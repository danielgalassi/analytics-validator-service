<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Analytics Validator Service</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/selector.css" type="text/css" />
</head>
<body>

	<jsp:useBean id="obj" class="org.validator.utils.TagSelector" scope="page">
		<jsp:setProperty name="obj" property="workDir" value="${workDir}" />
		<jsp:setProperty name="obj" property="metadata" value="${metadataFile}" />
	</jsp:useBean>

	<div class="wrapper">
	<form class="theForm" action="ValidatorService" method="POST">
		<div class="formtitle">Step 2 - Select Subject Area</div>
		<div class="input nobottomborder">
			<div class="inputtext">Validate</div>
			<div class="inputcontent">
				<select name="SubjectArea" onchange="this.form.submit()">
					<c:forEach var="item" items="${obj.listOfValues}">
						<option value="${item}">${item}</option>
					</c:forEach>
				</select>
			</div>
		</div>
	</form>
	</div>
</body>
</html>
