package com.logicaldoc.core.document.thumbnail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

/**
 * This builder uses ImageIO and it is able to handle JPEG, GIF, PNG, BMP
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ImageThumbnailBuilder implements ThumbnailBuilder {

	@Override
	public synchronized void build(File src, String srcFileName, int size, File dest, int scaleAlgorithm,
			float compressionQuality) throws IOException {
		BufferedImage bsrc = ImageIO.read(src);

		resizeAndSaveUnSquared(size, dest, scaleAlgorithm, compressionQuality, bsrc);
	}


	/**
	 * This method produces an output image in shape squared to size given input,
	 * the output images always have a square shape,
	 * then save the image produced in jpeg format to the destination file
	 */
	public void resizeAndSaveSquared(int size, File dest, int scaleAlgorithm, float compressionQuality, BufferedImage bsrc)
			throws FileNotFoundException, IOException {

		Image destImg = null;
		if (bsrc.getWidth() >= bsrc.getHeight()) {
			destImg = bsrc.getScaledInstance(size, -1, scaleAlgorithm);
		} else {
			destImg = bsrc.getScaledInstance(-1, size, scaleAlgorithm);
		}

		BufferedImage bdest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bdest.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, size, size);
		g.drawImage(destImg, null, null);

		// Find the jpeg writer
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = iter.next();

		// instantiate an ImageWriteParam object with default compression
		// options
		ImageWriteParam iwp = writer.getDefaultWriteParam();

		// Now, we can set the compression quality:
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		// a float between 0 and 1, 1 specifies minimum compression and maximum
		// quality
		iwp.setCompressionQuality(compressionQuality);

		FileImageOutputStream output = new FileImageOutputStream(dest);
		writer.setOutput(output);
		IIOImage image = new IIOImage(bdest, null, null);
		writer.write(null, image, iwp);
		writer.dispose();

		writer = null;
		image = null;
	}
	
	/**
	 * This method produces an output image resized on the larger side to size given input,
	 * the output images usually have a rectangular shape,
	 * then save the image produced in jpeg format to the destination file
	 */
	public void resizeAndSaveUnSquared(int size, File dest, int scaleAlgorithm, float compressionQuality,
			BufferedImage bsrc) throws FileNotFoundException, IOException {

		Image destImg = null;
		if (bsrc.getWidth() >= bsrc.getHeight()) {
			destImg = bsrc.getScaledInstance(size, -1, scaleAlgorithm);
		} else {
			destImg = bsrc.getScaledInstance(-1, size, scaleAlgorithm);
		}
		
		int width = destImg.getWidth(null);
		int height = destImg.getHeight(null);

		BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bdest.createGraphics();
		g.drawImage(destImg, null, null);

		// Find the jpeg writer
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = iter.next();

		// instantiate an ImageWriteParam object with default compression
		// options
		ImageWriteParam iwp = writer.getDefaultWriteParam();

		// Now, we can set the compression quality:
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		// a float between 0 and 1, 1 specifies minimum compression and maximum
		// quality
		iwp.setCompressionQuality(compressionQuality);

		FileImageOutputStream output = new FileImageOutputStream(dest);
		writer.setOutput(output);
		IIOImage image = new IIOImage(bdest, null, null);
		writer.write(null, image, iwp);
		writer.dispose();

		writer = null;
		image = null;
	}
}