package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.Exec;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Takes care of images thumbnail builder
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ImageThumbnailBuilder implements ThumbnailBuilder {

	protected static String CONVERT = "command.convert";

	@Override
	public synchronized void build(File src, String srcFileName, int size, File dest, int quality) throws IOException {
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			String commandLine = conf.getProperty(CONVERT) + " -compress JPEG -quality " + Integer.toString(quality)
					+ " -resize x" + Integer.toString(size) + " " + src.getPath() + " " + dest.getPath();
			Exec.exec(commandLine, null, null, 10);
		} catch (Throwable e) {
			throw new IOException("Error in IMG to JPEG conversion", e);
		}
	}
}