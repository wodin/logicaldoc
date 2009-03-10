package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.pdfbox.PDFToImage;

/**
 * This builder generates the thumbnail for a Pdf document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class PdfThumbnailBuilder extends ImageThumbnailBuilder {

	@Override
	public void build(File src, int size, File dest) throws IOException {
		String[] args = new String[] { "-startPage", "1", "-endPage", "1", "-outputPrefix",
				FilenameUtils.getBaseName(dest.getName()), src.getPath(), };
		File firstPage = new File(src.getName() + "-thumb1.jpg");
		try {
			PDFToImage.main(args);
			super.build(firstPage, size, dest);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			firstPage.delete();
		}
	}
}