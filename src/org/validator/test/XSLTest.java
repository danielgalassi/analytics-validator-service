/**
 * 
 */
package org.validator.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author danielgalassi@gmail.com
 *
 */
public class XSLTest implements Test {

	private String		name = "";
	private String		resultFile = "";
	private Document	test = null;
	private InputStream	script = null;

	public XSLTest(InputStream script, String resultsDir) {
		this.script = script;
		test = XMLUtils.loadDocument(script);
		setName();
		setResultFile(resultsDir);
	}

	@Override
	public void execute(File rpd) {
		reset();
		XMLUtils.xsl4Files(rpd, script, resultFile, null);
	}

	@Override
	public void reset() {
		try {
			script.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
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

	private void setResultFile(String resultsDir) {
		resultFile = resultsDir + name + ".xml";
	}

	@Override
	public String getResultFile() {
		return resultFile;
	}
}
