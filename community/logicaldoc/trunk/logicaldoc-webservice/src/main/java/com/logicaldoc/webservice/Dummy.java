package com.logicaldoc.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;

public final class Dummy {

	private Dummy() {

	}

	public static void main(String args[]) throws Exception {
		DmsService client = new DmsClient("http://localhost:8080/logicaldoc/services/Dms");
		DataHandler content = client.downloadDocument("admin", "admin", 28, "1.0");

		try {
			InputStream is = content.getInputStream();

			OutputStream os = new FileOutputStream(new File("c:\\pippo.pdf"));
			byte[] b = new byte[100000];
			int bytesRead = 0;
			while ((bytesRead = is.read(b)) != -1) {
				os.write(b, 0, bytesRead);
			}
			os.flush();
			os.close();
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
