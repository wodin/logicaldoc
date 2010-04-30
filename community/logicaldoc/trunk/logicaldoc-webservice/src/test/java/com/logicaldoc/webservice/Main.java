package com.logicaldoc.webservice;

import java.io.File;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:8080/logicaldoc/services/Auth");
		DocumentClient document = new DocumentClient("http://localhost:8080/logicaldoc/services/Document");

		// Apriamo una sessione
		String sid = auth.login("admin", "admin");

		WSDocument doc = document.create(sid, new WSDocument(), new File("pom.xml"));

		auth.logout(sid);
	}
}
