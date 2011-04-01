package com.logicaldoc.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.TagCloud;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.util.Context;

/**
 * Implementation of the TagService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagServiceImpl extends RemoteServiceServlet implements TagService {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(TagServiceImpl.class);

	@Override
	public GUITag[] getTagCloud() {
		try {
			ArrayList<GUITag> ret = new ArrayList<GUITag>();
			List<TagCloud> list = new ArrayList<TagCloud>();
			GenericDAO gendao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
			Generic generic = gendao.findByAlternateKey("tagcloud", "-");
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
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
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
}