package utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SaxToDom
{
	private XMLReader		reader;
	private InputSource		input;
	private SaxToDomHandler	handlers;
	private Document		doc;
	private File			metadata;

	public SaxToDom(Document doc, XMLReader reader, InputSource input, File metadata) {
		this.reader = reader;
		this.input  = input;
		this.doc = doc;
		this.metadata = metadata;
	}

	public Vector<String> findElements(
			String pickTag, 
			Vector<String> valueList, 
			String sMatchingAttrib, 
			String sReturningAttrib, 
			boolean append) {

		input = FileUtils.getIS(metadata);
		Vector<String> foundIdList = new Vector<String>();
		handlers = new SaxToDomHandler(doc, pickTag, valueList, foundIdList, sMatchingAttrib, sReturningAttrib, append);
		reader.setContentHandler(handlers);
		reader.setErrorHandler(handlers);

		try {
			reader.parse(input);
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}

		//System.out.println(pickTag + ": " + foundIdList.size());
		return foundIdList;
	}

	public Document makeDom(String pickTag, Vector<String> pickValues) {

		try {
			if (doc == null) {
				doc = XMLUtils.createDOMDocument();
				Element tagRepository = doc.createElement("Repository");
				Element tagDeclare = doc.createElement("DECLARE");
				tagRepository.appendChild(tagDeclare);
				doc.appendChild(tagRepository);
			}
		}
		// For the catch handlers below, use your usual logging facilities.
		catch (DOMException e) {
			e.printStackTrace();
		}

		//stores SA id and BM id
		Vector<String> listOfSAs = findElements(pickTag, pickValues, "name", "id", true);

		pickTag = "PresentationTable";
		//stores PresentationTable id
		Vector<String> listOfPresTables = findElements(pickTag, listOfSAs, "parentId", "id", true);

		pickTag = "PresentationColumn";
		//stores PresentationColumn id and referenced logical column id
		Vector<String> listOfPresCols = findElements(pickTag, listOfPresTables, "parentId", "id", true);

		pickTag = "BusinessModel";
		//stores the BM id list
		Vector<String> listOfBizModels = findElements(pickTag, listOfSAs, "id", "id", true);

		pickTag = "LogicalColumn";
		//stores the LogicalColumn parentId list 
		Vector<String> listofLogCols = findElements(pickTag, listOfPresCols, "id", "parentId", false);

		pickTag = "LogicalTable";
		//stores the LogicalTable list
		Vector<String> listOfLogTables = findElements(pickTag, listofLogCols, "id", "id", true);

		pickTag = "LogicalColumn";
		//stores the LogicalColumn list
		listofLogCols = findElements(pickTag, listOfLogTables, "parentId", "id", true);

		pickTag = "MeasureDefn";
		//stores the Measure Definition list
		Vector<String> listOfMeasureDefs = findElements(pickTag, listofLogCols, "parentId", "id", true);

		pickTag = "LogicalTable";
		//stores the LTS id
		Vector<String> tempListOfLogTables = findElements(pickTag, listOfLogTables, "id", "id", false);
		pickTag = "LogicalTableSource";
		//stores the LTS and PhysicalTable id list
		Vector<String> listOfLTSs = findElements(pickTag, tempListOfLogTables, "id", "id", true);
		tempListOfLogTables = null;

		pickTag = "PhysicalTable";
		//stores the PhysicalTable (Aliases included) list
		Vector<String> listOfPhysTables = findElements(pickTag, listOfLTSs, "id", "id", true);

		Vector<String> tempListOfSchemas = findElements(pickTag, listOfPhysTables, "id", "parentId", false);
		//stores the Schema list
		pickTag = "Schema";
		Vector<String> listOfSchemas = findElements(pickTag, tempListOfSchemas, "id", "id", true);

		Vector<String> tempListOfPhysCatalogs = findElements(pickTag, listOfSchemas, "id", "parentId", false);
		//stores the Schema list
		pickTag = "PhysicalCatalog";
		Vector<String> listOfPhysCatalogs = findElements(pickTag, tempListOfPhysCatalogs, "id", "id", true);
		tempListOfPhysCatalogs = null;

		Vector<String> tempListOfDBs = findElements(pickTag, listOfSchemas, "id", "parentId", false);
		Vector<String> tempListOfDBs2 = findElements(pickTag, listOfPhysCatalogs, "id", "parentId", false);
		for (String db : tempListOfDBs2)
			if (!tempListOfDBs.contains(db))
				tempListOfDBs.add(db);
		//NOT storing the DB list
		pickTag = "Database";
		Vector<String> listOfDatabases = findElements(pickTag, tempListOfDBs, "id", "id", true);
		tempListOfDBs2 = null;
		tempListOfDBs = null;

		pickTag = "PhysicalColumn";
		//stores the PhysicalTable list
		Vector<String> listOfPhysCols = findElements(pickTag, listOfPhysTables, "parentId", "id", true);

		pickTag = "PhysicalKey";
		//stores the PK list
		Vector<String> listOfPhysKeys = findElements(pickTag, listOfPhysTables, "parentId", "id", true);

		return doc;
	}
}
