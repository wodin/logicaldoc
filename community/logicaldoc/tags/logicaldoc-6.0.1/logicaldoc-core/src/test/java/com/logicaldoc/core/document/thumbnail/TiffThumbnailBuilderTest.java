package com.logicaldoc.core.document.thumbnail;

import static org.junit.Assert.*;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.junit.Test;

public class TiffThumbnailBuilderTest {

	@Test
	public void testBuild() throws IOException {
		
		TiffThumbnailBuilder ttb = new TiffThumbnailBuilder();
		
		File src = new File(URLDecoder.decode(getClass().getClassLoader().getResource("0742_0003.tif").getPath(), "UTF-8"));
		File targetDir = src.getParentFile();

		File dest = new File(targetDir, "0742_0003-thumb.jpg");
		if (dest.exists())
			dest.delete();
		
		ttb.build(src, null, 800, dest, Image.SCALE_SMOOTH, 0.93F);
		
		System.out.println("src.length(): " + src.length());
		System.out.println("dest.length(): " + dest.length());
		
		assertTrue(dest.exists());
		assertEquals(dest.length(), 114811);
	}

}
