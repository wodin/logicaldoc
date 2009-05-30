package org.logicaldoc.test.ws;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import junit.framework.TestCase;

import com.logicaldoc.webservice.DmsClient;

public class TestUpload extends TestCase {

	public TestUpload(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testCreateDocument() throws Exception {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		File file = new File("C:/tmp/test.pdf");

		String sourceDate = "2009-05-30";

		/**
		 * public String createDocument(String username, String password, long
		 * folder, String docTitle, String source, String sourceDate, String
		 * author, String sourceType, String coverage, String language, String
		 * keywords, String versionDesc, String filename, DataHandler content,
		 * String templateName, ExtendedAttribute[] extendedAttribute, String
		 * sourceId, String object, String recipient) throws Exception {
		 */
		DataSource ds = new FileDataSource(file);
		DataHandler content = new DataHandler(ds);

		// This will create a new document children of the folder 5 (Documents)
		// The result is the Id of the new created document, otherwise error...
		String result = client.createDocument("admin", "admin", 5, "docTitle", "source", sourceDate, "author",
				"sourceType", "coverage", "it", "keywords", "versionDesc", file.getName(), content, "", null, "sourceId", "object", "recipient");

		System.err.println("result = " + result);
	}

}
