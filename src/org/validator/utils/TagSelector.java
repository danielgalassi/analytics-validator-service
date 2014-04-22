package org.validator.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Utility class to quickly traverse a (metadata) file and return a list of values.
 * While I experimented with XPath, SAX seems to be a (~66%) quicker way of retrieving a value from XUDML.
 * @author danielgalassi@gmail.com
 *
 */
public class TagSelector {

	private static final Logger logger = LogManager.getLogger(TagSelector.class.getName());

	private XMLReader		reader;
	private InputSource		input;
	private SaxHandler		handlers;
	private String			tag = "PresentationCatalog";
	private String			attribute = "name";
	private File			metadata;
	private String			workDir;
	private Vector<String>	listOfValues = new Vector<String> ();

	public void setTag (String tag) {
		this.tag = tag;
	}

	public void setAttribute (String attribute) {
		this.attribute = attribute;
	}

	public void setWorkDir (String workDir) {
		this.workDir = workDir;
	}

	public void setMetadata (String metadata) {
		this.metadata = new File (workDir + metadata);
	}

	/**
	 * Parses metadata file with <code>SaxHandler</code>
	 */
	private void getValuesFromMetadata() {
		input = FileUtils.getStream(metadata);
		reader = XMLUtils.getXMLReader();

		if (input != null) {
			handlers = new SaxHandler(tag, attribute, listOfValues);
			reader.setContentHandler(handlers);
			reader.setErrorHandler(handlers);

			try {
				reader.parse(input);
			} catch (IOException | SAXException e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * Sorts and adds default values to list of attribute values.
	 */
	private void arrangeValues() {
		if (listOfValues.isEmpty()) {
			//default option if subject areas cannot be found
			listOfValues.add("No subject areas found");
		}
		else {
			//otherwise, sort and display instruction line at the top
			Collections.sort(listOfValues);
			listOfValues.add(0, "Browse Subject Areas");
		}
	}

	/**
	 * Retrieves the requested attribute from all found nodes matching a tag name.
	 * @return a set of attribute values
	 */
	public Vector<String> getListOfValues() {
		boolean	isRepositoryOK = (metadata != null && metadata.exists() && metadata.canRead());
		boolean	isTagSet = !tag.equals("");
		boolean	isAttributeSet = !attribute.equals("");

		if (isTagSet && isAttributeSet && isRepositoryOK) {
			getValuesFromMetadata();
		}
		arrangeValues();

		return listOfValues;
	}
}
