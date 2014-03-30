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

import org.validator.engine.ValidatorEngine;
import org.validator.metadata.Repository;
import org.validator.utils.FileUtils;
import org.validator.utils.XMLUtils;


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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String			resultCatalogLocation = null;
		String			workDirectory = null;
		long			startTime = System.currentTimeMillis();

		String			selectedSubjectArea = "None";
		String			repositoryFilename = "";
		String			sessionId = "";
		HttpSession		session = null;
		Repository		repository = null;

		//recover the subject area selected in jsp
		if (request.getParameter("SubjectArea") != null)
			selectedSubjectArea = request.getParameter("SubjectArea");

		sessionId			= request.getRequestedSessionId();
		session				= request.getSession();
		workDirectory		= (String) session.getAttribute("workDir");
		repositoryFilename	= (String) session.getAttribute("metadataFile");

		repository = new Repository(workDirectory, repositoryFilename);

		if (!repository.available()) {
			request.setAttribute("ErrorMessage", "Metadata file not found.");
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}

		//trimming repository file
		//keeping only selected subject area objects
		repository.trim(selectedSubjectArea);

		//validates the repository file can be used
		if (repository.available()) {
			//setup a results directory if tests are found
			boolean testsFound = (getServletContext().getResourcePaths(testCatalogLocation).size() > 0);
			if (testsFound) {

				resultCatalogLocation = workDirectory + "results" + File.separator;

				//setting up the validator engine with a repository and tests
				ValidatorEngine engine = new ValidatorEngine();
				engine.setRepository(repository);
				engine.setStartTime(startTime);
				engine.setResultCatalogLocation(resultCatalogLocation);

				//adding tests to the validator engine
				Set <String> testSuite = getServletContext().getResourcePaths(testCatalogLocation);
				for (String testCase : testSuite) {
					engine.addTest(getServletContext().getResourceAsStream(testCase));
				}

				//it's time to run all tests on this trimmed repository,
				//save results in that location and time the whole operation
				if (engine.ready())
					engine.run();

				//the results page is created
				InputStream			xsl2html = null;
				String				resultsFormat = (String) session.getAttribute("resultsFormat");
				Map<String, String>	stylesheetParams = new HashMap<String, String> ();
				String 				stylesheet = "";
				String				errorsOnlyMode = "false";
				File 				index = new File(resultCatalogLocation + "index.xml");

				//setting up stylesheet parameters
				stylesheetParams.put("SelectedSubjectArea", selectedSubjectArea);
				if (resultsFormat.equals("ShowErrorsOnly")) {
					errorsOnlyMode = "true";
				}
				stylesheetParams.put("ShowErrorsOnly", errorsOnlyMode);
				stylesheetParams.put("SessionFolder", sessionId);

				//creating both HTML pages (Summary and Details views)
				Vector<String> pages = new Vector<String>();
				pages.add("Summary");
				pages.add("Details");
				for (String page : pages) {
					stylesheet = viewCatalogLocation + page + ".xsl";
					xsl2html = getServletContext().getResourceAsStream(stylesheet);
					XMLUtils.applyStylesheet(index, xsl2html, resultCatalogLocation + page + ".html", stylesheetParams);
				}

				//results Zip file is created
				//TODO: fix link to detailed view for downloaded zip
				FileUtils.Zip(resultCatalogLocation,  pages, "Results.zip");

				//redirects to resutls page (summary level)
				RequestDispatcher rd = request.getRequestDispatcher(File.separator + 
						sessionId + File.separator + "results" + File.separator + "Summary.html");
				rd.forward(request, response);
			}
		}
	}
}
