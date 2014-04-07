package org.validator.utils;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This handler processes all events for a selected tag and retrieves the value of a selected attribute.
 * @author danielgalassi@gmail.com
 *
 */
public class SaxHandler extends DefaultHandler {

	/**
	 * Name of the XML tag to be evaluated, its value is then added to the result set.
	 */
	private String			tag;
	/**
	 * Tag to be evaluated.
	 */
	private String			attribute;
	/**
	 * Set of results (attribute values).
	 */
	private Vector<String>	listOfValues;

	/**
	 * Constructor for the handler.
	 * @param pickTag name of the tag to be evaluated
	 * @param pickAttrib name of the attribute to be picked up
	 * @param listOfValues set of results
	 */
	public SaxHandler(String pickTag, String pickAttrib, Vector<String> listOfValues) {
		this.tag = pickTag;
		this.attribute = pickAttrib;
		this.listOfValues = listOfValues;
	}

	/**
	 * This event finds the tag I need to evaluate.
	 */
	public void startElement(String uri, String name, String qName, Attributes attrs) {
		if (qName.equals(tag)) {
			if (attrs.getIndex(attribute) > -1) {
				listOfValues.add(attrs.getValue(attribute));
			}
		}
	}
}
