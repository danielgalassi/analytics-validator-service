package org.validator.XMLEngine;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/***
 * Parsing SAX events into a DOM tree
 * @author danielgalassi@gmail.com
 *
 */
class XMLSplitter extends DefaultHandler
{
	private Document		doc;
	private Node			currentNode;
	private boolean			isInteresting = false;
	private String			processingNode;
	private String			pickTag;

	public XMLSplitter(
			Document		doc, 
			String			pickTag) {

		this.doc = doc;
		currentNode = doc.getFirstChild().getFirstChild();
		this.pickTag = pickTag;
	}

	public void startElement(String uri, String name, String qName, Attributes attrs) {

		if (qName.equals(pickTag)) {
			isInteresting = true;
			processingNode = pickTag;
		}

		//processing top-level node and children
		if (isInteresting) {

			//Creates the element
			Element elem = doc.createElementNS(uri, qName);
			//Adds each attribute
			for (int i = 0; i < attrs.getLength(); ++i) {
				String ns_uri = attrs.getURI(i);
				String qname = attrs.getQName(i);
				String value = attrs.getValue(i);
				Attr attr = doc.createAttributeNS(ns_uri, qname);
				attr.setValue(value);
				elem.setAttributeNodeNS(attr);
			}

			// Actually add it in the tree, and adjust the right place.
			if (isInteresting) {
				currentNode.appendChild(elem);
				currentNode = elem;
			}
		}
	}

	public void endElement(String uri, String name, String qName) {
		if (isInteresting) {
			currentNode = currentNode.getParentNode();
			if (qName.equals(processingNode)) {
				isInteresting = false;
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (isInteresting) {
			String	str  = new String(ch, start, length);
			Text	text = doc.createTextNode(str);
			currentNode.appendChild(text);
		}
	}

	//Add a new text node in the DOM tree, at the right place.
	public void ignorableWhitespace(char[] ch, int start, int length) {
		if (isInteresting) {
			String	str  = new String(ch, start, length);
			Text	text = doc.createTextNode(str);
			currentNode.appendChild(text);
		}
	}

	//Add a new text PI in the DOM tree, at the right place.
	public void processingInstruction(String target, String data) {
		if (isInteresting) {
			ProcessingInstruction pi = doc.createProcessingInstruction(target, data);
			currentNode.appendChild(pi);
		}
	}
}
