package com.logicaldoc.bm;

import java.io.File;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.sun.mail.iap.ByteArray;

/**
 * Gives a random file from the docs in a source folder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class RandomFile {

	private String sourceDir = "docs";

	private File[] sourceFiles = null;

	private Random random = new Random();

	private ByteArray[] contents = null;

	public Object[] getFile() throws Exception {
		if (sourceFiles == null)
			init(new File(sourceDir));
		int index = random.nextInt(sourceFiles.length);
		return new Object[] { sourceFiles[index], contents[index] };
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public synchronized void init(File sourceDir) throws Exception {
		if (sourceFiles != null)
			return;

		// Ensure that the source directory is present, if specified
		if (sourceDir != null) {
			if (!sourceDir.exists()) {
				throw new Exception("The source directory to contain upload files is missing: " + sourceDir);
			}
			sourceFiles = sourceDir.listFiles();
			contents = new ByteArray[sourceFiles.length];

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			boolean loadMemory = "true".equals(config.getProperty("Upload.loadinmemory"));

			for (int i = 0; i < sourceFiles.length; i++) {
				contents[i] = loadMemory ? new ByteArray(FileUtils.readFileToByteArray(sourceFiles[i]), 0,
						(int) sourceFiles[i].length()) : null;
			}
		} else {
			sourceFiles = new File[0];
			contents = new ByteArray[0];
		}
	}
}
