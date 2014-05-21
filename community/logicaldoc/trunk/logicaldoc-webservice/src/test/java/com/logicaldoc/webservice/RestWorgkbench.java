package com.logicaldoc.webservice;

import com.logicaldoc.webservice.rest.auth.AuthClient;

public class RestWorgkbench {
	public static void main(String[] args) throws Exception {
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
