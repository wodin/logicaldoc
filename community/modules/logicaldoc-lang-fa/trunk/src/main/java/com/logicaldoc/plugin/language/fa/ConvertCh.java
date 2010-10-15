package com.logicaldoc.plugin.language.fa;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;

public class ConvertCh {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		FileInputStream fis = new FileInputStream("c:/tmp/Russian/stop.txt");
		InputStreamReader isr = new InputStreamReader(fis, "KOI8-R");

		FileOutputStream fos = new FileOutputStream("c:/tmp/Russian/stop_dest_fkoi8.txt");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		try {
			int x = IOUtils.copy(isr, osw);
			System.out.println(x);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		osw.flush();
		osw.close();
		fos.close();
	}

}
