package org.validator.utils;

import java.io.File;
import java.io.InputStream;
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
	public static void saveDocument (Document xml, String filename) {
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
	 * Applies a stylesheet to XML content
	 * @param xmlLocation path to XML file
	 * @param stylesheet path to stylesheet (XSL) file
	 * @param resultLocation path where to save the resulting file
	 */
	public static void applyStylesheet(String xmlLocation, String stylesheet, String resultLocation){
		File	xml = new File(xmlLocation);
		File	xsl = new File(stylesheet);
		File	resultingXML = new File(resultLocation);

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
	 * Applies a stylesheet (InputStream) to XML content
	 * @param xmlLocation path to XML file
	 * @param stylesheet InputStream to the stylesheet (XSL)
	 * @param resultLocation path where to save the resulting file
	 */
	public static void applyStylesheet(String xmlLocation, InputStream stylesheet, String resultLocation){
		File				xml = new File(xmlLocation);
		File				resultingXML = new File(resultLocation);
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
	 * Applies a stylesheet (InputStream) to XML content
	 * This method is configured to set XSL parameters
	 * @param xml a file in XML format 
	 * @param stylesheet InputStream to the stylesheet (XSL)
	 * @param resultLocation path where to save the resulting file
	 * @param params stylesheet parameters
	 */
	public static void applyStylesheet(File xml, InputStream stylesheet, String resultLocation, Map<String, String> params) {
		File				results = new File(resultLocation);
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
			//adding XSL parameters such as ShowErrorsOnly, SelectedSubjectArea and SessionFolder
			if (params != null) {
				if (!params.isEmpty()) {
					for (Map.Entry<String, String> param : params.entrySet()) {
						transformer.setParameter(param.getKey(), param.getValue());
					}
				}
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
}
