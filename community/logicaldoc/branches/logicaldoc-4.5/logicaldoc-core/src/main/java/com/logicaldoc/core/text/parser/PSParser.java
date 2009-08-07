package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class PSParser extends AbstractParser {
	
	protected static Log logger = LogFactory.getLog(PSParser.class);

	private String version = "";

	private BufferedReader reader;

	public void parse(File file) {
		try {
			InputStream stream = new FileInputStream(file);
			Reader reader = extractText(stream, null, null);
			content = readText(reader, "UTF-8");
			
		} catch (Exception ex) {
			logger.warn("Failed to extract PostScrip text content", ex);
		}
	}

	private String parse_v2() throws IOException {
		
		boolean isComment = false;
		boolean isText = false;
		boolean isConnector = false;
		int ichar = 0;
		StringBuilder sb = new StringBuilder();

		while ((ichar = reader.read()) > 0) {
			if (isConnector) {
				if (ichar < 108) {
					sb.append((char) 32);
				}

				isConnector = false;
			}

			if (ichar == 37) {
				isComment = true;
			}

			if ((ichar == 10) && isComment) {
				isComment = false;
			}

			if ((ichar == 41) && isText) {
				isConnector = true;
				isText = false;
			}

			if (isText) {
				sb.append((char) ichar);
			}

			if ((ichar == 40) && !isComment) {
				isText = true;
			}
		}
		reader.close();
		return sb.toString();
	}

	private String parse_v3() throws IOException {
		
		StringBuilder stmt = new StringBuilder();
		boolean isComment = false;
		boolean isText = false;
		boolean isBMP = false;
		boolean isStore = false;
		int store = 0;
		int ichar = 0;
		StringBuilder sb = new StringBuilder();

		while ((ichar = reader.read()) > 0) {
			if (ichar == 37) {
				isComment = true;
			}

			if ((ichar == 10) && isComment) {
				isComment = false;
			}

			if ((ichar == 41) && isText) {
				isText = false;
			}

			if (isText && !isBMP) {
				sb.append((char) ichar);
			}

			if ((ichar == 40) && !isComment && !isBMP) {
				isText = true;
			}

			if (isStore) {
				if ((store == 9) || (ichar == 32) || (ichar == 10)) {
					isStore = false;
					store = 0;

					if (stmt.toString().equals("BEGINBITM")) {
						isText = false;
						isBMP = true;
					}

					if (stmt.toString().equals("ENDBITMAP")) {
						isBMP = false;
					}

					stmt.delete(0, stmt.length());
				} else {
					stmt.append((char) ichar);
					store++;
				}
			}

			if (!isComment && !isStore && ((ichar == 66) || (ichar == 69))) {
				isStore = true;
				stmt.append((char) ichar);
				store++;
			}
		}
		reader.close();
		return sb.toString();
	}

	public String getVersion() {
		return version;
	}

	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			reader = new BufferedReader(new InputStreamReader(stream));

			String line = reader.readLine();
			String textExtracted = "";
			if ((line != null) && (line.length() >= 3)) {
				version = line.substring(line.length() - 3);

				if (version.startsWith("2")) {
					textExtracted = parse_v2();
				}

				if (version.startsWith("3")) {
					textExtracted = parse_v3();
				}
			}
			return new StringReader(textExtracted);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}
}