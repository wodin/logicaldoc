package com.logicaldoc.webservice;

import java.io.File;

import com.logicaldoc.webservice.model.WSDocument;

public class RestWorgkbench {
	public static void main(String[] args) throws Exception {
//		String base = "http://localhost:9080/services/rest";
//		AuthClient auth = new AuthClient(base + "/auth");
//		DocumentClient doc = new DocumentClient(base + "/document");
//		FolderClient fld = new FolderClient(base + "/folder");
//
//		// Open a session
//		String sid = auth.login("admin", "12345678");
//		System.out.println("Created session: " + sid);
//		if (auth.valid(sid))
//			System.out.println("Session is valid");
//
//		try {
//			File file = new File("D:/tmp/abc.odt");
//			
//		    long docId = doc.upload(sid, null, 4L, true, file.getName(), file);
//			
//		    System.out.println("created docId: "+docId);
//		    
//		    // System.out.println("Crearted document " + docId);
//			//
//			// docId = doc.upload(sid, docId, null, true, "testrest.gif", file);
//			// System.out.println("Updated document " + docId);
//
//			// doc.checkout(sid, 673284114L);
//			// doc.checkin(sid, 673284114L, "rest checkin", "phototest.gif",
//			// true, file);
//
////			WSDocument document = doc.getDocument(sid, 583761920L);
////			System.out.println(document);
//
//		} finally {
//			auth.logout(sid);
//		}
	}
}
