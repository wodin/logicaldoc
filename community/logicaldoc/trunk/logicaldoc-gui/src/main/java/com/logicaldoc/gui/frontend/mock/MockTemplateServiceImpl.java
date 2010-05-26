package com.logicaldoc.gui.frontend.mock;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.frontend.client.services.TemplateService;

/**
 * Implementation of the TemplateService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockTemplateServiceImpl extends RemoteServiceServlet implements TemplateService {

	private static final long serialVersionUID = 1L;

	@Override
	public void delete(String sid, long templateId) {

	}

	@Override
	public GUITemplate save(String sid, GUITemplate template) {
		if (template.getId() == 0)
			template.setId(9999);

		// TODO Rinumerare gli attributi in base all'ordine nell'array
		return template;
	}

	@Override
	public GUITemplate getTemplate(String sid, long templateId) {
		GUITemplate template = new GUITemplate();
		template.setId(templateId);
		template.setName("Template" + templateId);
		template.setDescription("Description" + templateId);
		return template;
	}
}
