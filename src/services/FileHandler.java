package services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class FileHandler
 */
@WebServlet(description = "This controller handles file-based requests", urlPatterns = { "/FileHandler" })
public class FileHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 10;  // 10MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 200; // 200MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 200; // 200MB

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Error: Form must have enctype=multipart/form-data.");
			writer.flush();
			return;
		}

		// configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// sets memory threshold - beyond which files are stored in disk 
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// sets temporary location to store files
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		System.out.println("Temporary file location: " + System.getProperty("java.io.tmpdir"));

		ServletFileUpload upload = new ServletFileUpload(factory);

		// sets maximum size of upload file
		upload.setFileSizeMax(MAX_FILE_SIZE);

		// sets maximum size of request (include file + form data)
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// constructs the directory path to store upload file
		// this path is relative to application's directory
		String uploadPath = getServletContext().getRealPath("")
				+ File.separator + request.getRequestedSessionId();

		// creates the directory if it does not exist
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
			System.out.println("Creating the directory: " + uploadDir.getAbsolutePath());
		}
		else {
			System.out.println("Directory " + uploadDir.getAbsolutePath() + " already exists!");
		}

		try {
			// parses the request's content to extract file data
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				// iterates over form's fields
				for (FileItem item : formItems) {
					// processes only fields that are not form fields
					if (item.isFormField()) {
		                String fieldname = item.getFieldName();
		                String fieldvalue = item.getString();
		                System.out.println("***" + fieldname + "\t" + fieldvalue);
					}
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);

						// saves the file on disk
						item.write(storeFile);
						request.setAttribute("message",
								"Upload has been done successfully!<br/>" +
								"File can be found: " + storeFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception ex) {
			request.setAttribute("message",
					"There was an error: " + ex.getMessage());
		}
		// redirects client to message page
		getServletContext().getRequestDispatcher("/message.jsp").forward(
				request, response);
	}
}
