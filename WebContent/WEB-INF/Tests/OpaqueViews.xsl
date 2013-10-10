<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<Test>
			<TestHeader>
				<TestName>OpaqueViews</TestName>
				<TestDescription>Evaluates Physical Tables are not Opaque Views</TestDescription>
				<Object>Physical Table</Object>
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
					<!-- type attribute does not exists (table) -->
					<xsl:when test="@type='none'">
						<xsl:attribute name="result">Pass</xsl:attribute>
						<xsl:attribute name="comment">OK</xsl:attribute>
					</xsl:when>
					<!-- Found an Opaque View -->
					<xsl:when test="@type='select'">
						<xsl:attribute name="result">Fail</xsl:attribute>
						<xsl:attribute name="comment">Consider materialising this view</xsl:attribute>
					</xsl:when>
					<!-- Table cannot be evaluated -->
					<xsl:otherwise>
						<xsl:attribute name="result">N/A</xsl:attribute>
						<xsl:attribute name="comment">Please check the repository</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</Object>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>

