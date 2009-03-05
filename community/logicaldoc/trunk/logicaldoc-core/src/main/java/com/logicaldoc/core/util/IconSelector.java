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
			icon = "generic.png";
		else if (ext.equals("pdf"))
			icon = "pdf.png";
		else if (ext.equals("txt"))
			icon = "text.png";
		else if (ext.equals("doc") || ext.equals("docx") || ext.equals("dot") || ext.equals("rtf") || ext.equals("sxw")
				|| ext.equals("wpd") || ext.equals("kwd") || ext.equals("abw") || ext.equals("zabw") || ext.equals("odt"))
			icon = "word.png";
		else if (ext.equals("xls")|| ext.equals("xslx") || ext.equals("xlt") || ext.equals("sxc") || ext.equals("dbf") || ext.equals("ksp")
				|| ext.equals("ods") || ext.equals("odb"))
			icon = "excel.png";
		else if (ext.equals("ppt") || ext.equals("pptx") || ext.equals("pps") || ext.equals("pot") || ext.equals("sxi") || ext.equals("kpr")
				|| ext.equals("odp"))
			icon = "powerpoint.png";
		else if (ext.equals("apf") || ext.equals("bmp") || ext.equals("cur") || ext.equals("dib") || ext.equals("gif")
				|| ext.equals("jpg") || ext.equals("psd") || ext.equals("tif") || ext.equals("tiff") || ext.equals("png"))
			icon = "picture.png";
		else if (ext.equals("htm") || ext.equals("html") || ext.equals("xml"))
			icon = "html.png";
        else if (ext.equals("mail"))       	
            icon = "mail.gif";
        else if (ext.equals("zip"))       	
            icon = "zip.png";
        else if (ext.equals("dwg"))       	
            icon = "dwg.gif";
        else
			icon = "generic.png";
		
		return icon;
	}
}