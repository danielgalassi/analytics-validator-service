<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes"/>
	<xsl:param name="SelectedSubjectArea"/>
	<xsl:param name="SessionFolder"/>
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
		p  {font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size:10pt; color: rgb(128, 128, 128);}
		tr {height:30; font-size: 8.5pt;}
		tr:hover {background: rgb(248,248,248);}
		table {
			border-spacing: 0 0;
			margin: 1px;
			border-right: 1px solid #DEDEDE;
			font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 8.5pt;}
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
				padding-left: 6px;
				padding-right: 6px;
				}
				</style>
				
				<title>Oracle Business Intelligence Validator Service Report</title>
			</head>
			<body>
				<!-- Validator Service headings -->
				<h1>OBIEE Validator Service Report</h1>
				<br/>
				<h2>Summary results <a href="{$SessionFolder}/results/Details.html" target="_blank">(see detailed results)</a> <a href="{$SessionFolder}/results/Results.zip" target="_blank">(download results)</a></h2>
				
				<br/>
				<h3>Subject Area: <xsl:value-of select="$SelectedSubjectArea"/> </h3>
				<br/>
				<h4>Tests executed: <xsl:value-of select="count(document(//results))"/> (in <xsl:value-of select="format-number(sum(//results/@elapsedTime), '0.000')"/> seconds)</h4>
				<h4>Total elapsed time: <xsl:value-of select="format-number(//index/@totalElapsedTime, '0.0')"/> seconds</h4>
				<br/>
				<!-- Table Section -->
				<table>
					<tbody>
					<!-- Table heading -->
						<tr>
							<th width="250px" height="28px" >Test</th>
							<th width="550px" height="28px" >Description</th>
							<th width="130px" height="28px" >Objects tested</th>
							<th width="130px" height="28px" >Elapsed time</th>
							<th width="130px" height="28px" >Failures</th>
							<th width="130px" height="28px" >N/A</th>
							<th width="130px" height="28px" >Success Rate</th>
						</tr>
				<!-- Test Results Section -->
						<xsl:for-each select="document(//results)">
							<xsl:variable name="testCount" select="position()"/>
							<xsl:for-each select=".//Test">
								<tr>
									<td height="28px" >
										<xsl:value-of select="./TestHeader/TestName"/>
									</td>
									<td height="28px" >
										<xsl:value-of select="./TestHeader/TestDescription"/>
									</td>
									<td height="28px" style="text-align:right; padding-right:50px;">
										<xsl:value-of select="count(.//Results/Object)"/>
									</td>
									<td height="28px" style="text-align:right; padding-right:45px;">
										<xsl:value-of select="format-number(//results[$testCount]/@elapsedTime, '0.000')"></xsl:value-of>s
									</td>
									<!-- Deprecated cell tag, previously used for the Error column -->
									<!--td height="28px" style="background: #FFF1BF; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 8pt; font-weight: bold; color: red; text-align:right; padding-right:55px;">
										<xsl:value-of select="count(.//Results/Object[not(@result)])"/>
									</td-->
									<!-- Fail column cell -->
									<xsl:if test="count(.//Results/Object[@result='Fail']) > 0">
										<td height="28px" style="background: #FFF1BF; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 8pt; font-weight: bold; color: red; text-align:right; padding-right:55px;">
											<xsl:value-of select="count(.//Results/Object[@result='Fail'])"/>
										</td>
									</xsl:if>
									<xsl:if test="count(.//Results/Object[@result='Fail']) = 0">
										<td height="28px" style="text-align:right; padding-right:55px;">
											<xsl:value-of select="count(.//Results/Object[@result='Fail'])"/>
										</td>
									</xsl:if>
									<!-- Inconclusive results (N/A) column cell -->
									<xsl:if test="count(.//Results/Object[@result='N/A']) > 0">
										<td height="28px" style="background: #F8FCCF; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 8pt; font-weight: bold; color: #93948E; text-align:right; padding-right:55px;">
											<xsl:value-of select="count(.//Results/Object[@result='N/A'])"/>
										</td>
									</xsl:if>
									<xsl:if test="count(.//Results/Object[@result='N/A']) = 0">
										<td height="28px" style="text-align:right; padding-right:55px;">
											<xsl:value-of select="count(.//Results/Object[@result='N/A'])"/>
										</td>
									</xsl:if>
									<!-- Success Rate -->
									<!-- If no objects were tested, display a hyphen and don't calculate the success rate -->
									<xsl:if test="number(count(.//Results/Object)) = 0">
										<!--td height="28px" style="text-align:right; padding-right:45px;"-->
										<td height="28px" style="text-align:right; padding-right:45px;">
											<xsl:if test="number(count(.//Results/Object)) = 0">-</xsl:if>
										</td>
									</xsl:if>
									<!-- If objects were tested, display the success rate calculated and stored in the variable successPct -->
									<xsl:if test="number(count(.//Results/Object)) > 0">
										<xsl:variable name="successPct" select="round(100 * number(count(.//Results/Object[@result='Pass'])) div number(count(.//Results/Object)))"/>
										<!-- RED, less than 75% -->
										<xsl:if test="$successPct &lt; 75">
											<td height="28px" style="background: #FFF1BF; color: red; text-align:right; padding-right:45px;">
												<xsl:value-of select="$successPct"/> %
											</td>
										</xsl:if>
										<!-- AMBER, 75-85% -->
										<xsl:if test="$successPct &gt;= 75 and successPct &lt; 85">
											<td height="28px" style="background: #F8FCCF; color: #93948E; text-align:right; padding-right:45px;">
												<xsl:value-of select="$successPct"/> %
											</td>
										</xsl:if>
										<!-- GREEN, over 85% -->
										<xsl:if test="$successPct &gt;= 85">
											<td height="28px" style="background: #CCFF99; color: green; text-align:right; padding-right:45px;">
												<xsl:value-of select="$successPct"/> %
											</td>
										</xsl:if>
									</xsl:if>
								</tr>
							</xsl:for-each>
						</xsl:for-each>
					</tbody>
				</table>
				<br/>
				<br/>
				<hr style="height: 1px; border: 0; background-color: #AAAAAA; width: 70%;"/>
				<div style="padding-top: 20px;">
					<p style="color: rgb(128, 128, 128); float: left;">Mozilla Firefox or Google Chrome are strongly recommended for best results.</p>
					<p style="color: rgb(128, 128, 128); float: right;">Generated using <a href="http://code.google.com/p/analytics-validator-service/" style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 8pt;color: rgb(128, 128, 128);" target="_blank">OBIEE Validator Service</a>.</p>
				</div>
				<p style="color: rgb(0, 0, 255); float: center;"> This HTML page is W3C compliant.</p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
