package com.logicaldoc.webservice;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.folder.WSFolder;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:8080/logicaldoc/services/Auth");
		// DocumentClient documentClient = new
		// DocumentClient("http://localhost:8080/logicaldoc/services/Document");

		FolderClient folderClient = new FolderClient("http://localhost:8080/logicaldoc/services/Folder");

		// Open a session
		String sid = auth.login("admin", "admin");
		System.out.println("sid: " + sid);

//		WSFolder wsFolderTest = new WSFolder();
//		wsFolderTest.setText("folder test 1");
//		wsFolderTest.setDescription("descr folder test");
//		wsFolderTest.setParentId(5);
//
//		WSFolder wsFolder = folderClient.create(sid, wsFolderTest);
//		System.out.println("wsFolder id: " + wsFolder.getId() + " -> 4290");
//		System.out.println("wsFolder text: " + wsFolder.getText());

		folderClient.delete(sid, 4290);

		auth.logout(sid);
	}
}
