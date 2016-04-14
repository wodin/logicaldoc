package com.logicaldoc.webservice;

import com.logicaldoc.webservice.rest.client.RestAuthClient;

public class XtestRestAuthClient {

	public static void main(String[] args) throws Exception {
		
		long start_time = System.nanoTime();
		RestAuthClient aaa = new RestAuthClient("http://localhost:8080/logicaldoc/services/rest/auth");
		String sid = aaa.login("admin", "admin");
		System.out.println("sid: " +sid);
		
		aaa.logout(sid);
		
		long end_time = System.nanoTime();
		double difference = (end_time - start_time)/1e6;
	}

}
