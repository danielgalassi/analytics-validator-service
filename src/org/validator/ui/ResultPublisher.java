/**
 * 
 */
package org.validator.ui;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.validator.utils.FileUtils;
import org.validator.utils.XMLUtils;

/**
 * @author danielgalassi@gmail.com
 *
 */
public class ResultPublisher {

	String				viewCatalog;
	String				resultCatalog;
	String 				stylesheet;
	ServletContext		context;
	File				index;
	Vector<String>		pages = new Vector<String>();
	Map<String, String>	stylesheetParams = new HashMap<String, String> ();

	public void setCatalogs(String resultCatalog, String viewCatalog) {
		this.resultCatalog = resultCatalog;
		this.viewCatalog = viewCatalog;
	}

	public void setContext(ServletContext context) {
		this.context = context;
	}

	public void setParameters(String name, String value) {
		stylesheetParams.put(name, value);
	}

	public void setIndex(File index) {
		this.index = index;
	}

	/**
	 * Creating HTML pages (Summary, Compact and Details views)
	 */
	private void generatePages() {
		pages.add("Summary");
		pages.add("Compact");
		pages.add("Details");
		for (String page : pages) {
			stylesheet = viewCatalog + page + ".xsl";
			InputStream	xsl2html = context.getResourceAsStream(stylesheet);
			XMLUtils.applyStylesheet(index, xsl2html, resultCatalog + page + ".html", stylesheetParams);
		}
		pages.remove("Summary");
	}

	private void generateZip() {
		FileUtils.Zip(resultCatalog,  pages, "Results.zip");
	}

	public void publishResults() {
		generatePages();
		generateZip();
	}
}
