package com.logicaldoc.core.document.thumbnail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This builder uses ImageIO and it is able to handle JPEG, GIF, PNG, BMP
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ImageThumbnailBuilder implements ThumbnailBuilder {

	@Override
	public void build(File src, int size, File dest) throws IOException {
		BufferedImage bsrc = ImageIO.read(src);

		Image destImg = null;
		if (bsrc.getWidth() >= bsrc.getHeight()) {
			destImg = bsrc.getScaledInstance(size, -1, Image.SCALE_DEFAULT);
		} else {
			destImg = bsrc.getScaledInstance(-1, size, Image.SCALE_DEFAULT);
		}

		BufferedImage bdest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bdest.createGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, size, size);

		g.drawImage(destImg, null, null);

		ImageIO.write(bdest, "JPG", dest);
	}
}