package com.logicaldoc.benchmark;

import java.io.File;
import java.util.Random;

/**
 * Gives a random file from the docs in the docsFolder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class RandomFile {
	private String sourceDir = "docs";

	private File[] files = null;

	public File getFile() {
		if (files == null)
			files = new File(sourceDir).listFiles();

		Random generator = new Random();
		return files[generator.nextInt(files.length)];
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
}
