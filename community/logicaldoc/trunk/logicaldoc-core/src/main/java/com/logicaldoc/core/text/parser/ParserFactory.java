package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.core.text.parser.wordperfect.WordPerfectParser;
import com.logicaldoc.util.plugin.PluginRegistry;

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

		// OpenDocument template extensions
		parsers.put("ott", OpenOfficeParser.class);
		parsers.put("ots", OpenOfficeParser.class);
		parsers.put("otp", OpenOfficeParser.class);

		// KOffice 1.6.x extensions
		parsers.put("kwd", KOfficeParser.class);
		parsers.put("ksp", KOfficeParser.class);
		parsers.put("kpr", KOfficeParser.class);

		// WordPerfect
		parsers.put("wpd", WordPerfectParser.class);

		// AbiWord http://www.abisource.com/
		parsers.put("abw", AbiWordParser.class);
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

	public static Parser getParser(File file, String filename, Locale locale, String encoding) {
		Parser parser = detectParser(file, null, filename, locale, encoding);
		parser.parse(file);
		return parser;
	}

	/**
	 * Internal method containing the lookup logic. can be invoked with a File
	 * OR an InputStream.
	 * 
	 * @param file
	 * @param is
	 * @param filename
	 * @param locale
	 * @param encoding
	 * @return
	 */
	protected static Parser detectParser(File file, InputStream is, String filename, Locale locale, String encoding) {
		if (parsers.isEmpty())
			init();

		String ext = filename != null ? FilenameUtils.getExtension(filename) : null;
		if (StringUtils.isEmpty(ext) && file != null) {
			String fileName = file.getName().toLowerCase();
			ext = FilenameUtils.getExtension(fileName);
		} else {
			ext = ext.toLowerCase();
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
			try {
				MagicMatch match = null;
				if (file != null)
					match = Magic.getMagicMatch(file, true);
				else
					match = Magic.getMagicMatch(IOUtils.toByteArray(is), true);
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
		parser.setFilename(filename);
		parser.setLocale(locale);
		parser.setEncoding(encoding);
		return parser;
	}

	public static Parser getParser(InputStream is, String filename, Locale locale, String encoding) {
		try {
			Parser parser = detectParser(null, is, filename, locale, encoding);
			parser.parse(is);
			return parser;
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}

	public static Parser getParser(String filename) {
		if (parsers.isEmpty())
			init();

		String ext = FilenameUtils.getExtension(filename).toLowerCase();

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
			parser = new DummyParser();
		}
		parser.setFilename(filename);
		return parser;
	}

	public static Set<String> getExtensions() {
		if (parsers.isEmpty())
			init();
		return parsers.keySet();
	}

	public static Map<String, Class> getParsers() {
		if (parsers.isEmpty())
			init();
		return parsers;
	}
}