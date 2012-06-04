package com.logicaldoc.core.parser;

import java.io.InputStream;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.logicaldoc.util.StringUtil;

/**
 * Text extractor for KOffice 1.6 documents.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5.2
 */
public class KOfficeParser extends AbstractParser {

	protected static Logger log = LoggerFactory.getLogger(KOfficeParser.class);

	// --------------------------------------------< KOfficeContentHandler >

	private class KOfficeContentHandler extends DefaultHandler {

		private StringBuilder content;

		private boolean appendChar;

		public KOfficeContentHandler() {
			content = new StringBuilder();
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
			if (rawName.equalsIgnoreCase("TEXT")) {
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

	@Override
	public void internalParse(InputStream input) {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			ZipInputStream zis = new ZipInputStream(input);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null && !ze.getName().equals("maindoc.xml")) {
				ze = zis.getNextEntry();
			}

			KOfficeContentHandler contentHandler = new KOfficeContentHandler();
			xmlReader.setContentHandler(contentHandler);
			try {
				xmlReader.parse(new InputSource(zis));
			} finally {
				zis.close();
			}

			content.append(StringUtil.writeToString(new StringReader(contentHandler.getContent())));
		} catch (Exception e) {
			log.warn("Failed to extract KOffice text content", e);
		}
	}
}
