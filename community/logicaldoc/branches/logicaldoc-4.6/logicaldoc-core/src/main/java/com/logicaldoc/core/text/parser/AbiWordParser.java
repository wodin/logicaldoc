package com.logicaldoc.core.text.parser;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.logicaldoc.util.StringUtil;

/**
 * Text extractor for AbiWord documents.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5.2
 */
public class AbiWordParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(AbiWordParser.class);

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

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			AbiWordContentHandler contentHandler = new AbiWordContentHandler();
			xmlReader.setContentHandler(contentHandler);
			xmlReader.parse(new InputSource(input));

			content = StringUtil.writeToString(new StringReader(contentHandler.getContent()));
		} catch (Exception e) {
			log.warn("Failed to extract AbiWord text content", e);
		}
	}
}