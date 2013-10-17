package org.validator.utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Class to trim a large metadata file keeping only objects associated with the selected subject area
 * @author danielgalassi@gmail.com
 *
 */
public class SaxToDom
{
	private XMLReader		reader;
	private SaxToDomHandler	handlers;
	private Document		doc;
	private File			metadata;

	public SaxToDom(Document doc, XMLReader reader, File metadata) {
		this.reader		= reader;
		this.doc		= doc;
		this.metadata	= metadata;
	}

	public Vector<String> findElements(
			String			pickTag, 
			Vector<String>	valueList, 
			String			matchingAttrib, 
			String			returningAttrib, 
			boolean			append) {

		InputSource metadataStream = FileUtils.getStream(metadata);
		Vector<String> foundIdList = new Vector<String>();
		handlers = new SaxToDomHandler(doc, pickTag, valueList, foundIdList, matchingAttrib, returningAttrib, append);
		reader.setContentHandler(handlers);
		//recommended but... omitted for the time being
		//reader.setErrorHandler(handlers);
		try {
			reader.parse(metadataStream);
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
		return foundIdList;
	}

	public Document makeDom(String pickTag, Vector<String> subjectAreaName) {

		try {
			if (doc == null) {
				doc = XMLUtils.createDOMDocument();
				Element repoTag = doc.createElement("Repository");
				Element declTag = doc.createElement("DECLARE");
				repoTag.appendChild(declTag);
				doc.appendChild(repoTag);
			}
		}
		// For the catch handlers below, use your usual logging facilities.
		catch (DOMException e) {
			e.printStackTrace();
		}

		//stores SA id and BM id
		Vector<String> subjectAreas = findElements(pickTag, subjectAreaName, "name", "id", true);

		pickTag = "PresentationTable";
		//stores PresentationTable id
		Vector<String> presentationTables = findElements(pickTag, subjectAreas, "parentId", "id", true);

		pickTag = "PresentationColumn";
		//stores PresentationColumn id and referenced logical column id
		Vector<String> presentationColumns = findElements(pickTag, presentationTables, "parentId", "id", true);

		pickTag = "LogicalColumn";
		//stores the LogicalColumn parentId list (a list of LogicalTable Ids) 
		Vector<String> tempLogicalTables = findElements(pickTag, presentationColumns, "id", "parentId", false);

		pickTag = "LogicalTable";
		//stores the LogicalTable list
		Vector<String> logicalTables = findElements(pickTag, tempLogicalTables, "id", "id", true);
		tempLogicalTables = null;

		pickTag = "LogicalColumn";
		//stores the LogicalColumn list
		Vector<String> logicalColumns = findElements(pickTag, logicalTables, "parentId", "id", true);

		//stores the LTS id
		pickTag = "LogicalTableSource";
		//stores the LTS and PhysicalTable id list
		Vector<String> logicalTableSources = findElements(pickTag, logicalTables, "id", "id", true);

		pickTag = "PhysicalTable";
		//stores the PhysicalTable (Aliases included) list
		Vector<String> physicalTables = findElements(pickTag, logicalTableSources, "id", "id", true);

		Vector<String> tempSchemas = findElements(pickTag, physicalTables, "id", "parentId", false);
		//stores the Schema list
		pickTag = "Schema";
		Vector<String> schemas = findElements(pickTag, tempSchemas, "id", "id", true);
		tempSchemas = null;

		Vector<String> tempPhysicalCatalogs = findElements(pickTag, schemas, "id", "parentId", false);
		//stores the Schema list
		pickTag = "PhysicalCatalog";
		Vector<String> physicalCatalogs = findElements(pickTag, tempPhysicalCatalogs, "id", "id", true);
		tempPhysicalCatalogs = null;

		Vector<String> tempDatabases2 = findElements(pickTag, physicalCatalogs, "id", "parentId", false);
		pickTag = "Schema";
		Vector<String> tempDatabases = findElements(pickTag, schemas, "id", "parentId", false);
		for (String db : tempDatabases2) {
			if (!tempDatabases.contains(db)) {
				tempDatabases.add(db);
			}
		}
		//stores the DB list
		pickTag = "Database";
		Vector<String> databases = findElements(pickTag, tempDatabases, "id", "id", true);
		tempDatabases2 = null;
		tempDatabases = null;

		pickTag = "BusinessModel";
		//stores the BM id list
		Vector<String> businessModels = findElements(pickTag, subjectAreas, "id", "id", true);

		pickTag = "MeasureDefn";
		//stores the Measure Definition list
		Vector<String> measureDefs = findElements(pickTag, logicalColumns, "parentId", "id", true);

		pickTag = "PhysicalColumn";
		//stores the PhysicalTable list
		Vector<String> physicalColumns = findElements(pickTag, physicalTables, "parentId", "id", true);

		pickTag = "PhysicalKey";
		//stores the PK list
		Vector<String> physicalKeys = findElements(pickTag, physicalTables, "parentId", "id", true);

		return doc;
	}
}
