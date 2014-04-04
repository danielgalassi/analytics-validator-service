package org.validator.services;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.validator.engine.ValidatorEngine;
import org.validator.metadata.Repository;
import org.validator.ui.ResultPublisher;


/**
 * Manages the trimming of the metadata file and tests execution.
 * All result files are generated here.
 * @author danielgalassi@gmail.com
 *
 */
@WebServlet(description = "This controller orchestrates repository metadata validation services.", urlPatterns = { "/ValidatorService" })
public class ValidatorService extends HttpServlet {

	private static final long	serialVersionUID = 1L;
	/**
	 * Application directory where all test cases reside.
	 */
	private static final String	testCatalog = "/WEB-INF/Tests/";
	/**
	 * Application directory where UI-generating code for test results reside.
	 */
	private static final String	viewCatalog = "/WEB-INF/Views/";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		long startTime			= System.currentTimeMillis();

		String subjectArea		= request.getParameter("SubjectArea");
		String sessionId		= request.getRequestedSessionId();
		HttpSession session		= request.getSession();
		String workDirectory	= (String) session.getAttribute("workDir");
		String uploadedRPD		= (String) session.getAttribute("metadataFile");
		boolean errorsOnlyMode	= ((String) session.getAttribute("resultsFormat")).equals("ShowErrorsOnly");
		String resultCatalog	= workDirectory + "results" + File.separator;

		//setting the repository (tags are discarded if not related to the selected subject area)
		Repository repository	= new Repository(workDirectory, uploadedRPD, subjectArea);

		if (!repository.available()) {
			request.setAttribute("ErrorMessage", "Metadata file not found.");
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}

		//setting up the validator engine with a repository and tests
		ValidatorEngine engine = new ValidatorEngine();
		engine.setRepository(repository);
		engine.setStartTime(startTime);
		engine.setResultCatalogLocation(resultCatalog);
		engine.setTestSuite(getServletContext(), testCatalog);

		//forwards to the error page if the test suite is empty 
		if (engine.getTestSuiteSize() == 0) {
			request.setAttribute("ErrorMessage", "Tests not found.");
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}

		engine.run();

		//publishes HTML pages featuring results to the session folder
		ResultPublisher publisher = new ResultPublisher();
		publisher.setCatalogs(resultCatalog, viewCatalog);
		publisher.setContext(getServletContext());
		publisher.setParameters("SelectedSubjectArea", subjectArea);
		publisher.setParameters("SessionFolder", sessionId);
		publisher.setParameters("ShowErrorsOnly", errorsOnlyMode + "");

		publisher.publishResults();

		//redirects to summary view
		RequestDispatcher rd = request.getRequestDispatcher(publisher.getSummaryPage());
		rd.forward(request, response);
	}
}
