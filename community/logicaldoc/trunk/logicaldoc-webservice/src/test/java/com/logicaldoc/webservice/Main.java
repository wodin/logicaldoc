package com.logicaldoc.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.logicaldoc.util.Exec;

public class Main {
	public static void main(String[] args) throws Exception {

		String command = "C:\\tmp\\swftools\\pdf2swf.exe -T 9 -p 1-10 -f -t -G -s storeallcharacters  C:\\tmp\\test\\test.pdf C:\\tmp\\test\\test.swf";

//		Exec.exec(Arrays.asList("C:\\tmp\\swftools\\pdf2swf.exe", "-T 9", "-p 1-10", "-f", "-t", "-G",
//				"-s storeallcharacters", "C:\\tmp\\test\\test.pdf", "C:\\tmp\\test\\test.swf"), null, null, 15);

		List<String> commandLine = Arrays.asList("C:\\tmp\\imagemagick\\convert.exe", "-compress JPEG", "-quality " + 90
				, "-resize x" + 600, "C:\\tmp\\test\\xx.jpg", "C:\\tmp\\test\\yy.jpg");
		
		command ="C:\\tmp\\imagemagick\\convert.exe -compress JPEG -quality 90 -resize x600 C:\\tmp\\test\\xx.jpg C:\\tmp\\test\\yy.jpg";
		System.out.println("Executing command: "+commandLine);
		Exec.exec(command, null, null, 10);
		
		// try {
		//
		// Process p = Runtime.getRuntime().exec(command);
		//
		// StreamEater errEater = new StreamEater(p.getErrorStream());
		//
		// StreamEater outEater = new StreamEater(p.getInputStream());
		//
		// Thread a=new Thread(errEater);
		// a.start();
		//
		// Thread b=new Thread(outEater);
		// b.start();
		//
		// try {
		// p.waitFor();
		// } catch (InterruptedException e) {
		//
		// }
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		//
		// }
	}

	public static class StreamEater implements Runnable {

		private InputStream stream;

		public StreamEater(InputStream stream) {
			super();
			this.stream = stream;
		}

		public void run() {

			try {

				InputStreamReader isr = new InputStreamReader(stream);

				BufferedReader br = new BufferedReader(isr);

				String line = br.readLine();

				while (line != null) {

					System.out.println(line);

					line = br.readLine();

				}

				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}