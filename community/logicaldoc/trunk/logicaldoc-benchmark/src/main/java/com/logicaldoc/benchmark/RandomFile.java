package com.logicaldoc.benchmark;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Gives a random file from the docs in the docsFolder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class RandomFile {
	
	private String sourceDir = "docs";

	private File[] sourceFiles = null;

	public File getFile() throws Exception {
		
		if (sourceFiles == null) {
			sourceFiles = getSourceFiles(new File(sourceDir));
		}

		Random generator = new Random();		
		return sourceFiles[generator.nextInt(sourceFiles.length)];
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
		
    public File[] getSourceFiles()
    {
        return sourceFiles;
    }	
    
    private static File[] getSourceFiles(File sourceDir) throws Exception
    {
        // Ensure that the source directory is present, if specified
        if (sourceDir != null)
        {
            if (!sourceDir.exists())
            {
                throw new Exception("The source directory to contain upload files is missing: " + sourceDir);
            }
            // Check that there is something in it
            File[] allFiles = sourceDir.listFiles();
            ArrayList<File> sourceFiles = new ArrayList<File>(allFiles.length);
            for (File file : allFiles)
            {
                if (file.isDirectory())
                {
                    continue;
                }
                sourceFiles.add(file);
            }
            File[] ret = new File[sourceFiles.size()];
            return sourceFiles.toArray(ret);
        }
        else
        {
            return new File[] {};
        }
    }    
}
