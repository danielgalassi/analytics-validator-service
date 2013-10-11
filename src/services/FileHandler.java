package services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import utils.FileUtils;

/**
 * Servlet to manage the first step of the validation process.
 * Work directory and ZIP extraction are managed by this servlet.
 * Also, session attributes are setup based on form values. 
 * @author danielgalassi@gmail.com
 *
 */
@WebServlet(description = "This controller handles file-based requests", urlPatterns = { "/FileHandler" })
public class FileHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 10;  // 10MB
	private static final int MAX_FILE_SIZE      = 1024 * 1024 * 200; // 200MB
	private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 200; // 200MB

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		boolean isZipFile = false;
		File xmlFile = null;
		HttpSession session = request.getSession(true);
		//System.out.println("Temp location: " + System.getProperty("java.io.tmpdir"));
		//System.out.println("Session Id: " + request.getRequestedSessionId());

		//checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			request.setAttribute("message", "There was an error.");
			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
		}

		//configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//sets memory threshold - beyond which files are stored in disk
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		//sets temporary location to store files
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		//sets maximum size of upload file
		upload.setFileSizeMax(MAX_FILE_SIZE);

		//sets maximum size of request (include file + form data)
		upload.setSizeMax(MAX_REQUEST_SIZE);

		//sets up the working directory for this session
		String sSessionId = request.getRequestedSessionId();
		String uploadPath = getServletContext().getRealPath("")
				+ File.separator + sSessionId + File.separator;
		FileUtils.setupWorkDir(uploadPath);

		try {
			//parses the request content to extract file data
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					if (item.isFormField()) {
						request.setAttribute(item.getFieldName(), item.getString());
						session.setAttribute(item.getFieldName(), item.getString());
						if (item.getFieldName().equals("fileFormat")) {
							if (request.getAttribute("fileFormat").equals("zip")) {
								isZipFile = true;
							}
						}
					}

					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						if (fileName.equals("")) {
							request.setAttribute("message", "Please select a file before submitting a request.");
							getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
							return;
						}

						String filePath = uploadPath + fileName;
						File metadataFile = new File(filePath);
						//saves the file on disk
						item.write(metadataFile);

						//unzip if appropriate
						if (isZipFile) {
							xmlFile = FileUtils.unZipIt(metadataFile.getAbsolutePath(), uploadPath);
							metadataFile.delete();
						}
						else {
							xmlFile = metadataFile;
						}

						session.setAttribute("workDir", uploadPath);
						session.setAttribute("metadataFile", xmlFile.getName());
					}
				}
			}
		} catch (Exception ex) {
			request.setAttribute("message",
					"There was an error: " + ex.getMessage());
		}
		//redirects client to message page
		getServletContext().getRequestDispatcher("/SubjectAreaSelector").forward(request, response);
	}
}
