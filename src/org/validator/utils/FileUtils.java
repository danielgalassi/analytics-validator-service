package org.validator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

/***
 * Set of methods to handle IO file operations.
 * @author danielgalassi@gmail.com
 *
 */
public class FileUtils {

	private static final Logger logger = LogManager.getLogger(FileUtils.class.getName());

	/**
	 * A reference for a file.
	 * @param file
	 * @return InputSource
	 */
	public static InputSource getStream(File file) {
		InputStream stream = null;
		InputSource source = null;

		try {
			stream = new FileInputStream(file);
			source = new InputSource(new InputStreamReader(stream));
			source.setEncoding("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			source = null;
		}
		return source;
	}

	/**
	 * Validates an argument is a Zip file using a "magic" number.
	 * @param zip potential Zip file to assess
	 * @return true if the file is in Zip format
	 */
	public static boolean isZipFile(File zip) {
		RandomAccessFile raf = null;
		long n = 0;

		try {
			raf = new RandomAccessFile(zip, "r");
			n = raf.readInt();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (n == 0x504B0304);
	}

	/**
	 * Deletes a file (or folder, recursively).
	 * @param file a file or directory in the filesystem
	 * @return true if the file or directory was deleted
	 */
	public static boolean deleteAll(File file) {
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				deleteAll(f);
			}
			else {
				f.delete();
			}
		}
		return file.delete();
	}

	/**
	 * Creates a directory with the name of the argument.
	 * If it already exists, it will be deleted and re-created.
	 * @param newFolder
	 * @return true if the directory has been created
	 */
	public static boolean setupWorkDirectory(String newFolder) {
		File workDirectory = new File(newFolder); 
		if (workDirectory.exists()) {
			logger.warn("Cleaning up directory {}", newFolder);
			deleteAll(workDirectory);
		}
		workDirectory.mkdir();
		return workDirectory.exists();
	}

	/**
	 * Generates a Zip archive.
	 * @param sSource
	 * @param sTarget
	 */
	public static void Zip(String sSource, String sTarget) {
		byte[] buffer = new byte[1024];

		try {
			FileOutputStream fos = new FileOutputStream(sTarget);
			ZipOutputStream zipOS = new ZipOutputStream(fos);
			ZipEntry zipEntry = new ZipEntry("results.html");
			zipOS.putNextEntry(zipEntry);
			FileInputStream in = new FileInputStream(sSource);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zipOS.write(buffer, 0, len);
			}

			in.close();
			zipOS.closeEntry();

			//remember close it
			zipOS.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Extracts a file from Zip archives to the folder specified in the argument.
	 * @param zipFile
	 * @param outputFolder
	 * @return reference of the extracted file
	 */
	public static File unZipIt(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];
		File newFile = null;

		try{
			//get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze!=null) {
				String fileName = ze.getName();
				newFile = new File(outputFolder + fileName);

				System.out.println("Unzipping: "+ newFile.getAbsolutePath());
				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);             

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();   
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch(IOException ex) {
			ex.printStackTrace();
		}

		return newFile;
	}

	/**
	 * Generates a Zip file with one or more entries.
	 * @param resultCatalogLocation source and target location in the filesystem
	 * @param pages entries to compress
	 * @param zipFilename name of the Zip file
	 */
	public static void Zip(String resultCatalogLocation, Vector<String> pages, String zipFilename) {
		byte[] buffer = new byte[1024];

		try {
			FileOutputStream fos = new FileOutputStream(resultCatalogLocation + zipFilename);
			ZipOutputStream zipOS = new ZipOutputStream(fos);
			for (String page : pages) {
				ZipEntry zipEntry = new ZipEntry(page + ".html");
				zipOS.putNextEntry(zipEntry);
				FileInputStream in = new FileInputStream(resultCatalogLocation + page + ".html");

				int len;
				while ((len = in.read(buffer)) > 0) {
					zipOS.write(buffer, 0, len);
				}

				in.close();
				zipOS.closeEntry();
			}
			//remember close it
			zipOS.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
