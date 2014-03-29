/**
 * 
 */
package org.validator.engine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.validator.metadata.Repository;
import org.validator.metadata.XSLTest;
import org.validator.utils.FileUtils;
import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author danielgalassi@gmail.com
 *
 */
public class ValidatorEngine {

	String			resultCatalogLocation;
	Repository		repository = null;
	Vector<XSLTest>	testSuite = null;
	long			serviceStartTime;

	public ValidatorEngine(Repository repository, long serviceStartTime) {
		this.repository = repository;
		this.serviceStartTime = serviceStartTime;
	}

	public void addTest(InputStream testStream) {
		if (testSuite == null) {
			testSuite = new Vector<XSLTest>();
		}
		testSuite.add(new XSLTest(testStream));
	}

	public void run() {
		Map <String, Double>	resultRef = null;
		long					startTimeInMs;

		if (testSuite.size() > 0) {
			resultRef	= new HashMap<String, Double>();
		}

		for (XSLTest test : testSuite) {
			//stopwatch starts
			startTimeInMs = System.currentTimeMillis();

			test.setResultFile(resultCatalogLocation);

			//executing test, generating the results file
			test.reset();
			XMLUtils.applyStylesheetWithParams(repository.toFile(), test.toStream(), test.getResultFile(), null);

			//stopwatch ends and test results filename is added to index list
			resultRef.put(test.getResultFile(), (double) (System.currentTimeMillis() - startTimeInMs) / 1000);
		}

		//XMLUtils.createIndexDocument(resultRef, resultCatalogLocation, serviceStartTime);
		createIndexDocument(resultRef);
	}

	public boolean ready() {
		boolean testSuiteReady = false;
		if (testSuite != null)
			if (testSuite.size() > 0)
				testSuiteReady = true;
		return (repository != null && testSuiteReady);
	}

	public void setResultCatalogLocation(String resultCatalogLocation) {
		this.resultCatalogLocation = resultCatalogLocation;
		FileUtils.setupWorkDirectory(resultCatalogLocation);		
	}

	/**
	 * Generates a catalog file with a list of tests
	 * @param resultRefs a Map with a <result file, elapsed time> entry for each test executed
	 */
	private void createIndexDocument (Map <String, Double> resultRefs) {
		Document index = XMLUtils.createDOMDocument();
		Element root = index.createElement("index");
		Element node = null;

		for (Map.Entry <String, Double> ref : resultRefs.entrySet()) {
			node = index.createElement("results");
			node.setTextContent(ref.getKey());
			node.setAttribute("elapsedTime", ref.getValue().toString());
			root.appendChild(node);
		}

		root.setAttribute("totalElapsedTime", ""+((double) (System.currentTimeMillis() - serviceStartTime) / 1000));
		index.appendChild(root);
		XMLUtils.saveDocument(index, resultCatalogLocation + "index.xml");
	}
}
