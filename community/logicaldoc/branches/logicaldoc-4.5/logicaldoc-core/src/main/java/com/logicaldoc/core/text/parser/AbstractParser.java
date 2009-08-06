package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Abstract implementation of a Parser
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public abstract class AbstractParser implements Parser {
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

	protected String readText(Reader reader, String encoding) throws UnsupportedEncodingException {
	
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
			e.printStackTrace();
			return "";
		}
		return new String(baos.toByteArray(), encoding);
	}
}