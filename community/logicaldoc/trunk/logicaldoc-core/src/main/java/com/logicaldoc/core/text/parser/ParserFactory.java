package com.logicaldoc.core.text.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;

/**
 * This is a factory, returning a parser instance for the given file. Created on
 * 4. November 2003, 21:54
 * 
 * @author Michael Scholz
 */
public class ParserFactory {
	
	protected static Log log = LogFactory.getLog(ParserFactory.class);

	// This is the list of registered parsers: key is the file extension, value
	// is the parser class
	private static Map<String, Class> parsers = new HashMap<String, Class>();

	/**
	 * Registers all parsers from extension points
	 */
	public static void init() {
		parsers.clear();

		// First of all register all standard parsers
		parsers.put("doc", DOCParser.class);
		parsers.put("dot", DOCParser.class);
		
		parsers.put("htm", HTMLParser.class);
		parsers.put("html", HTMLParser.class);

		parsers.put("pdf", PDFParser.class);
		parsers.put("rtf", RTFParser.class);

		// StarOffice, OpenOffice 1.0 - 1.1 extensions
		parsers.put("sxw", OpenOfficeParser.class);
		parsers.put("sxc", OpenOfficeParser.class);
		parsers.put("sxi", OpenOfficeParser.class); // Presentation

		// OpenOffice 2.3/3.0 extensions
		parsers.put("odt", OpenOfficeParser.class);
		parsers.put("ods", OpenOfficeParser.class);
		parsers.put("odp", OpenOfficeParser.class);

		// OpenDocument extensions
		parsers.put("ott", OpenOfficeParser.class);
		parsers.put("ots", OpenOfficeParser.class);
		parsers.put("otp", OpenOfficeParser.class);
		
		// KOffice 1.6.x extensions
		parsers.put("kwd", KOfficeParser.class);
		parsers.put("ksp", KOfficeParser.class);
		parsers.put("kpr", KOfficeParser.class);
		
		// WordPerfect
		parsers.put("wpd", WPDParser.class);
		
		// AbiWord http://www.abisource.com/
		parsers.put("abw", XMLParser.class);
		parsers.put("zabw", ZABWParser.class); // Compressed AbiWord document

		parsers.put("txt", TXTParser.class);
		parsers.put("csv", TXTParser.class);
		parsers.put("dbf", TXTParser.class);
		parsers.put("xml", XMLParser.class);
		parsers.put("xls", XLSParser.class);
		parsers.put("xlt", XLSParser.class);
		
		// MS Office 2003 Powerpoint
		parsers.put("ppt", PPTParser.class);
		parsers.put("pps", PPTParser.class);
		parsers.put("pot", PPTParser.class);

		// Acquire the 'Parse' extensions of the core plugin and add defined
		// parsers
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "Parser");
		for (Extension extension : exts) {
			String ext = extension.getParameter("extension").valueAsString().toLowerCase();
			String className = extension.getParameter("class").valueAsString();
			try {
				Class clazz = Class.forName(className);
				// Try to instantiate the parser
				Object parser = clazz.newInstance();
				if (!(parser instanceof Parser))
					throw new Exception("The specified parser " + className + " doesn't implement Parser interface");
				parsers.put(ext, clazz);
				log.info("Added new parser " + className + " for extension " + ext);
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
		}
	}

	public static Parser getParser(File file, Locale locale, String extension) {
		if (parsers.isEmpty())
			init();

		String ext = extension;
		if (StringUtils.isEmpty(ext)) {
			String filename = file.getName().toLowerCase();
			ext = FilenameUtils.getExtension(filename);
		}else{
			ext=extension.toLowerCase();
		}

		Parser parser = null;
		Class parserClass = parsers.get(ext);
		if (parserClass != null) {
			try {
				parser = (Parser) parserClass.newInstance();
			} catch (Exception e) {
				log.error(e.getMessage());
				parser = new TXTParser();
			}
		} else {
			log.warn("No registered parser for extension " + ext);
			Magic mimeDetector = new Magic();
			try {
				MagicMatch match = mimeDetector.getMagicMatch(file, true);
				if ("text/plain".equals(match.getMimeType())) {
					log.warn("Try to parse the file as plain text");
					parser = new TXTParser();
				} else {
					parser = new DummyParser();
				}
			} catch (Exception e) {
				parser = new DummyParser();
			}
		}

		if (locale == null)
			parser.parse(file);
		else
			parser.parse(file, locale);
		return parser;
	}

	/**
	 * Factory methods for parsers, the correct parser will be instantiated
	 * depending on the file extension.
	 * 
	 * @param file
	 * @param locale
	 * @return
	 */
	public static Parser getParser(File file, Locale locale) {
		return getParser(file, locale, null);
	}

	/**
	 * Factory methods for parsers, the correct parser will be instantiated
	 * depending on the file extension.
	 * 
	 * @param file
	 * @return
	 */
	public static Parser getParser(File file) {
		return getParser(file, null);
	}
}