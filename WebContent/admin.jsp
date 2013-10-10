<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Analytics Validator Service</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/selector.css" type="text/css" />
</head>
<body>

	<jsp:useBean id="appFolder" class="utils.FolderUtils" scope="page">
		<jsp:setProperty name="appFolder" property="folder"
			value="<%=getServletContext()%>" />
	</jsp:useBean>

	<div class="wrapper">
		<form class="theForm" action="ValidatorService" method="POST">
			<div class="formtitle">Admin Area</div>
			<div class="input nobottomborder">
				<!--div class="inputtext">Check files</div-->
				<div class="inputcontent">
					<table>
						<tbody>
							<c:forEach var="item" items="${appFolder.contents}">
								<tr>
									<td value="${item.get(0)}">Tick</td>
									<td>${item.get(0)}</td>
									<td>${item.get(1)}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</form>
	</div>
</body>
</html>
