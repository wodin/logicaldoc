package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;

/**
 * Implementations of this interface are specialized classes that produce
 * thumbnails for a specific type of document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public interface ThumbnailBuilder {

	/**
	 * Produce the thumbnail
	 * 
	 * @param tenant The tenant name
	 * @param src The source file
	 * @param srcFileName The original file name
	 * @param size The thumbnail size
	 * @param dest The destination thumbnail file
	 * @param quality Compression quality(0..100, 100 is maximum quality)
	 * @throws IOException
	 */
	public void buildThumbnail(String tenant, File src, String srcFileName, File dest, int size, int quality)
			throws IOException;

	/**
	 * Produce the full preview
	 * 
	 * @param tenant The tenant name
	 * @param src The source file
	 * @param srcFileName The original file name
	 * @param dest The destination root folder
	 * @throws IOException
	 */
	public File buildPreview(String tenant, File src, String srcFileName, File dest) throws IOException;
}