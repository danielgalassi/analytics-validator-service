/**
 * 
 */
package org.validator.services.metadata;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Orchestration of XSL stylesheets to be used as tests.
 * @author danielgalassi@gmail.com
 */
public class XSLTest implements Test{

	private static final Logger logger = LogManager.getLogger(XSLTest.class.getName());
	/**
	 * Name of the test case.
	 */
	private String		name = "";
	/**
	 * Another representation (<code>InputStream</code>) of the Stylesheet (XSL).
	 */
	private InputStream	script = null;

	/**
	 * Instantiates a test case.
	 * @param script InputStream pointing to the stylesheet
	 */
	public XSLTest(InputStream script) {
		this.script = script;
		setName();
		logger.info("Loading test {}", name);
	}

	/**
	 * Resets the script.
	 */
	public void reset() {
		try {
			script.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter method for the name of the test.
	 * @return name of the test
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter method for the name of the test.
	 */
	private void setName() {
		setDefaultName();
		Document test = XMLUtils.loadDocument(script);
		NodeList nameList = test.getElementsByTagName("TestName");
		if (nameList.getLength() == 1) {
			name = nameList.item(0).getTextContent();
		}
		reset();
	}

	/**
	 * Setter method to initialise the name of the test case.
	 */
	private void setDefaultName() {
		name = "Test" + System.currentTimeMillis();
	}

	/**
	 * Validates metadata, executing the test script.
	 */
	public void assertMetadata(Repository repository, String result) {
		XMLUtils.applyStylesheet(repository.toFile(), script, result, null);
	}

	/**
	 * Returns an InputStream pointing to the stylesheet.
	 * @return script for the test case
	 */
	public InputStream toStream() {
		return script;
	}
}
