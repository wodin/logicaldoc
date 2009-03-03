package com.logicaldoc.web.util;

import java.io.IOException;

import junit.framework.TestCase;

public class TestScaleJPG extends TestCase {

	public void testScale() throws IOException {
		String src = "C:/tmp/cucchiaio-legno.jpg";
		int width = 150;
		String dest = "C:/tmp/cucchiaio-legno-thumb.jpg";
		ScaleJPG.scale(src, width, dest);
	}

}
