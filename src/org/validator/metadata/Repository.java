/**
 * 
 */
package org.validator.metadata;

import java.io.File;
import java.util.Vector;

import org.validator.utils.FileUtils;
import org.validator.utils.SaxToDom;
import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

/**
 * A basic class to handle the metadata repository file
 * Repository files were handled through File calls, making the code in the Validator Service and Engine too verbose and difficult to follow. 
 * @author danielgalassi@gmail.com
 *
 */
public class Repository {

	/**
	 * The metadata file in XUDML (XML) format stored in the filesystem under the session directory. 
	 */
	private File repository = null;
	/**
	 * The session directory where the XUDML file is stored.
	 */
	private File directory = null;

	/**
	 * Instantiates a metadata file
	 * @param directory path to the repository metadata file
	 * @param repository name and extension of the metadata repository file
	 * @param selectedSubjectArea 
	 */
	public Repository(String directory, String repository, String selectedSubjectArea) {
		this.directory  = new File(directory);
		this.repository = new File(directory + repository);
		trim(selectedSubjectArea);
	}

	/**
	 * Evaluates the status of the file
	 * @return true if the file if found and can be read
	 */
	public boolean available() {
		return repository.exists() && repository.canRead();
	}

	/**
	 * Creates a smaller repository file discarding all objects not used for the selected Subject Area
	 * @param keepSubjectArea the name of the subject area to evaluate 
	 */
	public void trim(String keepSubjectArea) {
		XMLReader		reader = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, reader, repository);
		Vector<String>	subjectAreas = new Vector<String>();
		Document		dom = null;

		subjectAreas.add(keepSubjectArea);

		dom = xml.makeDom("PresentationCatalog",  subjectAreas);
		XMLUtils.saveDocument(dom,  directory + File.separator + "metadata.xml");
		repository.delete();
		repository = new File (directory + File.separator + "metadata.xml");
	}
	
	/**
	 * Returns a file representing the repository
	 * @return a File reference to the repository file
	 */
	public File toFile() {
		return repository;
	}
}
