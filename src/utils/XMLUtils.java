package utils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML Utilities
 * @author danielgalassi@gmail.com
 *
 */
public class XMLUtils {

	public static void publishException(Exception errMsg){
		System.out.println("Error: " + errMsg.getClass() + "\tDescription: " + errMsg.getMessage());
	}

	/**
	 * Create an empty DOM document
	 * @return DOM document
	 */
	public static Document createDOMDocument() {
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Creates a DOM document from a file
	 * @param filename
	 * @return DOM document
	 */
	public static Document loadDocument (File filename) {
		DocumentBuilderFactory dBFXML = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBXML = null;
		Document docXML = null;

		try {
			docBXML = dBFXML.newDocumentBuilder();
		} catch(Exception e) {
			publishException(e);
		}

		try {
			docXML = docBXML.parse(filename);
		} catch(Exception e) {
			publishException(e);
		}

		return docXML;
	}

	/**
	 * Creates a DOM document from an InputStream
	 * @param InputStream
	 * @return DOM document
	 */
	public static Document loadDocument (InputStream inputsFile) {
		DocumentBuilderFactory dBFXML = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBXML = null;
		Document docXML = null;

		try {
			docBXML = dBFXML.newDocumentBuilder();
		} catch(Exception e) {
			publishException(e);
		}

		try {
			docXML = docBXML.parse(inputsFile);
		} catch(Exception e) {
			publishException(e);
		}

		return docXML;
	}

	/**
	 * Store a DOM document as a file
	 * @param doc
	 * @param filename
	 */
	public static void saveDocument2File(Document doc, String filename) {
		Source	source = new DOMSource(doc);
		File	targetFile = new File(filename);
		Result	result = new StreamResult(targetFile);
		try {
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			System.out.println(e);
		} catch (TransformerException e) {
			System.out.println(e);
		}
	}

	/**
	 * Applies a stylesheet (file) to an XML document
	 * @param xmlFile
	 * @param xslFile
	 * @param resFile
	 */
	public static void xsl4Files(String xmlFile,
			String xslFile,
			String resFile){
		File	xml = new File(xmlFile);
		File	xsl = new File(xslFile);
		File	resultingXML = new File(resFile);
		Source	xmlSource = new javax.xml.transform.stream.StreamSource(xml);
		Source	xslSource = new javax.xml.transform.stream.StreamSource(xsl);
		Result	result = new javax.xml.transform.stream.StreamResult(resultingXML);
		Transformer trans = null;
		TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();
		try {
			trans = transFact.newTransformer(xslSource);
		} catch (TransformerConfigurationException tcE) {
			System.out.println("3"); publishException(tcE);
		}
		try {
			trans.transform(xmlSource, result);
		} catch (TransformerException tE) {
			System.out.println("4"); publishException(tE);
		}
	}

	/**
	 * Applies a stylesheet (InputStream) to an XML document
	 * @param xmlFile
	 * @param xsl
	 * @param resFile
	 */
	public static void xsl4Files(String xmlFile,
			InputStream xsl,
			String resFile){
		File	xml = new File(xmlFile);
		File	resultingXML = new File(resFile);
		Source	xmlSource = null;
		Source	xslSource = null;
		Result	result = null;
		Transformer trans = null;
		TransformerFactory transFact = null;

		xmlSource = new javax.xml.transform.stream.StreamSource(xml);
		xslSource = new javax.xml.transform.stream.StreamSource(xsl);
		result = new javax.xml.transform.stream.StreamResult(resultingXML);
		transFact = javax.xml.transform.TransformerFactory.newInstance();
		try {
			trans = transFact.newTransformer(xslSource);
			trans.setParameter("ShowErrorsOnly", "false");
		} catch (TransformerConfigurationException tcE) {
			System.out.println("3");
			publishException(tcE);
		}
		try {
			trans.transform(xmlSource, result);
		} catch (TransformerException tE) {
			System.out.println("4");
			publishException(tE);
		}
	}

	/**
	 * Applies a Stylesheet (InputStream) to an XML document.
	 * This method is configured to set XSL parameters
	 * @param xmlFile
	 * @param xsl
	 * @param resFile
	 * @param params XSL parameters
	 */
	public static void xsl4Files(File xmlFile, 
			InputStream xsl, 
			String resFile, 
			HashMap<String, String> params) {
		File				results = new File(resFile);
		Source				xmlSource = null;
		Source				xslSource = null;
		Transformer			trans = null;
		TransformerFactory	transFact = null;
		Result				result = null;

		xmlSource = new javax.xml.transform.stream.StreamSource(xmlFile);
		xslSource = new javax.xml.transform.stream.StreamSource(xsl);
		result = new javax.xml.transform.stream.StreamResult(results);
		transFact = javax.xml.transform.TransformerFactory.newInstance();

		try {
			trans = transFact.newTransformer(xslSource);
			if (params != null) {
				if (params.containsKey("ShowErrorsOnly"))
					trans.setParameter("ShowErrorsOnly", params.get("ShowErrorsOnly"));
				if (params.containsKey("SelectedSubjectArea"))
					trans.setParameter("SelectedSubjectArea", params.get("SelectedSubjectArea"));
				if (params.containsKey("SessionFolder"))
					trans.setParameter("SessionFolder", params.get("SessionFolder"));
			}
		} catch (TransformerConfigurationException tcE) {
			System.out.println("3");
			publishException(tcE);
		}

		try {
			trans.transform(xmlSource, result);
		} catch (TransformerException tE) {
			System.out.println("4");
			publishException(tE);
		}
	}

	/**
	 * Generates a catalog file with a list of tests
	 * @param testList
	 * @param elapsedTime
	 * @param targetDir
	 * @param startTime
	 */
	public static void createIndexDocument (
			Vector<String> testList, 
			Vector<Double> elapsedTime, 
			String targetDir, 
			long startTime) {
		Document docIndex = createDOMDocument();
		Element r = docIndex.createElement("index");

		for (int i=0; i<testList.size(); i++) {
			Element e = docIndex.createElement("results");
			e.setTextContent(testList.get(i));
			e.setAttribute("elapsedTime", elapsedTime.get(i).toString());
			r.appendChild(e);
		}

		r.setAttribute("totalElapsedTime", ""+((double) (System.currentTimeMillis() - startTime) / 1000));
		docIndex.appendChild(r);
		saveDocument2File(docIndex, targetDir + "index.xml");
	}
}
