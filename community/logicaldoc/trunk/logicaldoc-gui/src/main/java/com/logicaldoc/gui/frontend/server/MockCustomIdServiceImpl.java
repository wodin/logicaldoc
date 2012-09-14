package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.beans.GUISequence;
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
	public void resetSequence(String sid, long sequenceId, long value) throws InvalidSessionException {

	}

	@Override
	public GUISequence[] loadSequences(String sid) throws InvalidSessionException {
		GUISequence[] sequences = new GUISequence[30];
		for (int i = 0; i < sequences.length; i++) {
			GUISequence seq = new GUISequence();
			seq.setId(i);
			seq.setTemplate("Template " + i);
			seq.setFrequency(i % 2 == 0 ? "year" : "month");
			seq.setValue(45);
			seq.setYear(i % 2 == 0 ? 2010 : 2009);
			sequences[i] = seq;
		}
		return sequences;
	}
}