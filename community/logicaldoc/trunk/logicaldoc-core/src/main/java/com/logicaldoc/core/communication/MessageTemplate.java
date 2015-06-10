package com.logicaldoc.core.communication;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.ContextProperties;

/**
 * A template for messaging purposes.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class MessageTemplate extends PersistentObject {

	private String name = "";

	private String description = "";

	private String language = "en";

	private String body;

	private String subject;

	public MessageTemplate() {
	}

	private String getFormattedContent(Map<String, Object> dictionary, String text) {
		// This is needed to handle new lines
		dictionary.put("nl", "\n");

		// The product name
		dictionary.put("product", SystemInfo.get().getProduct());

		// This is the locale
		Locale locale = LocaleUtil.toLocale(language);
		dictionary.put("locale", locale);

		// General configurations
		ContextProperties config = (ContextProperties) com.logicaldoc.util.Context.getInstance().getBean(
				ContextProperties.class);
		dictionary.put("serverUrl", config.get("server.url"));

		// This is needed to format dates
		DateTool dateTool = new DateTool(I18N.getMessages(locale).get("format_date"), I18N.getMessages(locale).get(
				"format_dateshort"));
		dictionary.put("DateTool", dateTool);

		// Localized messages map
		dictionary.put("I18N", new I18NTool(I18N.getMessages(locale)));

		// This is needed to print document's URL
		dictionary.put("DocTool", new DocTool());

		// This is needed to print folder's URL
		dictionary.put("FolderTool", new FolderTool());

		VelocityContext context = new VelocityContext(dictionary);

		StringWriter writer = new StringWriter();
		try {
			Velocity.evaluate(context, writer, getName(), text.replace("\n", "${nl}"));
			return writer.toString();
		} catch (Exception e) {
			return text;
		}
	}

	public String getFormattedBody(Map<String, Object> dictionary) {
		return getFormattedContent(dictionary, getBody());
	}

	public String getFormattedSubject(Map<String, Object> dictionary) {
		return getFormattedContent(dictionary, getSubject());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		MessageTemplate newTemplate = new MessageTemplate();
		newTemplate.setBody(getBody());
		newTemplate.setDescription(getDescription());
		newTemplate.setLanguage(getLanguage());
		newTemplate.setName(getName());
		newTemplate.setSubject(getSubject());
		newTemplate.setTenantId(getTenantId());
		return newTemplate;
	}

}