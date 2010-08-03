package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.frontend.client.services.CustomIdService;

/**
 * Mock implementation of the CustomIdService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockCustomIdServiceImpl extends RemoteServiceServlet implements CustomIdService {

	private static final long serialVersionUID = 1L;

	@Override
	public void delete(String sid, long templateId) throws InvalidSessionException {

	}

	@Override
	public void save(String sid, GUICustomId customid) throws InvalidSessionException {

	}

	@Override
	public GUICustomId get(String sid, long templateId) throws InvalidSessionException {
		GUICustomId cid = new GUICustomId();
		cid.setTemplateId(templateId);
		cid.setTemplateName("Template " + templateId);
		cid.setScheme("<id>");
		return cid;
	}

	@Override
	public GUICustomId[] load(String sid) throws InvalidSessionException {
		GUICustomId[] ids = new GUICustomId[30];
		for (int i = 0; i < ids.length; i++) {
			GUICustomId cid = new GUICustomId();
			cid.setTemplateId(i);
			cid.setTemplateName("Template " + i);
			cid.setScheme("<id>");
			cid.setRegenerate(i % 2 == 0);
			ids[i] = cid;
		}
		return ids;
	}

	@Override
	public void reset(String sid, long templateId) throws InvalidSessionException {

	}
}