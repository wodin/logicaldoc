package com.logicaldoc.core.text.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Text extractor for HyperText Markup Language (HTML).
 */
public class HTMLParser extends AbstractParser {

    /**
     * Logger instance.
     */
    protected static Log logger = LogFactory.getLog(HTMLParser.class);


    //-------------------------------------------------------< TextExtractor >

    /**
     * {@inheritDoc}
     */
    public Reader extractText(InputStream stream,
                              String type,
                              String encoding) throws IOException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            HTMLSAXParser parser = new HTMLSAXParser();
            SAXResult result = new SAXResult(new DefaultHandler());

            Reader reader;
            if (encoding != null) {
                reader = new InputStreamReader(stream, encoding);
            } else {
                reader = new InputStreamReader(stream);
            }
            SAXSource source = new SAXSource(parser, new InputSource(reader));
            transformer.transform(source, result);

            return new StringReader(parser.getContents());
        } catch (TransformerConfigurationException e) {
            logger.warn("Failed to extract HTML text content", e);
            return new StringReader("");
        } catch (TransformerException e) {
            logger.warn("Failed to extract HTML text content", e);
            return new StringReader("");
        } finally {
            stream.close();
        }
    }

	public void parse(File file) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			Reader read = extractText(fis, null, "UTF-8");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			
			BufferedReader br = new BufferedReader(read);
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				System.out.println(inputLine);
				bw.write(inputLine);
				bw.newLine();
			}
			
			bw.flush();
			osw.flush();
			osw.close();
			content = new String(baos.toByteArray(), "UTF-8");

			baos.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
