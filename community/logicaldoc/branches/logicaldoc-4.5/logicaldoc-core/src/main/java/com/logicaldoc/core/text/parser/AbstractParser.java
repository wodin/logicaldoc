package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of a Parser
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public abstract class AbstractParser implements Parser {
	
	protected static Log logger = LogFactory.getLog(AbstractParser.class);
	
	protected String content = "";

	@Override
	public String getAuthor() {
		return "";
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getTags() {
		return "";
	}

	@Override
	public String getSourceDate() {
		return "";
	}

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public String getVersion() {
		return "";
	}

	@Override
	public void parse(File file, Locale locale) {
		parse(file);
	}

	public String readText(Reader reader, String encoding) throws IOException {
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			OutputStreamWriter osw = new OutputStreamWriter(baos, encoding);
			BufferedWriter bw = new BufferedWriter(osw);
			
			BufferedReader br = new BufferedReader(reader);
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine);
				bw.newLine();
			}
			
			bw.flush();
			osw.flush();
			osw.close();
		} catch (Exception e) {
			logger.error("Unable to extract text from document", e);
			return "";
		} finally {
			reader.close();
		}
		return new String(baos.toByteArray(), encoding);
	}
}