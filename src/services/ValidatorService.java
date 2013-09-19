package services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
	private static final long serialVersionUID = 1L;
	private File fRepository = null;
	private String sResultsDir = null;
	private String sServletContextDir = null;
	private final String sTestDir = "/WEB-INF/Tests";

	/**
	 * 
	 */
	private void testRunner() {
		String sTestName = "";
		String sResultFile = "";
		InputStream inputsXSLTest = null;
		Document docXSLTest = null;
		NodeList nlTestName = null;

		for (String s : getServletContext().getResourcePaths(sTestDir)) {

			inputsXSLTest = getServletContext().getResourceAsStream(s);

			docXSLTest = XMLUtils.InputStream2Document(inputsXSLTest);
			nlTestName = docXSLTest.getElementsByTagName("TestName");
			sTestName = "Test";
			if (nlTestName.getLength() == 1)
				sTestName = nlTestName.item(0).getTextContent();

			try {
				inputsXSLTest.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}

			sResultFile = sResultsDir + File.separator + sTestName + ".xml";
			XMLUtils.xsl4Files(fRepository, inputsXSLTest, sResultFile);
			System.out.println("Results: " + sResultFile);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//temporarily mocking up the session Id as "abc"
		//String sSessionId = request.getRequestedSessionId();
		String sSessionId = "abc";
		sServletContextDir = getServletContext().getRealPath("");

		fRepository = new File(sServletContextDir + File.separator + 
				sSessionId + File.separator + sSessionId + ".xml");

		if (fRepository.exists() && fRepository.canRead()) {
			//setup a results directory if tests are found
			if (getServletContext().getResourcePaths(sTestDir).size() > 0) {
				sResultsDir = sServletContextDir + File.separator + 
						sSessionId + File.separator + "results";
				FileUtils.setupWorkDir(sResultsDir);
			}

			//now it's time to run all tests.
			testRunner();
		}
	}
}
