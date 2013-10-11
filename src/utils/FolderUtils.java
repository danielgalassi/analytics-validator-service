package utils;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.ServletContext;

public class FolderUtils {

	private File appFolder = null;

	public void setFolder (ServletContext context) {
		appFolder = new File (context.getRealPath("/"));
	}

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
