<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Step 1. Start the validation process.</title>
</head>
<body>

<form action="upload" method="post" enctype="multipart/form-data">
<label for="fileFormat">Select file format </label>
<input name="fileFormat" type="radio" value="zip" checked>Zip file
<input name="fileFormat" type="radio" value="xudml">XUDML file<br/><br/> 
<label for="zipFile">Select metadata file </label>
<input id="metadataFile" type="file" name="metadata" size="35"/><br/>
<input type="submit" value="Upload"/>
</form>

</body>
</html>