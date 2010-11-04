package com.logicaldoc.webservice;

import javax.activation.DataHandler;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:8080/logicaldoc/services/Auth");
		DocumentClient documentClient = new DocumentClient("http://localhost:8080/logicaldoc/services/Document");
		// FolderClient folderClient = new
		// FolderClient("http://localhost:8080/logicaldoc/services/Folder");
		// SearchClient searchClient = new
		// SearchClient("http://localhost:8080/logicaldoc/services/Search");

		// Open a session
		String sid = auth.login("admin", "admin");
		System.out.println("sid: " + sid);

		// WSDocument[] docs = documentClient.getDocuments(sid, new
		// long[]{100,101,102,103});
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc: "+wsDocument.getTitle());
		// }

		DataHandler data = documentClient.getContent(sid, 68);
		System.out.println("data: " + data.toString());

		auth.logout(sid);
	}
}
