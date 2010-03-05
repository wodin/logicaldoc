package com.logicaldoc.core.document.thumbnail;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;

public class TiffThumbnailBuilder extends ImageThumbnailBuilder {
	@Override
	public synchronized void build(File src, String srcFileName, int size, File dest, int scaleAlgorithm,
			float compressionQuality) throws IOException {
		try {
			readTiff(src, dest, size, scaleAlgorithm, compressionQuality);
		} catch (Exception e) {
		}
	}

	public static void mergeTiffFiles(File file, File destFile) throws Exception {

		RenderedOp firstPage = JAI.create("fileload", file.getCanonicalPath());

		TIFFEncodeParam param = new TIFFEncodeParam();
		int comp = getCompression(firstPage);
		System.out.println("Compression is : " + comp);
		param.setCompression(comp);

		OutputStream out = new FileOutputStream(destFile);

		ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG", out, param);

		encoder.encode(firstPage);

		firstPage.dispose();
		out.close();
	}

	private static int getCompression(RenderedOp op) throws Exception {
		int TAG_COMPRESSION = 259;
		TIFFDirectory dir = (TIFFDirectory) op.getProperty("tiff_directory");
		if (dir.isTagPresent(TAG_COMPRESSION)) {
			TIFFField compField = dir.getField(TAG_COMPRESSION);
			return compField.getAsInt(0);
		}
		return 0;
	}

	public void readTiff(File file, File dest, int size, int scaleAlgorithm, float compressionQuality)
			throws IOException {
		SeekableStream seekableStream = new FileSeekableStream(file);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", seekableStream, null);
		RenderedImage image = decoder.decodeAsRenderedImage(0);
		BufferedImage bsrc = convertRenderedImage(image);

		resizeAndSave(size, dest, scaleAlgorithm, compressionQuality, bsrc);
	}

	public BufferedImage convertRenderedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
		img.copyData(raster);

		return result;
	}
}
