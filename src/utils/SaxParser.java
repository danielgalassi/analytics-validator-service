package utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SaxParser
{
	private XMLReader		reader;
	private InputSource		input;
	private SaxHandler		handlers;
	private Vector<String>	listOfValues;
	private String			pickTag = "none";
	private String			pickAttribute = "id";
	private File			metadata;

	public void setTag (String pickTag) {
		this.pickTag = pickTag;
	}

	public void setAttribute (String pickAttribute) {
		this.pickAttribute = pickAttribute;
	}
	
	public void setMetadata (File metadata) {
		this.metadata = metadata;
	}

	public Vector<String> getListOfValues() {
		input = FileUtils.getIS(metadata);
		reader = FileUtils.getXMLReader();
		listOfValues = new Vector<String> ();
		
		handlers = new SaxHandler(pickTag, pickAttribute, listOfValues);
		reader.setContentHandler(handlers);
		reader.setErrorHandler(handlers);

		try {
			reader.parse(input);
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}

		Collections.sort(listOfValues);
		listOfValues.add(0, "Browse Subject Areas");
		return listOfValues;
	}
}
