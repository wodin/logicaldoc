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
public class GenerateFiles {

	private String folderPrefix = "dir_";

	private String filePrefix = "doc_";

	private static final String TXT = ".txt";

	private List<String> lines = new ArrayList<String>();

	private List<File> folders = new ArrayList<File>();

	private List<File> files = new ArrayList<File>();

	private File rootFolder;

	private int linesPerFile = 100;

	private int deepLevel = 5;

	private int foldersPercentage = 2;

	private int filesTotal = 1000;

	private int maxSubFolders = 5;

	private int maxFilesPerFolders = 20;

	private int foldersTotal;

	private long startFolderId = 0;

	private long startDocId = 0;

	private boolean logicalDocLayout = false;

	/**
	 * Constructor of the File Generator
	 * 
	 * @param rootFolder Folder in which insert the files
	 * @param master File from which retrieve the text for the generating files
	 */
	public GenerateFiles(File rootFolder, String master) {
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

	public boolean isLogicalDocLayout() {
		return logicalDocLayout;
	}

	public void setLogicalDocLayout(boolean logicalDocLayout) {
		this.logicalDocLayout = logicalDocLayout;
	}

	public int getFilesTotal() {
		return filesTotal;
	}

	public void setFilesTotal(int filesTotal) {
		this.filesTotal = filesTotal;
	}

	public int getLinesPerFile() {
		return linesPerFile;
	}

	public void setLinesPerFile(int linesPerFile) {
		this.linesPerFile = linesPerFile;
	}

	public int getDeepLevel() {
		return deepLevel;
	}

	public void setDeepLevel(int deepLevel) {
		this.deepLevel = deepLevel;
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

	public String getFolderPrefix() {
		return folderPrefix;
	}

	public void setFolderPrefix(String folderPrefix) {
		this.folderPrefix = folderPrefix;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public long getStartFolderId() {
		return startFolderId;
	}

	public void setStartFolderId(long startFolderId) {
		this.startFolderId = startFolderId;
	}

	public long getStartDocId() {
		return startDocId;
	}

	public void setStartDocId(long startDocId) {
		this.startDocId = startDocId;
	}

	/**
	 * Launches the files generation.
	 */
	public void generate() throws IOException {
		foldersTotal = (int) ((double) (filesTotal * foldersPercentage) / (double) 100);
		folders.clear();
		files.clear();
		rootFolder.mkdirs();
		rootFolder.mkdir();

		while (folders.size() < foldersTotal)
			createTree(rootFolder, 0);

		while (files.size() < filesTotal)
			createFiles();
	}

	/**
	 * Create the folder tree
	 * 
	 * @param parent Parent folder
	 * @param currentDeepLevel Current depth folder reached
	 */
	private void createTree(File parent, int currentDeepLevel) throws IOException {
		if (folders.size() >= foldersTotal || currentDeepLevel >= deepLevel)
			return;

		int subDirs = generateRandomInt(maxSubFolders);
		for (int i = 0; i < subDirs && folders.size() < foldersTotal; i++) {
			long folderId = startFolderId + folders.size();
			File subDir;
			if (logicalDocLayout) {
				subDir = new File(parent, Long.toString(folderId));
			} else {
				subDir = new File(parent, folderPrefix + (folderId));
			}
			subDir.mkdir();
			folders.add(subDir);
			createTree(subDir, currentDeepLevel + 1);
		}
	}

	/**
	 * Creates and inserts a random number of files in folders
	 * 
	 * @throws IOException
	 */
	private void createFiles() throws IOException {
		for (File folder : folders) {
			int count = 0;
			int subFiles = generateRandomInt(maxFilesPerFolders);
			for (int i = 0; i < subFiles && files.size() < filesTotal; i++) {
				File file;
				long docId = startDocId + files.size();
				if (logicalDocLayout) {
					file = new File(folder, filePrefix + Long.toString(docId));
					file.mkdir();
					file = new File(file, filePrefix + (docId) + TXT);
				} else {
					file = new File(folder, filePrefix + (docId) + TXT);
				}
				writeFile(file);
				files.add(file);
				count++;
			}
			if (files.size() >= filesTotal)
				return;
		}
	}

	/**
	 * Generate a random number from 0 to <code>max</code>
	 * 
	 * @param max Max number value
	 * @return Number generate
	 */
	private int generateRandomInt(int max) {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(max + 1);
		return randomInt;
	}

	/**
	 * Generates a file using a random selection of lines
	 * 
	 * @param file
	 */
	private void writeFile(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			Random generator = new Random();
			for (int i = 0; i < linesPerFile; i++) {
				writer.write(lines.get(generator.nextInt(lines.size())) + "\n");
			}
		} finally {
			writer.close();
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage is GenerateFiles root_dir master_doc [files_total] [folders_percentage] [lines_per_file]");
			return;
		}

		GenerateFiles genFiles = new GenerateFiles(new File(args[0]), args[1]);
		if (args.length > 1)
			genFiles.setFilesTotal(Integer.parseInt(args[2]));
		if (args.length > 2)
			genFiles.setFoldersPercentage(Integer.parseInt(args[3]));
		if (args.length > 3)
			genFiles.setLinesPerFile(Integer.parseInt(args[4]));
		genFiles.generate();
	}

	public List<File> getFolders() {
		return folders;
	}

	public List<File> getFiles() {
		return files;
	}
}