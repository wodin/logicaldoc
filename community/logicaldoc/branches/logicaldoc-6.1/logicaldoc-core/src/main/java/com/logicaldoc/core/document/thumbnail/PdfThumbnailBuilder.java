package com.logicaldoc.core.document.thumbnail;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 * This builder generates the thumbnail for a Pdf document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class PdfThumbnailBuilder extends ImageThumbnailBuilder {
	protected static Log log = LogFactory.getLog(PdfThumbnailBuilder.class);

	@Override
	public synchronized void build(File src, String srcFileName, int size, File dest, int scaleAlgorithm,
			float compressionQuality) throws IOException {

		Document document = new Document();
		try {
			document.setFile(src.getPath());
		} catch (PDFException ex) {
			log.error("Error parsing PDF document " + ex);
		} catch (PDFSecurityException ex) {
			log.error("Error encryption not supported " + ex);
		} catch (FileNotFoundException ex) {
			log.error("Error file not found " + ex);
		} catch (IOException ex) {
			log.error("Error handling PDF document " + ex);
		}

		// save page captures to file.
		float scale = 1.0f;
		float rotation = 0f;

		// Paint the first page content to an image and write the image to a
		// file
		BufferedImage image = (BufferedImage) document.getPageImage(0, GraphicsRenderingHints.SCREEN,
				Page.BOUNDARY_CROPBOX, rotation, scale);
		RenderedImage rendImage = image;
		// capture the page image to file
		File file = null;
		try {
			log.debug("Capturing pdf image");
			file = new File(src.getName() + "-" + System.currentTimeMillis() + ".jpg");
			ImageIO.write(rendImage, "jpg", file);
			super.build(file, srcFileName, size, dest, scaleAlgorithm, compressionQuality);
		} catch (Throwable e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (file != null) {
					if (file.exists())
						file.delete();
				}
			} catch (Exception e) {
			}
		}
		image.flush();
		// clean up resources
		document.dispose();
		file = null;
	}
}