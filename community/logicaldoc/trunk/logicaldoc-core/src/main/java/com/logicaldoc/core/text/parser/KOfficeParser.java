package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
 * Text extractor for OpenOffice documents.
 * tested with KOffice 1.6.3 documents
 * 
 * @author Alessandro Gasparini
 */
public class KOfficeParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(KOfficeParser.class);
	
	public void parse(File file) {
		InputStream stream = null;
        try {
        	stream = new FileInputStream(file);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(false);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setFeature("http://xml.org/sax/features/validation", false);
            xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            ZipInputStream zis = new ZipInputStream(stream);
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

            content = contentHandler.getContent();
            
			
		} catch (Exception ex) {
            logger.warn("Failed to extract KOffice_1.6.x text content", ex);
            content = "";
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
    //--------------------------------------------< OpenOfficeContentHandler >

    private class KOfficeContentHandler extends DefaultHandler {

        private StringBuffer content;
        private boolean appendChar;

        public KOfficeContentHandler() {
            content = new StringBuffer();
            appendChar = false;
        }

        /**
         * Returns the text content extracted from parsed content.xml
         */
        public String getContent() {
            return content.toString();
        }

        public void startElement(String namespaceURI, String localName,
                                 String rawName, Attributes atts)
                throws SAXException {
            if (rawName.equalsIgnoreCase("TEXT")) {
                appendChar = true;
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (appendChar) {
                content.append(ch, start, length).append(" ");
            }
        }

        public void endElement(java.lang.String namespaceURI,
                               java.lang.String localName,
                               java.lang.String qName)
                throws SAXException {
            appendChar = false;
        }
    }

}
