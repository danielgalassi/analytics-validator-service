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
 * @author danielgalassi@gmail.com
 *
 */
public class Repository {

	private File repository = null;
	private File directory = null;
	
	public Repository(String directory, String repository) {
		this.directory  = new File(directory);
		this.repository = new File(directory + repository);
	}

	/**
	 * Evaluates the status of the file
	 * @return true if the file if found and can be read
	 */
	public boolean available() {
		return repository.exists() && repository.canRead();
	}

	/**
	 * 
	 * @param keepSubjectArea the name of the subject area to evaluate 
	 */
	public void trim(String keepSubjectArea) {
		XMLReader		reader = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, reader, repository);
		Vector<String>	subjectAreas = new Vector<String>();
		Document		dom = null;
		
		subjectAreas.add(keepSubjectArea);
		
		dom = xml.makeDom("PresentationCatalog",  subjectAreas);
		XMLUtils.saveDOM2File(dom,  directory + "metadata.xml");
		repository.delete();
		repository = new File (directory + "metadata.xml");
	}
	
	public File toFile() {
		return repository;
	}
}
