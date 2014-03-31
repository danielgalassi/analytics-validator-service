/**
 * 
 */
package org.validator.metadata;

import java.io.IOException;
import java.io.InputStream;

import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Orchestration of XSL stylesheets to be used as tests
 * @author danielgalassi@gmail.com
 */
public class XSLTest {

	private String		name = "";
	private Document	test = null;
	private InputStream	script = null;

	/**
	 * Instantiates a test case
	 * @param script InputStream pointing to the stylesheet
	 */
	public XSLTest(InputStream script) {
		this.script = script;
		test = XMLUtils.loadDocument(script);
		setName();
	}

	/**
	 * Resets the script
	 */
	public void reset() {
		try {
			script.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter method for the name of the test
	 * @return name of the test
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter method for the name of the test
	 */
	private void setName() {
		setDefaultName();
		NodeList nameList = test.getElementsByTagName("TestName");
		if (nameList.getLength() == 1) {
			name = nameList.item(0).getTextContent();
		}
	}

	/**
	 * Setter method to initialise the name of the test case
	 */
	private void setDefaultName() {
		name = "Test" + System.currentTimeMillis();
	}

	/**
	 * Returns an InputStream pointing to the stylesheet
	 * @return script for the test case
	 */
	public InputStream toStream() {
		return script;
	}
}
