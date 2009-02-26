package com.logicaldoc.core.searchengine;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.logicaldoc.core.i18n.DateBean;

/**
 * A document inside the full-text index
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public class LuceneDocument {
	public static final String FIELD_TEMPLATE_ID = "templateId";

	public static final String FIELD_KEYWORDS = "keywords";

	public static final String FIELD_CONTENT = "content";

	public static final String FIELD_TYPE = "type";

	public static final String FIELD_PATH = "path";

	public static final String FIELD_SOURCE_TYPE = "sourceType";

	public static final String FIELD_CREATION = "creation";

	public static final String FIELD_DATE = "date";

	public static final String FIELD_SOURCE_DATE = "sourceDate";

	public static final String FIELD_COVERAGE = "coverage";

	public static final String FIELD_SOURCE_AUTHOR = "sourceAuthor";

	public static final String FIELD_SOURCE = "source";

	public static final String FIELD_SIZE = "size";

	public static final String FIELD_TITLE = "title";

	public static final String FIELD_DOC_ID = "docId";

	public static final String FIELD_CUSTOM_ID = "customId";

	private File file = null;

	private Document doc;

	private String content = "";

	private com.logicaldoc.core.document.Document document = new com.logicaldoc.core.document.Document();

	public LuceneDocument(com.logicaldoc.core.document.Document d) {
		document = d;
	}

	/**
	 * Builds a lucene compatible document of a file. The document contains 7
	 * Fields: name - name of the document size - size of the document in bytes
	 * path - path of the document for calling it on the web browser (e.g
	 * DocFrame.do?menuId=1) type - file format (e.g pdf, sxw) date - date of
	 * creation content - full text of the document summary - first 500 letters
	 * of the content
	 * 
	 * @param f - File of which the document should be built.
	 * @return
	 */
	public Document getDocument(File f, String content) {
		file = f;
		doc = new Document();
		setDocId();
		setTitle();
		setSize();
		setDocData();
		setType();
		setContent(content);
		setKeywords();
		setPath();
		setTemplate();
		setExtendedAttributes();
		return doc;
	}

	public void setDocId() {
		doc.add(new Field(FIELD_DOC_ID, String.valueOf(document.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		if (StringUtils.isNotEmpty(document.getCustomId()))
			doc.add(new Field(FIELD_CUSTOM_ID, document.getCustomId(), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	/**
	 * Returns the content of the indexed document.
	 * 
	 * @return
	 */
	public String getContent() {
		return content;
	}

	protected void setTitle() {
		doc.add(new Field(FIELD_TITLE, document.getTitle(), Field.Store.YES, Field.Index.TOKENIZED));
	}

	protected void setSize() {
		//Save the size in bytes
		doc.add(new Field(FIELD_SIZE, Long.toString(document.getFileSize()), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	protected void setDocData() {
		doc.add(new Field(FIELD_SOURCE, document.getSource() != null ? document.getSource() : "", Field.Store.NO,
				Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_AUTHOR, document.getSourceAuthor() != null ? document.getSourceAuthor() : "",
				Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_TYPE, document.getSourceType() != null ? document.getSourceType() : "",
				Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_COVERAGE, document.getCoverage() != null ? document.getCoverage() : "", Field.Store.NO,
				Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_DATE, document.getSourceDate() != null ? DateBean.toCompactString(document
				.getSourceDate()) : "", Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_DATE, document.getDate() != null ? DateBean.toCompactString(document.getDate()) : "",
				Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_CREATION, document.getCreation() != null ? DateBean.toCompactString(document
				.getCreation()) : "", Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	protected void setPath() {
		doc.add(new Field(FIELD_PATH, document.getPath(), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	protected void setType() {
		String type = FilenameUtils.getExtension(document.getFileName());
		doc.add(new Field(FIELD_TYPE, type, Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	protected void setTemplate() {
		if (document.getTemplate() != null)
			doc.add(new Field(FIELD_TEMPLATE_ID, Long.toString(document.getTemplate().getId()), Field.Store.YES,
					Field.Index.UN_TOKENIZED));
	}

	protected void setExtendedAttributes() {
		for (String attribute : document.getAttributeNames()) {
			String value = document.getValue(attribute);
			if (StringUtils.isNotEmpty(value)) {
				// Prefix all extended attributes with 'ext_' in order to avoid
				// collisions with standard fields
				doc.add(new Field("ext_" + attribute, value, Field.Store.NO, Field.Index.TOKENIZED));
			}
		}
	}

	protected void setContent(String content) {
		doc.add(new Field(FIELD_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
	}

	protected void setKeywords() {
		doc.add(new Field(FIELD_KEYWORDS, document.getKeywordsString(), Field.Store.YES, Field.Index.TOKENIZED));
	}
}