package org.validator.utils;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class SaxToDomHandler extends DefaultHandler
{
	private Document		doc;
	private Node			currentNode;
	private boolean			isInteresting = false;
	private boolean			isReallyInteresting = false;
	private String			processingNode;
	private String			pickTag;
	private Vector<String>	listOfValues;
	private String			matchingAttrib;
	private String			returningAttrib;
	private Vector<String>	foundTokensList;
	private boolean			appendToDoc;

	public SaxToDomHandler(
			Document		doc, 
			String			pickTag, 
			Vector<String>	listOfValues, 
			Vector<String>	vLst, 
			String			matchingAttrib, 
			String			returningAttrib, 
			boolean			appendToDoc) {
		this.doc = doc;
		currentNode = doc.getFirstChild().getFirstChild();
		this.pickTag = pickTag;
		this.listOfValues = listOfValues;
		foundTokensList = vLst;
		this.matchingAttrib = matchingAttrib;
		this.returningAttrib = returningAttrib;
		this.appendToDoc = appendToDoc;
	}

	private void pickAttrib (Attributes attrs) {
		String value;
		if (attrs.getIndex(returningAttrib) > -1) {
			value = attrs.getValue(returningAttrib);
			if (!foundTokensList.contains(value)) {
				foundTokensList.add(attrs.getValue(returningAttrib));
			}
		}
	}

	public void startElement(String uri, String name, String qName, Attributes attrs) {

		if (qName.equals(pickTag)) {
			isInteresting = true;
			processingNode = pickTag;
		}

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

				//if this is such object and 
				//the name or id (sParam) matches one we need...
				if (qName.equals(processingNode) && 
						qname.equals(matchingAttrib) && 
						listOfValues.contains(value)) {
					isReallyInteresting = true;
					pickAttrib(attrs);
				}

				//finds list of PresentationCatalog child nodes
				if (isReallyInteresting && 
						qName.equals("RefBusinessModel") && 
						processingNode.equals("PresentationCatalog")) {
					pickAttrib(attrs);
				}

				//finds list of PresentationColumn child nodes
				if (isReallyInteresting && 
						qName.equals("RefLogicalColumn") && 
						processingNode.equals("PresentationColumn")) {
					pickAttrib(attrs);
				}

				//finds list of LTS
				if (isReallyInteresting && 
						qName.equals("RefLogicalTableSource") && 
						processingNode.equals("LogicalTable")) {
					pickAttrib(attrs);
				}

				//finds list of PhysicalTables
				if (isReallyInteresting && 
						qName.equals("RefPhysicalTable") && 
						processingNode.equals("LogicalTableSource")) {
					pickAttrib(attrs);
				}

				//need to find a way to pickup aliases...
				if (isReallyInteresting && 
						qName.equals("RefPhysicalTable") && 
						processingNode.equals("PhysicalTable")) {
					pickAttrib(attrs);
				}
			}
			// Actually add it in the tree, and adjust the right place.
			if (isReallyInteresting && appendToDoc) {
				currentNode.appendChild(elem);
				currentNode = elem;
			}
		}
	}

	public void endElement(String uri, String name, String qName) {
		if (isReallyInteresting) {
			if (appendToDoc) {
				currentNode = currentNode.getParentNode();
			}
			if (qName.equals(processingNode)) {
				isInteresting = false;
				isReallyInteresting = false;
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		String	str  = new String(ch, start, length);
		Text	text = doc.createTextNode(str);
		if (isReallyInteresting && appendToDoc) {
			currentNode.appendChild(text);
		}
	}

	//Add a new text node in the DOM tree, at the right place.
	public void ignorableWhitespace(char[] ch, int start, int length) {
		String	str  = new String(ch, start, length);
		Text	text = doc.createTextNode(str);
		if (isReallyInteresting && appendToDoc) {
			currentNode.appendChild(text);
		}
	}

	//Add a new text PI in the DOM tree, at the right place.
	public void processingInstruction(String target, String data) {
		ProcessingInstruction pi = doc.createProcessingInstruction(target, data);
		if (isReallyInteresting && appendToDoc) {
			currentNode.appendChild(pi);
		}
	}
}
