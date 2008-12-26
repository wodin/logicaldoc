package com.logicaldoc.core.text.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

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

		// StarOffice extensions
		parsers.put("sxw", SXParser.class);
		parsers.put("sxc", SXParser.class);
		parsers.put("sxp", SXParser.class);

		// OpenOffice 2.3 extensions
		parsers.put("odt", SXParser.class);
		parsers.put("ods", SXParser.class);
		parsers.put("odp", SXParser.class);

		// OpenDocument extensions
		parsers.put("ott", SXParser.class);
		parsers.put("ots", SXParser.class);
		parsers.put("otp", SXParser.class);

		parsers.put("txt", TXTParser.class);
		parsers.put("dbf", TXTParser.class);
		parsers.put("wpd", WPDParser.class);
		parsers.put("xml", XMLParser.class);
		parsers.put("xls", XLSParser.class);
		parsers.put("xlt", XLSParser.class);
		parsers.put("kwd", KOParser.class);
		parsers.put("kpr", KOParser.class);
		parsers.put("kpr", KOParser.class);
		parsers.put("ksp", KOParser.class);
		parsers.put("abw", XMLParser.class);
		parsers.put("zabw", ZABWParser.class);
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

	/**
	 * Factory methods for parsers, the correct parser will be instantiated
	 * depending on the file extension.
	 * 
	 * @param file
	 * @param locale
	 * @return
	 */
	public static Parser getParser(File file, Locale locale) {
		if (parsers.isEmpty())
			init();

		String filename = file.getName();
		String extension = filename.substring(filename.lastIndexOf(".") + 1);
		extension = extension.toLowerCase();

		Parser parser = null;
		Class parserClass = parsers.get(extension);
		if (parserClass != null) {
			try {
				parser = (Parser) parserClass.newInstance();
			} catch (Exception e) {
				log.error(e.getMessage());
				parser = new TXTParser();
			}
		} else {
			log.warn("No registered parser for extension " + extension);
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
	 * @return
	 */
	public static Parser getParser(File file) {
		return getParser(file, null);
	}
}