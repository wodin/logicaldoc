package com.logicaldoc.testbench;

import java.io.IOException;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		GenerateFiles genFiles=new GenerateFiles();
		genFiles.setLinesPerFile(120);
		genFiles.setFilesTotal(1000);
		genFiles.setLogicalDocLayout(true);
		genFiles.setStartFolderId(300);
		genFiles.setStartDocId(1000);
		genFiles.generate();
	}

}
