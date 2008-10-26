package com.logicaldoc.testbench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Document generator utility
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocGen {

	private static final String DIR_ = "dir_";

	private static final String DOC_ = "doc_";

	private static final String TXT = ".txt";

	private List<String> lines = new ArrayList<String>();

	private File rootFolder;

	private int linesPerDoc = 100;

	private int docsPerDir = 1000;

	private int totalDocs = 1000;

	public DocGen(File rootFolder, String master) {
		this.rootFolder = rootFolder;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(master)));
			String str;
			while ((str = in.readLine()) != null) {
				if (!"".equals(str.trim()))
					lines.add(str.trim());
			}
			in.close();
		} catch (IOException e) {
		}
	}

	public int getTotalDocs() {
		return totalDocs;
	}

	public void setTotalDocs(int totalDocs) {
		this.totalDocs = totalDocs;
	}

	public int getLinesPerDoc() {
		return linesPerDoc;
	}

	public void setLinesPerDoc(int linesPerDoc) {
		this.linesPerDoc = linesPerDoc;
	}

	public int getDocsPerDir() {
		return docsPerDir;
	}

	public void setDocsPerDir(int docsPerDir) {
		this.docsPerDir = docsPerDir;
	}

	/**
	 * Launch the documents generation.
	 * 
	 * @throws IOException
	 */
	public void generate() throws IOException {
		rootFolder.mkdirs();
		rootFolder.mkdir();

		int subdirIndex = 0;
		File subdir = null;
		for (int i = 0; i < totalDocs; i++) {
			if (i % docsPerDir == 0) {
				subdir = new File(rootFolder, DIR_ + subdirIndex++);
				subdir.mkdir();
			}
			generateFile(new File(subdir, DOC_ + i + TXT));
		}
	}

	/**
	 * Generates a file using a random selection of lines
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void generateFile(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			Random generator = new Random();
			for (int i = 0; i < linesPerDoc; i++) {
				writer.write(lines.get(generator.nextInt(lines.size())) + "\n");
			}
		} finally {
			writer.close();
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 2){
			System.out.println("Usage is DocGen root_dir master_doc [total_docs] [docs_per_dir] [lines_per_doc]");
			return;
		}

		DocGen docGen = new DocGen(new File(args[0]), args[1]);
		if (args.length > 1)
			docGen.setTotalDocs(Integer.parseInt(args[2]));
		if (args.length > 2)
			docGen.setDocsPerDir(Integer.parseInt(args[3]));
		if (args.length > 3)
			docGen.setLinesPerDoc(Integer.parseInt(args[4]));
		docGen.generate();
	}
}