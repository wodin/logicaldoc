package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

import com.logicaldoc.util.StringUtil;

/**
 * Text extractor for Portable Document Format (PDF). For parsing uses an
 * external library: PDFBox. Created on 4. November 2003, 18:09
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.5
 */
public class PDFParser extends AbstractParser {

	private String author;

	private String title;

	private String sourceDate;

	private String tags;

	protected static Log log = LogFactory.getLog(PDFParser.class);

	public String getContent() {
		return content;
	}

	/**
	 * @return Returns the author.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return Returns the sourceDate.
	 */
	public String getSourceDate() {
		return sourceDate;
	}

	/**
	 * @return Returns the tags.
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	@Override
	public void parse(InputStream input) {
		try {
			org.pdfbox.pdfparser.PDFParser parser = new org.pdfbox.pdfparser.PDFParser(new BufferedInputStream(input));
			try {
				parser.parse();
				PDDocument document = parser.getPDDocument();
				CharArrayWriter writer = new CharArrayWriter();

				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setLineSeparator("\n");
				stripper.writeText(document, writer);

				content = StringUtil.writeToString(new CharArrayReader(writer.toCharArray()));
			} finally {
				try {
					PDDocument doc = parser.getPDDocument();
					if (doc != null) {
						doc.close();
					}
				} catch (IOException e) {
				}
			}
		} catch (Exception e) {
			// it may happen that PDFParser throws a runtime
			// exception when parsing certain pdf documents
			log.warn("Failed to extract PDF text content", e);
		}
	}

	@Override
	public void parse(File file) {
		log.info("Parsing file " + file.getPath());

		author = "";
		title = "";
		sourceDate = "";
		tags = "";
		content = "";
		PDDocument pdfDocument = null;

		try {
			InputStream is = new FileInputStream(file);
			org.pdfbox.pdfparser.PDFParser parser = new org.pdfbox.pdfparser.PDFParser(is);

			if (parser != null) {
				parser.parse();
			} else {
				throw new Exception("Can not parse pdf file " + file.getName());
			}

			pdfDocument = parser.getPDDocument();

			if (pdfDocument == null) {
				throw new Exception("Can not get pdf document " + file.getName() + " for parsing");
			}

			try {
				PDDocumentInformation information = pdfDocument.getDocumentInformation();
				if (information == null) {
					throw new Exception("Can not get information from pdf document " + file.getName());
				}

				author = information.getAuthor();

				if (author == null) {
					author = "";
				}

				title = information.getTitle();

				if (title == null) {
					title = "";
				}

				Calendar calendar = null;
				try {
					calendar = information.getCreationDate();
				} catch (Throwable e) {
					log.error("Bad date format " + e.getMessage());
				}
				Date date = null;

				if (calendar != null) {
					date = calendar.getTime();
				}

				if (date != null) {
					sourceDate = DateFormat.getDateInstance().format(date);
				} else {
					sourceDate = "";
				}

				tags = information.getKeywords();

				if (tags == null) {
					tags = "";
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}

			// create a tmp output stream with the size of the content.
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

			PDFTextStripper stripper = new PDFTextStripper();

			try {
				if (pdfDocument.isEncrypted())
					throw new IOException("Encripted document");

				stripper.writeText(pdfDocument, writer);
			} catch (IOException e) {
				log.error("Unable to decrypt pdf document");
				e.printStackTrace();
				writer.write("encrypted document");
				title = file.getName().substring(0, file.getName().lastIndexOf('.'));
				author = "";
			}
			writer.flush();
			writer.close();
			content = new String(out.toByteArray(), "UTF-8");
			is.close();
			out.close();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}
}