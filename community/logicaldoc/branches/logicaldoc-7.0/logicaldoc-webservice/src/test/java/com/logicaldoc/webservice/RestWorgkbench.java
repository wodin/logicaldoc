package com.logicaldoc.webservice;

import java.io.File;

import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.rest.auth.AuthClient;
import com.logicaldoc.webservice.rest.document.DocumentClient;

public class RestWorgkbench {

	public static void main(String[] args) throws Exception {

		String base = "http://localhost:8080/services/rest";
		String soapBase = "http://localhost:8080/services/";

		AuthClient auth = new AuthClient(base + "/auth");
		DocumentClient doc = new DocumentClient(base + "/document");
		com.logicaldoc.webservice.document.DocumentClient soapclient = new com.logicaldoc.webservice.document.DocumentClient(soapBase + "/Document?wsdl");

		// Open a session
		String sid = auth.login("admin", "admin");
		System.out.println("Created session: " + sid);
		if (auth.valid(sid))
			System.out.println("Session is valid");

		try {
			File file = new File("C:/Users/alle/Desktop/P1000484_X363_raw.tif");
			// get the filesize in order to check
			long fsize = file.length();
			System.out.println("Original fileSize: " +fsize);

			long docID = 15371564L;
			doc.checkout(sid, docID);
			System.out.println("Checkout performed");
			doc.checkin(sid, docID, "rest checkin", file.getName(), false, file);
			System.out.println("Operation completed");
			
			// check the filesize after the checkin operation
			WSDocument xxx = soapclient.getDocument(sid, docID);
			System.out.println("FileSize after checkin: " + xxx.getFileSize());
		} finally {
			auth.logout(sid);
		}
	}

	public static void mainOLD(String[] args) throws Exception {
		String base = "http://localhost:9080/services/rest";
		AuthClient auth = new AuthClient(base + "/auth");

		// Open a session
		String sid = auth.login("admin", "12345678");
		System.out.println("Created session: " + sid);
		if (auth.valid(sid))
			System.out.println("Session is valid");

		auth.renew(sid);
		System.out.println("Session renewed");

		try {
		} finally {
			auth.logout(sid);
		}
	}
}
