package com.logicaldoc.webservice;

import com.logicaldoc.webservice.rest.auth.AuthClient;
import com.logicaldoc.webservice.rest.document.DocumentClient;
import com.logicaldoc.webservice.rest.folder.FolderClient;

public class RestWorgkbench {
	public static void main(String[] args) throws Exception {
		String base = "http://localhost:9080/services/rest";
		AuthClient auth = new AuthClient(base + "/auth");
		DocumentClient doc = new DocumentClient(base + "/document");
		FolderClient fld = new FolderClient(base + "/folder");

		// Open a session
		String sid = auth.login("admin", "12345678");
		System.out.println("Created session: " + sid);
		if (auth.valid(sid))
			System.out.println("Session is valid");

		try {
			// File file = new File("C:/tmp/logo.gif");
			//
			// long docId = doc.upload(sid, null, 4L, true, "testrest.gif",
			// file);
			// System.out.println("Crearted document " + docId);
			//
			// docId = doc.upload(sid, docId, null, true, "testrest.gif", file);
			// System.out.println("Updated document " + docId);

			// doc.checkout(sid, 673284114L);
			// doc.checkin(sid, 673284114L, "rest checkin", "phototest.gif",
			// true, file);

			long folderId = fld.createFolder(sid, 692027394L, "ciccio");
			System.out.println("created folder " + folderId);

		} finally {
			auth.logout(sid);
		}
	}
}
