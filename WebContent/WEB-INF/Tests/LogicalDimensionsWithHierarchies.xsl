<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<Test>
			<TestHeader>
				<TestName>DimensionHierarchies</TestName>
				<TestDescription></TestDescription>
				<Object type="Object">Logical Dimension</Object>
				<ParentObject type="Object">Business Model</ParentObject>
				<GrandParentObject type="Repository Layer">Business Model and Mapping Layer</GrandParentObject>
			</TestHeader>
			<Results>
				<xsl:for-each select="//BusinessModel">
					<xsl:variable name="bizModelId" select="@id"/>
					<xsl:variable name="bizModelName" select="@name"/>
					<xsl:for-each select="../LogicalTable[@parentId=$bizModelId and (starts-with(@name,'Dim') or starts-with(@name,'DIM'))]">
						<xsl:variable name="logTableId" select="@id"/>
						<Object>
							<xsl:attribute name="name">
								<xsl:value-of select="@name"/>
							</xsl:attribute>
							<xsl:attribute name="parentObject">
								<xsl:value-of select="$bizModelName"/>
							</xsl:attribute>
							<xsl:choose>
								<xsl:when test="count(../LogicalColumn[@parentId=$logTableId]/Levels/RefLogicalLevel)>0">
									<xsl:attribute name="result">Pass</xsl:attribute>
									<xsl:attribute name="comment">OK</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="result">Fail</xsl:attribute>
									<xsl:attribute name="comment">No dimension hierarchy found</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</Object>
					</xsl:for-each>
				</xsl:for-each>
			</Results>
		</Test>
	</xsl:template>
</xsl:stylesheet>

