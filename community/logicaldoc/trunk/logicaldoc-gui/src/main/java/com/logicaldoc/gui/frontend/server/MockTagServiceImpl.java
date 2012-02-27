package com.logicaldoc.gui.frontend.server;

import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.frontend.client.services.TagService;

/**
 * Implementation of the TagService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockTagServiceImpl extends RemoteServiceServlet implements TagService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUITag[] getTagCloud() {
		GUITag[] cloud = new GUITag[30];
		Random r = new Random();
		for (int i = 0; i < cloud.length; i++) {
			GUITag c = new GUITag();
			c.setScale(r.nextInt(9) + 1);
			c.setTag("Tag_" + i);
			c.setCount(r.nextInt(9) * i + 5);
			cloud[i] = c;
		}
		return cloud;
	}

	@Override
	public void delete(String sid, String tag) {
	}

	@Override
	public void rename(String sid, String tag, String newTag) {
	}

	@Override
	public void addTag(String sid, String tag) throws InvalidSessionException {

	}

	@Override
	public void removeTag(String sid, String tag) throws InvalidSessionException {

	}

	@Override
	public GUIParameter[] getSettings(String sid) throws InvalidSessionException {
		return new GUIParameter[] { new GUIParameter("tag.mode", "free") };
	}
}