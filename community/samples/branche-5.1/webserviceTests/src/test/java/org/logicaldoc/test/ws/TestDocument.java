package org.logicaldoc.test.ws;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import junit.framework.TestCase;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.webservice.DmsClient;
import com.logicaldoc.webservice.DocumentInfo;

public class TestDocument extends TestCase {

	public TestDocument(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDocumentCRUD() throws Exception {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		File file = new File("C:/tmp/test.pdf");

		String sourceDate = "2009-05-30";

		/**
		 * public String createDocument(String sessionId, long
		 * folder, String docTitle, String source, String sourceDate, String
		 * author, String sourceType, String coverage, String language, String
		 * keywords, String versionDesc, String filename, DataHandler content,
		 * String templateName, ExtendedAttribute[] extendedAttribute, String
		 * sourceId, String object, String recipient, String customId) throws Exception {
		 */
		DataSource ds = new FileDataSource(file);
		DataHandler content = new DataHandler(ds);

		// Get session token 
		String sessionId = client.login("admin", "admin");

		// This will create a new document children of the folder 5 (Documents)
		// The result is the Id of the new created document, otherwise the string "error"
		String result = client.createDocument(sessionId, 5, "docTitle", "source", sourceDate, "author",
				"sourceType", "coverage", "it", "keywords", "versionDesc", file.getName(), content, null, null, "sourceId", "object", "recipient", null);

		System.err.println("result = " + result);
		
		// Retrieve the metadata of the document created 
		long documentId = Long.parseLong(result);
		DocumentInfo docInfo = client.downloadDocumentInfo(sessionId, documentId);
		
		System.out.println(docInfo.getFilename());
		System.out.println(docInfo.getTitle());
		System.out.println(docInfo.getSourceDate());
		
		assertEquals("author", docInfo.getAuthor());
		assertTrue(docInfo.getSourceDate().startsWith("2009-05-30"));
		
		// Update the metadata of the document created
		// in particular we decided to update fields: title, source, sourceDate, sourceAuthor, tags
		
/*		public void update(String sessionId, long id, String title, String source, String sourceAuthor,
				String sourceDate, String sourceType, String coverage, String language, String[] tags, String sourceId,
				String object, String recipient, String templateName, Attribute[] extendedAttribute) throws Exception {*/
		
		try {
			String[] tags = new String[]{"tag", "keyword", "test"};
			String resultUpdate = client.update(sessionId, documentId, "MyTitle", "MySource", "MysourceAuthor", 
					"2010-03-06", "sourceType", "coverage", "it", tags, "sourceId", 
					"object", "recipient", "", null);
					
			System.err.println("resultUpdate: " +resultUpdate);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		//VERIFY THE CHANGES to the tags
		DocumentInfo docInfoUpd = client.downloadDocumentInfo(sessionId, documentId);
		String[] tags2 = docInfoUpd.getTags();
		assertNotNull(tags2);
		assertEquals(3, tags2.length);
		
		// Delete the document just created
		result = client.deleteDocument(sessionId, documentId);
		System.err.println("result = " + result);
		assertEquals("ok", result);
		
		// verify the effective deletion of the document
		try {
			docInfo = client.downloadDocumentInfo(sessionId, documentId);
			fail("The document " + documentId +" should be Deleted!");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
