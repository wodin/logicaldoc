package com.logicaldoc.core.text.parser;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Text extractor for XML documents. This class extracts the text content
 * and attribute values from XML documents.
 * <p>
 * This class can handle any XML-based format. However, it often makes
 * sense to use more specialized extractors that better understand the
 * specific content type.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class XMLParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(XMLParser.class);

	public void parse(File file) {
		
		StringBuffer buffer = new StringBuffer();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			
			Reader reader = extractText(fis, null, "UTF-8");
			content = readText(reader, "UTF-8");
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			content = "";
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}
		content = buffer.toString();
	}
    
    
    /**
     * Returns a reader for the text content of the given XML document.
     * Returns an empty reader if the given encoding is not supported or
     * if the XML document could not be parsed.
     *
     * @param stream XML document
     * @param type XML content type
     * @param encoding character encoding, or <code>null</code>
     * @return reader for the text content of the given XML document,
     *         or an empty reader if the document could not be parsed
     * @throws IOException if the XML document stream can not be closed
     */
    public Reader extractText(InputStream stream, String type, String encoding)
            throws IOException {
        try {
            CharArrayWriter writer = new CharArrayWriter();
            ExtractorHandler handler = new ExtractorHandler(writer);

            // TODO: Use a pull parser to avoid the memory overhead
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);

            // It is unspecified whether the XML parser closes the stream when
            // done parsing. To ensure that the stream gets closed just once,
            // we prevent the parser from closing it by catching the close()
            // call and explicitly close the stream in a finally block.
            InputSource source = new InputSource(new FilterInputStream(stream) {
                public void close() {
                }
            });
            if (encoding != null) {
                try {
                    Charset.forName(encoding);
                    source.setEncoding(encoding);
                } catch (Exception e) {
                	logger.warn("Unsupported encoding '" + encoding +"', using default (" + System.getProperty("file.encoding") +") instead.");
                }
            }
            reader.parse(source);

            return new CharArrayReader(writer.toCharArray());
        } catch (ParserConfigurationException e) {
            logger.warn("Failed to extract XML text content", e);
            return new StringReader("");
        } catch (SAXException e) {
            logger.warn("Failed to extract XML text content", e);
            return new StringReader("");
        } finally {
            stream.close();
        }
    }
}