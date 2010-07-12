package com.logicaldoc.webservice;

import com.logicaldoc.webservice.auth.AuthClient;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:8080/logicaldoc/services/Auth");
		// DocumentClient documentClient = new
		// DocumentClient("http://localhost:8080/logicaldoc/services/Document");
		// FolderClient folderClient = new
		// FolderClient("http://localhost:8080/logicaldoc/services/Folder");
		// SearchClient searchClient = new
		// SearchClient("http://localhost:8080/logicaldoc/services/Search");

		// Open a session
		String sid = auth.login("matteo", "matteo1982");
		System.out.println("sid: " + sid);

		auth.logout(sid);
	}
}
