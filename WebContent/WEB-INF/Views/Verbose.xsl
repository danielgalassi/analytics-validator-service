<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<!--xsl:output method="html"/-->
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes"/>
	<xsl:template match="/">
		<!--html-->
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<style type="text/css">
		h1 {font-family: Helvetica, sans-serif; font-weight: bold; font-size: 24pt; color: #676767;}
		h2 {font-family: Helvetica, sans-serif; font-weight: bold; font-size: 20pt; color: #777777;}
		h3 {font-family: Helvetica, sans-serif; font-size: 14pt; color: #777777;}
		h4 {font-family: Helvetica, sans-serif; font-size: 14pt; color: #888888;}
		li {font-family: Helvetica, sans-serif; font-size: 10pt; color: #474747;}
		a  {font-family: Helvetica, sans-serif; font-size: 10pt; color: #444444;}
		*  {font-family: Helvetica, sans-serif; font-size: 8pt; color: #333333;}
		tr {height:30; font-size: 8.5pt;}
		tr:hover {background: rgb(248,248,248);}
		table {
		white-space:nowrap;
			border-spacing: 0 0;
			margin: 1px;
			border-right: 1px solid #DEDEDE;
			font-family: Helvetica, sans-serif; font-size: 8.5pt;}
		thead th {
		white-space:nowrap;
			font-family: Helvetica, sans-serif; font-size: 8pt;
			background: #EFEFEF;
			border-left: 1px solid #DEDEDE;
			border-top: 1px solid #DEDEDE;}
		tbody td {
		white-space:nowrap;
        		font-family: Helvetica, sans-serif; font-size: 8.5pt;
			border-bottom: 1px solid #DEDEDE;
			border-left: 1px solid #DEDEDE;}
				</style>
				
				<title>Oracle Business Intelligence Security Matrix - Features</title>
			</head>
			<body>
				<!-- Security Matrix heading -->
				<h1>OBIEE Security Setup</h1>
				<br/>
				<xsl:for-each select="/WebCatalog">
					<h2>Feature Privilege Matrix</h2>
					<br/>
					<!-- Matrix Section -->
					<table>
						<tbody>
							<!-- Component list and ticks -->
							<!-- Component Names -->
							<xsl:for-each select="./ComponentList/Component">
								<tr>
									<td style="background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">
										<xsl:value-of select="@ComponentName"/>
									</td>
									<!-- greyed out cells -->
									<xsl:for-each select="../../..//ApplicationRole/@ApplicationRoleName">
										<td style="text-align:center; background: #ECECEC; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555;">
											<xsl:value-of select="."/>
										</td>
									</xsl:for-each>
								</tr>
								<!-- Privilege Section -->
								<xsl:for-each select="./Privilege">
									<tr>
										<!-- Privilege Names -->
										<td style="background: #F6F6F6; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #555555; padding-left:20px;">
											<xsl:value-of select="@PrivilegeName"/>
										</td>
										<xsl:variable name="compName" select="../@ComponentName"/>
										<xsl:variable name="privName" select="@PrivilegeName"/>
										<xsl:for-each select="../../..//ApplicationRole/@ApplicationRoleName">
											<xsl:variable name="appRole" select="."/>
											<xsl:choose>
												<xsl:when test="count(../../..//Component[@ComponentName=$compName]/Privilege[@PrivilegeName=$privName]/RoleList/Role[@access='Granted' and .= $appRole]) > 0">
													<td style="background: #CCFF99; font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: green; text-align:center;">YES</td>
												</xsl:when>
												<xsl:otherwise>
													<td style="font-family: Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #444444; text-align:center;">-</td>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
									</tr>
								</xsl:for-each>
							</xsl:for-each>
						</tbody>
					</table>
					
					<br/>
					<br/>
					<hr style="height: 1px; border: 0; background-color: #AAAAAA; width: 70%;"/>
				
				</xsl:for-each>
				<div style="padding-top: 20px;">
					<p style="color: rgb(128, 128, 128); float: left;">Mozilla Firefox or Google Chrome are strongly recommended for best results.</p>
					<p style="color: rgb(128, 128, 128); float: right;">Generated using <a href="http://code.google.com/p/obiee-security-audit/" style="font-family: Helvetica, sans-serif; font-size: 8pt;color: rgb(128, 128, 128);" target="_blank">OBIEE Security Audit</a> application.</p>
				</div>
				<p style="color: rgb(0, 0, 255); float: center;"> This HTML page is W3C compliant.</p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
