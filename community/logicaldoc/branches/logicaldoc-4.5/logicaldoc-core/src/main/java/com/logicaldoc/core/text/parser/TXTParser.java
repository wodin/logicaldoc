package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.CharsetDetector;

/**
 * Class for parsing text (*.txt) files.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class TXTParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(TXTParser.class);

	public void parse(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			
			// Determine the most probable encoding
			String msEncoding = null;
			try {
				String[] encodings = CharsetDetector.detectEncodings(fis);
				if (encodings != null && encodings.length > 0)
					msEncoding = encodings[0];
			} catch (IOException ioe) {
			}
			System.out.println("Detected encoding: " + msEncoding);
			fis = new FileInputStream(file);
			Reader reader = extractText(fis, null, msEncoding);

			content = readText(reader, "UTF-8");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			content = "";
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Wraps the given input stream to an {@link InputStreamReader} using the
	 * given encoding, or the platform default encoding if the encoding is not
	 * given or is unsupported.
	 * 
	 * @param stream
	 *            binary stream
	 * @param type
	 *            ignored
	 * @param encoding
	 *            character encoding, optional
	 * @return reader for the plain text content
	 * @throws IOException
	 *             if the binary stream can not be closed in case of an encoding
	 *             issue
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			if (encoding != null) {
				return new InputStreamReader(stream, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			logger.warn("Unsupported encoding '" + encoding + "', using default ("
					+ System.getProperty("file.encoding") + ") instead.");
		}
		return new InputStreamReader(stream);
	}
}