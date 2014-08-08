package com.logicaldoc.webservice;

import java.io.File;

import com.logicaldoc.webservice.rest.auth.AuthClient;
import com.logicaldoc.webservice.rest.document.DocumentClient;

public class RestWorgkbench {
	public static void main(String[] args) throws Exception {
		String base = "http://localhost:9080/services/rest";
		AuthClient auth = new AuthClient(base + "/auth");
		DocumentClient doc = new DocumentClient(base + "/document");

		// Open a session
		String sid = auth.login("admin", "12345678");
		System.out.println("Created session: " + sid);
		if (auth.valid(sid))
			System.out.println("Session is valid");

		try {
			File file=new File("C:/tmp/logo.gif");
			
			doc.checkout(sid, 673284114L);
			doc.checkin(sid, 673284114L, "rest checkin", "phototest.gif", true, file);
		} finally {
			auth.logout(sid);
		}
	}
}
