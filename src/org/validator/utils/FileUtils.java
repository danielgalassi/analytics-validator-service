package org.validator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/***
 * Set of methods to handle IO file operations.
 * @author danielgalassi@gmail.com
 *
 */
public class FileUtils {

	/**
	 * XMLReader factory
	 * @return XMLReader object for future SAX parsing operations 
	 */
	public static XMLReader getXMLReader() {
		SAXParserFactory SAXpf = SAXParserFactory.newInstance();
		//enabling the namespaces processing
		if(!SAXpf.isNamespaceAware()) {
			SAXpf.setNamespaceAware(true);
		}

		SAXParser SAXparser= null;
		XMLReader XMLr = null;
		try {
			SAXparser = SAXpf.newSAXParser();
			XMLr = SAXparser.getXMLReader();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return XMLr;
	}

	/**
	 * Generates an InputSource of the argument
	 * @param file
	 * @return InputSource
	 */
	public static InputSource getStream(File file) {
		InputStream in = null;
		InputSource is = null;

		try {
			in = new FileInputStream(file);
			is = new InputSource(new InputStreamReader(in));
			is.setEncoding("UTF-8");
		} catch (Exception e) {
			System.out.println("EXCEPTION!");
			e.printStackTrace();
			is = null;
		}

		return is;
	}

	public static boolean isZipFile(File zip) {
		RandomAccessFile raf = null;
		long n = 0;

		try {
			raf = new RandomAccessFile(zip, "r");
			n = raf.readInt();  
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (n == 0x504B0304);
	}

	private static boolean deleteAll(File file) {
		File[] fileList = file.listFiles();
		for (File f : fileList) {
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
	 * @return true if the directory exists
	 */
	public static boolean setupWorkDir(String newFolder) {
		File fDir = new File(newFolder); 
		if (fDir.exists()) {
			//System.out.println("Removing session directory");
			deleteAll(fDir);
		}
		fDir.mkdir();
		System.out.println("New work directory: " + fDir.getAbsolutePath());
		return fDir.exists();
	}

	/**
	 * Generates a ZIP archive
	 * @param sSource
	 * @param sTarget
	 */
	public static void Zip(String sSource, String sTarget) {
		byte[] buffer = new byte[1024];

		try{

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

		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Extracts a file from ZIP archives to the folder specified in the argument
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
}
