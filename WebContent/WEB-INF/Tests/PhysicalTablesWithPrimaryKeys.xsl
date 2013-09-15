<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<Test>
			<testName>PhysicalTableKeys</testName>
			<Results>
				<xsl:for-each select="//Database">
					<Database>
						<DatabaseName>
							<xsl:value-of select="@name"/>
						</DatabaseName>
						<xsl:variable name="databaseId" select="@id"/>
					<!-- direct path... Database -> Schema -->
						<xsl:for-each select="../Schema[@parentId=$databaseId]">
							<Schema>
								<xsl:value-of select="@name"/>
							</Schema>
						</xsl:for-each>
					<!-- indirect path... Database -> Catalog -> Schema -->
						<xsl:for-each select="../PhysicalCatalog[@parentId=$databaseId]">
							<xsl:variable name="catalogId" select="@id"/>
							<xsl:for-each select="../Schema[@parentId=$catalogId]">
								<Schema>
									<xsl:value-of select="@name"/>
								</Schema>
							</xsl:for-each>
						</xsl:for-each>
					</Database>
				</xsl:for-each>
			</Results>
		</Test>
	</xsl:template>
</xsl:stylesheet>
