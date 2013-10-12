package org.validator.services;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Admin
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private boolean delete(File file) {
		File[] fileList = file.listFiles();
		for (File f : fileList) {
			if (f.isDirectory()) {
				delete(f);
			}
			else {
				f.delete();
			}
		}
		return file.delete();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] sessions = request.getParameterValues("sessionFolder");
		String uploadPath = getServletContext().getRealPath("") + File.separator;

		for (int i=0; i < sessions.length; i++) {
			delete(new File(uploadPath + sessions[i]));
		}

		getServletContext().getRequestDispatcher("/admin.jsp").forward(request, response);
	}

}