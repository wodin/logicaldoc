package com.logicaldoc.core.document.pdf;

import java.io.File;
import java.io.IOException;

/**
 * Implementations of this interface are specialized classes that produce PDF
 * for a specific type of document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.3
 */
public interface PdfConverter {

	/**
	 * Produce the PDF
	 * 
	 * @param tenant The tenant name
	 * @param src The source file
	 * @param srcFileName The original file name
	 * @param dest The destination thumbnail file
	 * @throws IOException
	 */
	public void createPdf(String tenant, File src, String srcFileName, File dest) throws IOException;
}