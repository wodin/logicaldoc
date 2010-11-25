package com.logicaldoc.core.document.thumbnail;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

/**
 * This builder uses JAI and it is able to handle TIFF.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.1
 */
public class TiffThumbnailBuilder extends ImageThumbnailBuilder {
	
	@Override
	public synchronized void build(File src, String srcFileName, int size, File dest, int scaleAlgorithm,
			float compressionQuality) throws IOException {
		try {
			readTiff(src, dest, size, scaleAlgorithm, compressionQuality);
		} catch (Exception e) {
		}
	}

	public void readTiff(File file, File dest, int size, int scaleAlgorithm, float compressionQuality)
			throws IOException {
		SeekableStream seekableStream = new FileSeekableStream(file);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", seekableStream, null);
		RenderedImage image = decoder.decodeAsRenderedImage(0);
		BufferedImage bsrc = convertRenderedImage(image);

		resizeAndSaveUnSquared(size, dest, scaleAlgorithm, compressionQuality, bsrc);
	}


	/**
	 * Converts the given RenderedImage into a BufferedImage
	 * 
	 * @param image The RenderedImage image
	 * @return The BufferedImage image
	 */
	private BufferedImage convertRenderedImage(RenderedImage image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		ColorModel cm = image.getColorModel();
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		String[] keys = image.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], image.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
		image.copyData(raster);

		return result;
	}
}
