package org.validator.utils;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {

	private String			pickTag;
	private Vector<String>	listOfValues;
	private String			pickAttrib;

	public SaxHandler(String pickTag, String pickAttrib, Vector<String> listOfValues) {
		this.pickTag = pickTag;
		this.pickAttrib = pickAttrib;
		if (listOfValues == null) {
			this.listOfValues = new Vector<String> ();
		}
		else {
			this.listOfValues = listOfValues;
		}
	}

	public void startElement(String uri, String name, String qName, Attributes attrs) {
		if (qName.equals(pickTag)) {
			if (attrs.getIndex(pickAttrib) > -1) {
				listOfValues.add(attrs.getValue(pickAttrib));
			}
		}
	}
}
