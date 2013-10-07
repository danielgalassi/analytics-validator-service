package services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	private final String		sViewDir = "/WEB-INF/Views";
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
		String			sTestName = "";
		String			sResultFile = "";
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
			sTestName		= "Test" + System.currentTimeMillis();
			nlTestName		= docXSLTest.getElementsByTagName("TestName");
			if (nlTestName.getLength() == 1)
				sTestName = nlTestName.item(0).getTextContent();
			//setting up test name with retrieved tag content
			sResultFile = resultsDir + sTestName + ".xml";

			try {
				inputsXSLTest.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//adding results file name created by current test to index list
			testList.add(sResultFile);
			//executing test, generating the results file
			XMLUtils.xsl4Files(trimmedRPD, inputsXSLTest, sResultFile);

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

		trimmedRPD = new File(workDir + File.separator + "metadata.xml");

		//validates the repository file can be used
		if (trimmedRPD.exists() && trimmedRPD.canRead()) {
			//setup a results directory if tests are found
			if (getServletContext().getResourcePaths(sTestDir).size() > 0) {
				resultsDir = workDir + File.separator + "results" + File.separator;
				FileUtils.setupWorkDir(resultsDir);

				//it's time to run all tests.
				executeTests();
				//the results page is created
				InputStream inputsXSLHTML = getServletContext().getResourceAsStream(sViewDir+File.separator+"Verbose.xsl");
				String resFormat = (String) session.getAttribute("resultsFormat");
				if (!(resFormat.equals("Verbose")))
					inputsXSLHTML = getServletContext().getResourceAsStream(sViewDir+File.separator+"Summary.xsl");

				File fIndex = new File(resultsDir + "index.xml");
				XMLUtils.xsl4Files(fIndex, inputsXSLHTML, resultsDir + "MetadataValidated.html");
				System.out.println("Results HTML page generated");

				//results Zip file is created
				FileUtils.Zip(resultsDir  + "MetadataValidated.html",
						resultsDir + "MetadataValidated.zip");
				System.out.println("Results ZIP page generated");
				//System.out.println(System.currentTimeMillis());

				//redirects to resutls page (summary level)
				RequestDispatcher rd = request.getRequestDispatcher(File.separator + 
						sessionId + File.separator + "results" + File.separator+"MetadataValidated.html");
				rd.forward(request, response);
			}
		}
	}
}
