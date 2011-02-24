package com.logicaldoc.webservice;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.folder.WSFolder;
import com.logicaldoc.webservice.system.SystemClient;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient(
				"http://localhost:9080/logicaldoc/services/Auth");
		// DocumentClient documentClient = new
		// DocumentClient("http://localhost:9080/logicaldoc/services/Document");
		FolderClient folderClient = new FolderClient("http://localhost:9080/logicaldoc/services/Folder");
		// SearchClient searchClient = new
		// SearchClient("http://localhost:9080/logicaldoc/services/Search");
		SystemClient systemClient = new SystemClient(
				"http://localhost:9080/logicaldoc/services/System");

		// Open a session
		// This is a user 'author' with different permissions than the authors.
		// String sid = auth.login("matteo", "matteo1982");
		String sid = auth.login("admin", "admin");
		System.out.println("sid: " + sid);

		WSFolder[] path=folderClient.getPath(sid, 20L);
		System.out.println("\n");
		for (WSFolder wsFolder : path) {
			System.out.print(wsFolder.getName()+"/");
		}
		
		path=folderClient.getPath(sid, Folder.ROOTID);
		System.out.println("\n");
		for (WSFolder wsFolder : path) {
			System.out.print(wsFolder.getName()+"/");
		}
		
		
		// WSFolder newFolder = new WSFolder();
		// newFolder.setName("ddddd");
		// newFolder.setDescription("new folder ddddd");
		// newFolder.setParentId(5);
		// newFolder = folderClient.create(sid, newFolder);
		// WSFolder[] folders = folderClient.list(sid, 5);
		// for (WSFolder folder : folders) {
		// System.out.println("folder id: " + folder.getId());
		// System.out.println("folder name : " + folder.getName());
		// }
		//
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
