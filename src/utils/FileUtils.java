package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/***
 * 
 * @author danielgalassi@gmail.com
 *
 */
public class FileUtils {

	public static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

	public static XMLReader getXMLReader() {
		SAXParserFactory SAXpf = SAXParserFactory.newInstance();
		//enabling the namespaces processing
		if(!SAXpf.isNamespaceAware())
			SAXpf.setNamespaceAware(true);

		//get a SAXParser object
		SAXParser SAXparser= null;
		//get the XMLReader
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

	public static InputSource getIS(File theFile) {
		InputStream in = null;
		InputSource is = null;

		try {
			in = new FileInputStream(theFile);
			is = new InputSource(new InputStreamReader(in));
		} catch (Exception e) {
			e.printStackTrace();
		}

		is.setEncoding("UTF-8");
		return is;
	}

	public static boolean setupWorkDir(String sPath) {
		File fDir = new File(sPath); 
		if (fDir.exists())
			fDir.delete();
		else {
			fDir.mkdir();
			System.out.println("Session directory: " + fDir.getAbsolutePath());
		}
		return fDir.exists();
	}

	static boolean isZipFileValid(final File file) {
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(file);
			return true;
		} catch (ZipException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (zipfile != null) {
					zipfile.close();
					zipfile = null;
				}
			} catch (IOException e) {
			}
		}
	}

	public static boolean isZipStream(InputStream in) {
		if (!in.markSupported()) {
			in = new BufferedInputStream(in);
		}
		boolean isZip = true;
		try {
			in.mark(MAGIC.length);
			for (int i = 0; i < MAGIC.length; i++) {
				if (MAGIC[i] != (byte) in.read()) {
					isZip = false;
					break;
				}
			}
			in.reset();
		} catch (IOException e) {
			isZip = false;
		}
		return isZip;
	}

	public static boolean isZipFile(File f) {

		boolean isZip = true;
		byte[] buffer = new byte[MAGIC.length];
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			raf.readFully(buffer);
			for (int i = 0; i < MAGIC.length; i++) {
				if (buffer[i] != MAGIC[i]) {
					isZip = false;
					break;
				}
			}
			raf.close();
		} catch (Throwable e) {
			isZip = false;
		}
		return isZip;
	}

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
				newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("Unzipping: "+ newFile.getAbsolutePath());
				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);             

				int len;
				while ((len = zis.read(buffer)) > 0)
					fos.write(buffer, 0, len);

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
