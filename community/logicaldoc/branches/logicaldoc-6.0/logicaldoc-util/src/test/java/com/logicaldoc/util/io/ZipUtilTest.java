package com.logicaldoc.util.io;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZipUtilTest {

	@Before
	public void setUp() throws Exception {
		File dir = new File("target/test");
		dir.mkdirs();
		dir.mkdir();
	}

	@After
	public void tearDown() throws Exception {
		File dir = new File("target/test");
		if (dir.exists())
			try {
				FileUtils.forceDelete(dir);
			} catch (IOException e) {
			}
	}

	@Test
	public void testReadEntry() throws IOException {
		File file = new File("target/test.zip");
		FileUtil.copyResource("/test.zip", file);
		byte[] in = ZipUtil.readEntry(file, "index.xsl");
		ByteArrayInputStream is=new ByteArrayInputStream(in);
		assertNotNull(is);
		File dir = new File("target/test");
		dir.mkdirs();
		dir.mkdir();
		File tmp = new File("target/test/index.xsl");
		OutputStream out;
		try {
			out = new FileOutputStream(tmp);
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			is.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
