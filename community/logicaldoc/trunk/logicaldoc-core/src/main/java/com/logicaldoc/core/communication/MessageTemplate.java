package com.logicaldoc.core.communication;

import java.util.Map;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.script.ScriptingEngine;
import com.logicaldoc.util.Context;
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
		ScriptingEngine script = new ScriptingEngine(getName(), LocaleUtil.toLocale(language));

		// General configurations
		ContextProperties config = Context.get().getRegisty();
		dictionary.put("serverUrl", config.get("server.url"));

		return script.evaluate(text, dictionary);
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