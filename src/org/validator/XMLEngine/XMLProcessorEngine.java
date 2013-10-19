package org.validator.XMLEngine;

import java.io.File;
import java.io.IOException;

import org.validator.utils.FileUtils;
import org.validator.utils.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/***
 * 
 * @author danielgalassi@gmail.com
 *
 */
public class XMLProcessorEngine implements Runnable {

	private String			tag; 
	private Document		doc;
	private File			metadata;

	public XMLProcessorEngine(String pickTag, File metadata) {
		this.tag = pickTag;
		this.metadata = metadata;
		try {
			doc = XMLUtils.createDOMDocument();
			Element repoTag = doc.createElement("Repository");
			Element declTag = doc.createElement("DECLARE");
			repoTag.appendChild(declTag);
			doc.appendChild(repoTag);
		}
		catch (DOMException e) {
			e.printStackTrace();
		}

		System.out.println(pickTag + " created.");
	}

	@Override
	public void run() {
		InputSource	metadataStream = FileUtils.getStream(metadata);
		XMLSplitter	handlers = new XMLSplitter(doc, tag);
		XMLReader	reader = FileUtils.getXMLReader();

		reader.setContentHandler(handlers);
		//reader.setErrorHandler(handlers);

		try {
			reader.parse(metadataStream);
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
		XMLUtils.saveDocument2File(doc, "c:\\data\\workspace\\MyThreadExample\\" + tag + ".xml");
	}
}
