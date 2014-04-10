package org.validator.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * XML Utilities
 * @author danielgalassi@gmail.com
 *
 */
public class XMLUtils {

	private static final Logger logger = LogManager.getLogger(XMLUtils.class.getName());

	/**
	 * Create an empty DOM document.
	 * @return a DOM document
	 */
	public static Document createDOMDocument() {
		DocumentBuilder docBuilder = null;
		Document xml = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xml = docBuilder.newDocument();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return xml;
	}

	/**
	 * Creates a DOM document from a file.
	 * @param xmlFile a file in XML format
	 * @return a DOM document
	 */
	public static Document loadDocument (File xmlFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xml = null;

		try {
			builder = factory.newDocumentBuilder();
			xml = builder.parse(xmlFile);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}

		return xml;
	}

	/**
	 * Creates a DOM document from an InputStream.
	 * @param xmlStream an InputStream to XML content
	 * @return a DOM document
	 */
	public static Document loadDocument (InputStream xmlStream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xml = null;

		try {
			builder = factory.newDocumentBuilder();
			xml = builder.parse(xmlStream);
		} catch(Exception e) {
			logger.error(e.getMessage());
		}

		return xml;
	}

	/**
	 * Persists a DOM document to a file.
	 * @param xml a DOM document
	 * @param filename name of target file
	 */
	public static void saveDocument (Document xml, String filename) {
		Source	source = new DOMSource(xml);
		File	targetFile = new File(filename);
		Result	result = new StreamResult(targetFile);
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		} catch (TransformerException
				| TransformerFactoryConfigurationError e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Applies a stylesheet to XML content.
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
	 * Applies a stylesheet (InputStream) to XML content.
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
			logger.error(configException.getMessage());
		}

		try {
			transformer.transform(xmlSource, result);
		} catch (TransformerException transfException) {
			logger.error(transfException.getMessage());
		}
	}

	/**
	 * Applies a stylesheet (InputStream) to XML content.
	 * This method is configured to set XSL parameters.
	 * @param xml a file in XML format 
	 * @param stylesheet InputStream to the stylesheet (XSL)
	 * @param resultLocation path where to save the resulting file
	 * @param params stylesheet parameters
	 */
	public static void applyStylesheet(File xml, InputStream stylesheet, String resultLocation, Map<String, String> params) {
		File				results = new File(resultLocation);
		Transformer			transformer = null;
		TransformerFactory	transformerFactory = null;

		Source	xmlSource = new javax.xml.transform.stream.StreamSource(xml);
		Source	xslSource = new javax.xml.transform.stream.StreamSource(stylesheet);
		Result	result = new javax.xml.transform.stream.StreamResult(results);

		transformerFactory = javax.xml.transform.TransformerFactory.newInstance();

		try {
			transformer = transformerFactory.newTransformer(xslSource);
			//adding XSL parameters such as ShowErrorsOnly, SelectedSubjectArea and SessionFolder
			if (params != null) {
				if (!params.isEmpty()) {
					for (Map.Entry<String, String> param : params.entrySet()) {
						logger.trace("Parameter set for XSL transformer: {}={}", param.getKey(), param.getValue());
						transformer.setParameter(param.getKey(), param.getValue());
					}
				}
			}
		} catch (TransformerConfigurationException configException) {
			logger.error(configException.getMessage());
		}

		try {
			transformer.transform(xmlSource, result);
		} catch (TransformerException transfException) {
			logger.error(transfException.getMessage());
		}
	}

	/**
	 * XMLReader factory.
	 * @return XMLReader object for future SAX parsing operations 
	 */
	public static XMLReader getXMLReader() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		//enabling the namespaces processing
		if(!factory.isNamespaceAware()) {
			factory.setNamespaceAware(true);
		}

		SAXParser parser = null;
		XMLReader reader = null;
		try {
			parser = factory.newSAXParser();
			reader = parser.getXMLReader();
		} catch (ParserConfigurationException | SAXException e) {
			logger.error(e.getMessage());
		}
		return reader;
	}
}
