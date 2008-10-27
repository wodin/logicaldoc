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

	private List<File> folders = new ArrayList<File>();

	private File rootFolder;

	private int linesPerDoc = 100;

	private int deepLevel = 5;

	private int foldersPercentage = 2;

	private int documentsTotal = 1000;

	private int maxSubFolders = 5;
	
	private int foldersTotal;
	
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

	public int getDocumentsTotal() {
		return documentsTotal;
	}

	public void setDocumentsTotal(int documentsTotal) {
		this.documentsTotal = documentsTotal;
	}

	public int getLinesPerDoc() {
		return linesPerDoc;
	}

	public void setLinesPerDoc(int linesPerDoc) {
		this.linesPerDoc = linesPerDoc;
	}

	public int getDeepLevel() {
		return deepLevel;
	}

	public void setDeepLevel(int deepLevel) {
		this.deepLevel = deepLevel;
	}

	/**
	 * Launch the documents generation.
	 * 
	 * @throws IOException
	 */
	public void generate() throws IOException {
		foldersTotal = (int) ((double) (documentsTotal * foldersPercentage) / (double) 100);
		folders.clear();
		rootFolder.mkdirs();
		rootFolder.mkdir();

		
		while(folders.size()<foldersTotal)
		 createTree(rootFolder,0);
		
		
//
// int foldersNumber = (int) ((double) (documentsTotal * foldersPercentage) /
// (double) 100);
// int docsPerDir = (int) (documentsTotal / foldersNumber);
//
// int subdirIndex = 0;
// File subdir = null;
// for (int i = 0; i < documentsTotal; i++) {
// if (i % deepLevel == 0) {
// subdir = new File(rootFolder, DIR_ + subdirIndex++);
// subdir.mkdir();
// }
// generateFile(new File(subdir, DOC_ + i + TXT));
// }
	}


	private void createTree(File parent, int currentDeepLevel) {
		if(folders.size()>=foldersTotal || currentDeepLevel>=deepLevel)
			return;
		
		int subDirs=generateRandomInt(maxSubFolders);
		for(int i=0;i<subDirs && folders.size()<foldersTotal;i++){
			File subDir=new File(parent,"DIR_"+folders.size());
			subDir.mkdir();
			folders.add(subDir);
			createTree(subDir, currentDeepLevel+1);
		}
	}

	private int generateRandomInt(int max) {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(max+1);
		return randomInt;
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
		if (args.length < 2) {
			System.out.println("Usage is DocGen root_dir master_doc [docs_total] [folders_percentage] [lines_per_doc]");
			return;
		}

		DocGen docGen = new DocGen(new File(args[0]), args[1]);
		if (args.length > 1)
			docGen.setDocumentsTotal(Integer.parseInt(args[2]));
		if (args.length > 2)
			docGen.setFoldersPercentage(Integer.parseInt(args[3]));
		if (args.length > 3)
			docGen.setLinesPerDoc(Integer.parseInt(args[4]));
		docGen.generate();
	}

	public int getFoldersPercentage() {
		return foldersPercentage;
	}

	public void setFoldersPercentage(int foldersPercentage) {
		this.foldersPercentage = foldersPercentage;
	}

	public int getMaxSubFolders() {
		return maxSubFolders;
	}

	public void setMaxSubFolders(int maxSubFolders) {
		this.maxSubFolders = maxSubFolders;
	}
}