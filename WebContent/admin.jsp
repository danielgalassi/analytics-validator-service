<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Analytics Validator Service</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/admin.css" type="text/css" />
</head>
<body>

	<jsp:useBean id="appFolder" class="utils.FolderUtils" scope="page">
		<jsp:setProperty name="appFolder" property="folder"
			value="<%=getServletContext()%>" />
	</jsp:useBean>

	<div class="wrapper">
		<form class="adminForm" action="Admin" method="POST">
			<div class="formtitle">Admin Area</div>
			<div class="inputtext">Tick session to delete</div>
			<div class="input nobottomborder">
				<table>
					<tbody>
						<c:forEach var="item" items="${appFolder.contents}">
							<tr>
								<td height="28px" width="50px"><input type="checkbox"
									name="sessionFolder" value="${item.getName()}" /></td>
								<td height="28px" width="270px"
									style="font-family: Helvetica, sans-serif; font-size: 8.5pt; color: #444444; text-align: left;">
									${item.getName()}</td>
								<td height="28px" width="100px"
									style="font-family: Helvetica, sans-serif; font-size: 8.5pt; color: #444444; text-align: left;">${item.getLastModified()}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="buttons">
				<input class="orangebutton" type="submit" value="Delete" />
			</div>

		</form>
	</div>
</body>
</html>