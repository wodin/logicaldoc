package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
	public File buildPreview(String tenant, File src, String srcFileName, File dest) throws IOException {
		String docExtension = FilenameUtils.getExtension(srcFileName).toLowerCase();
		if (SWF_DIRECT_CONVERSION_EXTS.contains(docExtension))
			return document2swf(tenant, src, docExtension, dest);
		else
			return null;
	}

	/**
	 * Convert a generic document(image or PDF) to SWF (for document preview
	 * feature).
	 */
	protected File document2swf(String tenant, File src, String extension, File root) throws IOException {
		FileOutputStream fos = null;
		File out = null;
		File tmp = src;
		File tmp2 = null;
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			int timeout = 40;
			try {
				timeout = Integer.parseInt(conf.getProperty(tenant + ".gui.preview.timeout"));
			} catch (Throwable t) {
			}
			
			boolean isWin = SystemUtils.IS_OS_WINDOWS;

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
				Exec.exec(jpegCommand, null, null, timeout);

				tmp = tmp2;
				out = tmp;
				command += File.separatorChar + "jpeg2swf";
			} else if (extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("tif")) {
				// In this case we have to convert to temporary pdf first to
				// collect all the pages into a single file
				tmp2 = File.createTempFile("preview", ".pdf");
				String pdfCommand = (isWin ? "\"" : "") + new File(conf.getProperty(CONVERT)).getPath()
						+ (isWin ? "\"" : "") + " " + (isWin ? "\"" : "") + tmp.getPath() + (isWin ? "\"" : "") + " "
						+ (isWin ? "\"" : "") + tmp2.getPath() + (isWin ? "\"" : "");
				Exec.exec(pdfCommand, null, null, timeout);

				tmp = tmp2;
				out = tmp;
				command += File.separatorChar + "pdf2swf";
			}

			command = new File(command).getPath();
			List<String> commandLine = new ArrayList<String>();
			commandLine.add(command);

			if (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("tiff")
					|| extension.equalsIgnoreCase("tif")) {
				int pages = -1;
				try {
					pages = conf.getInt(tenant + ".gui.preview.pages");
				} catch (Throwable t) {

				}

				commandLine.add("-T 9");
				if (pages > 0)
					commandLine.add("-p 1-" + pages);
				commandLine.add("-t");
				commandLine.add("-G");
				commandLine.add("-s storeallcharacters");
			} else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
					|| extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
					|| extension.equalsIgnoreCase("gif")) {
				commandLine.add("-T 9");
			}

			out = new File(root.getAbsolutePath() + "/preview.swf");

			if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
					|| extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
					|| extension.equalsIgnoreCase("gif")) {
				commandLine.add("-o "+out.getPath());
				commandLine.add(tmp.getPath());
			}else{
				commandLine.add(tmp.getPath());
				commandLine.add(out.getPath());
			}

			log.debug("Executing command: " + commandLine.toString());

			if (command.contains("pdf2swf")) {
				Exec.exec(commandLine, null, null, timeout);
			} else {
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

		return out;
	}
}
