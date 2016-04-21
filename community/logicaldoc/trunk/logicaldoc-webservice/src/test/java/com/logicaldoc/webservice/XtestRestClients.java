package com.logicaldoc.webservice;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.rest.client.RestAuthClient;
import com.logicaldoc.webservice.rest.client.RestDocumentClient;
import com.logicaldoc.webservice.rest.client.RestFolderClient;
import com.logicaldoc.webservice.rest.client.RestSearchClient;

public class XtestRestClients {
	
	private static RestAuthClient authClient = null;
	private static RestDocumentClient docClient = null;
	private static RestFolderClient fldClient = null;
	private static RestSearchClient searchClient = null;
	
	private static String BASE_PATH = "http://localhost:8080/logicaldoc";
	//private static String BASE_PATH = "http://192.168.2.11:8080/logicaldoc";
	
	public static void main(String[] args) throws Exception {
		
		authClient = new RestAuthClient(BASE_PATH +"/services/rest/auth");
		docClient = new RestDocumentClient(BASE_PATH +"/services/rest/document");
		fldClient = new RestFolderClient(BASE_PATH +"/services/rest/folder");
		searchClient = new RestSearchClient(BASE_PATH +"/services/rest/search");
		
		String sid = loginGet();
		
		// Note: 04L is the id of the default workspace
		//listDocuments(sid, 04L);
		//listDocuments(sid, 04L, "InvoiceProcessing01-workflow*.png"); // 4 documents
		//listDocuments(sid, 04L, "InvoiceProcessing01-workflow.png"); // 1 document
		//listDocuments(sid, 04L, "InvoiceProcessing01-workflow(3).png"); // 1 document
		//listFolderChildren(sid, 3342386L);
		//listFolderChildren(sid, 4L);
		
		//createPath(sid, 04L, "/sgsgsgs/Barisoni/rurururu");
		//createPath(sid, 04L, "/La zanzara/Cruciani/coloqui via Sky");
		
		//getFolder(sid, 04L);
		
//		WSDocument myDoc = getDocument(sid, 3375109L);
//		
//		Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
////        System.out.println( sdf.format(cal.getTime()) );
//        
//		myDoc.setTitle("document test(" +sdf.format(cal.getTime()) +")");
//		
//		updateDocument(sid, myDoc);
		
		//createDocument02(sid);
		//createFolder(sid, 04L, "DJ KATCH");
		
		//deleteDocument(sid, 3375105);
		deleteFolder(sid, 4128768);
		
		long start_time = System.nanoTime();
		
		/*
		WSSearchOptions wsso = buildSearchOptions("en", "document management system");
		find(sid, wsso);
		
	       wsso = buildSearchOptions("en", "document management");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "Document Management");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "Document Management system");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "Management system");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "document system");
			find(sid, wsso);	
			
	        wsso = buildSearchOptions("en", "documental system");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "documental system");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "electronic document system");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "electronic document management system");
			find(sid, wsso);
			
	        wsso = buildSearchOptions("en", "electronic system for document management");
			find(sid, wsso);
		*/
		
		
		//logoutDelete(sid);
		
			long end_time = System.nanoTime();
			double difference = (end_time - start_time)/1e6;
			//System.out.println("Total Exec. time (ms): " +difference);
		
		// HttpRestWb - (Apache HttpClient 4.5.2)	
		// Total Exec. time (ms): 988.231266 
		// Total Exec. time (ms): 681.909198
		// Total Exec. time (ms): 737.044408
		// Total Exec. time (ms): 705.149953
		// Total Exec. time (ms): 739.934107
		// Total Exec. time (ms): 767.015352
			
		// HttpRestWb - With deserialization in Java object and serialization to JSON (to write string on system out)
		// Total Exec. time (ms): 1114.652089
		// Total Exec. time (ms): 951.179299
		// Total Exec. time (ms): 1080.464628
		// Total Exec. time (ms): 973.297579
		// Total Exec. time (ms): 1062.980412
		// Total Exec. time (ms): 1064.021243
		// Total Exec. time (ms): 1024.741121
		// Total Exec. time (ms): 1047.152382
			
		// HttpRestWb - With optimized object creation/reuse
		// Total Exec. time (ms): 994.939062
		// Total Exec. time (ms): 913.421004
		// Total Exec. time (ms): 1189.928415

		// XtestRestClients - (CXF JAXRSClientFactory with transform JSON-2-Java)	
		// Total Exec. time (ms): 1284.876061
		// Total Exec. time (ms): 1471.814473
		// Total Exec. time (ms): 1302.452651
		// Total Exec. time (ms): 1112.79949
		// Total Exec. time (ms): 1202.391816
	    // Total Exec. time (ms): 1134.588864
		// Total Exec. time (ms): 968.517025
		// Total Exec. time (ms): 1268.148093
		// Total Exec. time (ms): 1279.542668
		// Total Exec. time (ms): 1160.630676
		// Total Exec. time (ms): 932.782546
		// Total Exec. time (ms): 990.117454
		// Total Exec. time (ms): 1023.894365
		// Total Exec. time (ms): 931.256054
		// Total Exec. time (ms): 1292.162823
		// Total Exec. time (ms): 870.193555
		// Total Exec. time (ms): 889.628808
        // Total Exec. time (ms): 918.054599
	}
	
	private static void deleteFolder(String sid, int folderId) {
		try {
			fldClient.delete(sid, folderId);
			System.out.println("Successfully deleted folder");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteDocument(String sid, long docId) {
		try {
			docClient.delete(sid, docId);
			System.out.println("Successfully deleted document");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private static void createFolder(String sid, long parentId, String fname) throws Exception {
		long fldId = fldClient.createFolder(sid, parentId, fname);
		System.out.println("fldId: "+ fldId);
	}

	private static void createDocument(String sid) throws Exception {
				
		File xxxx = new File("C:\\tmp\\InvoiceProcessing02-dashboard.png");
		WSDocument document = new WSDocument();
		document.setFolderId(04L);
		document.setFileName(xxxx.getName());
		docClient.create(sid, document, xxxx);
	}
	
	private static void createDocument02(String sid) throws Exception {
		
		File xxxx = new File("C:\\tmp\\InvoiceProcessing02-dashboard.png");
		
		WSDocument document = new WSDocument();
		document.setFolderId(04L);
		document.setFileName(xxxx.getName());
		
		DataSource fds = new FileDataSource(xxxx);
		DataHandler handler = new DataHandler(fds);
		 
		WSDocument created = docClient.create(sid, document, handler);
		System.out.println(created.getId());
	}	

	private static void updateDocument(String sid, WSDocument document) throws Exception {

		docClient.update(sid, document);
	}

	private static WSDocument getDocument(String sid, long docId) throws Exception {
		
		WSDocument myDoc = docClient.getDocument(sid, docId);
		
		//Object to JSON in String
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(myDoc);
		System.out.println(jsonInString);
		
		return myDoc;
	}

	private static void getFolder(String sid, long fldId) throws Exception {

		WSFolder sss = fldClient.getFolder(sid, fldId);
		
		//Object to JSON in String
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(sss);
		System.out.println(jsonInString);
	}

	private static void createPath(String sid, long rootFolder, String path) throws Exception {
		
		WSFolder sss = fldClient.createPath(sid, rootFolder, path);

		//Object to JSON in String
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(sss);
		System.out.println("sss: " +jsonInString);
	}

	private static WSSearchOptions buildSearchOptions(String lang1, String expression) {
		
		WSSearchOptions options = new WSSearchOptions();

		String lang = lang1;

		// This is the language of the document
		options.setLanguage(lang);
		options.setExpression(expression);

		// This is the language of the query
		options.setExpressionLanguage(lang);

		// This is required and it is the maximum number of results that we want
		// for this search
		options.setMaxHits(50);
		
		return options;
	}	

	private static void find(String sid, WSSearchOptions options) throws Exception {			
		
		WSSearchResult res = searchClient.find(sid, options);
		
		//ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(res);	
		System.out.println(jsonStr);
	}

	private static void listDocuments(String sid, long folderId) throws Exception {
		WSDocument[] docs = docClient.list(sid, folderId);
		System.out.println("docs: " +docs);
		System.out.println("docs.length: " +docs.length);
		
		//Object to JSON in String
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(docs[1]);
		System.out.println("doc[1]: " +jsonInString);
	}
	
	private static void listDocuments(String sid, long folderId, String fileName) throws Exception {
		WSDocument[] docs = docClient.listDocuments(sid, folderId, fileName);
		System.out.println("docs: " +docs);
		
		
		if (docs != null) {
			System.out.println("docs.length: " +docs.length);
			if (docs.length > 0) {
				//Object to JSON in String
				ObjectMapper mapper = new ObjectMapper();
				String jsonInString = mapper.writeValueAsString(docs[0]);
				System.out.println("doc[1]: " +jsonInString);
			}
		}
	}	
	
	private static void listFolderChildren(String sid, long folderId) throws Exception {
		WSFolder[] dirs = fldClient.listChildren(sid, folderId);
		System.out.println("docs: " +dirs);
		System.out.println("docs.length: " +dirs.length);
		
		//Object to JSON in String
		if ((dirs != null) && (dirs.length > 0)) {
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(dirs[0]);
			System.out.println("dirs[0]: " +jsonInString);
		}
	}
	

	private static String loginGet() throws Exception {
		String sid = authClient.login("admin", "admin");
		System.out.println("sid: " +sid);
		return sid;
	}
	
	private static void logoutDelete(String sid) throws Exception {
		authClient.logout(sid);
	}	

}
