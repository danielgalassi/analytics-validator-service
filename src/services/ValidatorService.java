package services;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.FileUtils;

/**
 * Servlet implementation class ValidatorService
 */
@WebServlet(description = "This controller provides test runner services.", urlPatterns = { "/ValidatorService" })
public class ValidatorService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sSessionId = request.getRequestedSessionId();
		System.out.println("POST (2)= " + sSessionId);
		File fRepository = new File(getServletContext().getRealPath("") + File.separator + sSessionId + File.separator + sSessionId + ".xml");
		if (fRepository.exists() && fRepository.canRead()) {
			//setup a results directory when tests are found
			if (getServletContext().getResourcePaths("/WEB-INF/Tests").size() > 0) {
				String sResultsDir = getServletContext().getRealPath("") + File.separator + sSessionId + File.separator + "results";
				FileUtils.setupWorkDir(sResultsDir);
			}

			for (String s : getServletContext().getResourcePaths("/WEB-INF/Tests")) {
				System.out.println("Test found: " + s);
				//InputStream contents = request.getServletContext().getResourceAsStream(s);
			}
		}
	}

}
