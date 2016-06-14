package com.logicaldoc.core.script;

import java.io.StringWriter;
import java.util.Date;
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

/**
 * Represents a Facade on Velocity
 * 
 * @author Marco Meschieri - LogicalDOC since <product_release>
 */
public class ScriptingEngine {

	private static final String CLASS_TOOL = "ClassTool";

	public static final String DIC_I18N = "I18N";

	public static final String DOC_TOOL = "DocTool";

	public static final String FOLDER_TOOL = "FolderTool";

	public static final String CURRENT_DATE = "CURRENT_DATE";

	public static final String PRODUCT = "product";

	public static final String DATE_TOOL = "DateTool";

	public static final String KEY_LOCALE = "locale";

	public static final String LOG = "log";

	private static Logger log = LoggerFactory.getLogger(ScriptingEngine.class);

	private String logTag = "ScriptEngine";

	private Locale locale = Locale.ENGLISH;

	public ScriptingEngine() {
		super();
	}

	public ScriptingEngine(String logTag) {
		super();
		this.logTag = logTag;
	}

	public ScriptingEngine(String logTag, Locale locale) {
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
		// This is needed to handle new lines
		dictionary.put("nl", "\n");

		// The product name
		dictionary.put(PRODUCT, SystemInfo.get().getProduct());

		// This is the locale
		if (!dictionary.containsKey(KEY_LOCALE))
			dictionary.put(KEY_LOCALE, locale);

		// This is needed to format dates
		DateTool dateTool = new DateTool(I18N.getMessages((Locale) dictionary.get(KEY_LOCALE)).get("format_date"), I18N
				.getMessages((Locale) dictionary.get(KEY_LOCALE)).get("format_dateshort"));
		dictionary.put(DATE_TOOL, dateTool);

		// Put the current date
		dictionary.put(CURRENT_DATE, new Date());

		// Localized messages map
		dictionary.put(DIC_I18N, new I18NTool(I18N.getMessages((Locale) dictionary.get(KEY_LOCALE))));

		// This is needed to print document's URL
		dictionary.put(DOC_TOOL, new DocTool());

		// This is needed to print folder's URL
		dictionary.put(FOLDER_TOOL, new FolderTool());

		// Utility functions for manipulating classes and resources
		dictionary.put(CLASS_TOOL, new ClassTool());

		// Access to the system log
		dictionary.put(LOG, new LogTool());

		StringWriter writer = new StringWriter();
		try {
			VelocityContext context = new VelocityContext(dictionary);
			Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
			Velocity.setProperty("runtime.log.logsystem.log4j.logger", ScriptingEngine.class.getName());
			Velocity.evaluate(context, writer, StringUtils.isNotEmpty(logTag) ? logTag : "ScriptEngine",
					expression.replace("\n", "${nl}"));
			return writer.toString();
		} catch (Throwable e) {
			log.error(e.getMessage());
			return expression;
		}
	}
}