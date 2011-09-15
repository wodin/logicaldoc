package com.logicaldoc.core.text.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
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

	private static int count = Integer.MAX_VALUE;

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
	public void internalParse(InputStream input) {
		author = "";
		title = "";
		sourceDate = "";
		tags = "";
		PDDocument pdfDocument = null;

		// Will store the extracted text
		StringBuffer buffer = new StringBuffer();

		try {
			org.apache.pdfbox.pdfparser.PDFParser parser = new org.apache.pdfbox.pdfparser.PDFParser(input);

			if (parser != null) {
				parser.parse();

				pdfDocument = parser.getPDDocument();
				if (pdfDocument == null) {
					throw new Exception("Can not get pdf document for parsing");
				}

				if (pdfDocument.isEncrypted()) {
					try {
						pdfDocument.decrypt("");
					} catch (InvalidPasswordException e) {
						log.error("Error: The document is encrypted.");
						buffer.append("The document is encrypted");
						return;
					}
				}

				// Strip text from the entire document
				parseDocument(pdfDocument, buffer);

				// Now parse the forms
				parseForm(pdfDocument, buffer);

				// Store all the extracted contents
				content.append(buffer.toString());
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

		// PDF Box is memory intensive so execute a gc every 100 parses
		if (count % 100 == 0)
			System.gc();
		count++;
	}

	/**
	 * Extract text and metadata from the main document
	 */
	protected void parseDocument(PDDocument pdfDocument, StringBuffer buffer) {
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

			Writer writer = new CharArrayWriter();
			PDFTextStripper stripper = new PDFTextStripper("UTF-8");
			try {
				stripper.writeText(pdfDocument, writer);
				writer.flush();
				buffer.append(writer.toString());
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
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Extract the text from the form fields
	 */
	@SuppressWarnings("rawtypes")
	public void parseForm(PDDocument pdfDocument, StringBuffer buffer) throws IOException {
		PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
		PDAcroForm acroForm = docCatalog.getAcroForm();

		if (acroForm == null)
			return;

		buffer.append("\n");

		List fields = acroForm.getFields();
		Iterator fieldsIter = fields.iterator();

		log.debug(new Integer(fields.size()).toString() + " top-level fields were found on the form");

		while (fieldsIter.hasNext()) {
			PDField field = (PDField) fieldsIter.next();
			parseField(field, "|--", field.getPartialName(), buffer);
		}
	}

	/**
	 * Extracts the field contents populating the given buffer. This method is
	 * recursive, use carefully.
	 */
	private void parseField(PDField field, String sLevel, String sParent, StringBuffer buffer) throws IOException {
		List<COSObjectable> kids = field.getKids();
		if (kids != null) {
			Iterator<COSObjectable> kidsIter = kids.iterator();
			if (!sParent.equals(field.getPartialName())) {
				sParent = sParent + "." + field.getPartialName();
			}

			while (kidsIter.hasNext()) {
				Object pdfObj = kidsIter.next();
				if (pdfObj instanceof PDField) {
					PDField kid = (PDField) pdfObj;
					parseField(kid, "|  " + sLevel, sParent, buffer);
				}
			}
		} else {
			try {
				if (StringUtils.isNotEmpty(field.getValue())) {
					buffer.append(" ");
					buffer.append(field.getValue());
				}
			} catch (Throwable t) {

			}
		}
	}
}