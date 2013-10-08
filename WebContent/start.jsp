<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Analytics Validator Service</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/style.css" type="text/css" />
</head>
<body>

	<div class="wrapper">
		<form class="form1" action="upload" method="post"
			enctype="multipart/form-data">
			<div class="formtitle">Step 1 - Start the validation process</div>

			<div class="input">
				<div class="inputtext">File format</div>
				<div class="inputcontent">
					<input type="radio" id="zip" name="fileFormat" value="zip" checked>
					<label for="zip">ZIP</label>
					<input type="radio" id="xudml" name="fileFormat" value="xudml">
					<label for="xudml">XUDML</label>
				</div>
			</div>

			<div class="input">
				<div class="inputtext">Generate results</div>
					<div class="inputcontent">
						<input type="radio" id="Verbose" name="resultsFormat" value="Verbose" checked>
						<label for="Verbose">Verbose</label>
						<input type="radio" id="ShowErrorsOnly" name="resultsFormat" value="ShowErrorsOnly">
						<label for="ShowErrorsOnly">Errors Only</label>
					</div>
			</div>

			<div class="input nobottomborder">	
				<div class="inputtext">Select metadata file</div>
				<div class="inputcontent nobottomborder">
					<input id="metadataFile" type="file" name="metadata" size="15" />
				</div>
			</div>

			<div class="buttons">
				<input class="orangebutton" type="submit" value="Upload" />
			</div>
		</form>
	</div>

</body>
</html>
