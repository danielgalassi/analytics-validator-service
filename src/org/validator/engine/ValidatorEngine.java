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
 * The validator engine orchestrates and controls the execution of the test suite.
 * @author danielgalassi@gmail.com
 *
 */
public class ValidatorEngine {

	String			resultCatalogLocation;
	Repository		repository = null;
	Vector<XSLTest>	testSuite = null;
	long			serviceStartTime = 0;

	/**
	 * Validator Engine constructor. Repository, Test Suite and Session Directory are set using independent methods.
	 */
	public ValidatorEngine() {
		System.out.println("Initialising Validator Engine");
	}

	/**
	 * 
	 * @param repository
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	/**
	 * Sets the stopwatch to measure the time taken to validate the repository.
	 * @param serviceStartTime
	 */
	public void setStartTime(long serviceStartTime) {
		this.serviceStartTime = serviceStartTime;
	}

	/**
	 * Appends one test script to internal test suite
	 * @param testStream
	 */
	public void addTest(InputStream testStream) {
		if (testSuite == null) {
			testSuite = new Vector<XSLTest>();
		}
		testSuite.add(new XSLTest(testStream));
	}

	/**
	 * Executes the validation of the repository metadata using all test scripts loaded.
	 * Upon completion, an index document is created. Each entry in this document points to a result file.
	 */
	public void run() {
		Map <String, Double>	resultRef = null;
		long					startTimeInMs;

		if (testSuite.size() > 0) {
			resultRef	= new HashMap<String, Double>();
		}

		for (XSLTest test : testSuite) {
			//stopwatch starts
			startTimeInMs = System.currentTimeMillis();

			//executing test, generating the results file
			test.reset();
			String resultFile = resultCatalogLocation + test.getName() + ".xml";
			XMLUtils.applyStylesheet(repository.toFile(), test.toStream(), resultFile, null);

			//stopwatch ends and test results filename is added to index list
			resultRef.put(resultFile, (double) (System.currentTimeMillis() - startTimeInMs) / 1000);
		}

		createIndexDocument(resultRef);
	}

	/**
	 * Validates the repository and test suite have been setup and are available.
	 * @return true if all dependencies are met
	 */
	public boolean ready() {
		boolean istestSuiteSet	= false;
		boolean isRepositorySet	= false;
		boolean isResultDirSet	= false;

		if (testSuite != null)
			if (testSuite.size() > 0)
				istestSuiteSet = true;

		if (repository != null)
			isRepositorySet = true;

		if (!resultCatalogLocation.equals(""))
			isResultDirSet = true;

		return (isRepositorySet && istestSuiteSet && isResultDirSet && serviceStartTime != 0);
	}

	/**
	 * Sets the internal directory all results will be saved to.
	 * @param resultCatalogLocation path to the target directory
	 */
	public void setResultCatalogLocation(String resultCatalogLocation) {
		this.resultCatalogLocation = resultCatalogLocation;
		FileUtils.setupWorkDirectory(resultCatalogLocation);		
	}

	/**
	 * Generates a catalog file with a list of tests.
	 * @param resultRefs a Map with a result file, elapsed time> entry for each test executed
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
