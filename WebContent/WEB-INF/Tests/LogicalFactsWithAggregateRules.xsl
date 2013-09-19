<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<Test>
			<TestHeader>
				<TestName>AggregateRulesForFacts</TestName>
				<TestDescription></TestDescription>
				<Object>Logical Column</Object>
				<ParentObject type="Object">Logical Fact Table</ParentObject>
				<GrandParentObject type="Object">Business Model</GrandParentObject>
				<GreatGrandParentObject type="Repository Layer">Business Model</GreatGrandParentObject>
			</TestHeader>
			<Results>
				<xsl:for-each select="//BusinessModel">
					<xsl:variable name="bizModelId" select="@id"/>
					<xsl:variable name="bizModelName" select="@name"/>
					<xsl:for-each select="../LogicalTable[@parentId=$bizModelId and (starts-with(@name,'Fact') or starts-with(@name,'FACT'))]">
						<xsl:variable name="logTableId" select="@id"/>
						<xsl:variable name="logTableName" select="@name"/>
						<xsl:for-each select="../LogicalColumn[@parentId=$logTableId]">
							<xsl:variable name="logColumnId" select="@id"/>
							<Object>
								<xsl:attribute name="name">
									<xsl:value-of select="@name"/>
								</xsl:attribute>
								<xsl:attribute name="parentObject">
									<xsl:value-of select="$logTableName"/>
								</xsl:attribute>
								<xsl:attribute name="grandParentObject">
									<xsl:value-of select="$bizModelName"/>
								</xsl:attribute>
								<xsl:if test="not(@isDerived='true')">
									<xsl:choose>
										<xsl:when test="count(../MeasureDefn[@parentId=$logColumnId])>0">
											<xsl:attribute name="result">Pass</xsl:attribute>
											<xsl:attribute name="comment">OK</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="result">Fail</xsl:attribute>
											<xsl:attribute name="comment">Please check aggregation rules (sum, avg, etc) have been set</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>
								<xsl:if test="@isDerived='true'">
									<xsl:choose>
										<xsl:when test="contains(./Expr, '+') or contains(./Expr, '-') or contains(./Expr, '/') or contains(./Expr, '*')">
											<xsl:attribute name="result">Pass</xsl:attribute>
											<xsl:attribute name="comment">OK</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="result">N/A</xsl:attribute>
											<xsl:attribute name="comment">Please check the metric definition</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>
							</Object>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:for-each>
			</Results>
		</Test>
	</xsl:template>
</xsl:stylesheet>

