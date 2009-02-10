package com.logicaldoc.core.document;



/**
 * The Document is the central entity of LogicalDOC. A Document is a persistent
 * business object and represents metadata over a single file stored into the
 * DMS.
 * <p>
 * Each document has one or more Versions. The most recent version is the one
 * used as default when we refer to a Document, but all previous versions are
 * accessible from the history even if the are not indexed.
 * <p>
 * A Document is written in a single language, this language defines the
 * full-text index in which the document's content will be stored.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 1.0
 */
public class Document extends AbstractDocument {
	public Document() {
	}
}