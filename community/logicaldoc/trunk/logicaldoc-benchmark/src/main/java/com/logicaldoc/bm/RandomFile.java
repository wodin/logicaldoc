package com.logicaldoc.bm;

import java.io.File;
import java.util.Random;

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
	
	public File getFile() throws Exception {
		
		if (sourceFiles == null) 
			sourceFiles = getSourceFiles(new File(sourceDir));
		
		return sourceFiles[random.nextInt(sourceFiles.length)];
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
    
    private File[] getSourceFiles(File sourceDir) throws Exception
    {
        // Ensure that the source directory is present, if specified
        if (sourceDir != null)
        {
            if (!sourceDir.exists())
            {
                throw new Exception("The source directory to contain upload files is missing: " + sourceDir);
            }
            return sourceDir.listFiles();
        }
        else
        {
            return new File[] {};
        }
    }    
}
