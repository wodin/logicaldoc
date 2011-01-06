package com.logicaldoc.core.text.parser;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

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
	 * @return the sourceDate.
	 */
	public String getSourceDate() {
		return sourceDate;
	}

	/**
	 * @return the tags.
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
		author = "";
		title = "";
		sourceDate = "";
		tags = "";
		content = "";
		PDDocument pdfDocument = null;

		try {
			org.apache.pdfbox.pdfparser.PDFParser parser = new org.apache.pdfbox.pdfparser.PDFParser(input);

			if (parser != null) {
				parser.parse();

				pdfDocument = parser.getPDDocument();
				if (pdfDocument == null) {
					throw new Exception("Can not get pdf document for parsing");
				}

				try {
					PDDocumentInformation information = pdfDocument.getDocumentInformation();
					if (information == null) {
						throw new Exception("Can not get information from pdf document");
					}

					author = information.getAuthor();
					if (author == null) {
						author = "";
					}

					title = information.getTitle();
					if (title == null) {
						title = "";
					}

					try {
						Calendar calendar = information.getCreationDate();
						Date date = calendar.getTime();
						sourceDate = DateFormat.getDateInstance().format(date);
						// In Italian it will be like 27-giu-2007
						// sourceDate =
						// DateFormat.getDateInstance(DateFormat.SHORT,
						// Locale.ENGLISH).format(date);
					} catch (Throwable e) {
						log.error("Bad date format " + e.getMessage());
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

				Writer writer = new CharArrayWriter();
				PDFTextStripper stripper = new PDFTextStripper("UTF-8");
				
				try {
					if (pdfDocument.isEncrypted()) {
						writer.write("encrypted document");
						log.warn("Unable to decrypt pdf document");
						throw new IOException("Encrypted document");
					}

					stripper.writeText(pdfDocument, writer);
					writer.flush();
					content = writer.toString();
				} catch (Throwable tw) {
					log.error("Exception reading pdf document: " + tw.getMessage());
					author = "";
				} finally {
					try {
						writer.close();
					} catch (Throwable e) {
						log.error(e.getMessage(), e);
					}
				}

			}
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			try {
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void parse(File file) {

		log.info("Parsing file " + file.getPath());

		InputStream is = null;
		try {
			is = new FileInputStream(file);
			parse(is);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}