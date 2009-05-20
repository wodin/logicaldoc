package org.logicaldoc.test.ws;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import junit.framework.TestCase;

import com.logicaldoc.webservice.DmsClient;
import com.logicaldoc.webservice.DocumentInfo;

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

		String sourceDate = "2009-03-28";

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
	
	
	public void testDocumentCRD() throws Exception {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		File file = new File("C:/tmp/test.pdf");

		String sourceDate = "2009-03-28";

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
		// The result is the Id of the new created document, otherwise the string "error"
		String result = client.createDocument("admin", "admin", 5, "docTitle", "source", sourceDate, "author",
				"sourceType", "coverage", "it", "keywords", "versionDesc", file.getName(), content, "", null, "sourceId", "object", "recipient");

		System.err.println("result = " + result);
		
		
		// Retrieve the document created
		long documentId = Long.parseLong(result);
		DocumentInfo info = client.downloadDocumentInfo("admin", "admin", documentId);
		
		System.out.println(info.getFilename());
		System.out.println(info.getTitle());
		System.out.println(info.getSourceDate());
		
		assertEquals("author", info.getAuthor());
		assertTrue(info.getSourceDate().startsWith("2009-03-28"));
		
		// Delete the document just created
		result = client.deleteDocument("admin", "admin", documentId);
		System.err.println("result = " + result);
		assertEquals("ok", result);
		
		// verify the effective deletion of the document
		try {
			info = client.downloadDocumentInfo("admin", "admin", documentId);
			fail("The document " + documentId +" should be Deleted!");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
