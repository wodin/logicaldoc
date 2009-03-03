package com.logicaldoc.web.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScaleJPG {

	public static void scaleAffine(String src, int width, String dest) throws IOException {

		BufferedImage bsrc = ImageIO.read(new File(src));
		BufferedImage bdest = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bdest.createGraphics();

		double ratio = 1.0;
		if (bsrc.getWidth() >= bsrc.getHeight()) {
			ratio = (double) width / bsrc.getWidth();
		} else {
			ratio = (double) width / bsrc.getHeight();
		}
		System.err.println("ratio = " + ratio);

		AffineTransform at = AffineTransform.getScaleInstance(ratio, ratio);
		g.drawRenderedImage(bsrc, at);

		ImageIO.write(bdest, "JPG", new File(dest));
	}

	public static void scale(String src, int width, String dest) throws IOException {

		BufferedImage bsrc = ImageIO.read(new File(src));

		Image destImg = null;
		if (bsrc.getWidth() >= bsrc.getHeight()) {
			destImg = bsrc.getScaledInstance(width, -1, Image.SCALE_DEFAULT);
		} else {
			destImg = bsrc.getScaledInstance(-1, width, Image.SCALE_DEFAULT);
		}

		BufferedImage bdest = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bdest.createGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, width, width);

		g.drawImage(destImg, null, null);

		ImageIO.write(bdest, "JPG", new File(dest));
	}

	public static void main(String[] args) {
		if (args.length == 4) {
			try {
				ScaleJPG.scale(args[0], Integer.parseInt(args[1]), args[3]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("\nUsage: java ScaleJPG src width height dest\n");
		}
	}
}