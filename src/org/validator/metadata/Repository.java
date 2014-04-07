/**
 * 
 */
package org.validator.metadata;

import java.io.File;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A basic class to handle the metadata repository file.
 * Repository files were handled through File calls, making the code in the <code>ValidatorService</code> and <code>ValidatorEngine</code> too verbose and difficult to follow. 
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
	 * Evaluates the status of the file.
	 * @return true if the file if found and can be read
	 */
	public boolean available() {
		boolean isAvailable = false;
		if (repository != null) {
			isAvailable = (repository.exists() && repository.canRead());
		}
		return isAvailable;
	}

	/**
	 * Creates a smaller repository file discarding all objects not used for the selected Subject Area.
	 * @param keepSubjectArea the name of the subject area to evaluate 
	 */
	public void trim(String keepSubjectArea) {

		Document doc = XMLUtils.loadDocument(repository);

		Document newMD = XMLUtils.createDOMDocument();
		Element repoTag = newMD.createElement("Repository");
		Element declTag = newMD.createElement("DECLARE");

		XPathFactory xpathfactory = XPathFactory.newInstance();
		XPath xpath = xpathfactory.newXPath();

		try {
			long start = System.currentTimeMillis();
			//finding subject areas
			XPathExpression expr = xpath.compile("//PresentationCatalog[@name='" + keepSubjectArea + "']");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList presentationCatalog = (NodeList) result;
			Vector<String> subjectAreaIDs = new Vector<String>();
			NamedNodeMap y = null;

			for (int i = 0; i < presentationCatalog.getLength(); i++) {
				y = presentationCatalog.item(i).getAttributes();
				subjectAreaIDs.add(y.getNamedItem("id").getNodeValue());

				Node node = presentationCatalog.item(i);
				Node copyNode = newMD.importNode(node, true);
				repoTag.appendChild(copyNode);
			}

			//finding presentation tables
			NodeList presentationTable = null;
			Vector<String> presentationTableIDs = new Vector<String>();
			expr = xpath.compile("//PresentationTable[@parentId='" + subjectAreaIDs.get(0) + "']");
			result = expr.evaluate(doc, XPathConstants.NODESET);
			presentationTable = (NodeList) result;
			for (int i = 0; i < presentationTable.getLength(); i++) {
				y = presentationTable.item(i).getAttributes();
				presentationTableIDs.add(y.getNamedItem("id").getNodeValue());

				Node node = presentationTable.item(i);
				Node copyNode = newMD.importNode(node, true);
				repoTag.appendChild(copyNode);
			}

			//finding presentation columns
			NodeList presentationCol = null;
			for (int j = 0; j < presentationTableIDs.size(); j++) {
				Vector<String> presentationColIDs = new Vector<String>();
				expr = xpath.compile("//PresentationColumn[@parentId='" + presentationTableIDs.get(j) + "']");
				result = expr.evaluate(doc, XPathConstants.NODESET);
				presentationCol = (NodeList) result;
				for (int i = 0; i < presentationCol.getLength(); i++) {
					y = presentationCol.item(i).getAttributes();
					presentationColIDs.add(y.getNamedItem("id").getNodeValue());

					Node node = presentationCol.item(i);
					Node copyNode = newMD.importNode(node, true);
					repoTag.appendChild(copyNode);
				}
			}

			newMD.appendChild(repoTag);
			XMLUtils.saveDocument(newMD,  directory + File.separator + "metadata.xml");
			System.out.println((System.currentTimeMillis() - start) / 1000);
			//repository.delete();
			//repository = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		repository.delete();
		repository = null;

		//		XMLReader		reader = FileUtils.getXMLReader();
		//		SaxToDom		xml = new SaxToDom(null, reader, repository);
		//		Vector<String>	subjectAreas = new Vector<String>();
		//
		//		subjectAreas.add(keepSubjectArea);
		//
		//		Document dom = xml.makeDom("PresentationCatalog",  subjectAreas);
		//		XMLUtils.saveDocument(dom,  directory + File.separator + "metadata.xml");
		//		//original file cleanup and file swap
		//		repository.delete();
		//		repository = new File (directory + File.separator + "metadata.xml");
	}

	/**
	 * Returns a file representing the repository.
	 * @return a File reference to the repository file
	 */
	public File toFile() {
		return repository;
	}
}
