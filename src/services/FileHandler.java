package services;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class FileHandler
 */
@WebServlet(description = "This controller handles file-based requests", urlPatterns = { "/FileHandler" })
public class FileHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileHandler() {
        super();
    	System.out.println("Hello World!");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("and this is the doGet method.");
		System.out.println("Temp Dir is "+ System.getProperty("java.io.tmpdir"));
        //If there's no file to upload, there's no point in going any further.
        if (!ServletFileUpload.isMultipartContent(request)) {
        	System.out.println("No file upload request this time.");
        	getServletContext().getRequestDispatcher("/").forward(request, response);
        }
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
