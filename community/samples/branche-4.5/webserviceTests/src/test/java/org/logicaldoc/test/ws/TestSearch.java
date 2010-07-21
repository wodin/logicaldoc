package org.logicaldoc.test.ws;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.DmsClient;
import com.logicaldoc.webservice.Result;
import com.logicaldoc.webservice.SearchResult;

public class TestSearch extends TestCase {

	protected static Log log = LogFactory.getLog(TestFolder.class);

	public TestSearch(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * WARNING: because a search returns the results there must be documents
	 * indexed. By default the documents via web-service are indexed in the
	 * background and then it is possible that the search returns no results. If
	 * you want you can force the indexing from the GUI of LogicalDOC.
	 * 
	 * @throws IOException
	 */
	public void testSearch() throws IOException {

		String endpoint = "http://localhost:8080/logicaldoc/services/Dms";
		DmsClient client = new DmsClient(endpoint);

		String query = "Guida";

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
					System.out.println("res.size: " + res.getSize());
					System.out.println("res.date: " + res.getDate());
					System.out.println("res.type: " + res.getType());
					System.out.println("res.score: " + res.getScore());

					// Download the document
					// long documentId = res.getId();
					// DocumentInfo info = client.downloadDocumentInfo("admin",
					// "admin", documentId);
					// System.out.println(info.getFilename());
					//                    
					// File destFile = new File("C:/tmp", info.getFilename());
					// OutputStream os = new FileOutputStream(destFile);
					//        			
					// DataHandler dh = client.downloadDocument("admin",
					// "admin", documentId, "");
					// IOUtils.copy(dh.getInputStream(), os);
					// os.close();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
