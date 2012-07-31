package com.logicaldoc.core.communication;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.LocaleUtil;

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

	public String getFormattedBody(Map<String, String> args) {
		// Retrieve all labels and add/overwrite with provided arguments
		Map<String, String> ctx = I18N.getMessages(LocaleUtil.toLocale(getLanguage()));
		ctx.putAll(args);

		VelocityContext context = new VelocityContext(ctx);

		StringWriter writer = new StringWriter();
		try {
			Velocity.evaluate(context, writer, getName(), getBody());
			return writer.toString();
		} catch (Exception e) {
			return getBody();
		}
	}

	public String getFormattedSubject(Map<String, String> args) {
		// Retrieve all labels and add/overwrite with provided arguments
		Map<String, String> ctx = I18N.getMessages(LocaleUtil.toLocale(getLanguage()));
		ctx.putAll(args);

		VelocityContext context = new VelocityContext(ctx);

		StringWriter writer = new StringWriter();
		try {
			Velocity.evaluate(context, writer, getName(), getSubject());
			return writer.toString();
		} catch (Exception e) {
			return getBody();
		}
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
}