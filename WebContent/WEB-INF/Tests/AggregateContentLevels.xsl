<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<Test>
			<TestHeader>
				<TestName>AggregateContentLevels</TestName>
				<TestDescription>Evaluates that Logical Table Sources have the Aggregate Content set</TestDescription>
				<Object>Logical Table Source</Object>
				<ParentObject type="Object">Logical Table</ParentObject>
				<GrandParentObject type="Object">Business Model</GrandParentObject>
				<GreatGrandParentObject type="Repository Layer">Business Model</GreatGrandParentObject>
			</TestHeader>
			<Results>
				<xsl:for-each select="//BusinessModel">
					<xsl:variable name="bizModelId" select="@id"/>
					<xsl:variable name="bizModelName" select="@name"/>
					<xsl:for-each select="../LogicalTable[@parentId=$bizModelId]">
						<xsl:variable name="logTableId" select="@id"/>
						<xsl:variable name="logTableName" select="@name"/>
						<xsl:for-each select="../LogicalTableSource[@parentId=$logTableId]">
							<xsl:variable name="logTableSourceId" select="@id"/>
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
								<xsl:if test="count(.//GroupBy/Expr[string(.)]) = 0">
									<xsl:attribute name="result">Fail</xsl:attribute>
									<xsl:attribute name="comment">Please check aggregate content levels have been set</xsl:attribute>
								</xsl:if>
								<xsl:if test="count(.//GroupBy/Expr[string(.)]) > 0">
									<xsl:attribute name="result">Pass</xsl:attribute>
									<xsl:attribute name="comment">OK</xsl:attribute>
								</xsl:if>
							</Object>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:for-each>
			</Results>
		</Test>
	</xsl:template>
</xsl:stylesheet>

