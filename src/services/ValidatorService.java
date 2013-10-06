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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import utils.FileUtils;
import utils.XMLUtils;

/**
 * Servlet implementation class ValidatorService
 */
@WebServlet(description = "This controller provides test runner services.", urlPatterns = { "/ValidatorService" })
public class ValidatorService extends HttpServlet {
	private static final long	serialVersionUID = 1L;
	private File				fRepository = null;
	private String				sResultsDir = null;
	private String				sServletContextDir = null;
	private final String		sTestDir = "/WEB-INF/Tests";
	private final String		sViewDir = "/WEB-INF/Views";
	private long				startTime;

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
			testList = new Vector<String> ();
			elapsedTime = new Vector<Double> ();
		}

		for (String s : getServletContext().getResourcePaths(sTestDir)) {

			startTimeMs = System.currentTimeMillis();

			inputsXSLTest = getServletContext().getResourceAsStream(s);
			docXSLTest = XMLUtils.loadDocument(inputsXSLTest);
			nlTestName = docXSLTest.getElementsByTagName("TestName");
			sTestName = "Test";
			if (nlTestName.getLength() == 1)
				sTestName = nlTestName.item(0).getTextContent();

			try {
				inputsXSLTest.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}

			sResultFile = sResultsDir + sTestName + ".xml";
			elapsedTime.add((double) (System.currentTimeMillis() - startTimeMs) / 1000);

			testList.add(sResultFile);
			XMLUtils.xsl4Files(fRepository, inputsXSLTest, sResultFile);
			//System.out.println("Results: " + sResultFile);
		}
		XMLUtils.createIndexDocument(testList, elapsedTime, sResultsDir, startTime);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sSessionId = request.getRequestedSessionId();

		System.out.println(sSessionId);
		
		System.out.println(request.getAttribute("metadataFile"));

		startTime = (long) request.getAttribute("startTime");
		sServletContextDir = getServletContext().getRealPath("");
		fRepository = new File(sServletContextDir + File.separator + 
				sSessionId + File.separator + "metadata.xml");

		//validates the repository file can be used
		if (fRepository.exists() && fRepository.canRead()) {
			//setup a results directory if tests are found
			if (getServletContext().getResourcePaths(sTestDir).size() > 0) {
				sResultsDir = sServletContextDir + File.separator + 
						sSessionId + File.separator + "results" + File.separator;
				FileUtils.setupWorkDir(sResultsDir);

				//it's time to run all tests.
				executeTests();
				//the results page is created
				InputStream inputsXSLHTML = getServletContext().getResourceAsStream(sViewDir+File.separator+"Verbose.xsl");
				String resFormat = (String) request.getAttribute("resultsFormat");
				if (!(resFormat.equals("Verbose")))
					inputsXSLHTML = getServletContext().getResourceAsStream(sViewDir+File.separator+"Summary.xsl");

				File fIndex = new File(sResultsDir + "index.xml");
				XMLUtils.xsl4Files(fIndex, inputsXSLHTML, sResultsDir + "MetadataValidated.html");
				System.out.println("Results HTML page generated");

				//results Zip file is created
				FileUtils.Zip(sResultsDir  + "MetadataValidated.html",
						sResultsDir + "MetadataValidated.zip");
				System.out.println("Results ZIP page generated");
				//System.out.println(System.currentTimeMillis());

				//redirects to resutls page (summary level)
				RequestDispatcher rd = request.getRequestDispatcher(File.separator + 
						sSessionId + File.separator + "results" + File.separator+"MetadataValidated.html");
				rd.forward(request, response);
			}
		}
	}
}
