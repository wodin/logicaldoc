package com.logicaldoc.testbench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Document generator utility. All configuration parameters are taken from
 * /conf.properties (keys starting with generatefiles).
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class GenerateFiles {

	protected static Log log = LogFactory.getLog(GenerateFiles.class);

	private String folderPrefix = "dir_";

	private String filePrefix = "doc_";

	private static final String TXT = ".txt";

	private List<String> lines = new ArrayList<String>();

	private List<File> folders = new ArrayList<File>();

	private long filesCount = 0;

	private File rootFolder;

	private String master;

	private int linesPerFile = 100;

	private int deepLevel = 5;

	private int foldersPercentage = 2;

	private long filesTotal = 1000;

	private int maxSubFolders = 5;

	private int maxFilesPerFolders = 20;

	private int foldersTotal;

	private long startFolderId = 10000;

	private long startDocId = 10000;

	private boolean logicalDocLayout = false;

	/**
	 * Generate the files in the root folder
	 */
	public GenerateFiles() {

		try {
			Properties conf = new Properties();
			conf.load(this.getClass().getResourceAsStream("/conf.properties"));
			this.rootFolder = new File(conf.getProperty("files.rootFolder"));
			this.folderPrefix = conf.getProperty("files.folderPrefix");
			this.filePrefix = conf.getProperty("files.filePrefix");
			this.linesPerFile = Integer.parseInt(conf.getProperty("files.linesPerFile"));
			this.deepLevel = Integer.parseInt(conf.getProperty("files.deepLevel"));
			this.foldersPercentage = Integer.parseInt(conf.getProperty("files.foldersPercentage"));
			this.filesTotal = Integer.parseInt(conf.getProperty("files.filesTotal"));
			this.maxSubFolders = Integer.parseInt(conf.getProperty("files.maxSubFolders"));
			this.maxFilesPerFolders = Integer.parseInt(conf.getProperty("files.maxFilesPerFolders"));
			this.startFolderId = Long.parseLong(conf.getProperty("files.startFolderId"));
			this.startDocId = Long.parseLong(conf.getProperty("files.startDocId"));
			this.logicalDocLayout = Boolean.parseBoolean(conf.getProperty("files.logicalDocLayout"));

			BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(
					conf.getProperty("files.master"))));
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

	/**
	 * Retrieves the max number of files in a folder
	 */
	public int getMaxFilesPerFolders() {
		return maxFilesPerFolders;
	}

	public void setMaxFilesPerFolders(int maxFilesPerFolders) {
		this.maxFilesPerFolders = maxFilesPerFolders;
	}

	/**
	 * Retrieves the total number of folders
	 */
	public int getFoldersTotal() {
		return foldersTotal;
	}

	public void setFoldersTotal(int foldersTotal) {
		this.foldersTotal = foldersTotal;
	}

	/**
	 * Retrieves the folder in which insert the files
	 */
	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * Retrieves the file from which retrieve the text for the generating files
	 */
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public void setLogicalDocLayout(boolean logicalDocLayout) {
		this.logicalDocLayout = logicalDocLayout;
	}

	/**
	 * Retrieves the total number of files
	 */
	public long getFilesTotal() {
		return filesTotal;
	}

	public void setFilesTotal(long filesTotal) {
		this.filesTotal = filesTotal;
	}

	/**
	 * Retrieves the number of lines on each file
	 */
	public int getLinesPerFile() {
		return linesPerFile;
	}

	public void setLinesPerFile(int linesPerFile) {
		this.linesPerFile = linesPerFile;
	}

	/**
	 * Retrieves the the deep level number of a folder
	 */
	public int getDeepLevel() {
		return deepLevel;
	}

	public void setDeepLevel(int deepLevel) {
		this.deepLevel = deepLevel;
	}

	/**
	 * Retrieves the folder percentage every 100 files
	 */
	public int getFoldersPercentage() {
		return foldersPercentage;
	}

	public void setFoldersPercentage(int foldersPercentage) {
		this.foldersPercentage = foldersPercentage;
	}

	/**
	 * Retrieves the max number of sub folders in a root folder
	 */
	public int getMaxSubFolders() {
		return maxSubFolders;
	}

	public void setMaxSubFolders(int maxSubFolders) {
		this.maxSubFolders = maxSubFolders;
	}

	/**
	 * Retrieves the folder prefix
	 */
	public String getFolderPrefix() {
		return folderPrefix;
	}

	public void setFolderPrefix(String folderPrefix) {
		this.folderPrefix = folderPrefix;
	}

	/**
	 * Retrieves the file prefix
	 */
	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	/**
	 * Retrieves the value of the folder's start name
	 */
	public long getStartFolderId() {
		return startFolderId;
	}

	public void setStartFolderId(long startFolderId) {
		this.startFolderId = startFolderId;
	}

	/**
	 * Retrieves the value of the file's start name
	 */
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
		log.fatal("Start of files generation");

		foldersTotal = (int) ((double) (filesTotal * foldersPercentage) / (double) 100);
		folders.clear();
		filesCount = 0;
		rootFolder.mkdirs();
		rootFolder.mkdir();

		while (folders.size() < foldersTotal)
			createTree(rootFolder, 0);

		while (filesCount < filesTotal)
			createFiles();

		log.fatal("End of files generation");
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
			if (folders.size() % 100 == 0)
				log.info("Created folder " + subDir.getName());
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
			for (int i = 0; i < subFiles && filesCount < filesTotal; i++) {
				File file;
				long docId = startDocId + filesCount;
				if (logicalDocLayout) {
					file = new File(folder, filePrefix + Long.toString(docId));
					file.mkdir();
					file = new File(file, filePrefix + (docId) + TXT);
				} else {
					file = new File(folder, filePrefix + (docId) + TXT);
				}
				if (!file.exists())
					writeFile(file);
				filesCount++;
				if (filesCount % 100 == 0)
					log.info("Total created files: " + filesCount);

				count++;
			}
			if (filesCount >= filesTotal)
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
		GenerateFiles genFiles = new GenerateFiles();
		genFiles.generate();
	}

	public List<File> getFolders() {
		return folders;
	}
}