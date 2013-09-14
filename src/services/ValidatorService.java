package services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	/**
	 * 
	 */
	private void testRunner() {
		for (String s : getServletContext().getResourcePaths("/WEB-INF/Tests")) {
			System.out.println("Test found: " + s);
			InputStream xslTest = getServletContext().getResourceAsStream(s);
			XMLUtils.xsl4Files(fRepository, xslTest, sResultsDir + File.separator + "result101.xml");
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
			//setup a results directory when tests are found
			if (getServletContext().getResourcePaths("/WEB-INF/Tests").size() > 0) {
				System.out.println("!!!!!!!!!!!!!!!");
				sResultsDir = sServletContextDir + File.separator + 
						sSessionId + File.separator + "results";
				boolean x = FileUtils.setupWorkDir(sResultsDir);
				System.out.println(x);
			}
			//now it's time to run all tests.
			testRunner();
		}
	}
}
