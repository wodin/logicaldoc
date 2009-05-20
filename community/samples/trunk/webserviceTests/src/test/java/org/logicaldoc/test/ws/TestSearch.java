package org.logicaldoc.test.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.DataHandler;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.DmsClient;
import com.logicaldoc.webservice.DocumentInfo;
import com.logicaldoc.webservice.Result;
import com.logicaldoc.webservice.SearchResult;

import junit.framework.TestCase;

public class TestSearch extends TestCase {

	protected static Log log = LogFactory.getLog(TestFolder.class);

	public TestSearch(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testSearch() throws IOException {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		String query = "checkout";

		/**
		 * public SearchResult search(String username, String password, String
		 * query, String indexLanguage, String queryLanguage, int maxHits,
		 * String templateName, String[] templateFields) throws Exception {
		 */
		try {
			SearchResult sr = client.search("admin", "admin", query, "", "it", 20, null, null);

			System.out.println("HITS: " + sr.getTotalHits());
			System.out.println("search completed in ms: " + sr.getTime());
			if (sr.getResult() != null) {

				for (Result res : sr.getResult()) {
					System.out.println("title: " + res.getTitle());
                    System.out.println("res.id: " + res.getId());                        
                    System.out.println("res.summary: " + res.getSummary());
                    System.out.println("res.length: " + res.getLength());
                    System.out.println("res.date: " + res.getDate());
                    System.out.println("res.type: " + res.getType());
                    System.out.println("res.score: " + res.getScore());

                    // Download the document
//                    long documentId = res.getId();
//                    DocumentInfo info = client.downloadDocumentInfo("admin", "admin", documentId);			
//        			System.out.println(info.getFilename());
//                    
//                    File destFile = new File("C:/tmp", info.getFilename());
//                    OutputStream os = new FileOutputStream(destFile);
//        			
//        			DataHandler dh = client.downloadDocument("admin", "admin", documentId, "");
//        			IOUtils.copy(dh.getInputStream(), os);
//        			os.close();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
