/**
 * 
 */
package org.validator.engine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.validator.metadata.Repository;
import org.validator.metadata.Test;
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

	/**
	 * The target directory where validation results will be saved.
	 */
	private String			resultCatalogLocation;
	/**
	 * An OBIEE metadata repository object.
	 */
	private Repository		repository = null;
	/**
	 * The test suite consists of one or more <code>Test</code> implementations.
	 */
	private Vector<Test>	testSuite = new Vector<Test>();
	/**
	 * The time in milliseconds when the <code>ValidatorService</code> was triggered.
	 */
	private long			serviceStartTime = 0;

	/**
	 * Validator Engine constructor. Repository, Test Suite and Session Directory are set using independent methods.
	 */
	public ValidatorEngine() {
		System.out.println("Initialising Validator Engine");
	}

	/**
	 * Sets the repository file to assess in this validator engine
	 * @param repository metadata repository object
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	/**
	 * Sets the stopwatch to measure the time taken to validate the repository.
	 * @param serviceStartTime start time of the validator service in milliseconds
	 */
	public void setStartTime(long serviceStartTime) {
		this.serviceStartTime = serviceStartTime;
	}

	/**
	 * Loads all tests creating a suite
	 * @param context the scope of the current session
	 * @param testCatalog directory where all tests are stored
	 */
	public void setTestSuite(ServletContext context, String testCatalog) {

		boolean testsFound = false;
		try {
			testsFound = (context.getResourcePaths(testCatalog).size() > 0);
		} catch (Exception e) {
			//a NullPointerException is thrown if the directory is not found
			e.printStackTrace();
		}

		if (!testsFound) {
			return;
		}

		for (String testCase : context.getResourcePaths(testCatalog)) {
			addTest(context.getResourceAsStream(testCase));
		}
	}

	/**
	 * Getter method to make public the number of tests in the suite
	 * @return the number of tests loaded
	 */
	public int getTestSuiteSize() {
		return testSuite.size();
	}

	/**
	 * Appends one test script to internal test suite
	 * @param testStream a test case
	 */
	public void addTest(InputStream testStream) {
		testSuite.add(new XSLTest(testStream));
	}

	/**
	 * Executes the validation of the repository metadata using all test scripts loaded.
	 * Upon completion, an index document is created. Each entry in this document points to a result file.
	 */
	public void run() {
		Map <String, Double>	resultRef = new HashMap<String, Double>();
		long					startTimer;

		if (!ready()) {
			return;
		}

		//executes all scripts in test suite and times them
		for (Test test : testSuite) {
			startTimer = System.currentTimeMillis();
			String result = resultCatalogLocation + test.getName() + ".xml";
			test.assertMetadata(repository, result);
			resultRef.put(result, (double) (System.currentTimeMillis() - startTimer) / 1000);
		}

		createIndexDocument(resultRef);
	}

	/**
	 * Validates the repository and test suite have been setup and are available.
	 * @return true if all dependencies are met
	 */
	public boolean ready() {
		boolean isTestSuiteSet	= (testSuite.size() > 0);
		boolean isRepositorySet = (repository != null);
		boolean isResultDirSet	= (!resultCatalogLocation.equals(""));

		return (isRepositorySet && isTestSuiteSet && isResultDirSet && serviceStartTime != 0);
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
	 * @param resultRefs a Map with a (result file, elapsed time) entry for each test executed
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
