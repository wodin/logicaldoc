package org.logicaldoc.test.ws;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.DmsClient;
import com.logicaldoc.webservice.FolderContent;

public class TestFolder extends TestCase {

	protected static Log log = LogFactory.getLog(TestFolder.class);

	public TestFolder(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testFolderCRUD() throws IOException {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		/**
		 * public String createFolder(String username, String password, String
		 * name, long parent) throws Exception {
		 */

		// The parent folder is the document root folder: 5
		long parentFolderId = 5;
		try {
			// Folder Creation
			// result is the string "error" or the newly created folderId
			String result = client.createFolder("admin", "admin", "myFirstFolder", parentFolderId);
			System.out.println(result);
			assertFalse("error".equals(result));

			// Folder Retrieval
			long createdFolder = Long.parseLong(result);
			FolderContent fc = client.downloadFolderContent("admin", "admin", createdFolder);
			System.out.println(fc.getName());
			assertEquals("myFirstFolder", fc.getName());
			
			// Folder Rename
			// renameFolder(String username, String password, long folder, String name) throws Exception {
			client.renameFolder("admin", "admin", createdFolder, "New Folder Name");
			fc = client.downloadFolderContent("admin", "admin", createdFolder);
			System.out.println(fc.getName());

			// Folder Delete
			result = client.deleteFolder("admin", "admin", createdFolder);
			System.out.println("result: " + result);
			assertEquals("ok", result);

			// verify the folder deletion
			try {
				fc = client.downloadFolderContent("admin", "admin", createdFolder);
				fail("folder: " + createdFolder + " should not exist");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception during one of the CRD operation");
			// TODO Auto-generated catch block
		}
	}

}
