package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.exec.Exec;
import com.logicaldoc.util.io.FileUtil;

/**
 * Main starting point for all thumbnail builders
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8.1
 *
 */
public abstract class AbstractThumbnailBuilder implements ThumbnailBuilder {
	protected static Logger log = LoggerFactory.getLogger(AbstractThumbnailBuilder.class);

	protected static String CONVERT = "command.convert";

	protected static String SWFTOOLSPATH = "swftools.path";

	/** For these extensions we are able to directly convert to SWF */
	protected String SWF_DIRECT_CONVERSION_EXTS = "gif, png, pdf, jpeg, jpg, tif, tiff, bmp";
	
	@Override
	public File[] buildPreview(File src, String srcFileName, File dest) throws IOException {
		File[] pages = null;
		String docExtension = FilenameUtils.getExtension(srcFileName).toLowerCase();
		if (SWF_DIRECT_CONVERSION_EXTS.contains(docExtension))
			pages = document2swf(src, docExtension, dest);
		return pages;
	}

	/**
	 * Convert a generic document(image or PDF) to SWF (for document preview
	 * feature). The page files are ordered.
	 */
	protected File[] document2swf(File src, String extension, File root) throws IOException {
		FileOutputStream fos = null;
		File tmp = src;
		File tmp2=null;
		try {
			boolean isWin = SystemUtils.IS_OS_WINDOWS;

			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			String command = conf.getProperty(SWFTOOLSPATH);
			if (extension.equalsIgnoreCase("pdf"))
				command += File.separatorChar + "pdf2swf";
			else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
				command += File.separatorChar + "jpeg2swf";
			else if (extension.equalsIgnoreCase("png"))
				command += File.separatorChar + "png2swf";
			else if (extension.equalsIgnoreCase("bmp") || extension.equalsIgnoreCase("gif")) {
				// In this case we have to convert to temporary jpg first
				tmp2 = File.createTempFile("preview", ".jpg");
				String jpegCommand = (isWin ? "\"" : "") + new File(conf.getProperty(CONVERT)).getPath()
						+ (isWin ? "\"" : "") + " " + (isWin ? "\"" : "") + tmp.getPath() + (isWin ? "\"" : "") + " "
						+ (isWin ? "\"" : "") + tmp2.getPath() + (isWin ? "\"" : "");
				Exec.exec(jpegCommand, null, null, 10);

				tmp = tmp2;

				command += File.separatorChar + "jpeg2swf";
			} else if (extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("tif")) {
				// In this case we have to convert to temporary pdf first to
				// collect all the pages into a single file
				tmp2 = File.createTempFile("preview", ".pdf");
				String pdfCommand = (isWin ? "\"" : "") + new File(conf.getProperty(CONVERT)).getPath()
						+ (isWin ? "\"" : "") + " " + (isWin ? "\"" : "") + tmp.getPath() + (isWin ? "\"" : "") + " "
						+ (isWin ? "\"" : "") + tmp2.getPath() + (isWin ? "\"" : "");
				Exec.exec(pdfCommand, null, null, 10);

				tmp = tmp2;
				command += File.separatorChar + "pdf2swf";
			}

			command = new File(command).getPath();
			List<String> commandLine = new ArrayList<String>();
			commandLine.add(command);

			if (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("tiff")
					|| extension.equalsIgnoreCase("tif")) {
				int pages = -1;
				try {
					pages = conf.getInt("gui.preview.pages");
				} catch (Throwable t) {

				}

				commandLine.add("-T 9");
				if (pages > 0)
					commandLine.add("-p 1-" + pages);
				commandLine.add("-f");
				commandLine.add("-t");
				commandLine.add("-G");
				commandLine.add("-s storeallcharacters");
			} else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
					|| extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
					|| extension.equalsIgnoreCase("gif")) {
				commandLine.add("-T 9");
			}

			if (command.contains("pdf2swf")) {
				/*
				 * Save the preview as multiple SWFs, this will allow for
				 * handling huge documents composed by several pages.
				 */
				commandLine.add(tmp.getPath());
				commandLine.add(root.getAbsolutePath() + File.separator + "page-%");
			} else {
				commandLine.add("-o " + root.getAbsolutePath() + File.separator + "page-1");
				commandLine.add(tmp.getPath());
			}

			log.debug("Executing command: " + commandLine.toString());

			int timeout = 20;
			try {
				timeout = Integer.parseInt(conf.getProperty("gui.preview.timeout"));
			} catch (Throwable t) {
			}

			if (command.contains("pdf2swf"))
				Exec.exec(commandLine, null, null, timeout);
			else {
				// Seems that commands like jpeg2swf need to be executed as a
				// single line command
				StringBuffer sb = new StringBuffer();
				for (String cmd : commandLine) {
					sb.append(cmd);
					sb.append(" ");
				}
				Exec.exec(sb.toString(), null, null, timeout);
			}

		} catch (Throwable e) {
			log.error("Error in document to SWF conversion", e);
		} finally {
			IOUtils.closeQuietly(fos);
			FileUtil.strongDelete(tmp2);
		}
		
		if (root.exists()) {
			File[] pages = root.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("page-");
				}
			});

			Arrays.sort(pages, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					String name1 = f1.getName();
					String name2 = f2.getName();
					Integer n1 = new Integer(name1.substring(5));
					Integer n2 = new Integer(name2.substring(5));
					return n1.compareTo(n2);
				}
			});

			return pages;
		} else
			return new File[0];
	}
}
