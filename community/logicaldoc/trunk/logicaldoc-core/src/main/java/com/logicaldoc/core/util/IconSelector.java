package com.logicaldoc.core.util;

/**
 * utility class to select an icon based on a file extension
 * @author Sebastian Stein
 */
public class IconSelector {
	
	/** returns path to menu icon by parsing the provided file extension */
	public static String selectIcon(String ext) {
		String icon = "";
		ext = ext.toLowerCase();

		if (ext == null || ext.equalsIgnoreCase(""))
			icon = "document.gif";
		else if (ext.equals("pdf"))
			icon = "pdf.gif";
		else if (ext.equals("doc") || ext.equals("docx") || ext.equals("dot") || ext.equals("rtf") || ext.equals("sxw") || ext.equals("txt")
				|| ext.equals("wpd") || ext.equals("kwd") || ext.equals("abw") || ext.equals("zabw") || ext.equals("odt"))
			icon = "textdoc.gif";
		else if (ext.equals("xls")|| ext.equals("xslx") || ext.equals("xlt") || ext.equals("sxc") || ext.equals("dbf") || ext.equals("ksp")
				|| ext.equals("ods") || ext.equals("odb"))
			icon = "tabledoc.gif";
		else if (ext.equals("ppt") || ext.equals("pptx") || ext.equals("pps") || ext.equals("pot") || ext.equals("sxi") || ext.equals("kpr")
				|| ext.equals("odp"))
			icon = "presentdoc.gif";
		else if (ext.equals("apf") || ext.equals("bmp") || ext.equals("cur") || ext.equals("dib") || ext.equals("gif")
				|| ext.equals("jpg") || ext.equals("psd") || ext.equals("tif") || ext.equals("tiff"))
			icon = "picture.gif";
		else if (ext.equals("htm") || ext.equals("html") || ext.equals("xml"))
			icon = "internet.gif";
        else if (ext.equals("mail"))
            icon = "mail.gif";
        else
			icon = "document.gif";
		
		return icon;
	}
}