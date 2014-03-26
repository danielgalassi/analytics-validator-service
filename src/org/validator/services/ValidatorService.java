package org.validator.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
import org.w3c.dom.NodeList;
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
	private static final String	sTestDir = "/WEB-INF/Tests/";
	private static final String	viewDir = "/WEB-INF/Views/";

	private void trimRPD(File rpd, String selectedSubjectArea, String workDir) {
		XMLReader		XMLr = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, XMLr, rpd);
		Vector<String>	vFindSA = new Vector<String> ();

		vFindSA.add(selectedSubjectArea);

		Document doc = xml.makeDom("PresentationCatalog", vFindSA);
		XMLUtils.saveDocument2File(doc, workDir + "metadata.xml");
	}

	/**
	 * 
	 */
	private void executeTests(String resultsDir, File trimmedRPD, long startTime) {
		InputStream		script = null;
		Vector<String>	testResults = null;
		Vector<Double>	elapsedTime = null;
		XSLTest			test = null;
		long			startTimeMs;

		Set <String> testSuite = getServletContext().getResourcePaths(sTestDir);
		if (testSuite.size() > 0) {
			testResults	= new Vector<String> ();
			elapsedTime	= new Vector<Double> ();
		}

		for (String testCase : testSuite) {

			//stopwatch starts
			startTimeMs = System.currentTimeMillis();

			//initializing the test using a resource stream
			script = getServletContext().getResourceAsStream(testCase);
			test = new XSLTest(script, resultsDir);
			
			//adding results filename created by current test to index list
			testResults.add(test.getResultFile());
			
			//executing test, generating the results file
			test.execute(trimmedRPD);

			//stopwatch ends
			elapsedTime.add((double) (System.currentTimeMillis() - startTimeMs) / 1000);
		}

		XMLUtils.createIndexDocument(testResults, elapsedTime, resultsDir, startTime);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		File			trimmedRPD = null;
		String			resultsDir = null;
		String			workDir = null;
		long			startTime = System.currentTimeMillis();

		String			selectedSubjectArea = "None";
		String			rpdFileName = "";
		String			sessionId = "";
		HttpSession		session = null;
		File			rpd = null;

		//recover the subject area selected in jsp 
		if (request.getParameter("SubjectArea") != null)
			selectedSubjectArea = request.getParameter("SubjectArea");

		sessionId	= request.getRequestedSessionId();
		session		= request.getSession();
		workDir		= (String) session.getAttribute("workDir");
		rpdFileName	= (String) session.getAttribute("metadataFile");

		rpd = new File(workDir + rpdFileName);

		if (!rpd.exists()) {
			request.setAttribute("ErrorMessage", "Metadata file not found.");
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}

		//trimming repository file
		//keeping only selected subject area objects
		trimRPD(rpd, selectedSubjectArea, workDir);
		rpd.delete();

		trimmedRPD = new File(workDir + "metadata.xml");

		//validates the repository file can be used
		if (trimmedRPD.exists() && trimmedRPD.canRead()) {
			//setup a results directory if tests are found
			if (getServletContext().getResourcePaths(sTestDir).size() > 0) {
				resultsDir = workDir + "results" + File.separator;
				FileUtils.setupWorkDir(resultsDir);

				//it's time to run all tests.
				executeTests(resultsDir, trimmedRPD, startTime);

				//the results page is created
				InputStream				xsl2html = null;
				String					resultsFormat = (String) session.getAttribute("resultsFormat");
				HashMap<String, String>	xslParameters = new HashMap<String, String> ();
				String 					xsl = viewDir + "Summary.xsl";
				String					errorsOnly = "false";
				File 					index = new File(resultsDir + "index.xml");

				//setting up stylesheet parameters
				xslParameters.put("SelectedSubjectArea", selectedSubjectArea);
				if (resultsFormat.equals("ShowErrorsOnly")) {
					errorsOnly = "true";
				}
				xslParameters.put("ShowErrorsOnly", errorsOnly);
				xslParameters.put("SessionFolder", sessionId);

				//generating summary page
				xsl2html = getServletContext().getResourceAsStream(xsl);
				XMLUtils.xsl4Files(index, xsl2html, resultsDir + "Summary.html", xslParameters);

				//switching to verbose stylesheet
				xsl = viewDir + "Verbose.xsl";
				xsl2html = getServletContext().getResourceAsStream(xsl);

				//generating the verbose page
				XMLUtils.xsl4Files(index, xsl2html, resultsDir + "Details.html", xslParameters);
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
