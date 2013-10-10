<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<Test>
			<TestHeader>
				<TestName>PhysicalTablesAndViewsFollowingNamingStandards</TestName>
				<TestDescription>Evaluates Physical Tables and Views have been set up following naming standards</TestDescription>
				<Object>Physical Table / View</Object>
				<ParentObject type="Object">Schema</ParentObject>
				<GrandParentObject type="Object">Database</GrandParentObject>
				<GreatGrandParentObject type="Repository Layer">Physical Layer</GreatGrandParentObject>
			</TestHeader>
			<Results>
				<xsl:for-each select="//Database">
					<xsl:variable name="databaseId" select="@id"/>
					<xsl:variable name="databaseName" select="@name"/>
					<!-- direct path... Database -> Schema -->
					<xsl:for-each select="../Schema[@parentId=$databaseId]">
						<xsl:call-template name="tables">
							<xsl:with-param name="schemaId" select="@id"/>
							<xsl:with-param name="schemaName" select="@name"/>
							<xsl:with-param name="databaseName" select="$databaseName"/>
						</xsl:call-template>
					</xsl:for-each>
					<!-- indirect path... Database -> Catalog -> Schema -->
					<xsl:for-each select="../PhysicalCatalog[@parentId=$databaseId]">
						<xsl:variable name="catalogId" select="@id"/>
						<xsl:for-each select="../Schema[@parentId=$catalogId]">
							<xsl:call-template name="tables">
								<xsl:with-param name="schemaId" select="@id"/>
								<xsl:with-param name="schemaName" select="@name"/>
								<xsl:with-param name="databaseName" select="$databaseName"/>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:for-each>
			</Results>
		</Test>
	</xsl:template>
	<!-- this template is the actual test -->
	<xsl:template name="tables">
		<xsl:param name="schemaId"/>
		<xsl:param name="databaseName"/>
		<xsl:param name="schemaName"/>
		<xsl:for-each select="../PhysicalTable[@parentId=$schemaId]">
			<xsl:variable name="tableId" select="@id"/>
			<Object>
				<xsl:attribute name="name">
					<xsl:value-of select="@name"/>
				</xsl:attribute>
				<xsl:attribute name="parentObject">
					<xsl:value-of select="$schemaName"/>
				</xsl:attribute>
				<xsl:attribute name="grandParentObject">
					<xsl:value-of select="$databaseName"/>
				</xsl:attribute>
				<xsl:attribute name="greatGrandParentObject">Physical Layer</xsl:attribute>
				
				<xsl:choose>
					<!-- Standard OBIA naming convention -->
					<xsl:when test="(starts-with(@name,'WC_') or starts-with(@name,'W_')) and (substring(@name, string-length(@name)-2)='_D' or substring(@name, string-length(@name)-2)='_D' or substring(@name, string-length(@name)-2)='_D')">
						<xsl:attribute name="result">Pass</xsl:attribute>
						<xsl:attribute name="comment">OK</xsl:attribute>
					</xsl:when>
					<!-- Standard "own" naming convention -->
					<xsl:when test="starts-with(@name,'D_') or starts-with(@name,'F_')">
						<xsl:attribute name="result">Pass</xsl:attribute>
						<xsl:attribute name="comment">OK</xsl:attribute>
					</xsl:when>
					<xsl:when test="starts-with(@name,'Dim_') or starts-with(@name,'Fact_') or starts-with(@name,'Agg_')">
						<xsl:attribute name="result">Pass</xsl:attribute>
						<xsl:attribute name="comment">OK</xsl:attribute>
					</xsl:when>
					<!-- Name does not follow naming standards -->
					<xsl:otherwise>
						<xsl:attribute name="result">Fail</xsl:attribute>
						<xsl:attribute name="comment">Please check the name of this object</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</Object>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>

