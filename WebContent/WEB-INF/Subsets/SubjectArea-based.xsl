<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
	<xsl:output method="xml"/>
	<xsl:param name="SubjectArea" />
	<xsl:template match="/Repository/DECLARE">
		<Repository>
			<DECLARE>
				<!-- Subject Area -->
				<xsl:for-each select=".//PresentationCatalog[@name=$SubjectArea]">
					<xsl:variable name="SubjectAreaId" select="@id"/>
					<xsl:variable name="BusinessModelId" select="./RefBusinessModel/@id"/>
					<PresentationCatalog>
						<xsl:copy-of select="@*|node()"/>
					</PresentationCatalog>
					<!-- Presentation Tables -->
					<xsl:for-each select="..//PresentationTable[@parentId=$SubjectAreaId]">
						<xsl:variable name="PresentationTableId" select="@id"/>
						<PresentationTable>
							<xsl:copy-of select="@*|node()"/>
						</PresentationTable>
						<!-- Presentation Columns -->
						<xsl:for-each select="..//PresentationColumn[@parentId=$PresentationTableId]">
							<PresentationColumn>
								<xsl:copy-of select="@*|node()"/>
							</PresentationColumn>
							<!-- PresentationColumn/RefLogicalColumn id's reference LogicalColumn id -->
						</xsl:for-each>
					</xsl:for-each>
					<!-- Business Model -->
					<xsl:for-each select="..//BusinessModel[@id=$BusinessModelId]">
						<BusinessModel>
							<xsl:copy-of select="@*|node()"/>
						</BusinessModel>
					</xsl:for-each>
				</xsl:for-each>
			</DECLARE>
		</Repository>
	</xsl:template>
</xsl:stylesheet>

