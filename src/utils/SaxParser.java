package utils;

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
	private Vector<String>	listOfValues;
	private String			pickTag = "none";
	private String			pickAttribute = "id";
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
		listOfValues = new Vector<String> ();
		if (metadata == null || 
				workDir == null || 
				pickTag == null || 
				pickAttribute == null) {
			listOfValues.add("No subject areas found");
			return listOfValues;
		}
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

		Collections.sort(listOfValues);
		listOfValues.add(0, "Browse Subject Areas");
		return listOfValues;
	}
}
