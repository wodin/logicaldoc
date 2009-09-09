package com.logicaldoc.util.io;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.util.charset.CharsetMatch;

public class ZipUtilTest {

	@Before
	public void setUp() throws Exception {
		File dir = new File("target/test");
		dir.mkdirs();
		dir.mkdir();
	}

	@After
	public void tearDown() throws Exception {
		File dir = new File("target/test");
		if (dir.exists())
			try {
				FileUtils.forceDelete(dir);
			} catch (IOException e) {
			}
	}

	@Test
	public void testReadEntry() throws IOException {
		File file = new File("target/test.zip");
		FileUtil.copyResource("/test.zip", file);
		byte[] in = ZipUtil.readEntry(file, "index.xsl");
		ByteArrayInputStream is = new ByteArrayInputStream(in);
		assertNotNull(is);
		File dir = new File("target/test");
		dir.mkdirs();
		dir.mkdir();
		File tmp = new File("target/test/index.xsl");
		OutputStream out;
		try {
			out = new FileOutputStream(tmp);
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			is.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDetectedEncoding() throws IOException {

		System.out.println(" testGetDetectedEncoding()");

		File zipsource = new File("c:/tmp/zips/testsimple.zip");
		CharsetMatch cm = ZipUtil.detectedEncoding(zipsource);

		System.out.println(cm.getName());
		System.out.println(cm.getMatchType());
		System.out.println(cm.getConfidence());
	}

	@Test
	public void testGetDetectedEncodingUTF8() throws IOException {

		System.out.println(" testGetDetectedEncodingUTF8()");

		File zipsource = new File("c:/tmp/zips/htmls.zip");
		CharsetMatch cm = ZipUtil.detectedEncoding(zipsource);

		System.out.println(cm.getName());
		System.out.println(cm.getMatchType());
		System.out.println(cm.getConfidence());
	}

	@Test
	public void testCreateZip() {
		try {
			zipDirectory("C:/tmp/zips/Greek/GreekISO88597", "C:/tmp/zips/GreekWindows-1253.zip");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Zip the contents of the directory, and save it in the zipfile */
	public static void zipDirectory(String sourcePath, String zipPath) throws IOException, IllegalArgumentException {

		// Check that the directory is a directory, and get its contents
		File folder = new File(sourcePath);
		if (!folder.isDirectory())
			throw new IllegalArgumentException("Compress: not a directory:  " + sourcePath);

		String[] entries = folder.list();
		byte[] buffer = new byte[4096]; // Create a buffer for copying
		int bytes_read;

		// Create a stream to compress data and write it to the zipfile
		File destzip = new File(zipPath);
		ZipOutputStream out = new ZipOutputStream(destzip);
		out.setEncoding("windows-1253"); // define Latin/Greek Alphabet as encoding

		// Loop through all entries in the directory
		for (int i = 0; i < entries.length; i++) {

			File esource = new File(folder, entries[i]);
			if (esource.isDirectory()) {
				// Don't zip sub-directories
				continue;
			}

			FileInputStream in = new FileInputStream(esource); // Stream to read

			// file
			ZipEntry entry = new ZipEntry(esource.getName()); // Make a ZipEntry
			out.putNextEntry(entry); // Store entry
			
			while ((bytes_read = in.read(buffer)) != -1) {
				// Copy bytes
				out.write(buffer, 0, bytes_read);
			}
			
			in.close(); // Close input stream
		}
		
		// When we're done with the whole loop, close the output stream
		out.close();
	}

}
