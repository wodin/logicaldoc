package com.logicaldoc.webservice;

import java.io.File;

import com.logicaldoc.webservice.soap.client.SoapAuthClient;
import com.logicaldoc.webservice.soap.client.SoapDocumentClient;
import com.logicaldoc.webservice.soap.client.SoapFolderClient;

public class RestWorgkbench {
	public static void main(String[] args) throws Exception {
		String base = "http://localhost:9080/services/rest";
		SoapAuthClient auth = new SoapAuthClient(base + "/auth");
		SoapDocumentClient doc = new SoapDocumentClient(base + "/document");
		SoapFolderClient fld = new SoapFolderClient(base + "/folder");

		// Open a session
		String sid = auth.login("admin", "12345678");
		System.out.println("Created session: " + sid);
		if (auth.valid(sid))
			System.out.println("Session is valid");

		try {


		} finally {
			auth.logout(sid);
		}
	}
}
