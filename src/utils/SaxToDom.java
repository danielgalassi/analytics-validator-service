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
	private XMLReader   myReader;
	private InputSource myInput;
	private SaxToDomHandler handlers = null;
	private Document doc;

	public SaxToDom(Document docXML, XMLReader reader, InputSource input) {
		myReader = reader;
		myInput  = input;
		doc = docXML;
	}

	public Vector<String> findElements(String sTag, Vector<String> valueList, String sRelAttrib, String sRetAttrib, boolean append) {
		myInput = FileUtils.getIS(new File("c:\\data\\workspace\\analytics-validator-service\\sampleCases\\output_20130924.xml"));
		Vector<String> foundIdList = new Vector<String>();
		handlers = new SaxToDomHandler(doc, sTag, valueList, foundIdList, sRelAttrib, sRetAttrib, append);
		myReader.setContentHandler(handlers);
		myReader.setErrorHandler(handlers);

		try {
			myReader.parse(myInput);
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}

		//System.out.println(sTag + ": " + foundIdList.size());
		return foundIdList;
	}

	public Document makeDom(String sTag, Vector<String> valueList) {
		//System.out.println("makeDom()");
		try {
			if (doc == null) {
				//System.out.println("Creating the DOM document");
				doc = XMLUtils.createDOMDocument();
				Element repo = doc.createElement("Repository");
				Element DECL = doc.createElement("DECLARE");
				repo.appendChild(DECL);
				doc.appendChild(repo);
			}

			//stores SA id and BM id
			Vector<String> vSA = findElements(sTag, valueList, "name", "id", true);

			sTag = "PresentationTable";
			//stores PresentationTable id
			Vector<String> vPT = findElements(sTag, vSA, "parentId", "id", true);

			sTag = "PresentationColumn";
			//stores PresentationColumn id and referenced logical column id
			Vector<String> vPC = findElements(sTag, vPT, "parentId", "id", true);

			sTag = "BusinessModel";
			//stores the BM id list
			Vector<String> vBM = findElements(sTag, vSA, "id", "id", true);

			sTag = "LogicalColumn";
			//stores the LogicalColumn parentId list 
			Vector<String> vLC = findElements(sTag, vPC, "id", "parentId", false);

			sTag = "LogicalTable";
			//stores the LogicalTable list
			Vector<String> vLT = findElements(sTag, vLC, "id", "id", true);

			sTag = "LogicalColumn";
			//stores the LogicalColumn list
			vLC = findElements(sTag, vLT, "parentId", "id", true);

			sTag = "MeasureDefn";
			//stores the Measure Definition list
			Vector<String> vMD = findElements(sTag, vLC, "parentId", "id", true);

			sTag = "LogicalTable";
			//stores the LTS id
			Vector<String> vLTStemp = findElements(sTag, vLT, "id", "id", false);
			sTag = "LogicalTableSource";
			//stores the LTS and PhysicalTable id list
			Vector<String> vLTS = findElements(sTag, vLTStemp, "id", "id", true);

			sTag = "PhysicalTable";
			//stores the PhysicalTable (Aliases included) list
			Vector<String> vPhT = findElements(sTag, vLTS, "id", "id", false);

			sTag = "PhysicalColumn";
			//stores the PhysicalTable list
			Vector<String> vPhC = findElements(sTag, vPhT, "parentId", "id", true);

			sTag = "PhysicalKey";
			//stores the PK list
			Vector<String> vPK = findElements(sTag, vPhT, "parentId", "id", true);
		}
		// For the catch handlers below, use your usual logging facilities.
		catch (DOMException e) {
			e.printStackTrace();
		}
		return doc;
	}
}
//1st pass: PresentationCatalog + "name" + name of the Subject Area
//2nd pass: PresentationTable + "id" + id of the PresentationCatalog (parent)
//3rd pass: PresentationColumn + "id" + id of the presentation tables
