package org.validator.services;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.validator.utils.FileUtils;

/**
 * Servlet implementation class Admin
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] sessions = request.getParameterValues("sessionFolder");
		String uploadPath = getServletContext().getRealPath("") + File.separator;

		for (int i=0; i < sessions.length; i++) {
			FileUtils.deleteAll(new File(uploadPath + sessions[i]));
		}

		getServletContext().getRequestDispatcher("/admin.jsp").forward(request, response);
	}

}
