package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.charset.CharsetDetector;
import com.logicaldoc.util.charset.CharsetMatch;

/**
 * Class for parsing text (*.txt) files.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.5
 */
public class TXTParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(TXTParser.class);

	@Override
	public void parse(File file, Locale locale, String encoding) {

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);

			String msEncoding = null;
			if (StringUtils.isEmpty(encoding)) {
				// Determine the most probable encoding
				try {
					CharsetDetector cd = new CharsetDetector();
					cd.setText(bis);
					CharsetMatch cm = cd.detect();
					if (cm != null) {
						if (Charset.isSupported(cm.getName())) 
							msEncoding = cm.getName();
					}
				} catch (Throwable th) {
					log.warn("Error during TXT charset detection", th);
				} 
			} else {
				msEncoding = encoding;
			}
			parse(bis, locale, msEncoding);
		} catch (Exception ex) {
			log.warn("Failed to extract TXT text content", ex);
			content = "";
		} finally {
			try {
				if (bis != null) 
					bis.close();
				if (fis != null) 
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		try {
			if (encoding != null)
				content = StringUtil.writeToString(new InputStreamReader(input, encoding));
		} catch (UnsupportedEncodingException e) {
			log.warn("Unsupported encoding '" + encoding + "', using default (" + System.getProperty("file.encoding")
					+ ") instead.");
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}
}