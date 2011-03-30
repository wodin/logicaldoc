package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
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
		return template;
	}

	@Override
	public GUITemplate getTemplate(String sid, long templateId) {
		GUITemplate template = new GUITemplate();
		template.setId(templateId);
		template.setName("Template" + templateId);
		template.setDescription("Description" + templateId);
		
		GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[4];

		GUIExtendedAttribute att = new GUIExtendedAttribute();
		att.setName("Attribute A");
		att.setLabel("Attribute A");
		att.setPosition(0);
		att.setMandatory(true);
		att.setType(GUIExtendedAttribute.TYPE_INT);
		attributes[0] = att;

		att = new GUIExtendedAttribute();
		att.setName("Attribute B");
		att.setLabel("Attribute B");
		att.setPosition(1);
		att.setType(GUIExtendedAttribute.TYPE_DOUBLE);
		attributes[1] = att;

		att = new GUIExtendedAttribute();
		att.setName("Attribute C");
		att.setLabel("Attribute C");
		att.setPosition(2);
		att.setType(GUIExtendedAttribute.TYPE_STRING);
		attributes[2] = att;

		att = new GUIExtendedAttribute();
		att.setName("Attribute D");
		att.setLabel("Attribute C");
		att.setPosition(3);
		att.setType(GUIExtendedAttribute.TYPE_DATE);
		attributes[3] = att;
		
		template.setAttributes(attributes);
		
		return template;
	}
}
