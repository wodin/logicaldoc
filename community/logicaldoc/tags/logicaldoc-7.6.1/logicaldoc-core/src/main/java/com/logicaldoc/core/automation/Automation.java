package com.logicaldoc.core.automation;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;

/**
 * Represents a Facade on Velocity
 * 
 * @author Marco Meschieri - LogicalDOC since <product_release>
 */
public class Automation {

	private static final String CLASS_TOOL = "ClassTool";

	public static final String DIC_I18N = "I18N";

	public static final String DOC_TOOL = "DocTool";

	public static final String FOLDER_TOOL = "FolderTool";

	public static final String CURRENT_DATE = "CURRENT_DATE";

	public static final String PRODUCT = "product";

	public static final String DATE_TOOL = "DateTool";

	public static final String SYSTEM_TOOL = "SystemTool";

	public static final String KEY_LOCALE = "locale";

	public static final String SERVER_URL = "serverUrl";

	public static final String LOG = "log";

	private static Logger log = LoggerFactory.getLogger(Automation.class);

	private String logTag = "ScriptEngine";

	private Locale locale = Locale.ENGLISH;

	public Automation() {
		super();
	}

	public Automation(String logTag) {
		super();
		this.logTag = logTag;
	}

	public Automation(String logTag, Locale locale) {
		super();
		this.logTag = logTag;
		this.locale = locale;
	}

	/**
	 * Evaluate a given expression. The dictionary will automatically contain
	 * the following keys:
	 * <ol>
	 * <li>product: name of the product</li>
	 * <li>locale: the default locale</li>
	 * <li>CURRENT_DATE: the actual date</li>
	 * <li>DateTool</li>
	 * <li>I18N</li>
	 * <li>DocTool</li>
	 * <li>FolderTool</li>
	 * </ol>
	 * 
	 * @param expression The string expression to process
	 * @param dictionary The dictionary to use
	 * @return The processed result
	 */
	public String evaluate(String expression, Map<String, Object> dictionary) {
		Map<String, Object> extendedDictionary = new HashMap<String, Object>();

		// This is needed to handle new lines
		extendedDictionary.put("nl", "\n");

		// The product name
		extendedDictionary.put(PRODUCT, SystemInfo.get().getProduct());

		// This is the locale
		if (!dictionary.containsKey(KEY_LOCALE))
			dictionary.put(KEY_LOCALE, locale);

		// This is needed to format dates
		DateTool dateTool = new DateTool(I18N.getMessages((Locale) dictionary.get(KEY_LOCALE)).get("format_date"), I18N
				.getMessages((Locale) dictionary.get(KEY_LOCALE)).get("format_dateshort"));
		extendedDictionary.put(DATE_TOOL, dateTool);

		// Put the current date
		extendedDictionary.put(CURRENT_DATE, new Date());

		// Localized messages map
		extendedDictionary.put(DIC_I18N, new I18NTool(I18N.getMessages((Locale) dictionary.get(KEY_LOCALE))));

		// This is needed to print document's URL
		extendedDictionary.put(DOC_TOOL, new DocTool());

		// This is needed to print folder's URL
		extendedDictionary.put(FOLDER_TOOL, new FolderTool());

		// Utility functions for manipulating classes and resources
		extendedDictionary.put(CLASS_TOOL, new ClassTool());

		// System Utility functions
		extendedDictionary.put(SYSTEM_TOOL, new SystemTool());

		// Access to the system log
		extendedDictionary.put(LOG, new LogTool());

		dictionary.put(SERVER_URL, Context.get().getProperties().get("server.url"));

		if (dictionary != null)
			extendedDictionary.putAll(dictionary);

		StringWriter writer = new StringWriter();
		try {
			if (StringUtils.isNotEmpty(expression)) {
				VelocityContext context = new VelocityContext(extendedDictionary);
				Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
				Velocity.setProperty("runtime.log.logsystem.log4j.logger", Automation.class.getName());
				Velocity.evaluate(context, writer, StringUtils.isNotEmpty(logTag) ? logTag : "ScriptEngine",
						expression.replace("\n", "${nl}"));
			}
			return writer.toString();
		} catch (Throwable e) {
			log.error("Error in this script: " + expression, e);
			return expression;
		}
	}
}