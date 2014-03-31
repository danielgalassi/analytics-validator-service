package org.validator.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.ServletContext;

public class Folder {

	private File appFolder = null;

	/**
	 * Sets the path to a directory using a ServletContext
	 * @param context
	 */
	public void setFolder (ServletContext context) {
		appFolder = new File (context.getRealPath("/"));
	}

	/**
	 * Filters out files and web server directories (*-INF) 
	 * @return application directories
	 */
	public Vector<SimpleFile> getContents() {
		Vector<SimpleFile> contents = new Vector<SimpleFile>();
		SimpleFile file = null;
		File[] folderContents = null;

		if (!appFolder.exists()) {
			appFolder = null;
		}

		if (appFolder == null) {
			file = new SimpleFile ("empty", "empty");
			contents.add(file);
			return contents;
		}

		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		//filters out files and web server directories
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (!file.getName().equals("WEB-INF") && 
						!file.getName().equals("META-INF") && 
						file.isDirectory());
			}
		};

		folderContents = appFolder.listFiles(filter);

		for (int i=0; i<folderContents.length; i++) {
			file = new SimpleFile (folderContents[i].getName(), date.format(folderContents[i].lastModified()));
			contents.add(file);
		}

		return contents;
	}
}
