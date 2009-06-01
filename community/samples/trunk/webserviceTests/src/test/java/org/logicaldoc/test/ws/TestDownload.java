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
			// download the document with id 677
			int documentId = 8;
			String version = "";
			
			DocumentInfo info = client.downloadDocumentInfo("admin", "admin", documentId);			
			System.out.println(info.getFilename());
			
			File destFile = new File("C:/tmp", info.getFilename());
			//File destFile = new File("C:/tmp/destfile.pdf");
			os = new FileOutputStream(destFile);
			
			DataHandler dh = client.downloadDocument("admin", "admin", documentId, version);
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

		// Download the content representing the document root folder (id=5, name='Documents')
		FolderContent content = client.downloadFolderContent("admin", "admin", 5);
		for (Content con : content.getDocument()) {
			long documentId = con.getId();

			DocumentInfo info = client.downloadDocumentInfo("admin", "admin", documentId);			
			System.out.println(info.getFilename());

			String version = "";
			DataHandler dh = client.downloadDocument("admin", "admin", documentId, version);
			
			String location = "C:/tmp/" + info.getFilename();
			OutputStream os = new FileOutputStream(location);			
			IOUtils.copy(dh.getInputStream(), os);
			os.close();
		}
	}

}
