package com.logicaldoc.core.text.parser;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.logicaldoc.util.StringUtil;

/**
 * Text extractor for XML documents. This class extracts the text content and
 * attribute values from XML documents.
 * <p>
 * This class can handle any XML-based format. However, it often makes sense to
 * use more specialized extractors that better understand the specific content
 * type.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class XMLParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(XMLParser.class);

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		try {
			CharArrayWriter writer = new CharArrayWriter();
			ExtractorHandler handler = new ExtractorHandler(writer);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);

			// It is unspecified whether the XML parser closes the stream when
			// done parsing. To ensure that the stream gets closed just once,
			// we prevent the parser from closing it by catching the close()
			// call and explicitly close the stream in a finally block.
			InputSource source = new InputSource(new FilterInputStream(input) {
				public void close() {
				}
			});
			if (encoding != null) {
				try {
					Charset.forName(encoding);
					source.setEncoding(encoding);
				} catch (Exception e) {
					log.warn("Unsupported encoding '" + encoding + "', using default ("
							+ System.getProperty("file.encoding") + ") instead.");
				}
			}
			reader.parse(source);

			content = StringUtil.writeToString(new CharArrayReader(writer.toCharArray()));
		} catch (Exception e) {
			log.warn("Failed to extract XML text content", e);
		}
	}
}