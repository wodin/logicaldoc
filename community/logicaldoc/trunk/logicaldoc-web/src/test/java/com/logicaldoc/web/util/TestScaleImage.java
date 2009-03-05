package com.logicaldoc.web.util;

import junit.framework.TestCase;

public class TestScaleImage extends TestCase {

	public void testScale() {
		try {
			String src = "C:/tmp/cucchiaio-legno.jpg";
			int width = 150;
			String dest = "C:/tmp/cucchiaio-legno-thumb.jpg";
			ScaleImage.scale(src, width, dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
