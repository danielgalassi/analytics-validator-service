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

/***
 * Parsing SAX events into a DOM tree
 * @author danielgalassi
 *
 */
class SaxToDomHandler extends DefaultHandler
{
	private Document						doc;
	private Node						   node;
	private String							tag;
	private String				 processingNode;
	private Vector<String>		   listOfValues;
	private String				 matchingAttrib;
	private String				returningAttrib;
	private Vector<String>		 foundTokenList;
	private boolean					appendToDoc;
	private boolean				  isInteresting = false;
	private boolean			isReallyInteresting = false;

	public SaxToDomHandler(
			Document		doc, 
			String			pickTag, 
			Vector<String>	listOfValues, 
			Vector<String>	vLst, 
			String			matchingAttrib, 
			String			returningAttrib, 
			boolean			appendToDoc) {
		this.doc = doc;
		node = doc.getFirstChild().getFirstChild();
		this.tag = pickTag;
		this.listOfValues = listOfValues;
		foundTokenList = vLst;
		this.matchingAttrib = matchingAttrib;
		this.returningAttrib = returningAttrib;
		this.appendToDoc = appendToDoc;
	}

	private void pickAttrib (Attributes attrs) {
		if (attrs.getIndex(returningAttrib) > -1) {
			String value = attrs.getValue(returningAttrib);

			if (!foundTokenList.contains(value)) {
				foundTokenList.add(attrs.getValue(returningAttrib));
			}
		}
	}

	public void startElement(String uri, String name, String qName, Attributes attrs) {

		if (qName.equals(tag)) {
			isInteresting = true;
			processingNode = tag;
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

				//if this top-level node is required
				//the name or id (sParam) matches one we need...
				boolean isTag = qName.equals(processingNode);
				boolean isAttrib = qname.equals(matchingAttrib);
				boolean hasValue = listOfValues.contains(value);
				if (isTag && isAttrib && hasValue) {
					isReallyInteresting = true;
					pickAttrib(attrs);
				}

				boolean isRefBizModel	= qName.equals("RefBusinessModel");
				boolean isRefLglColumn	= qName.equals("RefLogicalColumn");
				boolean isRefLTS		= qName.equals("RefLogicalTableSource");
				boolean isRefPhysTable	= qName.equals("RefPhysicalTable");
				if (isReallyInteresting && 
						(isRefBizModel || isRefLglColumn || isRefLTS || isRefPhysTable)) {
					pickAttrib(attrs);
				}
			}
			// Actually add it in the tree, and adjust the right place.
			if (isReallyInteresting && appendToDoc) {
				node.appendChild(elem);
				node = elem;
			}
		}
	}

	public void endElement(String uri, String name, String qName) {
		if (isReallyInteresting) {
			if (appendToDoc) {
				node = node.getParentNode();
			}
			if (qName.equals(processingNode)) {
				isInteresting = false;
				isReallyInteresting = false;
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (isReallyInteresting && appendToDoc) {
			String	str  = new String(ch, start, length);
			Text	text = doc.createTextNode(str);
			node.appendChild(text);
		}
	}

	//Add a new text node in the DOM tree, at the right place.
	public void ignorableWhitespace(char[] ch, int start, int length) {
		if (isReallyInteresting && appendToDoc) {
			String	str  = new String(ch, start, length);
			Text	text = doc.createTextNode(str);
			node.appendChild(text);
		}
	}

	//Add a new text PI in the DOM tree, at the right place.
	public void processingInstruction(String target, String data) {
		if (isReallyInteresting && appendToDoc) {
			ProcessingInstruction pi = doc.createProcessingInstruction(target, data);
			node.appendChild(pi);
		}
	}
}
