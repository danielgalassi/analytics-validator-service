<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes"/>
	<xsl:param name="ShowErrorsOnly"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<style type="text/css">
		h1 {font-family: Helvetica, sans-serif; font-weight: bold; font-size: 24pt; color: #676767;}
		h2 {font-family: Helvetica, sans-serif; font-weight: bold; font-size: 20pt; color: #777777;}
		h3 {font-family: Helvetica, sans-serif; font-size: 16pt; color: #777777;}
		h4 {font-family: Helvetica, sans-serif; font-size: 12pt; color: #888888;}
		li {font-family: Helvetica, sans-serif; font-size: 10pt; color: #474747;}
		a  {font-family: Helvetica, sans-serif; font-size: 10pt; color: #444444;}
		*  {font-family: Helvetica, sans-serif; font-size:  8pt; color: #333333;}
		p  {font-family: Helvetica, sans-serif; font-size:10pt; color: rgb(128, 128, 128);}
		tr {height:30; font-size: 8.5pt;}
		tr:hover {background: rgb(248,248,248);}
		table {
			border-spacing: 0 0;
			margin: 1px;
			border-right: 1px solid #DEDEDE;
			font-family: Helvetica, sans-serif; font-size: 8.5pt;}
		th {
			font-family: Helvetica, sans-serif; font-size: 8pt;
			background: #EFEFEF;
			border-left: 1px solid #DEDEDE;
			border-top: 1px solid #DEDEDE;
			border-bottom: 1px solid #DEDEDE;}
		tbody td {
        		font-family: Helvetica, sans-serif; font-size: 8.5pt;
			border-bottom: 1px solid #DEDEDE;
			border-left: 1px solid #DEDEDE;}
				</style>
				
				<title>Oracle Business Intelligence Validator Service Report</title>
			</head>
			<body>
				<!-- Validator Service headings -->
				<h1>OBIEE Validator Service Report</h1>
				<br/>
				<h2>Metadata test results</h2>
				<br/>
				
				<xsl:for-each select="document(//results)">
					<xsl:for-each select=".//Test">
					<!-- Results Section -->
						<h3>Test: <xsl:value-of select="./TestHeader/TestName"/>
						</h3>
						<p>Description: <xsl:value-of select="./TestHeader/TestDescription"/>
						</p>
						<br/>
						<!-- Table Section -->
						<table>
							<tbody>
							<!-- Object Hierarchy across table heading -->
								<tr>
									<xsl:if test="count(./TestHeader/GreatGrandParentObject)>0">
										<xsl:for-each select="./TestHeader/GrandParentObject">
											<th width="250px" height="28px" style="text-align:center; background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">
												<xsl:value-of select="."/>
											</th>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="count(./TestHeader/GrandParentObject)>0">
										<xsl:for-each select="./TestHeader/ParentObject">
											<th width="350px" height="28px" style="text-align:center; background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">
												<xsl:value-of select="."/>
											</th>
										</xsl:for-each>
									</xsl:if>
									<xsl:if test="count(./TestHeader/ParentObject)>0">
										<xsl:for-each select="./TestHeader/Object">
											<th width="350px" height="28px" style="text-align:center; background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">
												<xsl:value-of select="normalize-space(.)"/>
											</th>
										</xsl:for-each>
									</xsl:if>
									<th width="50px" style="text-align:center; background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">Result</th>
									<th width="250px" style="text-align:center; background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">Comment</th>
								</tr>
								<!-- Test Results Section -->
								<xsl:for-each select="./Results/Object">
									<xsl:if test="$ShowErrorsOnly='false' and @result='Pass'">
										<tr>
											<xsl:if test="count(../../TestHeader/GreatGrandParentObject)>0">
												<td height="28px" style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:left;">
													<xsl:value-of select="@grandParentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/GrandParentObject)>0">
												<td height="28px" style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:left;">
													<xsl:value-of select="@parentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/ParentObject)>0">
												<td height="28px" style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:left;">
													<xsl:value-of select="@name"/>
												</td>
											</xsl:if>
										<!-- Green cells = Pass -->
											<xsl:if test="@result='Pass'">
												<td style="background: #CCFF99; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: green; text-align:center;">
													<xsl:value-of select="@result"/>
												</td>
												<td style="background: #CCFF99; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: green; text-align:left;">
													<xsl:value-of select="@comment"/>
												</td>
											</xsl:if>
										</tr>
									</xsl:if>
									<xsl:if test="($ShowErrorsOnly='true' and not(@result='Pass')) or (not($ShowErrorsOnly='true') and not(@result='Pass'))">
										<tr>
											<xsl:if test="count(../../TestHeader/GreatGrandParentObject)>0">
												<td height="28px" style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:left;">
													<xsl:value-of select="@grandParentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/GrandParentObject)>0">
												<td height="28px" style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:left;">
													<xsl:value-of select="@parentObject"/>
												</td>
											</xsl:if>
											<xsl:if test="count(../../TestHeader/ParentObject)>0">
												<td height="28px" style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:left;">
													<xsl:value-of select="@name"/>
												</td>
											</xsl:if>
										<!-- Red cells = Fail -->
											<xsl:if test="@result='Fail'">
												<td style="background: #FADCE6; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: red; text-align:center;">
													<xsl:value-of select="@result"/>
												</td>
												<td style="background: #FADCE6; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: red; text-align:left;">
													<xsl:value-of select="@comment"/>
												</td>
											</xsl:if>
										<!-- Amber cells = N/A -->
											<xsl:if test="@result='N/A'">
												<td style="background: #F8FCCF; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #93948E; text-align:center;">
													<xsl:value-of select="@result"/>
												</td>
												<td style="background: #F8FCCF; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #93948E; text-align:left;">
													<xsl:value-of select="@comment"/>
												</td>
											</xsl:if>
										<!-- Missing results -->
											<xsl:if test="not(@result)">
												<td style="background: #FADCE6; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: red; text-align:center;">Error</td>
												<td style="background: #FADCE6; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: red; text-align:left;">This object could not be tested, please check</td>
											</xsl:if>
										</tr>
									</xsl:if>
								</xsl:for-each>
							</tbody>
						</table>
					</xsl:for-each>
					<br/>
					<br/>
					<hr style="height: 1px; border: 0; background-color: #AAAAAA; width: 70%;"/>
				
				</xsl:for-each>
				
				<div style="padding-top: 20px;">
					<p style="color: rgb(128, 128, 128); float: left;">Mozilla Firefox or Google Chrome are strongly recommended for best results.</p>
					<p style="color: rgb(128, 128, 128); float: right;">Generated using <a href="http://code.google.com/p/analytics-validator-service/" style="font-family: Helvetica, sans-serif; font-size: 8pt;color: rgb(128, 128, 128);" target="_blank">OBIEE Validator Service</a>.</p>
				</div>
				<p style="color: rgb(0, 0, 255); float: center;"> This HTML page is W3C compliant.</p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>

