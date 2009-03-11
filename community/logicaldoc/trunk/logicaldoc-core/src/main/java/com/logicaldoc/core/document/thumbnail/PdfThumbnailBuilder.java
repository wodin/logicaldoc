package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;

import org.pdfbox.PDFToImage;

/**
 * This builder generates the thumbnail for a Pdf document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class PdfThumbnailBuilder extends ImageThumbnailBuilder {

	@Override
	public void build(File src, String srcFileName, int size, File dest) throws IOException {
		String[] args = new String[] { "-startPage", "1", "-endPage", "1", "-outputPrefix",
				"thumb", src.getPath()};
		File firstPage = new File("thumb1.jpg");
		try {
			PDFToImage.main(args);
			super.build(firstPage, srcFileName, size, dest);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			firstPage.delete();
		}
	}
}