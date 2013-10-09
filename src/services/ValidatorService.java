package services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;

import utils.FileUtils;
import utils.SaxToDom;
import utils.XMLUtils;

/**
 * Servlet implementation class ValidatorService
 */
@WebServlet(description = "This controller provides test runner services.", urlPatterns = { "/ValidatorService" })
public class ValidatorService extends HttpServlet {

	private static final long	serialVersionUID = 1L;
	private static final String	sTestDir = "/WEB-INF/Tests/";
	private static final String	viewDir = "/WEB-INF/Views/";

	private void trimRPD(File rpd, 
			String selectedSubjectArea, 
			String workDir) {
		XMLReader		XMLr = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, XMLr, rpd);

		Vector<String>	vFindSA = new Vector<String> ();
		vFindSA.add(selectedSubjectArea);

		XMLUtils.saveDocument2File(xml.makeDom("PresentationCatalog", vFindSA), 
				workDir + "metadata.xml");
	}

	/**
	 * 
	 */
	private void executeTests(String resultsDir, 
			File trimmedRPD, 
			long startTime) {
		String			testName = "";
		String			resultFile = "";
		InputStream		inputsXSLTest = null;
		Document		docXSLTest = null;
		NodeList		nlTestName = null;
		Vector<String>	testList = null;
		Vector<Double>	elapsedTime = null;
		long			startTimeMs;

		if (getServletContext().getResourcePaths(sTestDir).size() > 0) {
			testList	= new Vector<String> ();
			elapsedTime	= new Vector<Double> ();
		}

		for (String s : getServletContext().getResourcePaths(sTestDir)) {

			//stopwatch starts
			startTimeMs		= System.currentTimeMillis();

			inputsXSLTest	= getServletContext().getResourceAsStream(s);
			docXSLTest		= XMLUtils.loadDocument(inputsXSLTest);

			//setting up test name with a default value
			testName		= "Test" + System.currentTimeMillis();
			nlTestName		= docXSLTest.getElementsByTagName("TestName");
			if (nlTestName.getLength() == 1)
				testName = nlTestName.item(0).getTextContent();
			//setting up test name with retrieved tag content
			resultFile = resultsDir + testName + ".xml";

			try {
				inputsXSLTest.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//adding results file name created by current test to index list
			testList.add(resultFile);
			//executing test, generating the results file
			XMLUtils.xsl4Files(trimmedRPD, inputsXSLTest, resultFile, null);

			//stopwatch ends
			elapsedTime.add((double) (System.currentTimeMillis() - startTimeMs) / 1000);
		}

		XMLUtils.createIndexDocument(testList, elapsedTime, resultsDir, startTime);
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
		if(request.getParameter("SubjectArea") != null)
			selectedSubjectArea = request.getParameter("SubjectArea");

		sessionId	= request.getRequestedSessionId();
		session		= request.getSession();
		workDir		= (String) session.getAttribute("workDir");
		rpdFileName	= (String) session.getAttribute("metadataFile");

		rpd = new File(workDir + rpdFileName);

		if (!rpd.exists()) {
			request.setAttribute("message", "Metadata file not found.");
			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
			return;
		}

		//trimming repository file
		//keeping only selected subject area objects
		trimRPD (rpd, selectedSubjectArea, workDir);
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
				String					resFormat = (String) session.getAttribute("resultsFormat");
				HashMap<String, String>	params = new HashMap<String, String> ();
				String 					xsl = viewDir + "Summary.xsl";
				String					errorsOnly = "false";
				File 					index = new File(resultsDir + "index.xml");

				//setting up stylesheet parameters
				params.put("SelectedSubjectArea", selectedSubjectArea);
				if (resFormat.equals("ShowErrorsOnly"))
					errorsOnly = "true";
				params.put("ShowErrorsOnly", errorsOnly);
				params.put("SessionFolder", sessionId);

				//generating summary page
				xsl2html = getServletContext().getResourceAsStream(xsl);
				XMLUtils.xsl4Files(index, xsl2html, resultsDir + "Summary.html", params);

				//switching to verbose stylesheet
				xsl = viewDir + "Verbose.xsl";
				xsl2html = getServletContext().getResourceAsStream(xsl);

				//generating the verbose page
				XMLUtils.xsl4Files(index, xsl2html, resultsDir + "Details.html", params);
				System.out.println("Results HTML page generated");

				//results Zip file is created
				FileUtils.Zip(resultsDir + "Details.html",
						resultsDir + "Results.zip");
				System.out.println("Detailed Results ZIP page generated");

				//redirects to resutls page (summary level)
				RequestDispatcher rd = request.getRequestDispatcher(File.separator + 
						sessionId + File.separator + "results" + File.separator + "Summary.html");
				rd.forward(request, response);
			}
		}
	}
}
