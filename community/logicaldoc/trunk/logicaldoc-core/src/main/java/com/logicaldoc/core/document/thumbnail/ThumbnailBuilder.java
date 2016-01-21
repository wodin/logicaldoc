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
	 * @param sid The session identifier (optional)
	 * @param tenant The tenant name
	 * @param src The source file
	 * @param srcFileName The original file name
	 * @param size The thumbnail size
	 * @param dest The destination thumbnail file
	 * @param quality Compression quality(0..100, 100 is maximum quality)
	 * @throws IOException
	 */
	public void buildThumbnail(String sid, String tenant, File src, String srcFileName, File dest, int size, int quality)
			throws IOException;
}