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

	public Vector<Vector<String>> getContents() {

		Vector<Vector<String>> contents = new Vector<Vector<String>>();
		Vector<String> v = null;
		File[] folderContents = null;

		if (!appFolder.exists())
			appFolder = null;

		if (appFolder == null) {
			v = new Vector<String>();
			v.add("empty");
			v.add("empty");
			contents.add(v);
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
			v = new Vector<String>();
			v.add(folderContents[i].getName());
			v.add(date.format(folderContents[i].lastModified()));
			contents.add(v);
		}
		return contents;
	}
}
