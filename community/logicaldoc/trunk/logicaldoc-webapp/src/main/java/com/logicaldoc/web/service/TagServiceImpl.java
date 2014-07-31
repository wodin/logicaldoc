package com.logicaldoc.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.TagCloud;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * Implementation of the TagService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagServiceImpl extends RemoteServiceServlet implements TagService {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

	@Override
	public GUITag[] getTagCloud(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			ArrayList<GUITag> ret = new ArrayList<GUITag>();
			List<TagCloud> list = new ArrayList<TagCloud>();
			GenericDAO gendao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
			Generic generic = gendao.findByAlternateKey("tagcloud", "-", null, session.getTenantId());
			if (generic == null)
				return new GUITag[0];
			else
				gendao.initialize(generic);

			for (String tag : generic.getAttributeNames()) {
				TagCloud tc = new TagCloud(tag);
				StringTokenizer st = new StringTokenizer(generic.getValue(tag).toString(), "|", false);
				tc.setCount(Integer.parseInt(st.nextToken()));
				tc.setScale(Integer.parseInt(st.nextToken()));
				list.add(tc);
			}

			// Sort the tags collection by name
			Comparator<TagCloud> compName = new TagCloudComparatorName();
			Collections.sort(list, compName);

			for (TagCloud tagCloud : list) {
				GUITag c = new GUITag();
				c.setScale(tagCloud.getScale());
				c.setTag(tagCloud.getTag());
				c.setCount(tagCloud.getCount());
				ret.add(c);
			}

			return ret.toArray(new GUITag[0]);
		} catch (Throwable t) {
			return (GUITag[]) ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void delete(String sid, String tag) {

	}

	@Override
	public void rename(String sid, String tag, String newTag) {

	}

	class TagCloudComparatorName implements Comparator<TagCloud> {
		public int compare(TagCloud tc0, TagCloud tc1) {
			return tc0.getTag().compareTo(tc1.getTag());
		}
	}

	@Override
	public void addTag(String sid, String tag) throws ServerException {

	}

	@Override
	public void removeTag(String sid, String tag) throws ServerException {

	}

	@Override
	public GUIParameter[] getSettings(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		List<GUIParameter> params = new ArrayList<GUIParameter>();
		for (Object name : conf.keySet()) {
			if (name.toString().startsWith(session.getTenantName() + ".tag."))
				if (name.equals(session.getTenantName() + ".tag.mode"))
					params.add(new GUIParameter(name.toString(), "free"));
				else
					params.add(new GUIParameter(name.toString(), conf.getProperty(name.toString())));
		}

		return params.toArray(new GUIParameter[0]);
	}
}