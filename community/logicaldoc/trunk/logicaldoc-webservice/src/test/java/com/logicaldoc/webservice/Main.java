package com.logicaldoc.webservice;

import javax.activation.DataHandler;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:8080/logicaldoc/services/Auth");
		DocumentClient documentClient = new DocumentClient("http://localhost:8080/logicaldoc/services/Document");
		// FolderClient folderClient = new
		// FolderClient("http://localhost:8080/logicaldoc/services/Folder");
		// SearchClient searchClient = new
		// SearchClient("http://localhost:8080/logicaldoc/services/Search");

		// Open a session
		String sid = auth.login("matteo", "matteo1982");
		// String sid = auth.login("admin", "admin");
		System.out.println("sid: " + sid);

		// auth.grantGroup(sid, 6, 2, 4199, true);
		// auth.logout(sid);
		//
		// sid = auth.login("matteo", "matteo1982");

		WSDocument doc = documentClient.getDocument(sid, 55);
		doc.setTitle("test_4");
		DataHandler data = documentClient.getContent(sid, 34);
		WSDocument doc1 = documentClient.create(sid, doc, data);
		System.out.println("created doc: " + doc1.getId());

		// WSDocument[] docs = documentClient.getDocuments(sid, new long[] {
		// 143, 144, 145, 198, 67 });
		// for (WSDocument doc : docs) {
		// System.out.println("doc id: " + doc.getId());
		// System.out.println("doc name: " + doc.getTitle());
		// }

		// documentClient.move(sid, 199, 7);

		// WSDocument[] docs = documentClient.getVersions(sid, 197);
		// for (WSDocument doc : docs) {
		// System.out.println("doc version: " + doc.getVersion());
		// System.out.println("doc file version: " + doc.getFileVersion());
		// System.out.println("doc name: " + doc.getTitle());
		// }

		// WSFolder newFolder = new WSFolder();
		// newFolder.setName("ddddd");
		// newFolder.setDescription("new folder ddddd");
		// newFolder.setParentId(6);
		// WSFolder[] folders = folderClient.list(sid, 5);
		// for (WSFolder folder : folders) {
		// System.out.println("folder id: " + folder.getId());
		// System.out.println("folder name : " + folder.getName());
		// }

		// System.out.println("folder id : " + newFolder.getId());
		// System.out.println("folder desc: " + newFolder.getDescription());

		// WSDocument[] docs = documentClient.getDocuments(sid, new
		// long[]{100,101,102,103});
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc: "+wsDocument.getTitle());
		// }

		// DataHandler data = documentClient.getContent(sid, 68);
		// System.out.println("data: " + data.toString());

		auth.logout(sid);
	}
}
