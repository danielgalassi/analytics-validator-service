/**
 * 
 */
package org.validator.utils;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * 
 * @author danielgalassi@gmail.com
 *
 */
public class TagSelector {
	private String			pickTag = null;
	private String			pickAttribute = null;
	private File			metadata;
	private String			workDir;

	public void setTag (String pickTag) {
		this.pickTag = pickTag;
	}

	public void setAttribute (String pickAttribute) {
		this.pickAttribute = pickAttribute;
	}

	public void setWorkDir (String workDir) {
		this.workDir = workDir;
	}

	public void setMetadata (String metadata) {
		this.metadata = new File (workDir + metadata);
	}

	public Vector<String> getListOfValues() {
		long x = System.currentTimeMillis();
		boolean			isRepositoryOK = (metadata != null && metadata.exists() && metadata.canRead());
		boolean			isTagSet = (pickTag != null && !pickTag.equals(""));
		boolean			isAttributeSet = (pickAttribute != null || !pickAttribute.equals(""));
		Vector<String>	listOfValues = new Vector<String> ();

		if (isTagSet && isAttributeSet && isRepositoryOK) {
			Document repository = XMLUtils.loadDocument(metadata);
			XPathFactory xpathfactory = XPathFactory.newInstance();
			XPath xpath = xpathfactory.newXPath();
			
			try {
				XPathExpression expr = xpath.compile("//"+pickTag);
				Object result = expr.evaluate(repository, XPathConstants.NODESET);
				NodeList subjectAreas = (NodeList) result;

				for (int i=0; i < subjectAreas.getLength(); i++) {
					NamedNodeMap attrs = subjectAreas.item(i).getAttributes();
					listOfValues.add(attrs.getNamedItem("name").getNodeValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		//default option if subject areas cannot be found
		if (listOfValues.size() == 0) {
			listOfValues.add("No subject areas found");
		}
		else {
			//otherwise, sort and display instruction line at the top
			Collections.sort(listOfValues);
			listOfValues.add(0, "Browse Subject Areas");
		}
		System.out.println("Elapsed Time = " + ((System.currentTimeMillis() - x) / 1000));
		return listOfValues;
	}
}
