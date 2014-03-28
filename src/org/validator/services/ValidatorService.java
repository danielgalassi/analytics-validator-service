package org.validator.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.validator.test.XSLTest;
import org.validator.utils.FileUtils;
import org.validator.utils.SaxToDom;
import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;


/**
 * Manages the trimming of the metadata file and tests execution.
 * All result files are generated here.
 * @author danielgalassi@gmail.com
 *
 */
@WebServlet(description = "This controller provides test runner services.", urlPatterns = { "/ValidatorService" })
public class ValidatorService extends HttpServlet {

	private static final long	serialVersionUID = 1L;
	private static final String	testCatalogLocation = "/WEB-INF/Tests/";
	private static final String	viewCatalogLocation = "/WEB-INF/Views/";

	/**
	 * 
	 * @param rpd
	 * @param selectedSubjectArea
	 * @param workDirectory
	 */
	private void trimRPD(File rpd, String selectedSubjectArea, String workDirectory) {
		XMLReader		XMLr = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, XMLr, rpd);
		Vector<String>	subjecAreas = new Vector<String> ();

		subjecAreas.add(selectedSubjectArea);

		Document doc = xml.makeDom("PresentationCatalog", subjecAreas);
		XMLUtils.saveDocument2File(doc, workDirectory + "metadata.xml");
	}

	/**
	 * Executes all tests found
	 * @param trimmedRPD
	 * @param resultCatalogLocation
	 * @param startTime
	 */
	private void validate(File trimmedRPD, String resultCatalogLocation, long startTime) {
		InputStream		script = null;
//		Vector<String>	resultRefs = null;
//		Vector<Double>	elapsedTime = null;
		XSLTest			test = null;
		long			startTimeInMs;
		Map <String, Double> resultRef = null;

		Set <String> testSuite = getServletContext().getResourcePaths(testCatalogLocation);
		if (testSuite.size() > 0) {
			resultRef	= new HashMap<String, Double>();
//			resultRefs	= new Vector<String> ();
//			elapsedTime	= new Vector<Double> ();
		}

		for (String testCase : testSuite) {

			//stopwatch starts
			startTimeInMs = System.currentTimeMillis();

			//initializing the test using a resource stream
			script = getServletContext().getResourceAsStream(testCase);
			test = new XSLTest(script, resultCatalogLocation);
			
			//adding results filename created by current test to index list
//			resultRefs.add(test.getResultFile());
			
			//executing test, generating the results file
			test.execute(trimmedRPD);

			//stopwatch ends and test results filename is added to index list
			resultRef.put(test.getResultFile(), (double) (System.currentTimeMillis() - startTimeInMs) / 1000);
			//elapsedTime.add((double) (System.currentTimeMillis() - startTimeInMs) / 1000);
		}

		//XMLUtils.createIndexDocument(resultRefs, elapsedTime, resultCatalogLocation, startTime);
		XMLUtils.createIndexDocument(resultRef, resultCatalogLocation, startTime);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		File			trimmedRPD = null;
		String			resultCatalogLocation = null;
		String			workDirectory = null;
		long			startTime = System.currentTimeMillis();

		String			selectedSubjectArea = "None";
		String			repositoryFileName = "";
		String			sessionId = "";
		HttpSession		session = null;
		File			repository = null;

		//recover the subject area selected in jsp
		if (request.getParameter("SubjectArea") != null)
			selectedSubjectArea = request.getParameter("SubjectArea");

		sessionId			= request.getRequestedSessionId();
		session				= request.getSession();
		workDirectory		= (String) session.getAttribute("workDir");
		repositoryFileName	= (String) session.getAttribute("metadataFile");

		repository = new File(workDirectory + repositoryFileName);

		if (!repository.exists()) {
			request.setAttribute("ErrorMessage", "Metadata file not found.");
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}

		//trimming repository file
		//keeping only selected subject area objects
		trimRPD(repository, selectedSubjectArea, workDirectory);
		repository.delete();

		trimmedRPD = new File(workDirectory + "metadata.xml");

		//validates the repository file can be used
		if (trimmedRPD.exists() && trimmedRPD.canRead()) {
			//setup a results directory if tests are found
			if (getServletContext().getResourcePaths(testCatalogLocation).size() > 0) {
				resultCatalogLocation = workDirectory + "results" + File.separator;
				FileUtils.setupWorkDirectory(resultCatalogLocation);

				//it's time to run all tests on this trimmed repository,
				//save results in that location and time the whole operation
				validate(trimmedRPD, resultCatalogLocation, startTime);

				//the results page is created
				InputStream				xsl2html = null;
				String					resultsFormat = (String) session.getAttribute("resultsFormat");
				HashMap<String, String>	stylesheetParams = new HashMap<String, String> ();
				String 					stylesheet = viewCatalogLocation + "Summary.xsl";
				String					errorsOnlyMode = "false";
				File 					index = new File(resultCatalogLocation + "index.xml");

				//setting up stylesheet parameters
				stylesheetParams.put("SelectedSubjectArea", selectedSubjectArea);
				if (resultsFormat.equals("ShowErrorsOnly")) {
					errorsOnlyMode = "true";
				}
				stylesheetParams.put("ShowErrorsOnly", errorsOnlyMode);
				stylesheetParams.put("SessionFolder", sessionId);

				//generating summary page
				xsl2html = getServletContext().getResourceAsStream(stylesheet);
				XMLUtils.xsl4Files(index, xsl2html, resultCatalogLocation + "Summary.html", stylesheetParams);

				//switching to verbose stylesheet
				stylesheet = viewCatalogLocation + "Verbose.xsl";
				xsl2html = getServletContext().getResourceAsStream(stylesheet);

				//generating the verbose page
				XMLUtils.xsl4Files(index, xsl2html, resultCatalogLocation + "Details.html", stylesheetParams);
				//System.out.println("Results HTML page generated");

				//results Zip file is created
				//FileUtils.Zip(resultsDir + "Details.html", resultsDir + "Results.zip");
				//System.out.println("Detailed Results ZIP page generated");

				//redirects to resutls page (summary level)
				RequestDispatcher rd = request.getRequestDispatcher(File.separator + 
						sessionId + File.separator + "results" + File.separator + "Summary.html");
				rd.forward(request, response);
			}
		}
	}
}
