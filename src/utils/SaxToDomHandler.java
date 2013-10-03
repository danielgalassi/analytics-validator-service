package utils;

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
	private Document xmlDoc;
	private Node currentNode;
	private boolean isInteresting = false;
	private boolean isReallyInteresting = false;
	private String processingNode;
	private String sTag;
	private Vector<String> vValue;
	private String sParam;
	private String sReturnParam;
	private Vector<String> vList;
	private boolean appendToDoc;

	public SaxToDomHandler(Document doc, String sTagNm, Vector<String> vObjValue, Vector<String> vLst, String sPar, String sRetParam, boolean append) {
		xmlDoc = doc;
		currentNode = xmlDoc.getFirstChild().getFirstChild();
		sTag = sTagNm;
		vValue = vObjValue;
		vList = vLst;
		sParam = sPar;
		sReturnParam = sRetParam;
		appendToDoc = append;
	}

	private void pickAttrib (Attributes attrs, String a) {
		String qname;
		String value;
		for (int i = 0; i<attrs.getLength(); i++) {
			qname  = attrs.getQName(i);
			value  = attrs.getValue(i);
			if (qname.equals(sReturnParam) && !vList.contains(value)) {
				//System.out.println(i + ")>>\t" + ns_uri + "\t" + processingNode + qname + "\t" + value + "\t" + a + "\t" + attrs.getLength());
				vList.add(value);
				break;
			}
		}
	}

	public void startElement(String uri, String name, String qName, Attributes attrs) {

		if (qName.equals(sTag)) {
			isInteresting = true;
			processingNode = sTag;
		}

		if (isInteresting) {
			// Create the element.
			Element elem = xmlDoc.createElementNS(uri, qName);
			// Add each attribute.
			for (int i = 0; i < attrs.getLength(); ++i) {
				String ns_uri = attrs.getURI(i);
				String qname = attrs.getQName(i);
				String value = attrs.getValue(i);
				Attr attr = xmlDoc.createAttributeNS(ns_uri, qname);
				attr.setValue(value);
				elem.setAttributeNodeNS(attr);

				//if this is such object and 
				//the name or id (sParam) matches one we need...
				if (qName.equals(processingNode) && 
						qname.equals(sParam) && 
						vValue.contains(value)) {
					isReallyInteresting = true;
					pickAttrib(attrs, "1");
				}

				//finds list of PresentationCatalog child nodes
				if (isReallyInteresting && 
						qName.equals("RefBusinessModel") && 
						processingNode.equals("PresentationCatalog"))
					pickAttrib(attrs, "2");

				//finds list of PresentationColumn child nodes
				if (isReallyInteresting && 
						qName.equals("RefLogicalColumn") && 
						processingNode.equals("PresentationColumn"))
					pickAttrib(attrs, "3");

				//finds list of LTS
				if (isReallyInteresting && 
						qName.equals("RefLogicalTableSource") && 
						processingNode.equals("LogicalTable"))
					pickAttrib(attrs, "4");

				//finds list of PhysicalTables
				if (isReallyInteresting && 
						qName.equals("RefPhysicalTable") && 
						processingNode.equals("LogicalTableSource"))
					pickAttrib(attrs, "5");

				//need to find a way to pickup aliases...
				if (isReallyInteresting && 
						qName.equals("RefPhysicalTable") && 
						processingNode.equals("PhysicalTable"))
					pickAttrib(attrs, "6");
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
			if (appendToDoc)
				currentNode = currentNode.getParentNode();
			if (qName.equals(processingNode)) {
				isInteresting = false;
				isReallyInteresting = false;
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		String str  = new String(ch, start, length);
		Text   text = xmlDoc.createTextNode(str);
		if (isReallyInteresting && appendToDoc)
			currentNode.appendChild(text);
	}

	// Add a new text node in the DOM tree, at the right place.
	public void ignorableWhitespace(char[] ch, int start, int length) {
		String str  = new String(ch, start, length);
		Text   text = xmlDoc.createTextNode(str);
		if (isReallyInteresting && appendToDoc)
			currentNode.appendChild(text);
	}

	// Add a new text PI in the DOM tree, at the right place.
	public void processingInstruction(String target, String data) {
		ProcessingInstruction pi = xmlDoc.createProcessingInstruction(target, data);
		if (isReallyInteresting && appendToDoc)
			currentNode.appendChild(pi);
	}
}
