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
	private File				trimmedRPD = null;
	private String				resultsDir = null;
	private String				workDir = null;
	private final String		sTestDir = "/WEB-INF/Tests";
	private final String		viewDir = "/WEB-INF/Views";
	private long				startTime = System.currentTimeMillis();

	private void trimRPD(File rpd, String selectedSubjectArea) {
		XMLReader		XMLr = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, XMLr, rpd);

		Vector<String>	vFindSA = new Vector<String> ();
		vFindSA.add(selectedSubjectArea);

		XMLUtils.saveDocument2File(xml.makeDom("PresentationCatalog", vFindSA), 
				workDir + File.separator + "metadata.xml");
	}

	/**
	 * 
	 */
	private void executeTests() {
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
			resultFile = resultsDir + File.separator + testName + ".xml";

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

		rpd = new File(workDir + File.separator + rpdFileName);

		//trimming repository file
		//keeping only selected subject area objects
		trimRPD (rpd, selectedSubjectArea);
		rpd.delete();

		trimmedRPD = new File(workDir + File.separator + "metadata.xml");

		//validates the repository file can be used
		if (trimmedRPD.exists() && trimmedRPD.canRead()) {
			//setup a results directory if tests are found
			if (getServletContext().getResourcePaths(sTestDir).size() > 0) {
				resultsDir = workDir + File.separator + "results";
				FileUtils.setupWorkDir(resultsDir);

				//it's time to run all tests.
				executeTests();

				//the results page is created
				InputStream inputsXSLHTML = null;
				String resFormat = (String) session.getAttribute("resultsFormat");
				HashMap<String, String> params = new HashMap<String, String> ();
				String xsl = viewDir + File.separator + "Verbose.xsl";

				params.put("SelectedSubjectArea", selectedSubjectArea);

				if (resFormat.equals("Summary")) {
					params.put("ShowErrorsOnly", "false");
					xsl = viewDir + File.separator + "Summary.xsl";
				}

				if (resFormat.equals("Verbose"))
					params.put("ShowErrorsOnly", "false");

				if (resFormat.equals("ShowErrorsOnly"))
					params.put("ShowErrorsOnly", "true");

				inputsXSLHTML = getServletContext().getResourceAsStream(xsl);

				File fIndex = new File(resultsDir + File.separator + "index.xml");
				XMLUtils.xsl4Files(fIndex, inputsXSLHTML, resultsDir + File.separator + "MetadataValidated.html", params);
				System.out.println("Results HTML page generated");

				//results Zip file is created
				FileUtils.Zip(resultsDir + File.separator + "MetadataValidated.html",
						resultsDir + File.separator + "MetadataValidated.zip");
				System.out.println("Results ZIP page generated");
				//System.out.println(System.currentTimeMillis());

				//redirects to resutls page (summary level)
				RequestDispatcher rd = request.getRequestDispatcher(File.separator + 
						sessionId + File.separator + "results" + File.separator + "MetadataValidated.html");
				rd.forward(request, response);
			}
		}
	}
}
