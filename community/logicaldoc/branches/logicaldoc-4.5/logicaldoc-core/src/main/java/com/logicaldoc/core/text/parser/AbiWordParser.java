package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Text extractor for AbiWord documents.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5.2
 */
public class AbiWordParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(AbiWordParser.class);

	public void parse(File file) {

		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			
			String encoding = "UTF-8";
			Reader reader = extractText(stream, null, encoding);
			
			content = readText(reader, encoding);

		} catch (Exception ex) {
			logger.warn("Failed to extract AbiWord text content", ex);
			content = "";
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				saxParserFactory.setValidating(false);
				SAXParser saxParser = saxParserFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				xmlReader.setFeature("http://xml.org/sax/features/validation", false);
				xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

				AbiWordContentHandler contentHandler = new AbiWordContentHandler();
				xmlReader.setContentHandler(contentHandler);
				xmlReader.parse(new InputSource(stream));
				
				return new StringReader(contentHandler.getContent());
		} catch (Exception e) {
			logger.warn("Failed to extract AbiWord text content", e);
			return new StringReader("");
		} finally{
			stream.close();
		}
	}

	// --------------------------------------------< AbiWordContentHandler >

	private class AbiWordContentHandler extends DefaultHandler {

		private StringBuilder content;

		private boolean appendChar;

		public AbiWordContentHandler() {
			content = new StringBuilder();
			appendChar = false;
		}

		/**
		 * Returns the text content extracted from parsed content.xml
		 */
		public String getContent() {
			String tmp = content.toString();
			if (tmp != null && tmp.length() > 0) {
				// Clean all the unwanted characters
				tmp = tmp.replaceAll("[<>\"��`]", "");
			}
			return tmp;
		}

		public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
				throws SAXException {
			if (rawName.startsWith("p")) {
				appendChar = true;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (appendChar) {
				content.append(ch, start, length).append(" ");
			}
		}

		public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName)
				throws SAXException {
			appendChar = false;
		}
	}

}
