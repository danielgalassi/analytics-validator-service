package utils;

import java.io.File;
import java.io.IOException;
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

	public SaxParser(File metadata, String pickTag, String pickAttribute) {

		input = FileUtils.getIS(metadata);
		reader = FileUtils.getXMLReader();

		handlers = new SaxHandler(pickTag, pickAttribute, listOfValues);
		reader.setContentHandler(handlers);
		reader.setErrorHandler(handlers);

		try {
			reader.parse(input);
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<String> getValues() {
		System.out.println(listOfValues.size());
		return listOfValues;
	}
}
