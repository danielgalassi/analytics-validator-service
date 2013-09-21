package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/***
 * 
 * @author danielgalassi@gmail.com
 *
 */
public class FileUtils {

	public static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

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
			ZipEntry zipEntry = new ZipEntry("spy.log");
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
}
