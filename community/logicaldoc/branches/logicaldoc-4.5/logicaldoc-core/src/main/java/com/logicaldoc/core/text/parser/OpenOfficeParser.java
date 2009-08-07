package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
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
 * Text extractor for OpenOffice/OpenDocument documents.
 * 
 * Tested with OpenOffice documents produced with 2.3,2.4,3.0.1 OO release Works
 * with extensions (odt, ods, odp) and templates (ott, ots, otp). Tested with
 * StarOffice documents (sxw, sxc, sxi)
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5.2
 */
public class OpenOfficeParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(OpenOfficeParser.class);

	public void parse(File file) {

		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			
			Reader reader = extractText(stream, null, null);

			content = readText(reader, "UTF-8");

		} catch (Exception ex) {
			logger.warn("Failed to extract OpenOffice text content", ex);
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

			ZipInputStream zis = new ZipInputStream(stream);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null && !ze.getName().equals("content.xml")) {
				ze = zis.getNextEntry();
			}

			OpenOfficeContentHandler contentHandler = new OpenOfficeContentHandler();
			xmlReader.setContentHandler(contentHandler);
			try {
				xmlReader.parse(new InputSource(zis));
			} finally {
				zis.close();
			}

			return new StringReader(contentHandler.getContent());
		} catch (ParserConfigurationException e) {
			logger.warn("Failed to extract OpenOffice text content", e);
			return new StringReader("");
		} catch (SAXException e) {
			logger.warn("Failed to extract OpenOffice text content", e);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}

	// --------------------------------------------< OpenOfficeContentHandler >

	private class OpenOfficeContentHandler extends DefaultHandler {

		private StringBuffer content;

		private boolean appendChar;

		public OpenOfficeContentHandler() {
			content = new StringBuffer();
			appendChar = false;
		}

		/**
		 * Returns the text content extracted from parsed content.xml
		 */
		public String getContent() {
			return content.toString();
		}

		public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
				throws SAXException {
			if (rawName.startsWith("text:")) {
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
