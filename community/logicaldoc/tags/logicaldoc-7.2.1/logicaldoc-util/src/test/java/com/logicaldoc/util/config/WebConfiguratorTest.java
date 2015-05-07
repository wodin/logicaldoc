package com.logicaldoc.util.config;

import java.io.File;

import org.junit.Test;

/**
 * Test case for <code>StringUtil</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class WebConfiguratorTest {
	@Test
	public void testSplit() {
		File webXml = new File("target/test-classes/web.xml");
		WebConfigurator config = new WebConfigurator(webXml.getPath());
		config.addServlet("DocumentsData", "pippo");
	}
}