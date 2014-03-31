package org.validator.utils;


/**
 * Lightweight file implementation
 * @author danielgalassi@gmail.com
 *
 */
public class SimpleFile {

	private String name;
	private String lastModified;

	/**
	 * Instantiates a lightweight file object with a name and the last modified date
	 * @param name name of the file
	 * @param lastModified last modified date
	 */
	public SimpleFile(String name, String lastModified) {
		this.name = name;
		this.lastModified = lastModified;
	}

	/**
	 * Getter method for the name of the file
	 * @return name of the file
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter method for the last modified date
	 * @return last modified date
	 */
	public String getLastModified() {
		return lastModified;
	}
}
