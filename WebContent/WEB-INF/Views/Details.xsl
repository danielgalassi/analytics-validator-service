<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes"/>
	<xsl:param name="ShowErrorsOnly"/>
	<xsl:param name="SelectedSubjectArea"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<style type="text/css">
		h1 {font-family: Futura, "Trebuchet MS", Arial, sans-serif; font-size: 24pt; color: #676767;}
		h2 {font-family: Futura, "Trebuchet MS", Arial, sans-serif; font-size: 20pt; color: #777777;}
		h3 {font-family: Futura, "Trebuchet MS", Arial, sans-serif; font-size: 16pt; color: #777777;}
		h4 {font-family: Futura, "Trebuchet MS", Arial, sans-serif; font-size: 12pt; color: #888888;}
		li {font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 10pt; color: #474747;}
		a  {font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 10pt; color: #444444;}
		*  {font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size:  8pt; color: #333333;}
		p  {font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 10pt; color: rgb(128, 128, 128);}
		tr {height:30; font-size: 8.5pt;}
		tr:hover {background: rgb(248,248,248);}
		table {
			border-spacing: 0 0;
			margin: 1px;
			border-right: 1px solid #DEDEDE;
			font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 8.5pt;
			}
		th	{
			font-family: Helvetica, sans-serif;
			font-size: 9pt;
			font-weight: bold;
			text-align:center;
			background: #ECECEC;
			color: #555555;
			border-left: 1px solid #DEDEDE;
			border-top: 1px solid #DEDEDE;
			border-bottom: 1px solid #DEDEDE;
			}
		tbody td {
				font-family: Helvetica, sans-serif;
				font-size: 8pt;
        		font-weight: bold;
        		text-align:left;
        		color: #444444;
				border-bottom: 1px solid #DEDEDE;
				border-left: 1px solid #DEDEDE;
				padding-left:6px;
				padding-right:6px;
				}
				</style>
				
				<title>Oracle Business Intelligence Validator Service Report</title>
			</head>
			<body>
				<!-- Validator Service headings -->
				<h1>OBIEE Validator Service Report</h1>
				<br/>
				<h2>Detailed results</h2>
				<br/>
				<h3>Subject Area: <xsl:value-of select="$SelectedSubjectArea"/> </h3>
				<br/>
				<br/>
				
				<xsl:for-each select="document(//results)">
					<xsl:variable name="testCount" select="position()"/>
					<xsl:for-each select=".//Test">
					<!-- Results Section -->
						<h3>Test: <xsl:value-of select="./TestHeader/TestName"/> (<xsl:value-of select="format-number(//results[$testCount]/@elapsedTime, '0.000')"></xsl:value-of>s)
						</h3>
						<p>Description: <xsl:value-of select="./TestHeader/TestDescription"/>
						</p>
						<br/>
						<!-- No objects to validate -->
						<xsl:if test="count(./Results/Object) = 0">
							<p style="font-weight: bold;">No objects assessed.</p>
						</xsl:if>
						<!-- No errors found (failed or N/A results) -->
						<xsl:if test="$ShowErrorsOnly='true' and count(./Results/Object[@result='Fail']) = 0  and count(./Results/Object[@result='N/A']) = 0 and count(./Results/Object) > 0">
							<p style="font-weight: bold;">No failed or inconclusive results.</p>
						</xsl:if>
						<!-- Table Section -->
						<xsl:if test="($ShowErrorsOnly='false' and count(./Results/Object) > 0) or ($ShowErrorsOnly='true' and (count(./Results/Object[@result='Fail']) > 0  or count(./Results/Object[@result='N/A']) > 0))">
						<table>
							<tbody>
							<!-- Object Hierarchy across table heading -->
								<tr>
									<xsl:if test="count(./TestHeader/GreatGrandParentObject)>0">
										<xsl:for-each select="./TestHeader/GrandParentObject">
											<th width="250px" height="28px" >
												<xsl:value-of select="."/>
											</th>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="count(./TestHeader/GrandParentObject)>0">
										<xsl:for-each select="./TestHeader/ParentObject">
											<th width="350px" height="28px" >
												<xsl:value-of select="."/>
											</th>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="count(./TestHeader/ParentObject)>0">
										<xsl:for-each select="./TestHeader/Object">
											<th width="350px" height="28px" >
												<xsl:value-of select="normalize-space(.)"/>
											</th>
										</xsl:for-each>
									</xsl:if>
									<th width="50px" >Result</th>
									<th width="250px" >Comment</th>
								</tr>
								<!-- Test Results Section -->
								<xsl:for-each select="./Results/Object">
									<xsl:if test="$ShowErrorsOnly='false' and @result='Pass'">
										<tr>
											<xsl:if test="count(../../TestHeader/GreatGrandParentObject)>0">
												<td height="28px" >
													<xsl:value-of select="@grandParentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/GrandParentObject)>0">
												<td height="28px" >
													<xsl:value-of select="@parentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/ParentObject)>0">
												<td height="28px" >
													<xsl:value-of select="@name"/>
												</td>
											</xsl:if>
										<!-- Green cells = Pass -->
											<xsl:if test="@result='Pass'">
												<td style="background: #CCFF99; color: green; text-align:center;">
													<xsl:value-of select="@result"/>
												</td>
												<td style="background: #CCFF99; color: green;">
													<xsl:value-of select="@comment"/>
												</td>
											</xsl:if>
										</tr>
									</xsl:if>
									<xsl:if test="($ShowErrorsOnly='true' and not(@result='Pass')) or (not($ShowErrorsOnly='true') and not(@result='Pass'))">
										<tr>
											<xsl:if test="count(../../TestHeader/GreatGrandParentObject)>0">
												<td height="28px" >
													<xsl:value-of select="@grandParentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/GrandParentObject)>0">
												<td height="28px" >
													<xsl:value-of select="@parentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/ParentObject)>0">
												<td height="28px" >
													<xsl:value-of select="@name"/>
												</td>
											</xsl:if>
										<!-- Red cells = Fail -->
											<xsl:if test="@result='Fail'">
												<td style="background: #FFF1BF; color: red; text-align:center;">
													<xsl:value-of select="@result"/>
												</td>
												<td style="background: #FFF1BF; color: red;">
													<xsl:value-of select="@comment"/>
												</td>
											</xsl:if>
										<!-- Amber cells = N/A -->
											<xsl:if test="@result='N/A'">
												<td style="background: #F8FCCF; color: #93948E; text-align:center;">
													<xsl:value-of select="@result"/>
												</td>
												<td style="background: #F8FCCF; color: #93948E;">
													<xsl:value-of select="@comment"/>
												</td>
											</xsl:if>
										<!-- Missing results -->
											<xsl:if test="not(@result)">
												<td style="background: #FFF1BF; color: red; text-align:center;">Error</td>
												<td style="background: #FFF1BF; color: red;">This object could not be tested, please check</td>
											</xsl:if>
										</tr>
									</xsl:if>
								</xsl:for-each>
							</tbody>
						</table>
						</xsl:if>
					</xsl:for-each>
					<br/>
					<br/>
					<hr style="height: 1px; border: 0; background-color: #AAAAAA; width: 70%;"/>
				
				</xsl:for-each>
				
				<div style="padding-top: 20px;">
					<p style="color: rgb(128, 128, 128); float: left;">Mozilla Firefox or Google Chrome are strongly recommended for best results.</p>
					<p style="color: rgb(128, 128, 128); float: right;">Generated using <a href="http://code.google.com/p/analytics-validator-service/" style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 8pt;color: rgb(128, 128, 128);" target="_blank">OBIEE Validator Service</a>.</p>
				</div>
				<p style="color: rgb(0, 0, 255); float: center;"> This HTML page is W3C compliant.</p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>

