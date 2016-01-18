package com.logicaldoc.core.script;

import java.io.StringWriter;
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
import com.logicaldoc.util.config.ContextProperties;

/**
 * Represents a Facade on Velocity
 * 
 * @author Marco Meschieri - LogicalDOC since <product_release>
 */
public class ScriptEngine {

	private static final String KEY_LOCALE = "locale";

	private static Logger log = LoggerFactory.getLogger(ScriptEngine.class);

	private String logTag = "ScriptEngine";

	private Locale locale = Locale.ENGLISH;

	public ScriptEngine() {
		super();
	}

	public ScriptEngine(String logTag, Locale locale) {
		super();
		this.logTag = logTag;
		this.locale = locale;
	}

	public String evaluate(String expression, Map<String, Object> dictionary) {
		// This is needed to handle new lines
		dictionary.put("nl", "\n");

		// The product name
		dictionary.put("product", SystemInfo.get().getProduct());

		// This is the locale
		if (!dictionary.containsKey(KEY_LOCALE))
			dictionary.put(KEY_LOCALE, locale);

		// This is needed to format dates
		DateTool dateTool = new DateTool(I18N.getMessages((Locale) dictionary.get(KEY_LOCALE)).get("format_date"), I18N
				.getMessages((Locale) dictionary.get(KEY_LOCALE)).get("format_dateshort"));
		dictionary.put("DateTool", dateTool);

		// Localized messages map
		dictionary.put("I18N", new I18NTool(I18N.getMessages((Locale) dictionary.get(KEY_LOCALE))));

		// This is needed to print document's URL
		dictionary.put("DocTool", new DocTool());

		// This is needed to print folder's URL
		dictionary.put("FolderTool", new FolderTool());

		StringWriter writer = new StringWriter();
		try {
			VelocityContext context = new VelocityContext(dictionary);
			Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
			Velocity.setProperty("runtime.log.logsystem.log4j.logger", ScriptEngine.class.getName());
			Velocity.evaluate(context, writer, StringUtils.isNotEmpty(logTag) ? logTag : "ScriptEngine",
					expression.replace("\n", "${nl}"));
			return writer.toString();
		} catch (Throwable e) {
			log.error(e.getMessage());
			return expression;
		}
	}
}