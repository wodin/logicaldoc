package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import com.logicaldoc.util.StringUtil;

/**
 * Text extractor for HyperText Markup Language (HTML).
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class HTMLParser extends AbstractParser {

	/**
	 * Logger instance.
	 */
	protected static Log log = LogFactory.getLog(HTMLParser.class);

	public void parse(File file, Locale locale, String encoding) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			String enc = getCharEncoding(fis);
			fis = new FileInputStream(file);
			parse(fis, null, enc);
		} catch (Exception ex) {
			log.warn("Failed to extract HTML text content", ex);
		}
	}

	private String getCharEncoding(FileInputStream fis) throws IOException {
		Reader read = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(read);
		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			// System.out.println(inputLine);
			if (inputLine.indexOf("charset") != -1) {
				// System.out.println("inputLine: " + inputLine);
				String chs = inputLine.substring(inputLine.indexOf("charset") + "charset=".length());
				if (chs.indexOf("\"") != -1)
					chs = chs.substring(0, chs.indexOf("\""));
				if (chs.indexOf(">") != -1)
					chs = chs.substring(0, chs.indexOf(">"));
				// System.out.println("charset: " + chs);
				// verify if is a valid charset
				boolean supportedCharset = Charset.isSupported(chs);
				if (supportedCharset == true)
					return chs;
			}
		}
		return "UTF-8";
	}

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();

			XMLParserConfiguration parserConfig = new HTMLConfiguration();

			ElementRemover remover = new ElementRemover();
			remover.removeElement("script");
			remover.removeElement("noscript");
			remover.removeElement("style");

			XMLDocumentFilter[] filters = { remover };
			parserConfig.setProperty("http://cyberneko.org/html/properties/filters", filters);

			HTMLSAXParser parser = new HTMLSAXParser(parserConfig);

			SAXResult result = new SAXResult(new DefaultHandler());

			Reader reader;
			if (encoding != null) {
				reader = new InputStreamReader(input, encoding);
			} else {
				reader = new InputStreamReader(input);
			}
			SAXSource source = new SAXSource(parser, new InputSource(reader));
			transformer.transform(source, result);
			content=StringUtil.writeToString(new StringReader(parser.getContents()));
		} catch (Exception e) {
			log.warn("Failed to extract HTML text content", e);
		}
	}
}