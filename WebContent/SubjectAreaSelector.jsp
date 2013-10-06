<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Step 2. Select the subject area to review.</title>
</head>
<body>

	<jsp:useBean id="obj" class="utils.SaxParser" scope="page">
		<jsp:setProperty name="obj" property="metadata" value="${metadataFile}" />
		<jsp:setProperty name="obj" property="tag" value="PresentationCatalog" />
		<jsp:setProperty name="obj" property="attribute" value="name" />
	</jsp:useBean>

	<form action="ValidatorService" method="POST">
	<label>Select the Subject Area to validate </label>
	<select onchange="this.form.submit()">
			<c:forEach var="item" items="${obj.listOfValues}">
				<option>${item}</option>
			</c:forEach>
		</select>
	</form>
</body>
</html>
