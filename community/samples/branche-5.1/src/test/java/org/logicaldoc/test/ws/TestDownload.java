package org.logicaldoc.test.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.DataHandler;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.Content;
import com.logicaldoc.webservice.DmsClient;
import com.logicaldoc.webservice.DocumentInfo;
import com.logicaldoc.webservice.FolderContent;

public class TestDownload extends TestCase {

	protected static Log log = LogFactory.getLog(TestDownload.class);

	public TestDownload(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDownloadDocument() throws IOException {
		
		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		OutputStream os = null;

		try {
			// Download the document with id 7
			// You should be sure that there is a document on LD with this ID
			int documentId = 7;
			String version = "";
			
			// Get session token 
			String sessionId = client.login("admin", "admin");
			
			DocumentInfo info = client.downloadDocumentInfo(sessionId, documentId);			
			System.out.println(info.getFilename());
			
			File destFile = new File("C:/tmp", info.getFilename());
			//File destFile = new File("C:/tmp/destfile.pdf");
			os = new FileOutputStream(destFile);
			
			DataHandler dh = client.downloadDocument(sessionId, documentId, version);
			IOUtils.copy(dh.getInputStream(), os);
		} catch (RuntimeException e) {
			log.error("Unable to download the document: " + e.getMessage(), e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		os.close();
	}

	public void testDownloadFolderContent() throws Exception {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);
		
		// Get session token 
		String sessionId = client.login("admin", "admin");

		// Download the content representing the document root folder (id=5, name='Documents')
		FolderContent content = client.downloadFolderContent(sessionId, 5);
		for (Content con : content.getDocument()) {
			long documentId = con.getId();

			DocumentInfo info = client.downloadDocumentInfo(sessionId, documentId);			
			System.out.println(info.getFilename());

			String version = "";
			DataHandler dh = client.downloadDocument(sessionId, documentId, version);
			
			File outFile = new File("C:/tmp", info.getFilename());
			OutputStream os = new FileOutputStream(outFile);			
			IOUtils.copy(dh.getInputStream(), os);
			os.close();
		}
	}

}
