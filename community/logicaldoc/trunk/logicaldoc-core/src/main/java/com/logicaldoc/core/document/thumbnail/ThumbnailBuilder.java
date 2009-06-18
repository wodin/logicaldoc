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
	 * @param src The source file
	 * @param srcFileName The original file name
	 * @param size The thumbnail size
	 * @param dest The destination thumbnail file
	 * @param scaleAlgorithm Algorithm for the scaling(one of Image.SCALE_x)
	 * @param compressionQuality JPEG compression quality(0..1, 1 is maximum quality)
	 * @throws IOException
	 */
	public void build(File src, String srcFileName,int size, File dest, int scaleAlgorithm, float compressionQuality) throws IOException;
}