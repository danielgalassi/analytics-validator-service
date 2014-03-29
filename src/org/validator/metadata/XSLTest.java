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

	public XSLTest(InputStream script) {
		this.script = script;
		test = XMLUtils.loadDocument(script);
		setName();
	}

	public void reset() {
		try {
			script.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	private void setName() {
		setDefaultName();
		NodeList nameList = test.getElementsByTagName("TestName");
		if (nameList.getLength() == 1) {
			name = nameList.item(0).getTextContent();
		}
	}

	private void setDefaultName() {
		name = "Test" + System.currentTimeMillis();
	}

	public InputStream toStream() {
		return script;
	}
}
