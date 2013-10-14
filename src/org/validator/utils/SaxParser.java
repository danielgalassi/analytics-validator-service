package org.validator.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Utility class to quickly traverse a (metadata) file and return a list of values
 * @author danielgalassi@gmail.com
 *
 */
public class SaxParser
{
	private XMLReader		reader;
	private InputSource		input;
	private SaxHandler		handlers;
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
		boolean			metadataOK = (metadata != null && metadata.exists() && metadata.canRead());
		boolean			tagSet = (pickTag != null && !pickTag.equals(""));
		boolean			attributeSet = (pickAttribute != null || !pickAttribute.equals(""));
		Vector<String>	listOfValues = new Vector<String> ();

		if (tagSet && attributeSet && metadataOK) {
			input = FileUtils.getStream(metadata);
			reader = FileUtils.getXMLReader();

			if (input != null) {
				handlers = new SaxHandler(pickTag, pickAttribute, listOfValues);
				reader.setContentHandler(handlers);
				reader.setErrorHandler(handlers);

				try {
					reader.parse(input);
				} catch (IOException | SAXException e) {
					e.printStackTrace();
				}
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
		return listOfValues;
	}
}
