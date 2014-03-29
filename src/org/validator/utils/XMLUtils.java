package org.validator.utils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * Create an empty DOM document
	 * @return a DOM document
	 */
	public static Document createDOMDocument() {
		DocumentBuilder docBuilder = null;
		Document xml = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xml = docBuilder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml;
	}

	/**
	 * Creates a DOM document from a file
	 * @param xmlFile a file in XML format
	 * @return a DOM document
	 */
	public static Document loadDocument (File xmlFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xml = null;

		try {
			builder = factory.newDocumentBuilder();
		} catch(Exception e) {
			e.printStackTrace();
		}

		try {
			xml = builder.parse(xmlFile);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Creates a DOM document from an InputStream
	 * @param xmlStream an InputStream to XML content
	 * @return a DOM document
	 */
	public static Document loadDocument (InputStream xmlStream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xml = null;

		try {
			builder = factory.newDocumentBuilder();
		} catch(Exception e) {
			e.printStackTrace();
		}

		try {
			xml = builder.parse(xmlStream);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Persists a DOM document to a file
	 * @param xml a DOM document
	 * @param filename name of target file
	 */
	public static void saveDOM2File(Document xml, String filename) {
		Source	source = new DOMSource(xml);
		File	targetFile = new File(filename);
		Result	result = new StreamResult(targetFile);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			System.out.println(e);
		} catch (TransformerException e) {
			System.out.println(e);
		}
	}

	/**
	 * Applies a stylesheet (file) to an XML document
	 * @param xmlFile
	 * @param stylesheet
	 * @param resultFile
	 */
	public static void xsl4Files(String xmlFile, String stylesheet, String resultFile){
		File	xml = new File(xmlFile);
		File	xsl = new File(stylesheet);
		File	resultingXML = new File(resultFile);

		Source				xmlSource = new javax.xml.transform.stream.StreamSource(xml);
		Source				xslSource = new javax.xml.transform.stream.StreamSource(xsl);
		Result				result = new javax.xml.transform.stream.StreamResult(resultingXML);
		Transformer			transformer = null;
		TransformerFactory	transformerFactory = javax.xml.transform.TransformerFactory.newInstance();

		try {
			transformer = transformerFactory.newTransformer(xslSource);
		} catch (TransformerConfigurationException configException) {
			configException.printStackTrace();
		}

		try {
			transformer.transform(xmlSource, result);
		} catch (TransformerException transfException) {
			transfException.printStackTrace();
		}
	}

	/**
	 * Applies a stylesheet (InputStream) to an XML document
	 * @param xmlFile
	 * @param stylesheet
	 * @param resultFile
	 */
	public static void xsl4Files(String xmlFile,
			InputStream stylesheet,
			String resultFile){
		File				xml = new File(xmlFile);
		File				resultingXML = new File(resultFile);
		Source				xmlSource = null;
		Source				xslSource = null;
		Result				result = null;
		Transformer			transformer = null;
		TransformerFactory	factory = null;

		xmlSource	= new javax.xml.transform.stream.StreamSource(xml);
		xslSource	= new javax.xml.transform.stream.StreamSource(stylesheet);
		result		= new javax.xml.transform.stream.StreamResult(resultingXML);
		factory		= javax.xml.transform.TransformerFactory.newInstance();

		try {
			transformer = factory.newTransformer(xslSource);
			transformer.setParameter("ShowErrorsOnly", "false");
		} catch (TransformerConfigurationException configException) {
			configException.printStackTrace();
		}

		try {
			transformer.transform(xmlSource, result);
		} catch (TransformerException transfException) {
			transfException.printStackTrace();
		}
	}

	/**
	 * Applies a stylesheet (InputStream) to an XML document.
	 * This method is configured to set XSL parameters
	 * @param xml a file in XML format 
	 * @param stylesheet transformation rules for XML files
	 * @param resultFile resulting file upon application of the stylesheet
	 * @param params stylesheet parameters
	 */
	public static void applyStylesheetWithParams(File xml, 
			InputStream stylesheet, 
			String resultFile, 
			HashMap<String, String> params) {
		File				results = new File(resultFile);
		Source				xmlSource = null;
		Source				xslSource = null;
		Transformer			transformer = null;
		TransformerFactory	transformerFactory = null;
		Result				result = null;

		xmlSource = new javax.xml.transform.stream.StreamSource(xml);
		xslSource = new javax.xml.transform.stream.StreamSource(stylesheet);
		result = new javax.xml.transform.stream.StreamResult(results);

		transformerFactory = javax.xml.transform.TransformerFactory.newInstance();

		try {
			transformer = transformerFactory.newTransformer(xslSource);
			if (params != null) {
				if (params.containsKey("ShowErrorsOnly"))
					transformer.setParameter("ShowErrorsOnly", params.get("ShowErrorsOnly"));
				if (params.containsKey("SelectedSubjectArea"))
					transformer.setParameter("SelectedSubjectArea", params.get("SelectedSubjectArea"));
				if (params.containsKey("SessionFolder"))
					transformer.setParameter("SessionFolder", params.get("SessionFolder"));
			}
		} catch (TransformerConfigurationException configException) {
			configException.printStackTrace();
		}

		try {
			transformer.transform(xmlSource, result);
		} catch (TransformerException transfException) {
			transfException.printStackTrace();
		}
	}

	/**
	 * Generates a catalog file with a list of tests
	 * @param resultRefs a Map with a <result file, elapsed time> entry for each test executed
	 * @param sessionDirectory the location where files for this session are stored
	 * @param startTime start time of the validation process (from the selection of the subject area) 
	 */
	public static void createIndexDocument (Map <String, Double> resultRefs, String sessionDirectory, long startTime) {
		Document index = createDOMDocument();
		Element root = index.createElement("index");
		Element node = null;

		for (Map.Entry <String, Double> ref : resultRefs.entrySet()) {
			node = index.createElement("results");
			node.setTextContent(ref.getKey());
			node.setAttribute("elapsedTime", ref.getValue().toString());
			root.appendChild(node);
		}

		root.setAttribute("totalElapsedTime", ""+((double) (System.currentTimeMillis() - startTime) / 1000));
		index.appendChild(root);
		saveDOM2File(index, sessionDirectory + "index.xml");
	}
}
