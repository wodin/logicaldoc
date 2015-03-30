package com.logicaldoc.util.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamEater implements Runnable {

	private InputStream stream;

	private String prefix;

	public StreamEater(String prefix, InputStream stream) {
		super();
		this.prefix = prefix;
		this.stream = stream;
	}

	public void run() {

		try {

			InputStreamReader isr = new InputStreamReader(stream);

			BufferedReader br = new BufferedReader(isr);

			String line = br.readLine();

			while (line != null) {
				System.out.println(prefix + ":" + line);
				line = br.readLine();
			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
