package services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import utils.FileUtils;
import utils.SaxToDom;
import utils.XMLUtils;

/**
 * Servlet implementation class FileHandler
 */
@WebServlet(description = "This controller handles file-based requests", urlPatterns = { "/FileHandler" })
public class FileHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 10;  // 10MB
	private static final int MAX_FILE_SIZE      = 1024 * 1024 * 200; // 200MB
	private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 200; // 200MB
	private static boolean isZipFile = false;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File xmlFile = null;

		System.out.println("Temp location: " + System.getProperty("java.io.tmpdir"));
		System.out.println("Session Id: " + request.getRequestedSessionId());

		//checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Error: Form must have enctype=multipart/form-data.");
			writer.flush();
			return;
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
				+ File.separator + sSessionId;
		FileUtils.setupWorkDir(uploadPath);

		try {
			//parses the request content to extract file data
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0)
				for (FileItem item : formItems) {
					if (item.isFormField()) {
						request.setAttribute(item.getFieldName(), item.getString());
						if (item.getFieldName().equals("fileFormat"))
								if (request.getAttribute("fileFormat").equals("zip"))
							isZipFile = true;
					}

					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						File storedFile = new File(filePath);
						//saves the file on disk
						item.write(storedFile);
						request.setAttribute("message",
								"Upload has been done successfully!<br/>" +
										"File can be found: " + storedFile.getAbsolutePath());
						//System.out.println(storeFile.getAbsolutePath());
						if (isZipFile) {
							xmlFile = FileUtils.unZipIt(storedFile.getAbsolutePath(), uploadPath);
							storedFile.delete();
						}
						else
							xmlFile = storedFile;

						//trims the metadata XML file,
						//keeping one subject area
						pruneFile(xmlFile, uploadPath);
						if (!xmlFile.delete())
							xmlFile.deleteOnExit();
					}
				}
		} catch (Exception ex) {
			request.setAttribute("message",
					"There was an error: " + ex.getMessage());
		}
		//redirects client to message page
		//getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
		getServletContext().getRequestDispatcher("/ValidatorService").forward(request, response);
	}

	private void pruneFile(File xmlFile, String sessionFolder) {

//		SaxParser sas = new SaxParser(xmlFile, "PresentationCatalog", "name");
//		Vector<String> v = sas.getValues();
//		System.out.println(v.size());
//		for (String s : v)
//			System.out.println(s);

		//TODO: it's time to introduce the SA selector page and move this stub from the FileHandler
		InputSource is = FileUtils.getIS(xmlFile);
		XMLReader XMLr = FileUtils.getXMLReader();

		SaxToDom xml = new SaxToDom(null, XMLr, is, xmlFile);
		Vector<String> vFindSA = new Vector<String> ();
		vFindSA.add("Inventory - Balances");
		XMLUtils.saveDocument2File(
				xml.makeDom("PresentationCatalog", vFindSA), 
				sessionFolder + File.separator + "metadata.xml");
	}
}
