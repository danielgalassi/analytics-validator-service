package org.validator.utils;


/**
 * Lightweight file implementation.
 * This class has limited functionality but it's main purpose it's to make it easy to list filesystem contents.
 * @author danielgalassi@gmail.com
 *
 */
public class SimpleFile {

	/**
	 * Name of the file in the filesystem.
	 */
	private String name;
	/**
	 * Date and time when the file was last modified.
	 */
	private String lastModified;

	/**
	 * Instantiates a lightweight file object with a name and the last modified date.
	 * @param name name of the file
	 * @param lastModified last modified date
	 */
	public SimpleFile(String name, String lastModified) {
		this.name = name;
		this.lastModified = lastModified;
	}

	/**
	 * Getter method for the name of the file.
	 * @return name of the file
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter method for the last modified date.
	 * @return last modified date
	 */
	public String getLastModified() {
		return lastModified;
	}
}
